package com.dieselpoint.standardkv.impl.rocksdb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.rocksdb.WriteOptions;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Transaction;
import com.dieselpoint.standardkv.WriteBatch;
import com.dieselpoint.util.FileUtil;
import com.dieselpoint.util.NameUtil;


public class RocksDBBucket implements Bucket {
	
	public static long BLOCK_SIZE = 1024 * 4;
	
	private RocksDB db;
	private String path;
	private ConcurrentHashMap<String, RocksDBTable> tables = new ConcurrentHashMap<>();
	private ColumnFamilyOptions cfo = this.getColumnFamilyOptions();
	private WriteOptions wo = new WriteOptions();
	
	public RocksDBBucket(String path) {
		this.path = path;
	
		try {

			/* Bizarrely, you can't open a db at all unless you specify all the columnFamily names.
			 * https://github.com/facebook/rocksdb/wiki/Column-Families
			 * "When opening a DB in a read-write mode, you need to specify all Column Families 
			 * that currently exist in a DB. If that's not the case, DB::Open 
			 * call will return Status::InvalidArgument()."
			 * This is still true as of version 5.7.3.
			 */
			
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
				
				String tableName = new String(desc.getName(), StandardCharsets.UTF_8);
				tables.put(tableName, new RocksDBTable(db, tableName, handle));
			}

		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}


	@Override
	public List<String> getTableNames() {
		List<String> out = new ArrayList<>();
		try {
			List<byte[]> colFamNames = RocksDB.listColumnFamilies(new Options(), path);
			for (byte [] colFamName: colFamNames) {
				out.add(new String(colFamName, "UTF-8"));
			}
		} catch (RocksDBException | UnsupportedEncodingException e) {
			throw new StoreException(e);
		}
		return out;
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
	public KVTable getTable(String tableName) {
		return tables.get(tableName);
	}
	
	@Override
	public RocksDBTable createTable(String tableName) {
		try {
			NameUtil.checkForLegalName(tableName);
			
			ColumnFamilyDescriptor desc = new ColumnFamilyDescriptor(tableName.getBytes(StandardCharsets.UTF_8), cfo);
			ColumnFamilyHandle handle = db.createColumnFamily(desc);

			RocksDBTable table = new RocksDBTable(db, tableName, handle);
			tables.put(tableName, table);
			return table;
			
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

	@Override
	public Transaction startTransaction() {
		throw new UnsupportedOperationException();
	}


	@Override
	public void write(WriteBatch batch) {
		try {
			db.write(wo, ((RocksDBWriteBatch)batch).getInternalWB());
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public WriteBatch newWriteBatch() {
		return new RocksDBWriteBatch();
	}


	@Override
	public void dropTable(String name) {
		RocksDBTable table = tables.get(name);
		if (table == null) {
			throw new StoreException("Table doesn't exist:" + name);
		}
		tables.remove(name);
		try {
			db.dropColumnFamily(table.getHandle());
		} catch (IllegalArgumentException | RocksDBException e) {
			throw new StoreException(e);
		}
	}
	

}
