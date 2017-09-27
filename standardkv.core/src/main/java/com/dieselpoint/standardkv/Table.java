package com.dieselpoint.standardkv;

import com.dieselpoint.buffers.Buffer;

public interface Table {
	
	public void put(Buffer key, Buffer value);

	public void put(Transaction trans, Buffer key, Buffer value);
	
	/**
	 * Fetch a value from the table
	 * @param key the key to find
	 * @return the value, or null if not found
	 */
	public Buffer get(Buffer key);

	/**
	 * Fetch a value from the table using a transaction.
	 * @param key the key to find
	 * @return the value, or null if not found
	 */
	public Buffer get(Transaction trans, Buffer key);
	
	public void remove(Buffer key);

	public void remove(Transaction trans, Buffer key);
	
	public Cursor newCursor();
	
}
