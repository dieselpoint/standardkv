package com.dieselpoint.buffers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrimitiveInputStream extends DataInputStream {

	public PrimitiveInputStream(InputStream in) {
		super(in);
	}
	
	public int readvInt() throws IOException {
		return ByteUtil.readvInt(this);
	}
	
	public long readvLong() throws IOException {
		return ByteUtil.readvLong(this);
	}
	
	public String readString() throws IOException {
		return ByteUtil.readString(this);
	}
	
	
}
