package spoon.test.properties.testclasses;

public class Sample {

	public Sample() {
		new Thread() {
		};
	}

	public Sample(int j) {
		this(j, 0);
		new Thread() {
		};
	}

	public Sample(int j, int k) {
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
