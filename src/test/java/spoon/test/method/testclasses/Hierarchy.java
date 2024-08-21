package spoon.test.method.testclasses;

public class Hierarchy {
	interface A1 {
		void m();
	}
	interface A2 {
		void m();
	}
	interface B extends A1 {
		void m();
	}
	interface C extends B, A1 {
		void m();
	}
	public interface D extends C, A2 {
		void m();
	}
}
