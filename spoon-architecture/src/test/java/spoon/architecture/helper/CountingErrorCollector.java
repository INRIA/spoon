package spoon.architecture.helper;

import com.google.common.flogger.FluentLogger;
import spoon.architecture.errorhandling.IError;

public class CountingErrorCollector<T> implements IError<T> {

	private static final FluentLogger logger = FluentLogger.forEnclosingClass();
	private int counter;
	@Override
	public void printError(T element) {
		logger.atInfo().log("Print error for element of type %s:\n %s", element.getClass(), element);
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
