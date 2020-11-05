package spoon.architecture.helper;

import spoon.architecture.errorhandling.IError;

public class CountingErrorCollector<T> implements IError<T> {

	private int counter;
	@Override
	public void printError(T element) {
		System.out.println(element);
		counter++;
	}
	/**
	 * Returns the number of collected errors
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}

}
