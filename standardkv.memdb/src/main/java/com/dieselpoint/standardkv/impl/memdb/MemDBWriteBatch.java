package com.dieselpoint.standardkv.impl.memdb;

import java.util.ArrayList;
import java.util.List;

import com.dieselpoint.buffers.Buffer;
import com.dieselpoint.standardkv.KVTable;
import com.dieselpoint.standardkv.WriteBatch;

public class MemDBWriteBatch implements WriteBatch {
	
	private List<Pair> list = new ArrayList<>();

	/*
	@Override
	public void put(Buffer key, Buffer value) {
		Pair pair = new Pair();
		pair.key = key;
		pair.value = value;
		list.add(pair);
	}

	@Override
	public void remove(Buffer key) {
		put(key, null);
	}
	*/
	
	protected List<Pair> getList() {
		return list;
	}
	

	public static class Pair {
		Buffer key;
		Buffer value;
	}


	@Override
	public void put(KVTable table, Buffer key, Buffer value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(KVTable table, Buffer key) {
		// TODO Auto-generated method stub
		
	}
}
