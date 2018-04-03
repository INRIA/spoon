package spoon.test.testclasses;

public class SampleClass {

	public SampleClass() {
		new Thread() {
		};
	}

	public SampleClass(int j) {
		this(j, 0);
		new Thread() {
		};
	}

	public SampleClass(int j, int k) {
		super();
		new Thread() {
		};
	}
	
	void method() {
	}

	void method2() {
		new Thread() {
		};
	}

	Thread method3() {
		return new Thread() {
		};
	}
}
