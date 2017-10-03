package com.dieselpoint.standardkv;

import com.dieselpoint.buffers.Buffer;

public interface WriteBatch {
	
	public void put(KVTable table, Buffer key, Buffer value);
	
	public void remove(KVTable table, Buffer key);
	
}
