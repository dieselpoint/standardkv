package com.dieselpoint.standardkv.impl.rocksdb;

import java.io.IOException;

import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.FlushOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import com.dieselpoint.util.FileUtil;

public class Test {

	String db_path = "/temp/rocksdb";
	
	static {
		RocksDB.loadLibrary();
	}

	public static void main(String[] args) throws RocksDBException, IOException {
		Test test = new Test();
		test.test1();
		test.test2();
		test.test3();
	}
	
	private void test1() throws RocksDBException, IOException {
		FileUtil.deleteDir(db_path);
		
		Options options = new Options();
		options.setCreateIfMissing(true);
		RocksDB db = RocksDB.open(options, db_path);

		db.put("hello".getBytes(), "world".getBytes());

		byte[] value = db.get("hello".getBytes());
		
		FlushOptions fo = new FlushOptions();
		db.flush(fo);
		fo.close();
		
		System.out.println(new String(value));

		db.close();
	}
	
	private void test2() throws RocksDBException, IOException {
		FileUtil.deleteDir(db_path);
		
		BlockBasedTableConfig tableOptions = new BlockBasedTableConfig();
		tableOptions
		.setFilter(new BloomFilter(10));
		//.setBlockSize(1024 * 512);
		
		ColumnFamilyOptions cfo = new ColumnFamilyOptions();
		cfo.setTableFormatConfig(tableOptions);
		
		ColumnFamilyDescriptor desc = new ColumnFamilyDescriptor("schlump".getBytes(), cfo);
		
		Options options = new Options();
		options.setCreateIfMissing(true);
		RocksDB db = RocksDB.open(options, db_path);
		
		// set options here
		ColumnFamilyHandle handle = db.createColumnFamily(desc);

		db.put("hello".getBytes(), "world".getBytes());

		byte[] value = db.get("hello".getBytes());

		System.out.println(new String(value));

		db.close();
		
		Options opts = new Options();
		RocksDB.listColumnFamilies(opts, db_path);
		
		
	}
	
	private void test3() throws RocksDBException, IOException {
		
		FileUtil.deleteDir(db_path);
		
		Options options = new Options();
		options.setCreateIfMissing(true);
		RocksDB db = RocksDB.open(options, db_path);

		db.put("hello".getBytes(), "world".getBytes());
		db.put("foo".getBytes(), "bar".getBytes());
		
		FlushOptions fo = new FlushOptions();
		db.flush(fo);
		fo.close();
		
		RocksIterator it = db.newIterator();
		it.seekToFirst();
		
		byte [] key = it.key();
		System.out.println(key);
		System.out.println(new String(key));
		
		it.next();
		System.out.println(key);
		System.out.println(new String(key));

		// get the new key
		key = it.key();
		System.out.println(key);
		System.out.println(new String(key));
		
		db.close();
		
		
		
		
	}

}
