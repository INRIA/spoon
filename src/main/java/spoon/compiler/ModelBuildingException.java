package spoon.compiler;

import spoon.SpoonException;

/** thrown when the Spoon model of a program cannot be built */
public class ModelBuildingException extends SpoonException {

	public ModelBuildingException(String msg) {
		super(msg);
	}

	public ModelBuildingException(String msg, Exception e) {
		super(msg, e);
	}
	
	private static final long serialVersionUID = 5029153216403064030L;

}
