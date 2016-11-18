package com.dieselpoint.standardkv.impl.memdb;



import java.util.concurrent.ConcurrentHashMap;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Store;
import com.dieselpoint.standardkv.StoreException;

/**
 * This is not transactional. For testing only. It needs within-bucket transactions, across all tables in a bucket.
 * See http://stackoverflow.com/questions/11189784/efficient-concurrent-tree
 * 
 */
public class MemDBStore implements Store {
	
	private ConcurrentHashMap<String, Bucket> map = new ConcurrentHashMap();

	@Override
	public void init(String name) {
	}

	@Override
	public Bucket getBucket(String name) {
		return map.get(name);
	}

	@Override
	public void close() {
	}

	@Override
	public synchronized Bucket createBucket(String name) {
		if (getBucket(name) != null) {
			throw new StoreException("Bucket already exists: " + name);
		}
		Bucket bucket = new MemDBBucket(name);
		map.put(name, bucket);
		return bucket;
	}

}
