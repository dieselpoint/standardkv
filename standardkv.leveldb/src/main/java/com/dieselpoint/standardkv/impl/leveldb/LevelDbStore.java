package com.dieselpoint.standardkv.impl.leveldb;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Store;
import com.dieselpoint.standardkv.StoreException;

public class LevelDbStore implements Store {
	
	/*
	 * The fusesource implementation of this driver is incredibly inefficient.
	 * It also doesn't use ByteBuffers.
	 * Don't bother to optimize or get down to a low level.
	 * It's a waste of time. Just look for a better driver. Or kv store.
	 */
	
	private DB db;
	
	public void init(String dir) {
		File file = new File(dir);
		Options options = new Options();
		options.createIfMissing(true);

		try {
			db = factory.open(file, options);
		} catch (IOException e) {
			throw new StoreException(e);
		}
	}


	@Override
	public Bucket getBucket(String name) {
		return new LevelDbBucket(db, name);
	}
	

	@Override
	public void close() {
		try {
			db.close();
		} catch (IOException e) {
			throw new StoreException(e);
		}
	}


	@Override
	public Bucket createBucket(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
