package org.rocksdb;

public class RocksWriteBatchSub extends org.rocksdb.WriteBatch {

	public void put(ColumnFamilyHandle columnFamilyHandle, byte[] key, int keyLen, byte[] value, int valueLen) {
		assert (isOwningHandle());
		put(nativeHandle_, key, keyLen, value, valueLen, columnFamilyHandle.nativeHandle_);
	}

	public void remove(ColumnFamilyHandle columnFamilyHandle, byte[] key, int keyLen) {
		assert (isOwningHandle());
		remove(nativeHandle_, key, keyLen, columnFamilyHandle.nativeHandle_);
	}

}
