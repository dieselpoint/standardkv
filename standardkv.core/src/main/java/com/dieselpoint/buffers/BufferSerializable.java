package com.dieselpoint.buffers;

/**
 * Classes that implement this interface can serialize themselves to a Buffer
 * and populate themselves from a Buffer.
 * @author ccleve
 *
 */
public interface BufferSerializable {
	
	public void readFromBuffer(Buffer buf);
	
	public void writeToBuffer(Buffer buf);

}
