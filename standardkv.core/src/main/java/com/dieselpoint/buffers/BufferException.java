package com.dieselpoint.buffers;

/**
 * Unchecked exception thrown by buffer operations.
 * @author ccleve
 *
 */
@SuppressWarnings("serial")
public class BufferException extends RuntimeException {
	
	public BufferException(String message) {
		super(message);
	}

	public BufferException(String message, Throwable cause) {
		super(message, cause);
	}

	public BufferException(Throwable cause) {
		super(cause);
	}
}
