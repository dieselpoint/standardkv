package com.dieselpoint.standardkv.impl.rocksdb;



import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.rocksdb.RocksDB;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Store;
import com.dieselpoint.standardkv.StoreException;

public class RocksDBStore implements Store {
	
	/*
	 * RocksDB iteration is slow, and the java api is inefficient.
	 * It's possible for me to modify the jni code, but it looks like this is a big undertaking.
	 * Fortunately, there are changes being checked in every day in the rocksdb project.
	 * So, let's wait for them to fix these things:
	 * 
	 * 1. Windows compilation, and include the dll in the jar file.
	 * 2. Expose slices.
	 * 3. Make it easier to do dynamic column families. Right now discovering existing col families is difficult
	 * because of the inconsistency in the way Options and DBOptions are handled.	  
	 * 
	 * An alternative would be to create a completely new Java project with
	 * jni code to talk to a generic rocks dll or so. That's a big undertaking. Wait.
	 */
	
	static {
		RocksDB.loadLibrary();
	}
	
	//private Logger logger = LoggerFactory.getLogger(getClass());
	private String rootDir;
	private ConcurrentHashMap<String, RocksDBBucket> buckets = new ConcurrentHashMap<String, RocksDBBucket>();
	
	
	
	public void init(String dir) {
		rootDir = dir;
	}

	@Override
	public Bucket getBucket(String bucketName) {
		return buckets.get(bucketName);
	}
	
	private RocksDBBucket openBucket(String bucketName) {
		//logger.info("Opening bucket in " + rootDir + " bucket " + bucketName);
		return new RocksDBBucket(rootDir, bucketName);
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

	@Override
	public Bucket createBucket(String name) {
		if (getBucket(name) != null) {
			throw new StoreException("Bucket already exists: " + name);
		}
		RocksDBBucket bucket = openBucket(name);
		buckets.put(name, bucket);
		return bucket;
	}

}
