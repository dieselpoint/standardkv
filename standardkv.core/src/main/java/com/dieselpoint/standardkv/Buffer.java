package com.dieselpoint.standardkv;

import java.io.IOException;
import java.io.InputStream;

public class Buffer implements Comparable<Buffer> {

	/*
	 * This could wrap a ByteBuffer if we subclass it and override 
	 * every method that references arr. To make this easy, most of 
	 * the methods in this class should never access the byte [] array directly.
	 */
	
	protected int internalOffset;
	protected int position; // relative to offset, not the buffer
	protected int size; // also relative to offset, not the buffer
	protected byte [] arr;
	protected int hash;

	public Buffer() {
		this(16);
	}

	public Buffer(int initialCapacity) {
		arr = new byte[initialCapacity];
	}

	public Buffer(CharSequence seq) {
		arr = new byte[seq.length() * 2];
		append(seq);
	}

	public Buffer(byte[] arr) {
		this(arr, arr.length);
	}

	public Buffer(byte[] arr, int size) {
		this.arr = arr;
		this.size = size;
	}
	
	public byte get(int i) {
		return arr[internalOffset + i];
	}
	
	public Buffer slice(int offset, int size) {
		Buffer out = new Buffer(this.arr);
		out.internalOffset = internalOffset + offset;
		out.size = size;
		if (internalOffset + size > arr.length) {
			throw new BufferException("Slice too big.");
		}
		return out;
	}

