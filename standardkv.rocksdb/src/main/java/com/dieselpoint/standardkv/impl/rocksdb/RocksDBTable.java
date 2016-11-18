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
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBTable implements Table {

	private RocksDBSub db;
	private String tableName;
	private ColumnFamilyHandle handle;
	private WriteOptions wo = new WriteOptions();

	public RocksDBTable(RocksDBSub db, String tableName, ColumnFamilyHandle handle) {
		this.db = db;
		this.tableName = tableName;
		this.handle = handle;
	}

	@Override
	public void put(ByteSpan key, ByteSpan value) {
		try {
			// this is temporary until rocksdb gets its act together and releases slices
			ByteArray keyLocal = (ByteArray) key;
			ByteArray valueLocal = (ByteArray) value;
			
			db.put(handle, keyLocal.getArray(), key.size(), valueLocal.getArray(), value.size());
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
