package com.dieselpoint.standardkv;

public interface Bucket {
	
	/**
	 * Get a reference to a table, creating it if necessary.
	 */
	public Table getTable(String name);

	/**
	 * Delete the bucket in entirety.
	 */
	public void delete();

	/**
	 * Close the bucket. May do nothing on some implementations.
	 */
	void close();
	
}
