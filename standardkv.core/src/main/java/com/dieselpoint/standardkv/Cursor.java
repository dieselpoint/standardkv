package com.dieselpoint.standardkv;

import com.dieselpoint.buffers.Buffer;


public interface Cursor {

	public void beforeFirst();
	
	public void last();
	
	public void seek(Buffer key);
	
	public boolean next();

	public Buffer getKey();
	
	public Buffer getValue();
	
	public boolean isEOF();

	public void close();
	
}
