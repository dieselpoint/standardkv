package com.dieselpoint.standardkv;

import java.util.List;

public interface Bucket {

	/**
	 * Get a list of the tables in this bucket. Returns an 
	 * empty list if there aren't any.
	 */
	public List<String> getTableNames();
	
	/**
	 * Get a reference to a table. Returns null if it does not exist.
	 */
	public KVTable getTable(String name, boolean createIfNecessary);
	
	/**
	 * Delete a table.
	 */
	public void dropTable(String name);

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
