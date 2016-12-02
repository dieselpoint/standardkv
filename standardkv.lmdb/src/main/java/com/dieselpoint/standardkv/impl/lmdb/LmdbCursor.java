package com.dieselpoint.standardkv.impl.lmdb;

import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Entry;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;

import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;


public class LmdbCursor implements Cursor {
	
	private Transaction tx;
	private org.fusesource.lmdbjni.Cursor curs;
	private Entry entry;

	public LmdbCursor(Env env, Database db) {
		tx = env.createTransaction(true);
		curs = db.openCursor(tx);
	}

	/*
	@Override
	public void seek(ByteBuf key) {
		
		// to seek to first()?
		// curs.get(GetOp.FIRST);
		
		entry = curs.seek(SeekOp.RANGE, key.getTrimmedArray());
	}

	@Override
	public boolean hasNext() {
		return entry != null;
	}

	@Override
	public Record next() {
		Record rec = new Record();
		rec.setKey(new ByteArray(entry.getKey()));
		rec.setValue(new ByteArray(entry.getValue()));

		entry = curs.get(GetOp.NEXT);
		
		return rec;
	}
*/
	@Override
	public void close() {
		tx.commit();
		curs.close();
	}

	/*
	@Override
	public void first() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void last() {
		throw new UnsupportedOperationException("not yet implemented");
	}
*/
	@Override
	public void beforeFirst() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean next() {
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
