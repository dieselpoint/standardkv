package com.dieselpoint.standardkv.impl.rocksdb;

import java.io.IOException;

import org.junit.Test;

import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.KVTable;

public class TestColumnFamilies {

	@Test
	public void test() throws IOException {
		RocksDBStore store = new RocksDBStore();
		store.init("/home/ccleve/temp/colfams");

		// add two keys to each table
		// then cursor through them
		
		Bucket bucket0 = store.getBucket("bucket0", true);
		KVTable table0 = bucket0.getTable("tbl0", true);
		table0.put(new ByteArray("t0k0"), new ByteArray("t0v0"));
		table0.put(new ByteArray("t0k1"), new ByteArray("t0v1"));
		
		Bucket bucket1 = store.getBucket("bucket1", true);
		KVTable table1 = bucket1.getTable("tbl1", true);
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
