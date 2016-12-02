package com.dieselpoint.standardkv.impl.lmdb;

import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Env;

import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.WriteBatch;

public class LmdbTable implements Table {
	
	private Env env;
	private Database db;

	public LmdbTable(Env env, Database db) {
		this.env = env;
		this.db = db;
	}


	/*
	@Override
	public void write(List<Record> records) {
		Transaction tx = env.createTransaction();
		for (Record rec: records) {
			db.put(tx, rec.getKey().getTrimmedArray(), rec.getValue().getTrimmedArray());
		}
		tx.commit();
	}

	@Override
	public void put(ByteBuf key, ByteBuf value) {
		db.put(key.getTrimmedArray(), value.getTrimmedArray());
	}
	*/

	@Override
	public Cursor newCursor() {
		return new LmdbCursor(env, db);
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
	public void put(ByteSpan key, ByteSpan value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void remove(ByteSpan key) {
		// TODO Auto-generated method stub
		
	}


}
