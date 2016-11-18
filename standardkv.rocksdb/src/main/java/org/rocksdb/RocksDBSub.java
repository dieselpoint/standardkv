package org.rocksdb;

import java.util.List;

import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * Cracker class that exposes some things that should have been exposed in
 * RocksDB.
 */
public class RocksDBSub extends RocksDB {

	protected RocksDBSub(long nativeHandle) {
		super(nativeHandle);
	}

	public void put(final byte[] key, int keyLen, final byte[] value, int valueLen) throws RocksDBException {
		put(nativeHandle_, key, keyLen, value, valueLen);
	}

	public void put(final ColumnFamilyHandle columnFamilyHandle, final byte[] key, int keyLen, final byte[] value,
			int valueLen) throws RocksDBException {
		put(nativeHandle_, key, keyLen, value, valueLen, columnFamilyHandle.nativeHandle_);
	}

	public void remove(final byte[] key, int keyLen) throws RocksDBException {
		remove(nativeHandle_, key, keyLen);
	}

	public void remove(final ColumnFamilyHandle columnFamilyHandle, final byte[] key, int keyLen)
			throws RocksDBException {
		remove(nativeHandle_, key, keyLen, columnFamilyHandle.nativeHandle_);
	}

	public static RocksDBSub open(final DBOptions options, final String path,
			final List<ColumnFamilyDescriptor> columnFamilyDescriptors,
			final List<ColumnFamilyHandle> columnFamilyHandles) throws RocksDBException {

		final byte[][] cfNames = new byte[columnFamilyDescriptors.size()][];
		final long[] cfOptionHandles = new long[columnFamilyDescriptors.size()];
		for (int i = 0; i < columnFamilyDescriptors.size(); i++) {
			final ColumnFamilyDescriptor cfDescriptor = columnFamilyDescriptors.get(i);
			cfNames[i] = cfDescriptor.columnFamilyName();
			cfOptionHandles[i] = cfDescriptor.columnFamilyOptions().nativeHandle_;
		}

		final long[] handles = open(options.nativeHandle_, path, cfNames, cfOptionHandles);
		final RocksDBSub db = new RocksDBSub(handles[0]);
		db.storeOptionsInstance(options);

		for (int i = 1; i < handles.length; i++) {
			columnFamilyHandles.add(new ColumnFamilyHandle(db, handles[i]));
		}

		return db;
	}

	private void storeOptionsInstance(DBOptionsInterface options) {
		options_ = options;
	}

	public RocksIteratorSub newIterator(final ColumnFamilyHandle columnFamilyHandle) {
		return new RocksIteratorSub(this, iteratorCF(nativeHandle_, columnFamilyHandle.nativeHandle_));
	}

}
