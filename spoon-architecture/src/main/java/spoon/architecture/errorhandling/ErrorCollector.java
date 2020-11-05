package spoon.architecture.errorhandling;

import java.util.ArrayList;
import java.util.Collection;
import spoon.SpoonException;

/**
 * This class defines an {@link IError} implementation. Instead of print the error instant all errors are collected and are printed by calling {@link #printCollectedErrors()}.
 */
public class ErrorCollector<T> implements IError<T> {

	private Collection<T> errors;
	private String  errorMessage = "Element violates rule: ";
	/**
	 * Creates a default error collector with default error message.
	 */
	public ErrorCollector() {
		errors = new ArrayList<>();
	}
	/**
	 * Creates an error collector with given error message as prefix before printing the error.
	 * @param errorMessage  prefix before the element violating a rule
	 */
	public ErrorCollector(String errorMessage) {
		errors = new ArrayList<>();
		this.errorMessage = errorMessage;
	}
	@Override
	public void printError(T element) {
		errors.add(element);
	}
/**
 * Prints all collected errors and throws an {@code SpoonException} afterwards for failing junit test case.
 */
	public void printCollectedErrors() {
		for (T t : errors) {
			System.out.println(errorMessage + t);
		}
		if (!errors.isEmpty()) {
			throw new SpoonException("There are rule violations:\n");
		}
	}
}
