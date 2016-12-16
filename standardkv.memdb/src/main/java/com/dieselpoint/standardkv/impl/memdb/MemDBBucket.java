package com.dieselpoint.standardkv.impl.memdb;

import java.util.concurrent.ConcurrentHashMap;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.Util;

public class MemDBBucket implements Bucket {

	private ConcurrentHashMap<String, MemDBTable> tables = new ConcurrentHashMap();

	public MemDBBucket(String name) {
	}

	@Override
	public void delete() {
		tables.clear();
	}

	@Override
	public void close() {
	}

	@Override
	public Table getTable(String tableName, boolean createIfNecessary) {
		if (createIfNecessary) {
			return tables.computeIfAbsent(tableName, k -> createTable(tableName));
		} else {
			return tables.get(tableName);
		}
	}

	private MemDBTable createTable(String tableName) {
		Util.checkForLegalName(tableName);
		MemDBTable table = new MemDBTable(tableName);
		return table;
	}

}
