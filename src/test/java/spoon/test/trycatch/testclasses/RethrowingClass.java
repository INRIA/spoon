package spoon.test.trycatch.testclasses;

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
