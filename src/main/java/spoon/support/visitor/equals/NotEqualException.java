package spoon.support.visitor.equals;

import spoon.SpoonException;

class NotEqualException extends SpoonException {
	static final NotEqualException INSTANCE = new NotEqualException();

	private NotEqualException() {
	}
}
