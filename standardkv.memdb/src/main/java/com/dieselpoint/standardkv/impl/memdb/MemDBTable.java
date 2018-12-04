package com.dieselpoint.standardkv.impl.memdb;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.KVCursor;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.Transaction;
import com.dieselpoint.standardkv.WriteBatch;
import com.dieselpoint.standardkv.impl.memdb.MemDBWriteBatch.Pair;

public class MemDBTable implements KVTable {
	
	/*
	 * This map implementationis fast for get, put, and remove. It does not provide isolation, though.
	 * Modifications will show up while you're iterating.
	 */
	private ConcurrentSkipListMap<Buffer, Buffer> map = new ConcurrentSkipListMap();

	public MemDBTable(String name) {
	}

	/*
	@Override
	public void write(WriteBatch batch) {
		List<Pair> list = ((MemDBWriteBatch)batch).getList();
		for (Pair pair: list) {
			put(pair.key, pair.value);
		}
	}
	*/

	@Override
	public void put(Buffer key, Buffer value) {
		// must make copies
		ByteArray keyCopy = new ByteArray(key.size());
		keyCopy.appendBuffer(key);
		
		ByteArray valueCopy = new ByteArray(value.size());
		valueCopy.appendBuffer(value);
		
		map.put(keyCopy, valueCopy);
	}

	@Override
	public void remove(Buffer key) {
		map.remove(key);
	}

	@Override
	public KVCursor newCursor() {
		return new MemDBCursor(map);
	}

	/*
	@Override
	public WriteBatch newWriteBatch() {
		return new MemDBWriteBatch();
	}
	*/

	@Override
	public Buffer get(Buffer key) {
		return map.get(key);
	}

	@Override
	public void put(Transaction trans, Buffer key, Buffer value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Buffer get(Transaction trans, Buffer key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Transaction trans, Buffer key) {
		// TODO Auto-generated method stub
		
	}

	
}
