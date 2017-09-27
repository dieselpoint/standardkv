package com.dieselpoint.standardkv;

public interface Transaction {
	
	public void commit();
	
	public void rollback();

}
