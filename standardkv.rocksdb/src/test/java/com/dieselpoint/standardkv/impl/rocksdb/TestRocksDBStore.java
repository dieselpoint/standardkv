package com.dieselpoint.standardkv.impl.rocksdb;

import com.dieselpoint.standardkv.Store;
import com.dieselpoint.standardkv.StoreFactory;
import com.dieselpoint.standardkv.TestStore;

public class TestRocksDBStore extends TestStore {

	@Override
	public Store getStore() {
		return StoreFactory.getStore("/temp/rocksdb", StoreFactory.ROCKSDB);
	}

}
