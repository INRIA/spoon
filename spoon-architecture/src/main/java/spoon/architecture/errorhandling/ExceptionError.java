package spoon.architecture.errorhandling;

import spoon.SpoonException;
/**
 * This class defines a default implementation for {@code IError}. Any constraint violation will result in an thrown {@code SpoonException}.
 * <p>
 * You can use this e.g. for failing the CI if any architecture rule is violated. The exception message will consist of a String and the violated element printed.
 * Use {@code #ExceptionError(String)} for a custom errorPrefix string.
 */
public class ExceptionError<T> implements IError<T> {

	private String errorPrefix;

	public ExceptionError(String errorPrefix) {
		this.errorPrefix = errorPrefix;
	}
	public ExceptionError() {
		this.errorPrefix = "Element violates architectural rule";
	}
	@Override
	public void printError(T element) {
		throw new SpoonException(errorPrefix + element);
	}
}
