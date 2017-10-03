package com.dieselpoint.standardkv.impl.rocksdb;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBWriteBatch implements WriteBatch {
	
	private org.rocksdb.WriteBatch wb = new org.rocksdb.WriteBatch();
	
	@Override
	public void put(KVTable table, Buffer key, Buffer value) {
		// TODO this is temporary
		byte [] keyarr = ((ByteArray)key).getTrimmedArray();
		byte [] valuearr = ((ByteArray)value).getTrimmedArray();
		
		wb.put(((RocksDBTable)table).getHandle(), keyarr, valuearr);
	}

	@Override
	public void remove(KVTable table, Buffer key) {
		byte [] keyarr = ((ByteArray)key).getTrimmedArray();
		wb.remove(((RocksDBTable)table).getHandle(), keyarr);
	}
	
	protected org.rocksdb.WriteBatch getInternalWB() {
		return wb;
	}
}
