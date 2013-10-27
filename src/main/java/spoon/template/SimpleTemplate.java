package spoon.template;

public abstract class SimpleTemplate {

	final protected <R> R S(String parameterName, Class<R> type) {
		return null;
	}

	final protected void S(String parameterName) {
	}

	final protected <T extends Throwable> void S_throws(String parameterName)
			throws T {
	}

	final protected <R, T extends Throwable> R S_throws(String parameterName,
			Class<R> type) throws T {
		return null;
	}

}
