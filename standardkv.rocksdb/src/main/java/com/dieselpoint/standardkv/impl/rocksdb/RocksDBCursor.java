package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBSub;
import org.rocksdb.RocksIteratorSub;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;


public class RocksDBCursor implements Cursor {
	
	private RocksIteratorSub iterator;
	private boolean beforeFirst = true;

	public RocksDBCursor(RocksDBSub db, ColumnFamilyHandle handle) {
		iterator = db.newIterator(handle);
	}
	
	@Override
	public void seek(ByteSpan key) {
		
		// this is temporary
		ByteArray keyLocal = (ByteArray) key;
		
		iterator.seek(keyLocal.getArray(), keyLocal.size());
		beforeFirst = false;
	}

	@Override
	public void close() {
		iterator.close();
	}

	@Override
	public boolean next() {
		if (beforeFirst) {
			iterator.seekToFirst();
			beforeFirst = false;
		} else {
			iterator.next();
		}
		return iterator.isValid();
	}

	@Override
	public void beforeFirst() {
		beforeFirst = true;
	}

	@Override
	public ByteSpan getKey() {
		return new ByteArray(iterator.key());
	}

	@Override
	public ByteSpan getValue() {
		return new ByteArray(iterator.value());
	}

	@Override
	public boolean isEOF() {
		return !iterator.isValid();
	}

}
