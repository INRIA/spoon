package spoon.reflect.declaration;

import spoon.SpoonException;

/**
 * This exception is thrown when the parent of an element has not been correctly
 * initialized.
 * 
 * @see CtElement#setParent(CtElement)
 * @see CtElement#getParent()
 * @see CtElement#updateAllParentsBelow()
 * @author Renaud Pawlak
 */
public class ParentNotInitializedException extends SpoonException {

	private static final long serialVersionUID = 1L;

	public ParentNotInitializedException(String message) {
		super(message);
	}

}
