package com.dieselpoint.util;

import com.dieselpoint.standardkv.StoreException;

public class NameUtil {

	
	/**
	 * Return true if the source consists entirely of characters that
	 * are allowed in names, that is, letters, numbers, periods, hyphens, and underscores.
	 * @param source
	 * @return
	 */
	public static boolean hasNameCharsOnly(String source) {
		final int len = source.length();
		for (int i = 0; i < len; i++) {
			char ch = source.charAt(i);
			if (Character.isLetterOrDigit(ch) || ch == '.' || ch == '-' || ch == '_') {
				continue;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Various parts of the system need names, including bucket and table names. This method
	 * makes sure that the names will work everywhere.
	 */
	public static void checkForLegalName(String name) {
		
		if (name == null || name.trim().length() == 0) {
			throw new StoreException("Name is empty");
		}
		
		if (!hasNameCharsOnly(name)) {
			throw new StoreException("Name must contain letters, numbers, periods, hyphens and underscores only.");
		}
	}
	
	
	
}
