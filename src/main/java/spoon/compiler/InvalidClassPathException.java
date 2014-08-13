package spoon.compiler;

import spoon.SpoonException;

public class InvalidClassPathException extends SpoonException {
	private static final long serialVersionUID = 1L;
	public InvalidClassPathException() {
		super();
	}
	public InvalidClassPathException(String msg) {
		super(msg);
	}
	public InvalidClassPathException(Throwable e) {
		super(e);
	}
	public InvalidClassPathException(String msg, Exception e) {
		super(msg, e);
	}
}
