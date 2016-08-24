package spoon.test.method.testclasses;

public class Tacos {
	public <T> void method1(T t) {
	}

	public <T extends String> void method1(T t) {
	}

	public <T extends Integer> void method1(T t) {
	}
}
