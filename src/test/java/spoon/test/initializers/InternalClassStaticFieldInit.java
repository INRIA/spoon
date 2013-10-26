package spoon.test.initializers;

public class InternalClassStaticFieldInit {
	static class InternalClass{
		static final String tmp;
		static {
			tmp = "nop";
		}
	}
}
