package spoon.test.generics.testclasses4;

public class Test2 {
	private interface TestInt {
		String implementThis(String string);
	}

	// static, extends, implements and return method() - all that things needed
	static class Test3 extends ParentForTest implements TestInt {
		@Override
		public String implementThis(final String string) {
			return testMethod();
		}
	}
}
