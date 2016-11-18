package com.dieselpoint.standardkv.impl.leveldb;

import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.ByteArray;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.Util;




public class LevelDbBucket implements Bucket {
	
	private DB db;
	private String bucketName;

	public LevelDbBucket(DB db, String bucketName) {
		if (!Util.isAllLettersOrDigits(bucketName)) {
			throw new StoreException("bucketName must consist of letters and digits only");
		}
		this.db = db;
		this.bucketName = bucketName;
	}

	@Override
	public Table getTable(String tableName) {
		if (!Util.isAllLettersOrDigits(tableName)) {
			throw new StoreException("tableName must consist of letters and digits only");
		}
		return new LevelDbTable(db, bucketName, tableName);
	}
	
	@Override
	public void delete() {

		ByteArray keyBuf = new ByteArray();
		keyBuf.append(bucketName);
		keyBuf.appendByte(LevelDbTable.DOT);
		byte [] prefix = keyBuf.getTrimmedArray();
		int prefixLen = prefix.length;
		
		DBIterator it = db.iterator();
		it.seek(prefix);
		while (it.hasNext()) {
			Entry <byte [], byte []> entry = it.next();
			byte [] key = entry.getKey();
			
			// see if key matches prefix
			if (key.length < prefix.length) {
				break;
			}
			for (int i = 0; i < prefixLen; i++) {
				if (key[i] != prefix[i]) {
					break;
				}
			}
			
			db.delete(key);
		}
	}

	@Override
	public void close() {
		// do nothing
	}

}
