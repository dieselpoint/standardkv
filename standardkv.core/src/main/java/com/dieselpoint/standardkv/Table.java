package com.dieselpoint.standardkv;

import com.dieselpoint.buffers.Buffer;

public interface Table {
	
	public void write(WriteBatch batch);

	public void put(Buffer key, Buffer value);
	
	/**
	 * Fetch a value from the table
	 * @param key the key to find
	 * @return the value, or null if not found
	 */
	public Buffer get(Buffer key);
	
	public void remove(Buffer key);
	
	public Cursor newCursor();
	
	public WriteBatch newWriteBatch();
	
}
