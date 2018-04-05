package com.dieselpoint.buffers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Static methods for reading and writing bytes.
 * 
 * TODO use sun.misc.Unsafe to read/write primitives.
 * 
 * @author ccleve
 *
 */
public class ByteUtil {

	public static void writeByte(byte b, OutputStream out) throws IOException {
		out.write(b);
	}

	public static void writeBoolean(boolean b, OutputStream out) throws IOException {
		out.write(b ? 1 : 0);
	}
	
	public static void writevInt(int i, OutputStream out) throws IOException {
		while ((i & ~0x7F) != 0) {
			out.write((byte) ((i & 0x7f) | 0x80));
			i >>>= 7;
		}
		out.write((byte) i);
	}

	public static void writeInt(int i, OutputStream out) throws IOException {
		out.write((i >>> 24) & 0xFF);
		out.write((i >>> 16) & 0xFF);
		out.write((i >>> 8) & 0xFF);
		out.write(i & 0xFF);
	}
	
	public static void writevLong(long lng, OutputStream out) throws IOException {
		while ((lng & ~0x7F) != 0) {
			out.write((byte) ((lng & 0x7f) | 0x80));
			lng >>>= 7;
		}
		out.write((byte) lng);
	}

	public static void writeLong(long i, OutputStream out) throws IOException {
		out.write((byte)((i >>> 56) & 0xFF));
		out.write((byte)((i >>> 48) & 0xFF));
		out.write((byte)((i >>> 40) & 0xFF));
		out.write((byte)((i >>> 32) & 0xFF));
		out.write((byte)((i >>> 24) & 0xFF));
		out.write((byte)((i >>> 16) & 0xFF));
		out.write((byte)((i >>> 8) & 0xFF));
		out.write((byte)(i & 0xFF));
	}
	
	public static void writeString(CharSequence seq, OutputStream out) throws IOException {

		// nulls written as zero-len string
		if (seq == null) {
			writevInt(0, out);
			return;
		}
		
		int len = seq.length();
		writevInt(len, out);
		for (int i = 0; i < len; i++) {
			writeUtf8Char(seq.charAt(i), out);
		}
	}

	public static void writeUtf8Char(char ch, OutputStream out) throws IOException {

		if ((ch >= 0x0001) && (ch <= 0x007F)) {
			out.write(ch);
		} else if (ch > 0x07FF) {
			out.write(0xE0 | ((ch >> 12) & 0x0F));
			out.write(0x80 | ((ch >> 6) & 0x3F));
			out.write(0x80 | ((ch >> 0) & 0x3F));
		} else {
			out.write(0xC0 | ((ch >> 6) & 0x1F));
			out.write(0x80 | ((ch >> 0) & 0x3F));
		}
	}

	
	public static boolean readBoolean(InputStream in) throws IOException {
		return in.read() == 1;
	}
	
	
	public static byte readByte(InputStream in) throws IOException {
		return (byte) in.read();
	}
	
	public static int readvInt(InputStream in) throws IOException {
		byte b = (byte)in.read();
		int num = b & 0x7F;
		for (int shift = 7; b < 0; shift += 7) {
			b = (byte)in.read();
			num |= (b & 0x7F) << shift;
		}
		return num;
	}

	public static int readInt(InputStream in) throws IOException {
		int i = 0;
		i |= (in.read() & 0xFF) << 24;
		i |= (in.read() & 0xFF) << 16;
		i |= (in.read() & 0xFF) << 8;
		i |= (in.read() & 0xFF);
		return i;
	}
	
	public static long readvLong(InputStream in) throws IOException {
		byte b = (byte)in.read();
		long num = b & 0x7F;
		for (int shift = 7; b < 0; shift += 7) {
			b = (byte)in.read();
			num |= (b & 0x7FL) << shift;
		}
		return num;
	}

	public static String readString(InputStream in) throws IOException {
		int len = readvInt(in);
		if (len == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(readUtf8Char(in));
		}
		return sb.toString();
	}
	
	public static char readUtf8Char(InputStream in) throws IOException {
		int c, char2, char3;

		c = in.read() & 0xFF;

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
				char2 = in.read() & 0xFF;
				return (char) (((c & 0x1F) << 6) | (char2 & 0x3F));

			case 14 :
				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				char2 = in.read() & 0xFF;
				char3 = in.read() & 0xFF;
				return (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));

