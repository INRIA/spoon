package spoon.reflect.path;

/**
 *  This exception is throw when there are errors during a CtPath building or evaluation.
 */
public class CtPathException extends Exception {
	public CtPathException() {
	}

	public CtPathException(Throwable cause) {
		super(cause);
	}

	public CtPathException(String message) {
		super(message);
	}
}
