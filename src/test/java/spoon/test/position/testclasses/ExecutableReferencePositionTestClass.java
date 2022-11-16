package spoon.test.position.testclasses;

public class ExecutableReferencePositionTestClass {

	private ExecutableReferencePositionTestClass() {
	}

	private <T> ExecutableReferencePositionTestClass(int marker) {
	}

	private static void referencedPlain() {
	}

	private static <T> void referencedTyped() {
	}

	public static void entrypoint() {
		Runnable r1 = ExecutableReferencePositionTestClass::referencedPlain;
		Runnable r2 = ExecutableReferencePositionTestClass::<String>referencedTyped;

		referencedPlain();
		ExecutableReferencePositionTestClass.<String>referencedTyped();

		new ExecutableReferencePositionTestClass();
		new <String>ExecutableReferencePositionTestClass(20);

		new TestClassWithGenericParameter<String>();
	}

	private static class TestClassWithGenericParameter<X> {}

	private static class TestUntypedExplicitConstructorCall {

		private TestUntypedExplicitConstructorCall() {
			this(20);
		}

		private TestUntypedExplicitConstructorCall(int marker) {
		}
	}

	private static class TestTypedExplicitConstructorCall {

		private TestTypedExplicitConstructorCall() {
			<String>this(20);
		}

		private <T> TestTypedExplicitConstructorCall(int marker) {
		}
	}

	private static class TestUntypedExplicitConstructorCallNoArgs {

		private TestUntypedExplicitConstructorCallNoArgs(int marker) {
			this();
		}

		private TestUntypedExplicitConstructorCallNoArgs() {
		}
	}

	private static class GenericSuperClass {
		protected GenericSuperClass() {

		}
		protected <T> GenericSuperClass(int marker) {

		}
	}

	private static class TestUntypedExplicitSuperConstructorCall extends GenericSuperClass {

		private TestUntypedExplicitSuperConstructorCall() {
			super();
		}
	}

	private static class TestTypedExplicitSuperConstructorCall extends GenericSuperClass {

		private TestTypedExplicitSuperConstructorCall() {
			<String>super(20);
		}
	}
}
