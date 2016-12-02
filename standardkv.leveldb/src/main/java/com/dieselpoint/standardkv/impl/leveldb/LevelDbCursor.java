package com.dieselpoint.standardkv.impl.leveldb;

import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.StoreException;


public class LevelDbCursor implements Cursor {
	
	private DBIterator iterator;
	private ByteArray keyBuf = new ByteArray();
	private int keyPrefixLen;

	public LevelDbCursor(DB db, ByteArray keyBuf, int keyPrefixLen) {
		iterator = db.iterator();
		
		keyBuf.setSize(keyPrefixLen);
		this.keyBuf.append(keyBuf);
		this.keyPrefixLen = keyPrefixLen;
	}


	/*
	@Override
	public void seek(ByteBuf key) {
		keyBuf.setSize(keyPrefixLen);
		keyBuf.append(key);
		iterator.seek(keyBuf.getTrimmedArray());
	}
	*/

	/*
	@Override
	public Record next() {
		Entry <byte[], byte[]> entry = iterator.next();
		
		Record rec = new Record();
		
		byte [] keyarr = entry.getKey();
		
		// this gets rid of the key prefix
		int keylen = keyarr.length - keyPrefixLen;
		System.arraycopy(keyarr, keyPrefixLen, keyarr, 0, keylen);
		ByteArray key = new ByteArray(keyarr, keylen); 
		
		rec.setKey(key);
		rec.setValue(new ByteArray(entry.getValue()));
		
		return rec;
	}
	*/
	

	@Override
	public void close() {
		try {
			iterator.close();
		} catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/*

	@Override
	public void first() {
		iterator.seekToFirst();
	}

	
	@Override
	public void last() {
		iterator.seekToLast();
	}
	*/


	@Override
	public void beforeFirst() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean next() {
		if (!iterator.hasNext()) {
			return false;
		}
		
		//iterator.next()
		
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public boolean isEOF() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void seek(ByteSpan key) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ByteSpan getKey() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ByteSpan getValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
