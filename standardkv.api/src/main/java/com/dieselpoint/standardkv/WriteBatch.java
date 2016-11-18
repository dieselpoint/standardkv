package com.dieselpoint.standardkv;

public interface WriteBatch {
	
	public void put(ByteSpan key, ByteSpan value);
	
	public void remove(ByteSpan key);
	
}
