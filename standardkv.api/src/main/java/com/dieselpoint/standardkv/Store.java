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
	 * Get a reference to a table, creating it if necessary.
	 */
	public Bucket getBucket(String name);

	/**
	 * Close the store.
	 */
	public void close();


}
