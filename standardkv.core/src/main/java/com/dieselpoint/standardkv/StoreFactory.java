package com.dieselpoint.standardkv;

public class StoreFactory {
	
	// convenience constants
	public static final String ROCKSDB = "com.dieselpoint.standardkv.impl.rocksdb.RocksDBStore";

	private StoreFactory() {
	}

	
	/**
	 * Open a Store. Specify the full classname of the com.dieselpoint.standardkv.Store implementation in the 
	 * "className" parameter, or use one of the predefined classname constants in this class, for example,
	 * StoreFactory.ROCKSDB.
	 * 
	 * @param storeInitString a Store implementation-specific string. Usually just a database name.
	 * @param className name of a class that implements com.dieselpoint.standardkv.Store
	 * @return the Store object
	 */
	public static Store getStore(String storeInitString, String className) {
		Class<Store> clazz;
		try {
			clazz = (Class<Store>) Class.forName(className);
			Store store = clazz.newInstance();
			store.init(storeInitString);
			return store;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new StoreException(e);
		}
	}

}
