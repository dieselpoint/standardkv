package com.dieselpoint.standardkv;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.dieselpoint.buffers.ByteArray;


public class TestStore {

	@Test
	public void test() throws UnsupportedEncodingException {
		
		// TODO move the tests to a new tests-and-benchmarks module
		
		//Store store = StoreFactory.getStore("/temp/lmdbtest", "lmdb");
		//Store store = StoreFactory.getStore("/temp/leveldb", "LevelDbStore");
		Store store = StoreFactory.getStore("/temp/dbtest", StoreFactory.ROCKSDB);
		
		Bucket bucket = store.getBucket("mybucket", true);
		Table table = bucket.getTable("footable", true);
		
		table.put(new ByteArray("foo"), new ByteArray("bar"));
		table.put(new ByteArray("foo"), new ByteArray("bar1"));
		table.put(new ByteArray("foo2"), new ByteArray("bar2"));

		Cursor curs = table.newCursor();
		
		curs.seek(new ByteArray("fo"));
		
		while (curs.next()) {
			System.out.println("key:" + curs.getKey().readUtf8String() + " value:" + curs.getValue().readUtf8String());
		}
		
		Cursor c = table.newCursor();
		while (c.next()) {
			System.out.println("key:" + c.getKey().readUtf8String());
		}
		
		bucket.delete();
		
		
	}

	
	
}
