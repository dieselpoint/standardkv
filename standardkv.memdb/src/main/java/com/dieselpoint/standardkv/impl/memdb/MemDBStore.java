package com.dieselpoint.standardkv.impl.memdb;



import java.util.concurrent.ConcurrentHashMap;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Store;

/**
 * This is not transactional. For testing only. It needs within-bucket transactions, across all tables in a bucket.
 * See http://stackoverflow.com/questions/11189784/efficient-concurrent-tree
 * 
 */
public class MemDBStore implements Store {
	
	private ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

	@Override
	public void init(String name) {
	}

	@Override
	public void close() {
	}

	@Override
	public Bucket getBucket(String bucketName) {
		return buckets.get(bucketName);
	}
	
	@Override
	public MemDBBucket createBucket(String bucketName) {
		MemDBBucket bucket = new MemDBBucket(bucketName);
		buckets.put(bucketName, bucket);
		return bucket;
	}

	@Override
	public void deleteBucket(String bucketName) {
		buckets.remove(bucketName);
	}

}
