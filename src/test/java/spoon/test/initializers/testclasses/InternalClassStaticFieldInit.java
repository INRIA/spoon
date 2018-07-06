package spoon.test.initializers.testclasses;

public class InternalClassStaticFieldInit {
	static class InternalClass{
		static final String tmp;
		static {
			tmp = "nop";
		}
	}
}
