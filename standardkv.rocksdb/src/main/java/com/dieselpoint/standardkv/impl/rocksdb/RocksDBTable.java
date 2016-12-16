package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksDBSub;
import org.rocksdb.WriteOptions;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.Util;
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBTable implements Table {

	private RocksDBSub db;
	private ColumnFamilyHandle handle;
	private WriteOptions wo = new WriteOptions();

	public RocksDBTable(RocksDBSub db, String tableName, ColumnFamilyHandle handle) {
		
		if (Util.isEmpty(tableName)) {
			throw new StoreException("tableName is empty");
		}
		
		this.db = db;
		this.handle = handle;
	}

	@Override
	public void put(ByteSpan key, ByteSpan value) {
		try {
			
			byte [] keyarr;
			int keylen;
			byte [] valuearr;
			int valuelen;
			
			// this is temporary until rocksdb gets its act together and releases slices
			if (key instanceof ByteArray) {
				ByteArray arr = (ByteArray) key;
				keyarr = arr.getArray();
				keylen = arr.size();
			} else {
				keylen = key.size();
				keyarr = new byte[keylen];
				key.copyTo(0, keyarr, 0, keylen);
			}

			if (value instanceof ByteArray) {
				ByteArray arr = (ByteArray) value;
				valuearr = arr.getArray();
				valuelen = arr.size();
			} else {
				valuelen = value.size();
				valuearr = new byte[valuelen];
				value.copyTo(0, valuearr, 0, valuelen);
			}
			
			db.put(handle, keyarr, keylen, valuearr, valuelen);
			
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public Cursor newCursor() {
		return new RocksDBCursor(db, handle);
	}

	@Override
	public void write(WriteBatch batch) {
		try {
			db.write(wo, ((RocksDBWriteBatch) batch).getInternalWB());
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public void remove(ByteSpan key) {
		try {
			// this is temporary
			ByteArray keyLocal = (ByteArray) key;
			
			db.remove(handle, keyLocal.getArray(), key.size());
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public WriteBatch newWriteBatch() {
		return new RocksDBWriteBatch(handle);
	}

	public void compact() {
		try {
			db.compactRange(handle);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	
}
