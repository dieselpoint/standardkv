package com.dieselpoint.standardkv;

import com.dieselpoint.buffers.Buffer;


public interface KVCursor {

	public void beforeFirst();
	
	public void last();
	
	public void seek(Buffer key);
	
	public boolean next();

	public Buffer getKey();
	
	public Buffer getValue();
	
	public boolean isEOF();

	public void close();
	
}
