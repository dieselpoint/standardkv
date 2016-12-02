package com.dieselpoint.standardkv.impl.lmdb;

import java.io.File;

import org.fusesource.lmdbjni.Env;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.Store;
import com.dieselpoint.standardkv.Util;



// NOT YET IMPLEMENTED
public class LmdbStore implements Store {
	
	/*
	 * Consider making this implementation more efficient
	 * by going directly against the JNI class
	 * in the lmdbjni project. Just bypass everything.
	 */

	
	private File parentDir;
	private Env env;
	
	public void init(String name) {
		
		// name can have path components. If not absolute, relative to current dir
		parentDir = new File(name);
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		
		env = new Env();
		
		long dbSize = 1024L * 1024 * 1024 * 100; // 100 gb
		if (Util.IS_WINDOWS) {
			// 10mb only for testing. does not run on Windows in production
			dbSize = 1024L * 1024 * 10;
		}
		env.setMapSize(dbSize);
		env.setMaxDbs(50);
		
		Env.pushMemoryPool(1024 * 128);
		
		env.open(parentDir.getAbsolutePath());
	}

	/*
	@Override
	public Table getTable(String name) {
		// TODO cache this. maybe.
		Database db = env.openDatabase(name, Constants.DUPSORT | Constants.CREATE);
		return new LmdbTable(env, db);
	}
	*/

	@Override
	public void close() {
		Env.popMemoryPool();
		env.close();
	}

	/*
	@Override
	public void delete() {
		close();
		try {
			FileUtil.deleteDir(parentDir);
		} catch (IOException e) {
			throw new StoreException(e);
		}
	}
	*/

	@Override
	public Bucket getBucket(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bucket createBucket(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
