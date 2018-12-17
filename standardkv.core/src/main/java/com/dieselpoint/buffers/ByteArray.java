package com.dieselpoint.buffers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;

/**
 * An implementation of Buffer over a byte [] array.
 * @author ccleve
 *
 */
public class ByteArray implements Buffer, Externalizable {
	
	private int position;
	private int size;
	private byte [] array;
	private int hash;
	
	public ByteArray() {
		this(16);
	}

	public ByteArray(int initialCapacity) {
		array = new byte[initialCapacity];
	}

	public ByteArray(CharSequence seq) {
		array = new byte[seq.length() * 2];
		appendString(seq);
	}

	public ByteArray(byte[] arr) {
		this(arr, arr.length);
	}

	public ByteArray(byte[] arr, int size) {
		this.array = arr;
		this.size = size;
	}
	
	public void setArray(byte[] arr) {
		this.array = arr;
	}

	@Override
	public int compareTo(Buffer o) {
		if (o instanceof ByteArray) {
			ByteArray other = (ByteArray) o;
			int len = Math.min(other.size, size);
			byte [] arr = this.array;
			byte [] otherArr = other.array;
			for (int i = 0; i < len; i++) {
				int cmp = (arr[i] & 0xFF) - (otherArr[i] & 0xFF);
				if (cmp != 0) {
					return cmp;
				}
			}
			return size - other.size;
		} else {
			throw new UnsupportedOperationException("not yet implemented");
		}
	}

	@Override
	public void position(int position) {
		this.position = position;
	}

	@Override
	public int position() {
		return position;
	}

	@Override
	public void size(int size) {
		this.size = size;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean eof() {
		return (position >= size); 
	}

	@Override
	public byte readByte() {
		return array[position++];
	}
	
	@Override
	public byte readByte(int position) {
		return array[position];
	}

	@Override
	public int readInt() {
		int out = ByteUtil.readInt(array, position);
		position += 4;
		return out;
	}

	@Override
	public long readLong() {
		long out = ByteUtil.readLong(array, position);
		position += 8;
		return out;
	}


	@Override
	public int readvInt() {
		byte b = readByte();
		int num = b & 0x7F;
		for (int shift = 7; b < 0; shift += 7) {
			b = readByte();
			num |= (b & 0x7F) << shift;
		}
		return num;
	}

	@Override
	public long readvLong() {
		byte b = readByte();
		long num = b & 0x7F;
		for (int shift = 7; b < 0; shift += 7) {
			b = readByte();
			num |= (b & 0x7FL) << shift;
		}
		return num;
	}

	@Override
	public char readUtf8Char() {
		
		int c, char2, char3;

		c = (int)(readByte() & 0xFF);

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
				char2 = readByte();
				return (char) (((c & 0x1F) << 6) | (char2 & 0x3F));

			case 14 :
				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				char2 = readByte();
				char3 = readByte();
				return (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));

			default :
				/* 10xx xxxx,  1111 xxxx */
				throw new BufferException("bad UTF String format:" + Integer.toHexString(c));
		}
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

	@Override
	public void appendByte(byte b) {
		size++;
		ensureCapacity(size);
		array[size - 1] = b;
	}

	@Override
	public void appendInt(int i) {
		int newSize = size + 4;
		ensureCapacity(newSize);
		ByteUtil.writeInt(array, i, size);
		size = newSize;
	}

	@Override
	public void appendLong(long l) {
		int newSize = size + 8;
		ensureCapacity(newSize);
		ByteUtil.writeLong(array, l, size);
		size = newSize;
	}

	@Override
	public void appendBytes(byte[] source, int sourceOffset, int sourceSize) {
		int newSize = size + sourceSize;
		ensureCapacity(newSize);
		System.arraycopy(source, sourceOffset, array, size, sourceSize);
		size = newSize;
	}

	@Override
	public void appendvInt(int i) {
		ensureCapacity(size + 5);
		while ((i & ~0x7F) != 0) {
			array[size++] = (byte) ((i & 0x7f) | 0x80);
			i >>>= 7;
		}
		array[size++] = (byte) i;
	}

	@Override
	public void appendvLong(long l) {
		ensureCapacity(size + 9);
		while ((l & ~0x7F) != 0) {
			array[size++] = (byte) ((l & 0x7f) | 0x80);
			l >>>= 7;
		}
		array[size++] = (byte) l;
	}

	
	@Override
	public void appendUtf8Char(char ch) {
		ensureCapacity(size + 3);

        if ((ch >= 0x0001) && (ch <= 0x007F)) {
            array[size++] = (byte) ch;

        } else if (ch > 0x07FF) {
        	array[size++] = (byte) (0xE0 | ((ch >> 12) & 0x0F));
        	array[size++] = (byte) (0x80 | ((ch >>  6) & 0x3F));
        	array[size++] = (byte) (0x80 | ((ch >>  0) & 0x3F));
        } else {
        	array[size++] = (byte) (0xC0 | ((ch >>  6) & 0x1F));
        	array[size++] = (byte) (0x80 | ((ch >>  0) & 0x3F));
        }
	}
	
	
	
	@Override
	public void appendString(CharSequence seq) {
		int len = seq.length();
		for (int i = 0; i < len; i++) {
			appendUtf8Char(seq.charAt(i));
		}
	}
	
