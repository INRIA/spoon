class SkipException extends Exception {
	private static final long serialVersionUID = 1L;

	Object skipped;

	SkipException(Object e) {
		super("skipping " + e.toString());
		skipped = e;
	}

}

public class Simple {

}