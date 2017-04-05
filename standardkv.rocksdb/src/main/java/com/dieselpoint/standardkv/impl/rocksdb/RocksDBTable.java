package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteOptions;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.Util;
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBTable implements Table {

	private RocksDB db;
	private ColumnFamilyHandle handle;
	private WriteOptions wo = new WriteOptions();

	public RocksDBTable(RocksDB db, String tableName, ColumnFamilyHandle handle) {
		
		if (Util.isEmpty(tableName)) {
			throw new StoreException("tableName is empty");
		}
		
		this.db = db;
		this.handle = handle;
	}

	@Override
	public void put(ByteSpan key, ByteSpan value) {
		try {
			
			// TODO stupid unnecessary rocks stuff
			// this is temporary until rocksdb gets its act together and releases slices
			byte [] keyarr = new byte[key.size()];
			key.copyTo(0, keyarr, 0, key.size());
			
			byte [] valuearr = new byte[value.size()];
			value.copyTo(0, valuearr, 0, value.size());
			
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
	public void write(WriteBatch batch) {
		try {
			db.write(wo, ((RocksDBWriteBatch)batch).getInternalWB());
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public void remove(ByteSpan key) {
		try {
			// TODO this is temporary. stupid rocks stuff
			ByteArray keyLocal = (ByteArray) key;
			byte [] arr = keyLocal.getTrimmedArray();
			
			db.delete(handle, arr);
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

	@Override
	public ByteSpan get(ByteSpan key) {
		
		int keylen = key.size();
		byte [] keyarr = new byte[keylen];
		key.copyTo(0, keyarr, 0, keylen);
		
		try {
			byte [] value = db.get(handle, keyarr);
			return new ByteArray(value);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	
}