			default :
				/* 10xx xxxx,  1111 xxxx */
				throw new BufferException("bad UTF String format:" + Integer.toHexString(c));
		}
	}

	public static Buffer readBuffer(InputStream in) throws IOException {
		int size = ByteUtil.readvInt(in);
		ByteArray arr = new ByteArray(size);
		in.read(arr.getArray(), 0, size);
		arr.size(size);
		return arr;
	}

	public static void writeBuffer(Buffer buf, OutputStream out) throws IOException {
		if (buf == null) {
			ByteUtil.writevInt(0, out);
			return;
		}
		int size = buf.size();
		ByteUtil.writevInt(size, out);
		buf.copyBytesTo(out, size);
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
		array[offset    ] = (byte) ((i >>> 24) & 0xFF);
		array[offset + 1] = (byte) ((i >>> 16) & 0xFF);
		array[offset + 2] = (byte) ((i >>> 8) & 0xFF);
		array[offset + 3] = (byte) (i & 0xFF);
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
		array[offset    ] = (byte) ((i >>> 56) & 0xFF);
		array[offset + 1] = (byte) ((i >>> 48) & 0xFF);
		array[offset + 2] = (byte) ((i >>> 40) & 0xFF);
		array[offset + 3] = (byte) ((i >>> 32) & 0xFF);
		array[offset + 4] = (byte) ((i >>> 24) & 0xFF);
		array[offset + 5] = (byte) ((i >>> 16) & 0xFF);
		array[offset + 6] = (byte) ((i >>> 8) & 0xFF);
		array[offset + 7] = (byte) ((i & 0xFF));
	}
	
	/**
	 * Write a char to a location in a byte array. Serializes the
	 * char out as two bytes, big endian.
	 *
	 * @param array the array to write to
	 * @param ch the char to write
	 * @param offset the offset into the array to start writing
	 */
	public static void writeChar(byte [] array, char ch, int offset) {
		array[offset    ] = (byte) ((ch >>> 8) & 0xFF);
		array[offset + 1] = (byte) ((ch & 0xFF));
	}
	

	/**
	 * Read an integer from a location in a byte array, bigendian.
	 *
	 * @param array the array to read from
	 * @param offset the offset into the array to start reading
	 * @return the integer that was read
	 */
	public static int readInt(byte[] array, int offset) {
		int i = 0;
		i |= (array[offset] & 0xFF) << 24;
		i |= (array[offset + 1] & 0xFF) << 16;
		i |= (array[offset + 2] & 0xFF) << 8;
		i |= (array[offset + 3] & 0xFF);
		return i;
	}

	/**
	 * Read a long from a location in a byte array, bigendian.
	 *
	 * @param array the array to read from
	 * @param offset the offset into the array to start reading
	 * @return the long that was read
	 */
	public static long readLong(byte[] array, int offset) {
		long i = 0;
		i |= ((long) array[offset] & 0xFF) << 56;
		i |= ((long) array[offset + 1] & 0xFF) << 48;
		i |= ((long) array[offset + 2] & 0xFF) << 40;
		i |= ((long) array[offset + 3] & 0xFF) << 32;
		i |= ((long) array[offset + 4] & 0xFF) << 24;
		i |= ((long) array[offset + 5] & 0xFF) << 16;
		i |= ((long) array[offset + 6] & 0xFF) << 8;
		i |= ((long) array[offset + 7] & 0xFF);
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
		i |= (array[offset] & 0xFF) << 8;
		i |= (array[offset + 1] & 0xFF);
		return i;
	}

	public static void writeByte(byte b, Buffer buf) {
		buf.appendByte(b);
	}

	public static void writeInt(int i, Buffer buf) {
		buf.appendInt(i);
	}

	public static void writeBuffer(Buffer source, Buffer dest) {
		if (source == null) {
			dest.appendvInt(0);
			return;
		}
		int size = source.size();
		dest.appendvInt(size);
		dest.appendBuffer(source);
	}

	public static byte readByte(Buffer buf) {
		return buf.readByte();
	}

	public static int readInt(Buffer buf) {
		return buf.readInt();
	}

	public static Buffer readBuffer(Buffer buf) {
		int size = buf.readvInt();
		ByteArray out = new ByteArray(size);
		buf.readBytes(out, size);
		return out;
	}

	
	
}
