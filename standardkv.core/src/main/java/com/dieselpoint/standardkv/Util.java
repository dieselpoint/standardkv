package com.dieselpoint.standardkv;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Util {
	
	public static final boolean IS_WINDOWS = osMatch("Windows");
	public static final boolean IS_MAC = osMatch("Mac");
	public static final boolean IS_UNIX = !IS_WINDOWS;
	
	private static boolean osMatch(String prefix) {
		String os = System.getProperty("os.name");
		return os.startsWith(prefix); 
	}

	/**
	 * Write an integer to a location in a byte array. Serializes the
	 * integer out as four bytes, big-endian.
	 *
	 * @param array the array to write to
	 * @param i the integer to write
	 * @param offset the offset into the array to start writing
	 */
	public static void writeInt(byte[] array, int i, int offset) {
		array[offset++] = (byte) ((i >>> 24) & 0xFF);
		array[offset++] = (byte) ((i >>> 16) & 0xFF);
		array[offset++] = (byte) ((i >>> 8) & 0xFF);
		array[offset] = (byte) (i & 0xFF);
	}

	/**
	 * Write a long to a location in a byte array. Serializes the
	 * long out as eight bytes, big endian.
	 *
	 * @param array the array to write to
	 * @param i the long to write
	 * @param offset the offset into the array to start writing
	 */
	public static void writeLong(byte[] array, long i, int offset) {
		array[offset++] = (byte) ((i >>> 56) & 0xFF);
		array[offset++] = (byte) ((i >>> 48) & 0xFF);
		array[offset++] = (byte) ((i >>> 40) & 0xFF);
		array[offset++] = (byte) ((i >>> 32) & 0xFF);
		array[offset++] = (byte) ((i >>> 24) & 0xFF);
		array[offset++] = (byte) ((i >>> 16) & 0xFF);
		array[offset++] = (byte) ((i >>> 8) & 0xFF);
		array[offset] = (byte) ((i & 0xFF));
	}

	/**
	 * Read an integer from a location in a byte array. Assumes the int was
	 * serialized out using writeInt().
	 *
	 * @param array the array to read from
	 * @param offset the offset into the array to start reading
	 * @return the integer that was read
	 */
	public static int readInt(byte[] array, int offset) {
		int i = 0;
		i |= ((int) array[offset++] & 0xFF) << 24;
		i |= ((int) array[offset++] & 0xFF) << 16;
		i |= ((int) array[offset++] & 0xFF) << 8;
		i |= array[offset] & 0xFF;
		return i;
	}

	/**
	 * Read a long from a location in a byte array. Assumes the long was
	 * serialized out using writeLong().
	 *
	 * @param array the array to read from
	 * @param offset the offset into the array to start reading
	 * @return the long that was read
	 */
	public static long readLong(byte[] array, int offset) {
		long i = 0;
		i |= ((long) array[offset++] & 0xFF) << 56;
		i |= ((long) array[offset++] & 0xFF) << 48;
		i |= ((long) array[offset++] & 0xFF) << 40;
		i |= ((long) array[offset++] & 0xFF) << 32;
		i |= ((long) array[offset++] & 0xFF) << 24;
		i |= ((long) array[offset++] & 0xFF) << 16;
		i |= ((long) array[offset++] & 0xFF) << 8;
		i |= array[offset] & 0xFF;
		return i;
	}

	/**
	 * Reads a char from a location in a byte array.
	 *
	 * @param array the array to read from
	 * @param offset the offset into the array to start reading
	 * @return the char that was read
	 */
	public static char readChar(byte[] array, int offset) {
		char i = 0;
		i |= (array[offset++] & 0xFF) << 8;
		i |= array[offset] & 0xFF;
		return i;
	}


	/**
	 * Return true if the source consists entirely of letters or digits. Handles
	 * non-Western characters correctly.
	 */ 
	public static boolean isAllLettersOrDigits(String source) {
		final int len = source.length();
		for (int i = 0; i < len; i++) {
			if (!Character.isLetterOrDigit(source.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Delete a directory, including all its contents.
	 */
	public static void deleteDir(String dir) throws IOException {
		Path directory = Paths.get(dir);
		if (!Files.exists(directory)) {
			return;
		}
		
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
		   @Override
		   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		       Files.delete(file);
		       return FileVisitResult.CONTINUE;
		   }

		   @Override
		   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		       Files.delete(dir);
		       return FileVisitResult.CONTINUE;
		   }
		});
	}
	
	
}
