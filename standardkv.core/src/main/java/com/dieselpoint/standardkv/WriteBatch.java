package com.dieselpoint.standardkv;

import com.dieselpoint.buffers.Buffer;

public interface WriteBatch {
	
	public void put(Buffer key, Buffer value);
	
	public void remove(Buffer key);
	
}