	@Override
	public void appendChars(char[] text, int offset, int len) {
		int end = offset + len;
		for (int i = offset; i < end; i++) {
			appendUtf8Char(text[i]);
		}
	}
	
	public void insert(int position, Buffer buf) {
		
		if (buf instanceof ByteArray) {
			ByteArray other = (ByteArray) buf;
			
			int bufSize = buf.size();
			ensureCapacity(size + bufSize);

			// shift current contents up
			System.arraycopy(array, position, array, position + bufSize, size - position);

			// copy new contents into the space we created above
			System.arraycopy(other.array, 0, array, position, bufSize);

			size += bufSize;			
		} else {
			throw new UnsupportedOperationException("not yet implemented");
		}
	}

	
	public void remove(int from, int count) {
		System.arraycopy(array, from + count, array, from, size - (from + count));
		size -= count;
	}

	@Override
	public void clear() {
		size = 0;
		position = 0;
		hash = 0;
	}
	

	public byte[] getTrimmedArray() {
		// TODO this is potentially bad, because sometimes it returns an array
		// that backs the buffer and sometimes it doesn't
		if (array.length == size) {
			return array;
		}
		byte[] newArray = new byte[size];
		System.arraycopy(array, 0, newArray, 0, size);
		return newArray;
	}

	@Override
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
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ByteArray)) {
			return false;
		}
		ByteArray other = (ByteArray) obj;
		if (other.size != this.size) {
			return false;
		}
		int end = size;
		for (int i = 0; i < end; i++) {
			if (array[i] != other.array[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void appendBuffer(Buffer source) {
		appendBuffer(source, 0, source.size());
	}
	
	@Override
	public void appendBuffer(Buffer source, int sourceOffset, int sourceSize) {
		if (source instanceof ByteArray) {
			ByteArray sourceArr = (ByteArray) source;
			ensureCapacity(size + sourceSize);
			System.arraycopy(sourceArr.array, sourceOffset, array, size, sourceSize);
			size += sourceSize;
		} else if (source == null) {
			throw new NullPointerException("source is null");
		} else {
			throw new UnsupportedOperationException("not yet implemented");
		}
	}

	@Override
	public String readString() {
		StringBuilder sb = new StringBuilder();
		readString(sb);
		return sb.toString();
	}

	@Override
	public String readString(int charCount) {
		StringBuilder sb = new StringBuilder();
		readString(sb, charCount);
		return sb.toString();
	}

	@Override
	public void readString(Appendable dest) {
		try {
			while (!eof()) {
				dest.append(readUtf8Char());
			}
		} catch (IOException e) {
			throw new BufferException(e);
		}
	}

	@Override
	public void readString(Appendable dest, int charCount) {
		try {
			for (int i = 0; i < charCount; i++) {
				dest.append(readUtf8Char());
			}
		} catch (IOException e) {
			throw new BufferException(e);
		}
	}
	
	public byte [] getArray() {
		return array;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(size);
		out.write(getArray(), 0, size);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		ensureCapacity(size);
		in.read(array, 0, size);
		this.size = size;
	}
	
	public String toUtf8String() {
		int pos = position();
		position(0);
		StringBuilder out = new StringBuilder(size);
		while (!eof()) {
			out.append(readUtf8Char());
		}
		position(pos);
		return out.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int len = size;
		for (int i = 0; i < len; i++) {
			int b = array[i] & 0xFF;
			sb.append(Integer.toString(b));// .toHexString(b));
			sb.append(' ');
		}
		return sb.toString();
	}

	@Override
	public void appendInputStream(InputStream source, int count) throws IOException {
		ensureCapacity(size + count);
		source.read(array, size, count);
		size += count;
	}

	@Override
	public void copyBytesTo(byte[] dest, int destOffset, int count) {
		System.arraycopy(array, 0, dest, destOffset, count);
	}

	@Override
	public void copyBytesTo(OutputStream dest, int count) throws IOException {
		dest.write(array, 0, count);
	}

	@Override
	public void readBytes(Buffer dest, int count) {
		if (dest instanceof ByteArray) {
			ByteArray destArr = (ByteArray) dest;
			destArr.ensureCapacity(destArr.size + count);
			System.arraycopy(array, position, destArr.getArray(), destArr.size, count);
			position += count;
			destArr.size(destArr.size() + count);
		} else {
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}

	@Override
	public void appendHexString(CharSequence seq) {

		int len = seq.length();
		int charCount = len / 2;
		ensureCapacity(size + charCount);

		if (len % 2 != 0) {
			throw new IllegalArgumentException("The hexadecimal input string must have an even length.");
		}

		int ptr = size;
		for (int i = 0; i < len; i += 2) {
			int h = hexToBin(seq.charAt(i));
			int l = hexToBin(seq.charAt(i + 1));
			array[ptr] = (byte) (h * 16 + l);
			ptr++;
		}

		size = ptr;
	}

	private static int hexToBin(char ch) {
		if ('0' <= ch && ch <= '9')
			return ch - '0';
		if ('A' <= ch && ch <= 'F')
			return ch - 'A' + 10;
		if ('a' <= ch && ch <= 'f')
			return ch - 'a' + 10;
		throw new IllegalArgumentException("Illegal hexadecimal character: " + ch);
	}	
	
}
