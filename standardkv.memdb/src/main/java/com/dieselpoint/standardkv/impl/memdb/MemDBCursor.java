package com.dieselpoint.standardkv.impl.memdb;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;


public class MemDBCursor implements Cursor {
	
	private ConcurrentSkipListMap<ByteSpan, ByteSpan> map;	
	private Iterator<Entry<ByteSpan, ByteSpan>> it;
	private Entry<ByteSpan, ByteSpan> entry;
	
	public MemDBCursor(ConcurrentSkipListMap<ByteSpan, ByteSpan> map) {
		this.map = map;
	}

	@Override
	public void beforeFirst() {
		it = map.entrySet().iterator();
		entry = null;
	}

	@Override
	public void seek(ByteSpan key) {
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
	public ByteSpan getKey() {
		return entry.getKey();
	}

	@Override
	public ByteSpan getValue() {
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

}
