package com.dieselpoint.standardkv.impl.rocksdb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.Util;
import com.dieselpoint.util.FileUtil;


public class RocksDBBucket implements Bucket {
	
	public static long BLOCK_SIZE = 1024 * 4;
	
	private RocksDB db;
	private String path;
	private ConcurrentHashMap<String, RocksDBTable> tables = new ConcurrentHashMap<>();
	private ColumnFamilyOptions cfo = this.getColumnFamilyOptions();
	
	
	public RocksDBBucket(String rootDir, String bucketName) {

		Util.checkForLegalName(bucketName);

		File pathFile = new File(rootDir, bucketName);
		pathFile.mkdirs();
		
		this.path = pathFile.getAbsolutePath();
		
		try {
			// may as well open all tables now. can revisit later.
			List<byte[]> colFamNames = RocksDB.listColumnFamilies(new Options(), path);
			ArrayList<ColumnFamilyDescriptor> descriptors = new ArrayList<ColumnFamilyDescriptor>();
			ArrayList<ColumnFamilyHandle> handles = new ArrayList<ColumnFamilyHandle>();
			
			// required
			ColumnFamilyDescriptor defaultDesc = new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfo);
			descriptors.add(defaultDesc);
			
			for (byte [] colFamName: colFamNames) {
				ColumnFamilyDescriptor desc = new ColumnFamilyDescriptor(colFamName, cfo);
				descriptors.add(desc);
			}

			DBOptions dbo = new DBOptions();
			dbo.setCreateIfMissing(true);
			
			//db = RocksDB.openReadOnly(dbo, path, descriptors, handles); // look at DBOptions
			db = RocksDB.open(dbo, path, descriptors, handles); // look at DBOptions

			int count = handles.size();
			for (int i = 0; i < count; i++) {
				ColumnFamilyDescriptor desc = descriptors.get(i);
				ColumnFamilyHandle handle = handles.get(i);
				
				String tableName = new String(desc.columnFamilyName(), StandardCharsets.UTF_8);
				tables.put(tableName, new RocksDBTable(db, tableName, handle));
			}
			

		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public void close() {
		// must explicitly close columnfamilyhandles per
		// https://github.com/facebook/rocksdb/issues/1080
		// https://github.com/facebook/rocksdb/issues/974
		for (RocksDBTable table: tables.values()) {
			table.close();
		}
		db.close();
	}
	
	
	@Override
	public Table getTable(String tableName, boolean createIfNecessary) {
		if (createIfNecessary) {
			return tables.computeIfAbsent(tableName, k -> createTable(tableName));
		} else {
			return tables.get(tableName);
		}
	}

	
	private RocksDBTable createTable(String tableName) {
		try {
			Util.checkForLegalName(tableName);
			
			ColumnFamilyDescriptor desc = new ColumnFamilyDescriptor(tableName.getBytes(StandardCharsets.UTF_8), cfo);
			ColumnFamilyHandle handle = db.createColumnFamily(desc);

			return new RocksDBTable(db, tableName, handle);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}	
	
	
	private ColumnFamilyOptions getColumnFamilyOptions() {
		BlockBasedTableConfig tableOptions = new BlockBasedTableConfig();
		//tableOptions.setFilter(new BloomFilter(10));
		tableOptions.setBlockSize(BLOCK_SIZE);

		ColumnFamilyOptions cfo = new ColumnFamilyOptions();
		cfo.setTableFormatConfig(tableOptions);
		
		// add this back when they add snappy to the windows .dll
		// cfo.setCompressionType(CompressionType.SNAPPY_COMPRESSION);
		
		return cfo;
	}
	
	
	@Override
	public void delete() {
		db.close();
		try {
			FileUtil.deleteDir(path);
		} catch (IOException e) {
			throw new StoreException(e);
		}
	}
	

}
