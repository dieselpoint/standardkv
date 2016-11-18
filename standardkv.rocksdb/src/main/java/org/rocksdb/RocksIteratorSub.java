package org.rocksdb;

public class RocksIteratorSub extends RocksIterator {

	protected RocksIteratorSub(RocksDB rocksDB, long nativeHandle) {
		super(rocksDB, nativeHandle);
		// TODO Auto-generated constructor stub
	}
	
	public void seek(byte[] target, int targetLen) {
		assert (isOwningHandle());
		seek0(nativeHandle_, target, targetLen);
	}

}
