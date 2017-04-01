package com.dieselpoint.standardkv;

public interface Table {
	
	public void write(WriteBatch batch);

	public void put(ByteSpan key, ByteSpan value);
	
	/**
	 * Fetch a value from the table
	 * @param key the key to find
	 * @return the value, or null if not found
	 */
	public ByteSpan get(ByteSpan key);
	
	public void remove(ByteSpan key);
	
	public Cursor newCursor();
	
	public WriteBatch newWriteBatch();
	
}
