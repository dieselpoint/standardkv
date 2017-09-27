package com.dieselpoint.standardkv;

public interface Bucket {
	
	
	/**
	 * Get a reference to a table. Returns null if it does not exist.
	 */
	public Table getTable(String name, boolean createIfNecessary);

	/**
	 * Create a new transaction.
	 */
	public Transaction startTransaction();

	/**
	 * Write a batch of modifications in one atomic operation.
	 */
	public void write(WriteBatch batch); 
	
	/**
	 * Create a new WriteBatch object.
	 */
	public WriteBatch newWriteBatch();
	
	/**
	 * Delete the bucket in entirety.
	 */
	public void delete();

	/**
	 * Close the bucket. May do nothing on some implementations.
	 */
	void close();
	
}
