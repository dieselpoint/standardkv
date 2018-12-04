package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.KVCursor;


public class RocksDBCursor implements KVCursor {
	
	private RocksIterator iterator;
	private boolean beforeFirst = true;

	public RocksDBCursor(RocksDB db, ColumnFamilyHandle handle) {
		iterator = db.newIterator(handle);
	}
	
	@Override
	public void seek(Buffer key) {
		
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
	public void last() {
		iterator.seekToLast();
	}
	
	@Override
	public Buffer getKey() {
		return new ByteArray(iterator.key());
	}

	@Override
	public Buffer getValue() {
		return new ByteArray(iterator.value());
	}

	@Override
	public boolean isEOF() {
		return !iterator.isValid();
	}


}
