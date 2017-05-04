package spoon.test.methodreference.testclasses;

public class Cloud<T> {

	void method(T param) {}
}

class Sun {
	void foo() {
		Cloud<String> cc = new Cloud<>();
		cc.method("x");
	}
}
