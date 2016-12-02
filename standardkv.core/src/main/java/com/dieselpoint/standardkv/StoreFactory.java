package com.dieselpoint.standardkv;

public class StoreFactory {

	private StoreFactory() {
	}

	public static Store getStore(String name) {
		return getStore(name, "leveldb");
	}

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
