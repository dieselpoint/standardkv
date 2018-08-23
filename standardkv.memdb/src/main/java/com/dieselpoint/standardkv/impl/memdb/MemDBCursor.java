package com.dieselpoint.standardkv.impl.memdb;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.standardkv.KVCursor;


public class MemDBCursor implements KVCursor {
	
	private ConcurrentSkipListMap<Buffer, Buffer> map;	
	private Iterator<Entry<Buffer, Buffer>> it;
	private Entry<Buffer, Buffer> entry;
	
	public MemDBCursor(ConcurrentSkipListMap<Buffer, Buffer> map) {
		this.map = map;
	}

	@Override
	public void beforeFirst() {
		it = map.entrySet().iterator();
		entry = null;
	}

	@Override
	public void seek(Buffer key) {
		it = map.tailMap(key).entrySet().iterator();
		next();
	}

	@Override
	public boolean next() {
		if (!it.hasNext()) {
			return false;
		}
		entry = it.next();
		return true;
	}

	@Override
	public Buffer getKey() {
		return entry.getKey();
	}

	@Override
	public Buffer getValue() {
		return entry.getValue();
	}

	@Override
	public boolean isEOF() {
		if (it == null) {
			return true;
		}
		return !it.hasNext();
	}

	@Override
	public void close() {
	}

	@Override
	public void last() {
		entry = map.lastEntry();
		it = map.tailMap(entry.getKey(), true).entrySet().iterator();
	}

}
