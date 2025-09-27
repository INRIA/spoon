package spoon.test.refactoring.testclasses;

public class MethodRenaming implements InterfaceRenaming {

	public static void main(String[] args) {
		MethodRenaming example = new MethodRenaming();
		example.defaultMethod();
		example.regularMethod();
		SimpleNestedClass.staticNestedMethod();
		SimpleNestedClass nestedInstance = new SimpleNestedClass();
		nestedInstance.nestedMethod();
	}

	public void regularMethod() {
	}

	public static class SimpleNestedClass {
		public static void staticNestedMethod() {
		}

		public void nestedMethod() {
		}
	}
}
