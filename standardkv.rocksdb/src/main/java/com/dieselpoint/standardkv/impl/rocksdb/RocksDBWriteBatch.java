package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksWriteBatchSub;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBWriteBatch implements WriteBatch {
	
	private org.rocksdb.RocksWriteBatchSub wb = new org.rocksdb.RocksWriteBatchSub();
	private ColumnFamilyHandle handle;

	public RocksDBWriteBatch(ColumnFamilyHandle handle) {
		this.handle = handle;
	}
	
	@Override
	public void put(ByteSpan key, ByteSpan value) {
		// this is temporary
		ByteArray keyLocal = (ByteArray) key;
		ByteArray valueLocal = (ByteArray) value;
		
		wb.put(handle, keyLocal.getArray(), key.size(), valueLocal.getArray(), value.size());
	}

	@Override
	public void remove(ByteSpan key) {
		ByteArray keyLocal = (ByteArray) key;
		wb.remove(handle, keyLocal.getArray(), key.size());
	}
	
	protected RocksWriteBatchSub getInternalWB() {
		return wb;
	}
}
