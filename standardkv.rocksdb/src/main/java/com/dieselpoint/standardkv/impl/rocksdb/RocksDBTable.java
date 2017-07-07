package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteOptions;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.WriteBatch;
import com.dieselpoint.util.CommonUtil;

public class RocksDBTable implements Table {

	private RocksDB db;
	private ColumnFamilyHandle handle;
	private WriteOptions wo = new WriteOptions();

	public RocksDBTable(RocksDB db, String tableName, ColumnFamilyHandle handle) {
		
		if (CommonUtil.isEmpty(tableName)) {
			throw new StoreException("tableName is empty");
		}
		
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
	public void write(WriteBatch batch) {
		try {
			db.write(wo, ((RocksDBWriteBatch)batch).getInternalWB());
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
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
	
}
