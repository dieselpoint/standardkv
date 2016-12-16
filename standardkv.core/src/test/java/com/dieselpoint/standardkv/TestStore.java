package com.dieselpoint.standardkv;

import java.io.UnsupportedEncodingException;

import org.junit.Test;


public class TestStore {

	@Test
	public void test() throws UnsupportedEncodingException {
		
		//Store store = StoreFactory.getStore("/temp/lmdbtest", "lmdb");
		Store store = StoreFactory.getStore("/temp/leveldb", "LevelDbStore");
		
		Bucket bucket = store.getBucket("mybucket", true);
		Table table = bucket.getTable("footable", true);
		
		table.put(new ByteArray("foo"), new ByteArray("bar"));
		table.put(new ByteArray("foo"), new ByteArray("bar1"));
		table.put(new ByteArray("foo2"), new ByteArray("bar2"));

		Cursor curs = table.newCursor();
		
		curs.seek(new ByteArray("fo"));
		
		while (curs.next()) {
			System.out.println("key:" + curs.getKey().getString() + " value:" + curs.getValue().getString());
		}
		
		Cursor c = table.newCursor();
		while (c.next()) {
			System.out.println("key:" + c.getKey().getString());
		}
		
		bucket.delete();
		
		
	}

	
	
}
