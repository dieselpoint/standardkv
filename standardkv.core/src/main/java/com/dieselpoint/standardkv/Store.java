package com.dieselpoint.standardkv;

/**
 * A store is a database. It can contain multiple tables.
 */
public interface Store {
	
	/**
	 * Initialize with arbitrary initialization string. Could designate the directory where the store's files reside, for example.
	 */
	public void init(String name);
	
	/**
	 * Get a reference to a bucket. Returns null if the bucket does not exist.
	 */
	public Bucket getBucket(String name);
	
	/**
	 * Create a bucket. Throw StoreException if it exists.
	 */
	public Bucket createBucket(String name);
	
	/**
	 * Delete a bucket. Throw StoreException if it doesn't exist.
	 */
	public void deleteBucket(String name);
	
	
	/**
	 * Gets bucket or creates new one if it doesn't exist. Does it atomically.
	 */
	default public Bucket getOrCreateBucket(String name) {
		synchronized (this) {
			Bucket bucket = getBucket(name);
			if (bucket == null) {
				bucket = createBucket(name);
			}
			return bucket;
		}
	}
	
	/**
	 * Close the store.
	 */
	public void close();


}
