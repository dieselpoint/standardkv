package com.dieselpoint.standardkv.impl.leveldb;

import org.iq80.leveldb.DB;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.WriteBatch;


public class LevelDbTable implements Table {
	
	public static final byte DOT = '.';
	
	private DB db;
	private ByteArray keyBuf = new ByteArray();
	private int keyPrefixLen;

	
	public LevelDbTable(DB db, String bucketName, String tableName) {
		this.db = db;
		keyBuf.append(bucketName);
		keyBuf.appendByte(DOT);
		keyBuf.append(tableName);
		keyBuf.appendByte(DOT);
		keyPrefixLen = keyBuf.size();
	}



	@Override
	public Cursor newCursor() {
		return new LevelDbCursor(db, keyBuf, keyPrefixLen);
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
		/*
		keyBuf.setSize(keyPrefixLen);
		keyBuf.append(key);
		db.put(keyBuf.getTrimmedArray(), value.getTrimmedArray());
		*/
		
	}



	@Override
	public void remove(ByteSpan key) {
		// TODO Auto-generated method stub
		
	}



}
