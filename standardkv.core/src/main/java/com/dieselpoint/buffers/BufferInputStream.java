package com.dieselpoint.buffers;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrap a Buffer in this class to make it an InputStream.
 * @author ccleve
 */
public class BufferInputStream extends InputStream {

	private Buffer buf;
	
	public BufferInputStream(Buffer buf) {
		this.buf = buf;
	}
	
	@Override
	public int read() throws IOException {
		return (buf.readByte() & 0xFF);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int remaining = buf.size() - buf.position();
		int count = Math.min(len, remaining);
		buf.copyBytesTo(b, off, count); // doesn't advance position
		buf.position(buf.position() + count);
		return count;
	}

	@Override
	public long skip(long n) throws IOException {
		int remaining = buf.size() - buf.position();
		int count = (int)Math.min(n, remaining);
		buf.position(buf.position() + count);
		return count;
	}

	@Override
	public int available() throws IOException {
		return buf.size() - buf.position();
	}

}
