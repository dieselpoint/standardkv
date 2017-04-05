package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;


public class RocksDBCursor implements Cursor {
	
	private RocksIterator iterator;
	private boolean beforeFirst = true;

	public RocksDBCursor(RocksDB db, ColumnFamilyHandle handle) {
		iterator = db.newIterator(handle);
	}
	
	@Override
	public void seek(ByteSpan key) {
		
		// TODO this is temporary. stupidly, rocks doesn't expose a keylen param in .seek()
		ByteArray keyLocal = (ByteArray) key;
		byte [] arr = keyLocal.getTrimmedArray();
		
		iterator.seek(arr);
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
