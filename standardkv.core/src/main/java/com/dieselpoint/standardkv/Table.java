package com.dieselpoint.standardkv;

public interface Table {
	
	public void write(WriteBatch batch);

	public void put(ByteSpan key, ByteSpan value);
	
	public void remove(ByteSpan key);
	
	public Cursor newCursor();
	
	public WriteBatch newWriteBatch();
	
}
