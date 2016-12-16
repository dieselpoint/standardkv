package com.dieselpoint.standardkv;

public interface Bucket {
	
	/**
	 * Get a reference to a table. Returns null if it does not exist.
	 */
	public Table getTable(String name, boolean createIfNecessary);

	/**
	 * Delete the bucket in entirety.
	 */
	public void delete();

	/**
	 * Close the bucket. May do nothing on some implementations.
	 */
	void close();
	
}
