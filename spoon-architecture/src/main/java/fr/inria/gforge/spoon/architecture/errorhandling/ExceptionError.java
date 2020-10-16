package fr.inria.gforge.spoon.architecture.errorhandling;

import spoon.SpoonException;

public class ExceptionError<T> implements IError<T> {

	@Override
	public void printError(T element) {
		throw new SpoonException("Element violates architectural rule " + element);
	}
}
