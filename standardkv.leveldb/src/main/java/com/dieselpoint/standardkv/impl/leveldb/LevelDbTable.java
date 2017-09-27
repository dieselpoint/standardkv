package com.dieselpoint.standardkv.impl.leveldb;

import org.iq80.leveldb.DB;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.buffers.ByteSpan;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.Transaction;
import com.dieselpoint.standardkv.WriteBatch;


public class LevelDbTable implements Table {
	
	public static final byte DOT = '.';
	
	private DB db;
	private ByteArray keyBuf = new ByteArray();
	private int keyPrefixLen;

	
	public LevelDbTable(DB db, String bucketName, String tableName) {
		this.db = db;
		keyBuf.appendString(bucketName);
		keyBuf.appendByte(DOT);
		keyBuf.appendString(tableName);
		keyBuf.appendByte(DOT);
		keyPrefixLen = keyBuf.size();
	}



	@Override
	public Cursor newCursor() {
		return new LevelDbCursor(db, keyBuf, keyPrefixLen);
	}






	@Override
	public void remove(Buffer key) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void put(Buffer key, Buffer value) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Buffer get(Buffer key) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void put(Transaction trans, Buffer key, Buffer value) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Buffer get(Transaction trans, Buffer key) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void remove(Transaction trans, Buffer key) {
		// TODO Auto-generated method stub
		
	}





}
