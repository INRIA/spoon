package spoon.test.processing;

public class SampleForInsertBefore {

	private void method() {

	}
	
	private void method2() {
		new Thread(){};
	}
	
	private Thread method3() {
		return new Thread(){};
	}
}
