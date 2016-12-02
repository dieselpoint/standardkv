package com.dieselpoint.standardkv;

public interface Bucket {
	
	/**
	 * Get a reference to a table. Returns null if it does not exist.
	 */
	public Table getTable(String name);
	
	/**
	 * Create a table. Returns a StoreException if it already exists.
	 */
	public Table createTable(String name);

	/**
	 * Delete the bucket in entirety.
	 */
	public void delete();

	/**
	 * Close the bucket. May do nothing on some implementations.
	 */
	void close();
	
}
