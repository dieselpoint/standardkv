package com.dieselpoint.standardkv;

import java.io.UnsupportedEncodingException;

import com.dieselpoint.buffers.ByteArray;


abstract public class TestStore {


	public void test() throws UnsupportedEncodingException {
		
		Store store = getStore();
		
		Bucket bucket = store.createBucket("mybucket");
		KVTable table = bucket.createTable("footable");
		
		table.put(new ByteArray("aa"), new ByteArray("a_value"));
		table.put(new ByteArray("foo"), new ByteArray("bar"));
		table.put(new ByteArray("foo"), new ByteArray("bar1"));
		table.put(new ByteArray("foo2"), new ByteArray("bar2"));

		KVCursor curs = table.newCursor();
		
		curs.seek(new ByteArray("fo"));
		
		while (true) {
			System.out.println("key:" + curs.getKey().readString() + " value:" + curs.getValue().readString());
			if (!curs.next()) {
				break;
			}
		}
		
		KVCursor c = table.newCursor();
		while (c.next()) {
			System.out.println("key:" + c.getKey().readString());
		}
		
		bucket.delete();
		
		
	}

	
	abstract public Store getStore();
	
	
}
