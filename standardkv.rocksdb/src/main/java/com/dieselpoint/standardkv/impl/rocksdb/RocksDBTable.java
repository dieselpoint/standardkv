package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Transaction;
import com.dieselpoint.util.NameUtil;

public class RocksDBTable implements KVTable {

	private RocksDB db;
	private ColumnFamilyHandle handle;


	public RocksDBTable(RocksDB db, String tableName, ColumnFamilyHandle handle) {
		NameUtil.checkForLegalName(tableName);
		this.db = db;
		this.handle = handle;
	}

	@Override
	public void put(Buffer key, Buffer value) {
		try {
			
			// TODO stupid unnecessary rocks stuff
			// this is temporary until rocksdb gets its act together and releases slices
			byte [] keyarr = ((ByteArray)key).getTrimmedArray();

			byte [] valuearr = ((ByteArray)value).getTrimmedArray();
			
			db.put(handle, keyarr, valuearr);
			
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public Cursor newCursor() {
		return new RocksDBCursor(db, handle);
	}


	@Override
	public void remove(Buffer key) {
		try {
			// TODO this is temporary. stupid rocks stuff
			ByteArray keyLocal = (ByteArray) key;
			byte [] arr = keyLocal.getTrimmedArray();
			
			db.delete(handle, arr);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	public void compact() {
		try {
			db.compactRange(handle);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public Buffer get(Buffer key) {
		
		// TODO fix
		byte [] keyarr = ((ByteArray)key).getTrimmedArray();
		
		try {
			byte [] value = db.get(handle, keyarr);
			if (value == null) {
				return null;
			} 
			return new ByteArray(value);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	public void close() {
		// see RocksDBBucket.close() for the reason for this
		handle.close();
	}
	
	protected ColumnFamilyHandle getHandle() {
		return handle;
	}

	@Override
	public void put(Transaction trans, Buffer key, Buffer value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Buffer get(Transaction trans, Buffer key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(Transaction trans, Buffer key) {
		throw new UnsupportedOperationException();
	}
	
	
}
