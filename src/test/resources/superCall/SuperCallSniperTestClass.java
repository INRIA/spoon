package superCall;

/**
 * Test class to reproduce issue #4021
 */
public class SuperCallSniperTestClass {
	int m = 0;
	
	public int a(int x) {
		return x;
	}
}

class Child extends SuperCallSniperTestClass {
	public int b(int x) {
		return super.a(-x);
	}
}
