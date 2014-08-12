package spoon;

/** is a generic runtime exception for Spoon */
public class SpoonException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public SpoonException() {
		super();
	}
	public SpoonException(String msg) {
		super(msg);
	}
	public SpoonException(Throwable e) {
		super(e);
	}
	public SpoonException(String msg, Exception e) {
		super(msg, e);
	}
}
