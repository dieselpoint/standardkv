package com.dieselpoint.standardkv.impl.memdb;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.Transaction;
import com.dieselpoint.standardkv.WriteBatch;
import com.dieselpoint.util.NameUtil;

public class MemDBBucket implements Bucket {

	private ConcurrentHashMap<String, MemDBTable> tables = new ConcurrentHashMap<>();

	public MemDBBucket(String name) {
	}

	@Override
	public void close() {
	}

	@Override
	public KVTable getTable(String tableName) {
		return tables.get(tableName);
	}

	@Override
	public MemDBTable createTable(String tableName) {
		NameUtil.checkForLegalName(tableName);
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
