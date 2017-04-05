package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;

import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBWriteBatch implements WriteBatch {
	
	private org.rocksdb.WriteBatch wb = new org.rocksdb.WriteBatch();
	private ColumnFamilyHandle handle;

	public RocksDBWriteBatch(ColumnFamilyHandle handle) {
		this.handle = handle;
	}
	
	@Override
	public void put(ByteSpan key, ByteSpan value) {
		// TODO this is temporary
		byte [] keyarr = key.getTrimmedArray();
		byte [] valuearr = value.getTrimmedArray();
		
		wb.put(handle, keyarr, valuearr);
	}

	@Override
	public void remove(ByteSpan key) {
		byte [] keyarr = key.getTrimmedArray();
		wb.remove(handle, keyarr);
	}
	
	protected org.rocksdb.WriteBatch getInternalWB() {
		return wb;
	}
}