	public void copyTo(int srcOffset, byte[] dest, int destOffset, int count) {
		System.arraycopy(arr, srcOffset + internalOffset, dest, destOffset, count);
	}
	
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
	public int comparePartial(Buffer buf) {

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
	public int compareTo(Buffer buf) {
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

	public int getInternalOffset() {
		return internalOffset;
	}

	public byte[] getArray() {
		return arr;
	}

	public byte[] getTrimmedArray() {
		byte[] newArray = new byte[size];
		this.copyTo(0, newArray, 0, size);
		return newArray;
	}

	public void setArray(byte[] array) {
		this.arr = array;
		this.size = array.length;
		this.internalOffset = 0;
	}

	public void appendByte(byte value) {
		size++;
		ensureCapacity(size);
		put(size - 1, value);
	}

	public void append(byte[] value) {
		append(value, 0, value.length);
	}

	public void append(byte[] value, int offset, int length) {
		int newSize = size + length;
		ensureCapacity(newSize);
		System.arraycopy(value, offset, arr, size + internalOffset, length);
		size = newSize;
	}

	public void appendLong(long value) {
		int newSize = size + 8;
		ensureCapacity(newSize);
		Util.writeLong(array, value, size);
		size = newSize;
	}

	public void appendInt(int value) {
		int newSize = size + 4;
		ensureCapacity(newSize);
		Util.writeInt(array, value, size);
		size = newSize;
	}

	public void appendChar(char value) {
		int newSize = size + 2;
		ensureCapacity(newSize);
		array[size++] = (byte) (value >>> 8);
		array[size++] = (byte) (value & 0xFF);
	}

	public void append(Buffer inputBuf) {
		int newSize = internalOffset + size + inputBuf.size();
		ensureCapacity(newSize);
		System.arraycopy(inputArray.array, 0, array, size, inputArray.size);
		size = newSize;
	}
	
	public void append(InputStream in) throws IOException {
		while (true) {
			int avail = in.available();
			if (avail < 0)
				break;

			// when avail is 0, sometimes it means that no bytes are
			// available temporarily (as in an http connection), and
			// sometimes it means we are at the end of file. The only way
			// to distinguish is to test with a read(). Unfortunately, a
			// read(array, size, 0) will always return 0, so we have
			// to try to read at least one byte.
			if (avail == 0)
				avail = 1;
			ensureCapacity(size + avail);
			int count = in.read(array, size, avail);
			if (count < 0)
				break;
			size += count;
		}
	}

	public void append(InputStream in, int len) throws IOException {
		ensureCapacity(size + len);
		while (true) {
			int count = in.read(array, size, len);
			if (count == -1)
				break;
			size += count;
			len = len - count;
			if (len <= 0)
				break;
		}
	}

	/**
	 * Append an integer as a group of compressed bytes. Requires between 1 and 5 byte of space.
	 */
	public void appendvInt(int i) {
		ensureCapacity(size + 5);
		while ((i & ~0x7F) != 0) {
			array[size++] = (byte) ((i & 0x7f) | 0x80);
			i >>>= 7;
		}
		array[size++] = (byte) i;
	}

	public void appendvLong(long lng) {
		ensureCapacity(size + 9);
		while ((lng & ~0x7F) != 0) {
			array[size++] = (byte) ((lng & 0x7f) | 0x80);
			lng >>>= 7;
		}
		array[size++] = (byte) lng;
	}

	public void ensureCapacity(int capacity) {
		if (array.length < capacity) {
			int newCapacity = getNewCapacity(capacity);
			byte[] newArray = new byte[newCapacity];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;
		}
	}

	public void ensureCapacityNoCopy(int capacity) {
		if (array.length < capacity) {
			int newCapacity = getNewCapacity(capacity);
			array = new byte[newCapacity];
		}
	}

	protected int getNewCapacity(int oldCapacity) {
		// make 50% bigger than required
		int newCapacity = Math.max(16, (int) ((float) oldCapacity * 1.5f));
		return newCapacity;
	}

	private void putInternal(int index, byte value) {
		arr[index] = value; // 
	}
	
	public void put(int index, byte value) {
		arr[index + internalOffset] = value;
	}

	public void put(int index, char value) {
		put(index++, (byte) (value >>> 8));
		put(index, (byte) (value & 0xFF));
	}

	public void put(int index, int value) {
		Util.writeInt(array, value, index);
	}

	public void put(int index, long value) {
		Util.writeLong(array, value, index);
	}

	public void clear() {
		size = 0;
		position = 0;
	}

	
	public boolean equals(Object o) {
		return super.equals((ByteArray) o);
	}

	/**
	 * Returns a hash code for this object. Based on the hash code for String.
	 *
	 * @return a hash code value for this object.
	 */
	public int hashCode() {
        int h = hash;
        if (h == 0 && size > 0) {
            byte val[] = array;

            for (int i = 0; i < size; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
	}

	/**
	 * Appends a CharSequence using a modified UTF-8 format. See java.io.DataInputStream for an explanation of the
	 * format. The format is further modified by omitting the two-byte length indicator in the beginning. Can be used 
	 * to append String, StringBuffer, StringBuilder, and
	 * FastStringBuffer, all of which implement CharSequence.
	 */
	public void append(CharSequence seq) {
		
        int strlen = seq.length();
        int utflen = 0;
        int c, count = 0;

        // calculate bytes required
        for (int i = 0; i < strlen; i++) {
            c = seq.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        ensureCapacity(size + utflen);
        
        byte[] bytearr = array;
        count = size; // starting point for append

        int i=0;
        for (i=0; i<strlen; i++) {
           c = seq.charAt(i);
           if (!((c >= 0x0001) && (c <= 0x007F))) break;
           bytearr[count++] = (byte) c;
        }

        for (;i < strlen; i++){
            c = seq.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;

            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }
        
        size = count;
	}



	public void appendBoolean(boolean value) {
		if (value)
			appendByte((byte) 1);
		else
			appendByte((byte) 0);
	}


	/**
	 * Insert the specified buffer at the specified byte position.
	 * 
	 * @param position
	 *            starting offset in this ByteArray where the buffer should be inserted
	 * @param buf
	 *            the buffer to insert
	 */
	public void insert(int position, ByteArray buf) {

		int bufSize = buf.size();
		ensureCapacity(size + bufSize);

		// shift current contents up
		System.arraycopy(array, position, array, position + bufSize, size - position);

		// copy new contents into the space we created above
		System.arraycopy(buf.getArray(), 0, array, position, bufSize);

		size += bufSize;
	}

	
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
	public void remove(int from, int count) {
		System.arraycopy(array, from + count, array, from, size - (from + count));
		size -= count;
	}
	
	/**
	 * The reverse of appendvInt() -- pops a vInt off the end, reduces the size of the array. This method assumes that
	 * the previous entry is also a vInt. If not, this method is unreliable.
	 * 
	 * @return the popped vInt
	 */
	public int popVInt() {

		// back up until we hit a positive byte, then select the next byte as the start of the vInt
		int start = size - 2;
		while (start > 0 && array[start] < 0) {
			start--;
		}
		start++;

		position = start;
		int out = getvInt();
		size = start;
		return out;
	}


	
}
