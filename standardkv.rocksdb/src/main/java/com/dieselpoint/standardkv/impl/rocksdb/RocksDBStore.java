package com.dieselpoint.standardkv.impl.rocksdb;



import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.rocksdb.RocksDB;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Store;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.util.FileUtil;
import com.dieselpoint.util.NameUtil;

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
	 * 
	 * Probably better to clone the project and modify the code.
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
	public Bucket getBucket(String bucketName) {
		return buckets.computeIfAbsent(bucketName, k -> openBucket(k));
	}
	
	private synchronized RocksDBBucket openBucket(String bucketName) {
		// don't make this public or getBucket() will do odd things when an 
		// external process calls it.

		String path = getCanonicalPath(bucketName);
		File pathFile = new File(path);
		if (!pathFile.exists()) {
			return null;
		}
		
		RocksDBBucket bucket = new RocksDBBucket(path);
		return bucket;
	}
	
	public synchronized RocksDBBucket createBucket(String bucketName) {
		
		NameUtil.checkForLegalName(bucketName);

		String path = getCanonicalPath(bucketName);
		File pathFile = new File(path);
		if (pathFile.exists()) {
			throw new StoreException("Bucket already exists: " + bucketName);
		}
		
		pathFile.mkdirs();

		RocksDBBucket bucket = new RocksDBBucket(path);
		buckets.put(bucketName, bucket);
		return bucket;
	}	
	
	private String getCanonicalPath(String bucketName) {
		try {
			File file = new File(rootDir, bucketName);
			return file.getCanonicalPath();
		} catch (IOException e) {
			throw new StoreException(e);
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

	@Override
	public void deleteBucket(String bucketName) {

		RocksDBBucket bucket = buckets.remove(bucketName);
		if (bucket == null) {
			throw new StoreException("Bucket not found: " + bucketName);
		}

		bucket.close();

		String path = getCanonicalPath(bucketName);
		try {
			FileUtil.deleteDir(path);
		} catch (IOException e) {
			throw new StoreException(e);
		}

	}


}
