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
	public KVTable getTable(String name);
	
	
	/**
	 * Create a new table.
	 */
	public KVTable createTable(String name);
	
	/**
	 * Gets a table or creates it if it doesn't exist. Does it atomically.
	 */
	default public KVTable getOrCreateTable(String name) {
		synchronized (this) {
			KVTable table = getTable(name);
			if (table == null) {
				table = createTable(name);
			}
			return table;
		}
	}
	
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
