package com.dieselpoint.standardkv.impl.rocksdb;

import org.rocksdb.RocksDBException;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.WriteBatch;

public class RocksDBWriteBatch implements WriteBatch {

	private org.rocksdb.WriteBatch wb = new org.rocksdb.WriteBatch();

	@Override
	public void put(KVTable table, Buffer key, Buffer value) {
		try {
			// TODO this is temporary
			byte[] keyarr = ((ByteArray) key).getTrimmedArray();
			byte[] valuearr = ((ByteArray) value).getTrimmedArray();
			wb.put(((RocksDBTable) table).getHandle(), keyarr, valuearr);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	@Override
	public void remove(KVTable table, Buffer key) {
		byte[] keyarr = ((ByteArray) key).getTrimmedArray();
		try {
			wb.delete(((RocksDBTable) table).getHandle(), keyarr);
		} catch (RocksDBException e) {
			throw new StoreException(e);
		}
	}

	protected org.rocksdb.WriteBatch getInternalWB() {
		return wb;
	}
}
