package com.dieselpoint.buffers;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wrap a Buffer in this class to make it an OutputStream.
 * @author ccleve
 */
public class BufferOutputStream extends OutputStream {

	private Buffer buf;

	public BufferOutputStream(Buffer buf) {
		this.buf = buf;
	}
	
	@Override
	public void write(int b) throws IOException {
		buf.appendByte((byte)(0xFF & b));
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buf.appendBytes(b, off, len);
	}

}
