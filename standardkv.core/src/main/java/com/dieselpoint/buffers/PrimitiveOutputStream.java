package com.dieselpoint.buffers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PrimitiveOutputStream extends DataOutputStream {

	public PrimitiveOutputStream(OutputStream out) {
		super(out);
	}
	
	public void writevInt(int i) throws IOException {
		ByteUtil.writevInt(i, out);
	}

	public void writevLong(long lng) throws IOException {
		ByteUtil.writevLong(lng, out);
	}

	public void writeString(CharSequence seq) throws IOException {
		ByteUtil.writeString(seq, out);
	}
	
}
