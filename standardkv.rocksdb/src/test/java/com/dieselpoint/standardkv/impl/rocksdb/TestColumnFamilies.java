package com.dieselpoint.standardkv.impl.rocksdb;

import java.io.IOException;

import org.junit.Test;

import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.util.FileUtil;

public class TestColumnFamilies {

	@Test
	public void test() throws IOException {
		RocksDBStore store = new RocksDBStore();
		store.init(FileUtil.getTempDir("/temp"));

		// add two keys to each table
		// then cursor through them
		
		Bucket bucket0 = store.createBucket("bucket0");
		KVTable table0 = bucket0.createTable("tbl0");
		table0.put(new ByteArray("t0k0"), new ByteArray("t0v0"));
		table0.put(new ByteArray("t0k1"), new ByteArray("t0v1"));
		
		Bucket bucket1 = store.createBucket("bucket1");
		KVTable table1 = bucket1.createTable("tbl1");
		table1.put(new ByteArray("t1k0"), new ByteArray("t1v0"));
		table1.put(new ByteArray("t1k1"), new ByteArray("t1v1"));
		
		Cursor curs0 = table0.newCursor();
		curs0.beforeFirst();
		while (curs0.next()) {
			System.out.println(curs0.getKey().readString() + " " + curs0.getValue().readString());
		}
		
		Cursor curs1 = table1.newCursor();
		curs1.beforeFirst();
		while (curs1.next()) {
			System.out.println(curs1.getKey().readString() + " " + curs1.getValue().readString());
		}
		
	}

	
}
