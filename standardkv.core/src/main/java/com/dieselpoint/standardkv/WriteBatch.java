package com.dieselpoint.standardkv;

import com.dieselpoint.buffers.Buffer;

public interface WriteBatch {
	
	public void put(Table table, Buffer key, Buffer value);
	
	public void remove(Table table, Buffer key);
	
}
