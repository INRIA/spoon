package spoon.support.reflect.reference;

import spoon.SpoonException;

/** Spoon-specific ClassNotFoundException (simply encapsulates a ClassNotFoundException as a runtime exception) */
public class SpoonClassNotFoundException extends SpoonException {
	public SpoonClassNotFoundException(String msg, java.lang.ClassNotFoundException cnfe) {
		super(msg, cnfe);
	}

	private static final long serialVersionUID = 1L;

}
