package spoon.architecture.errorhandling;

import java.util.ArrayList;
import java.util.Collection;
import spoon.SpoonException;

public class ErrorCollector<T> implements IError<T> {

	private Collection<T> errors;
	private String  errorMessage = "Element violates rule: ";
	public ErrorCollector() {
		errors = new ArrayList<>();
	}
	public ErrorCollector(String errorMessage) {
		errors = new ArrayList<>();
		this.errorMessage = errorMessage;
	}
	@Override
	public void printError(T element) {
		errors.add(element);
	}

	public void printCollectedErrors() {
		for (T t : errors) {
			System.out.println(errorMessage + t);
		}
		if (!errors.isEmpty()) {
			throw new SpoonException("There are rule violations");
		}
	}
}
