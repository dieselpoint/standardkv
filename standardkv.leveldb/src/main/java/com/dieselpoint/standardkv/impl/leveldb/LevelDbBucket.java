package com.dieselpoint.standardkv.impl.leveldb;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import com.dieselpoint.buffers.ByteArray;
import com.dieselpoint.standardkv.Bucket;
import com.dieselpoint.standardkv.StoreException;
import com.dieselpoint.standardkv.Table;
import com.dieselpoint.standardkv.Transaction;
import com.dieselpoint.standardkv.Util;
import com.dieselpoint.standardkv.WriteBatch;
import com.dieselpoint.util.CommonUtil;

public class LevelDbBucket implements Bucket {

	private DB db;
	private String bucketName;

	private ConcurrentHashMap<String, Table> tables = new ConcurrentHashMap<String, Table>();

	public LevelDbBucket(DB db, String bucketName) {
		if (!CommonUtil.isAllLettersOrDigits(bucketName)) {
			throw new StoreException("bucketName must consist of letters and digits only");
		}
		this.db = db;
		this.bucketName = bucketName;
	}

	@Override
	public void delete() {

		ByteArray keyBuf = new ByteArray();
		keyBuf.appendString(bucketName);
		keyBuf.appendByte(LevelDbTable.DOT);
		byte[] prefix = keyBuf.getTrimmedArray();
		int prefixLen = prefix.length;

		DBIterator it = db.iterator();
		it.seek(prefix);
		while (it.hasNext()) {
			Entry<byte[], byte[]> entry = it.next();
			byte[] key = entry.getKey();

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

	@Override
	public Table getTable(String tableName, boolean createIfNecessary) {
		if (createIfNecessary) {
			return tables.computeIfAbsent(tableName, k -> createTable(tableName));
		} else {
			return tables.get(tableName);
		}
	}

	private LevelDbTable createTable(String tableName) {
		Util.checkForLegalName(tableName);
		return new LevelDbTable(db, bucketName, tableName);
	}

	@Override
	public Transaction startTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(WriteBatch batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WriteBatch newWriteBatch() {
		// TODO Auto-generated method stub
		return null;
	}

}
