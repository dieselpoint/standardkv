package com.dieselpoint.standardkv.impl.memdb;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.Cursor;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.WriteBatch;
import com.dieselpoint.standardkv.impl.memdb.MemDBWriteBatch.Pair;

public class MemDBTable implements Table {
	
	/*
	 * This map implementationis fast for get, put, and remove. It does not provide isolation, though.
	 * Modifications will show up while you're iterating.
	 */
	private ConcurrentSkipListMap<ByteSpan, ByteSpan> map = new ConcurrentSkipListMap();

	public MemDBTable(String name) {
	}

	@Override
	public void write(WriteBatch batch) {
		List<Pair> list = ((MemDBWriteBatch)batch).getList();
		for (Pair pair: list) {
			put(pair.key, pair.value);
		}
	}

	@Override
	public void put(ByteSpan key, ByteSpan value) {
		// must make copies
		ByteArray keyCopy = new ByteArray(key.size());
		keyCopy.append(key);
		
		ByteArray valueCopy = new ByteArray(value.size());
		valueCopy.append(value);
		
		map.put(keyCopy, valueCopy);
	}

	@Override
	public void remove(ByteSpan key) {
		map.remove(key);
	}

	@Override
	public Cursor newCursor() {
		return new MemDBCursor(map);
	}

	@Override
	public WriteBatch newWriteBatch() {
		return new MemDBWriteBatch();
	}

	
}
