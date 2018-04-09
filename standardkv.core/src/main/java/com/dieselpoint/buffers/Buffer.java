package com.dieselpoint.buffers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The main abstraction for Buffers.
 * @author ccleve
 *
 */
public interface Buffer extends Comparable<Buffer> {
	/*
	 * TODO
	 * It's possible to supply default methods for almost 
	 * all the read*() and append*() methods.
	 * 
	 * May need to revisit the size-prefix scheme if buffers need to be sortable
	 * when they contain strings or byte arrays.
	 * 
	 * https://github.com/airlift/slice has some good ideas. It can't be used, because
	 * buffers are of fixed size, it doesn't do UTF/strings properly, and it
	 * doesn't do vInts.
	 * It's used in Presto internally.
	 * But it uses sun.misc.Unsafe to read/write primitives
	 * 
	 * 	look at how it uses unsafe to get ints and longs and other things
	 */
	
	public void position(int position);
	public int position();
	public void size(int size);
	public int size();
	public boolean eof();
	public void clear();
	
	public byte readByte(int position);
	// TODO add rest of read*(position) methods
	
	public byte readByte();
	public int readInt();
	public long readLong();
	public int readvInt();
	public long readvLong();
	public char readUtf8Char();

	/**
	 * Read a string up to the end of the buffer and put it in dest.
	 * @param dest buffer to receive the string
	 */
	public void readString(Appendable dest);
	public String readString();
	
	/**
	 * Read a string consisting of the specified number of characters.
	 * @param charCount number of chars to read
	 * @return a String
	 */
	public String readString(int charCount);
	public void readString(Appendable dest, int charCount);
	
	public void readBytes(Buffer dest, int count);
	
	public void copyBytesTo(byte [] dest, int destOffset, int count);
	public void copyBytesTo(OutputStream dest, int count) throws IOException;
	
	
	public void appendByte(byte b);
	public void appendInt(int i);
	public void appendLong(long l);
	public void appendBytes(byte [] source, int sourceOffset, int sourceSize);
	public void appendvInt(int i);
	public void appendvLong(long l);
	public void appendBuffer(Buffer source, int sourceOffset, int sourceSize);
	public void appendBuffer(Buffer source);
	public void appendInputStream(InputStream source, int count) throws IOException;

	public void appendUtf8Char(char ch);
	public void appendString(CharSequence seq);

	
	
	
	/**
	 * Remove bytes from this array. Shifts bytes down and removes "count" bytes
	 * from the size. Leaves the position unchanged, which means that position
	 * may point to a different byte.
	 * 
	 * @param from
	 *            index of first byte to remove
	 * @param count
	 *            number of bytes to remove
	 */
	public void remove(int from, int count);
	
	/**
	 * Insert the specified buffer at the specified byte position.
	 * 
	 * @param position
	 *            starting offset in this ByteArray where the buffer should be inserted
	 * @param buf
	 *            the buffer to insert
	 */
	public void insert(int position, Buffer buf);
	
	public int hashCode();
	
	public boolean equals(Object other);

	
	
}
