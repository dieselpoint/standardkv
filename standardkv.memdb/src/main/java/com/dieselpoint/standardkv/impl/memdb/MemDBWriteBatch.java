package com.dieselpoint.standardkv.impl.memdb;

import java.util.ArrayList;
import java.util.List;

import com.dieselpoint.standardkv.ByteSpan;
import com.dieselpoint.standardkv.WriteBatch;

public class MemDBWriteBatch implements WriteBatch {
	
	private List<Pair> list = new ArrayList();
	

	@Override
	public void put(ByteSpan key, ByteSpan value) {
		Pair pair = new Pair();
		pair.key = key;
		pair.value = value;
		list.add(pair);
	}

	@Override
	public void remove(ByteSpan key) {
		put(key, null);
	}
	
	protected List<Pair> getList() {
		return list;
	}
	

	public static class Pair {
		ByteSpan key;
		ByteSpan value;
	}
}
