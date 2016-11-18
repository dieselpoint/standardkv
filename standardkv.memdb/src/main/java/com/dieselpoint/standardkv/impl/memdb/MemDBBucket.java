package com.dieselpoint.standardkv.impl.memdb;

import java.util.concurrent.ConcurrentHashMap;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Table;


public class MemDBBucket implements Bucket {
	
	private ConcurrentHashMap<String, MemDBTable> map = new ConcurrentHashMap();

	public MemDBBucket(String name) {
	}

	@Override
	public Table getTable(String name) {
		return map.get(name);
	}

	@Override
	public void delete() {
		map.clear();
	}

	@Override
	public void close() {
	}

	@Override
	public Table createTable(String name) {
		if (getTable(name) != null) {
			throw new StoreException("Table already exists: " + name);
		}
		MemDBTable table = new MemDBTable(name);
		map.put(name, table);
		return table;
	}
	

}
