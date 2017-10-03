package com.dieselpoint.standardkv.impl.rocksdb;



import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.rocksdb.RocksDB;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Store;

public class RocksDBStore implements Store {
	
	/*
	 * RocksDB iteration is slow, and the java api is inefficient.
	 * It's possible for me to modify the jni code, but it looks like this is a big undertaking.
	 * Fortunately, there are changes being checked in every day in the rocksdb project.
	 * So, let's wait for them to fix these things:
	 * 
	 * 1. Expose slices.
	 * 2. Make it easier to do dynamic column families. Right now discovering existing col families is difficult
	 * because of the inconsistency in the way Options and DBOptions are handled.	  
	 * 
	 * An alternative would be to create a completely new Java project with
	 * jni code to talk to a generic rocks dll or so. That's a big undertaking. Wait.
	 */
	
	static {
		RocksDB.loadLibrary();
	}

	private String rootDir;
	private ConcurrentHashMap<String, RocksDBBucket> buckets = new ConcurrentHashMap<String, RocksDBBucket>();
	
	public void init(String dir) {
		rootDir = dir;
	}

	@Override
	public Bucket getBucket(String bucketName, boolean createIfNecessary) {
		return buckets.computeIfAbsent(bucketName, k -> openBucket(bucketName, createIfNecessary));
	}
	
	private RocksDBBucket openBucket(String bucketName, boolean createIfNecessary) {
		if (createIfNecessary || RocksDBBucket.exists(rootDir, bucketName)) {
			return new RocksDBBucket(rootDir, bucketName);
		} else {
			return null;
		}
	}	

	@Override
	public void close() {
		// doing it this way allows buckets to be reopened, and it's safer if there is a crash on .close()
		Iterator<Entry<String, RocksDBBucket>> iterator = buckets.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, RocksDBBucket> entry = iterator.next();
			entry.getValue().close();
			iterator.remove();
		}
	}


}
