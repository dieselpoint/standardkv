package com.dieselpoint.standardkv.impl.memdb;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.Transaction;
import com.dieselpoint.standardkv.Util;
import com.dieselpoint.standardkv.WriteBatch;

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
	public KVTable getTable(String tableName, boolean createIfNecessary) {
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

	@Override
	public Transaction startTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(WriteBatch batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WriteBatch newWriteBatch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getTableNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dropTable(String name) {
		// TODO Auto-generated method stub
		
	}

}
