package com.dieselpoint.standardkv;

import java.io.IOException;
import java.io.InputStream;



public class ByteArray extends ByteSpan {

	/*
	 * For now, ByteArray does not have an offset field when specifies the span
	 * relative to the start of the the array. To implement it, all access to 
	 * the array field needs to be wrapped and accessed only through some kind
	 * of put() method that uses the offset
	 */
	
	protected byte[] array;
	private int hash;

	public ByteArray() {
		this(16);
	}

	public ByteArray(int initialCapacity) {
		array = new byte[initialCapacity];
	}

	public ByteArray(CharSequence seq) {
		array = new byte[seq.length() * 2];
		append(seq);
	}

	public ByteArray(byte[] arr) {
		this(arr, arr.length);
	}

	public ByteArray(byte[] arr, int size) {
		this.array = arr;
		this.size = size;
	}

	public byte[] getArray() {
		return array;
	}

	public byte[] getTrimmedArray() {
		byte[] newArray = new byte[size];
		System.arraycopy(array, 0, newArray, 0, size);
		return newArray;
	}

	public void setArray(byte[] array) {
		this.array = array;
		this.size = array.length;
	}

	public void appendByte(byte value) {
		size++;
		ensureCapacity(size);
		array[size - 1] = value;
	}

	public void append(byte[] value) {
		append(value, 0, value.length);
	}

	public void append(byte[] value, int offset, int length) {
		int newSize = size + length;
		ensureCapacity(newSize);
		System.arraycopy(value, offset, array, size, length);
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

	public void append(ByteArray inputArray) {
		int newSize = size + inputArray.size;
		ensureCapacity(newSize);
		System.arraycopy(inputArray.array, 0, array, size, inputArray.size);
		size = newSize;
	}

	public void append(ByteSpan span) {
		int newSize = size + span.size();
		ensureCapacity(newSize);
		span.copyTo(0, array, size, span.size());
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

	public void put(int index, byte value) {
		array[index] = value;
	}

	public void put(int index, char value) {
		array[index++] = (byte) (value >>> 8);
		array[index] = (byte) (value & 0xFF);
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

	@Override
	public byte get(int i) {
		return array[i];
	}

	@Override
	public ByteSpan slice(int offset, int size) {
		throw new UnsupportedOperationException("Need to implement offset to make this work. See notes at top of file. Wait until we need it.");
	}

	@Override
	public void copyTo(int srcOffset, byte[] dest, int destOffset, int count) {
		System.arraycopy(array, srcOffset, dest, destOffset, count);
	}


}
