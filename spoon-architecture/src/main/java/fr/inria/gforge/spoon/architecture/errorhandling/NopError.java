package fr.inria.gforge.spoon.architecture.errorhandling;

/** This class is a default implementation omitting the error and doing nothing. */
public class NopError<T> implements IError<T> {

	@Override
	public void printError(T element) {

	}

}
