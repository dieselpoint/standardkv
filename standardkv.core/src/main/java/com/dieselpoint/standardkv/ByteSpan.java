/*******************************************************************************
 * Copyright 2000-2010 Dieselpoint, Inc. All rights reserved.
 *   
 *  This software is licensed, not sold, and is subject to the terms
 *  of the license agreement. This software is proprietary and 
 *  may not be copied except as contractually agreed. This 
 *  software contains confidential trade secrets which may not be 
 *  disclosed or distributed. "Dieselpoint" is a trademark of 
 *  Dieselpoint, Inc.
 ******************************************************************************/
package com.dieselpoint.standardkv;

import java.io.IOException;



/**
 * Wraps a span of bytes and provides various methods for
 * iterating over and extracting values from them.
 */
public abstract class ByteSpan implements Comparable<ByteSpan> {
	
	protected int position; // relative to offset, not the buffer
	protected int size; // also relative to offset, not the buffer

	
	abstract public byte get(int i);

	abstract public ByteSpan slice(int offset, int size);

	abstract public void copyTo(int srcOffset, byte[] dest, int destOffset, int count);

	
	public byte get() {
		return get(position++);
	}

	public boolean getBoolean(int i) {
		return get(i) == 1;
	}
	
	public boolean getBoolean() {
		return getBoolean(position++);
	}
	
	public char getChar(int i) {
		char ch = (char) (get(i) << 8);
		ch |= get(i + 1) & 0xFF;
		return ch;
	}
		
	public char getChar() {
		char ch = getChar(position);
		position += 2;
		return ch;
	}

	public int getInt(int i) {
		int pos = i;
		int out = 0;
		out |= ((int) get(pos++) & 0xFF) << 24;
		out |= ((int) get(pos++) & 0xFF) << 16;
		out |= ((int) get(pos++) & 0xFF) << 8;
		out |= get(pos++) & 0xFF;
		return out;
	}

	public int getInt() {
		int i = getInt(position);
		position+= 4;
		return i;
	}
	
	public long getLong(int i) {
		int pos = i;
		long out = 0;
		out |= ((long) get(pos++) & 0xFF) << 56;
		out |= ((long) get(pos++) & 0xFF) << 48;
		out |= ((long) get(pos++) & 0xFF) << 40;
		out |= ((long) get(pos++) & 0xFF) << 32;
		out |= ((long) get(pos++) & 0xFF) << 24;
		out |= ((long) get(pos++) & 0xFF) << 16;
		out |= ((long) get(pos++) & 0xFF) << 8;
		out |= get(pos++) & 0xFF;
		return out;
	}

	public long getLong() {
		long l = getLong(position);
		position += 8;
		return l;
	}
	
	public int size() {
		return size;
	}
    
	/**
	 * Compare buffers byte for byte up to the length of the specified buf. Important: bytes must be treated as
	 * *unsigned* values for UTF-8 to sort correctly
	 */
	public int comparePartial(ByteSpan buf) {

		int bufSize = buf.size();
		int commonLen = bufSize < size ? bufSize : size;

		for (int i = 0; i < commonLen; i++) {
			// we must cast to ints to do an unsigned comparison
			int cmp = ((int) get(i) & 0xFF) - ((int) buf.get(i) & 0xFF);
			if (cmp != 0)
				return cmp;
		}

		if (bufSize <= size) {
			return 0;
		} else {
			// bufSize is larger, so it's greater than this object
			return -1;
		}
	}
	
	/**
	 * Compare buffers byte for byte up to the length of the smaller buffer. If the bytes are equal, then the shorter
	 * buffer is first.
	 */
	public int compareTo(ByteSpan buf) {
		int cmp = comparePartial(buf);
		if (cmp == 0) {
			return size - buf.size();
		}
		return cmp;
	}
	
	public int getvInt() {
		byte b = get();
		int num = b & 0x7F;
		for (int shift = 7; b < 0; shift += 7) {
			b = get();
			num |= (b & 0x7F) << shift;
		}
		return num;
	}
	
	public long getvLong() {
		byte b = get();
		long num = b & 0x7F;
		for (int shift = 7; b < 0; shift += 7) {
			b = get();
			num |= (b & 0x7FL) << shift;
		}
		return num;
	}

	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	public boolean eof() {
		return (position >= size); 
	}

	public boolean equals(ByteSpan other) {
		if (other.size != this.size) {
			return false;
		}
		int end = size;
		for (int i = 0; i < end; i++) {
			if (get(i) != other.get(i)) {
				return false;
			}
		}
		return true;
	}

	public String getString(int i) {
		int oldPos = position;
		position = i;
		String str = getString();
		position = oldPos;
		return str;
	}
	
	public String getString() {
		StringBuilder sb = new StringBuilder(size - position);
		while (position < size) {
			sb.append(getNextUTF8Char());
		}
		return sb.toString();
	}


	/**
	 * Starting at position, append UTF-8 chars to the Appendable. Go to the end of the span.
	 */
	public void writeString(Appendable dest) {
		try {
			while (position < size) {
				dest.append(getNextUTF8Char());
			}
		} catch (IOException e) {
			throw new StoreException(e);
		}
	}	

	
	public char getNextUTF8Char() {

		//if (position >= size)
		//	return '\uFFFF'; // not a valid unicode char
		
		int c, char2, char3;

		c = (int)(get() & 0xFF);

		switch (c >> 4) {
			case 0 :
			case 1 :
			case 2 :
			case 3 :
			case 4 :
			case 5 :
			case 6 :
			case 7 :
				/* 0xxxxxxx*/
				return (char) c;

			case 12 :
			case 13 :
				/* 110x xxxx   10xx xxxx*/
				char2 = get();
				return (char) (((c & 0x1F) << 6) | (char2 & 0x3F));

			case 14 :
				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				char2 = get();
				char3 = get();
				return (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));

			default :
				/* 10xx xxxx,  1111 xxxx */
				throw new StoreException("bad UTF String format:" + Integer.toHexString(c));
		}
	}
	
	public String toString() {
		StringBuilder strBuf = new StringBuilder();
		for (int i = 0; i < size; i++) {
			strBuf.append(Integer.toString(get(i)) + " ");
		}
		return strBuf.toString();
	}


	public byte[] getTrimmedArray() {
		byte[] newArray = new byte[size];
		copyTo(0, newArray, 0, size);
		return newArray;
	}
}
