package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.ColumnFamilyHandle;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBWriteBatch implements WriteBatch {
	
	private org.rocksdb.WriteBatch wb = new org.rocksdb.WriteBatch();
	private ColumnFamilyHandle handle;

	public RocksDBWriteBatch(ColumnFamilyHandle handle) {
		this.handle = handle;
	}
	
	@Override
	public void put(Buffer key, Buffer value) {
		// TODO this is temporary
		byte [] keyarr = ((ByteArray)key).getTrimmedArray();
		byte [] valuearr = ((ByteArray)value).getTrimmedArray();
		
		wb.put(handle, keyarr, valuearr);
	}

	@Override
	public void remove(Buffer key) {
		byte [] keyarr = ((ByteArray)key).getTrimmedArray();
		wb.remove(handle, keyarr);
	}
	
	protected org.rocksdb.WriteBatch getInternalWB() {
		return wb;
	}
}
