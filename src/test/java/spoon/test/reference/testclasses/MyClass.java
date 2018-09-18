package spoon.test.reference.testclasses;

public class MyClass {
	public MyClass() {
		this("Default");
	}

	public MyClass(String param) {
		this(param, 0);
	}

	public MyClass(String param, int paramint) {

	}

	public <T> void method1(T t) {
		method2();
	}

	public <T extends String> void method1(T t) {
		method2();
	}

	public void method2() {
		method1("String");
		method1(5);
		method5(1, "Call method 5");
	}

	public void method3() {
		method2();
		method4("Call method 4");
	}

	public void method4(String param) {

	}

	public <S> void method5(S s, String s2) {

	}
}