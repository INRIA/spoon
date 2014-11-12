package spoon.test.trycatch;

/**
 * Created by gerard on 07/11/2014.
 */
public class RethrowingClass {

	static class FirstException extends Exception {
	}

	static class SecondException extends Exception {
	}

	public void rethrowException(String exceptionName)
			throws FirstException, SecondException {
		try {
			if (exceptionName.equals("First")) {
				throw new FirstException();
			} else {
				throw new SecondException();
			}
		} catch (Exception e) {
			throw e;
		}
	}

}
