package com.dieselpoint.standardkv;

public interface Cursor {

	public void beforeFirst();
	
	public void seek(ByteSpan key);
	
	public boolean next();

	public ByteSpan getKey();
	
	public ByteSpan getValue();
	
	public boolean isEOF();

	public void close();
	
}
