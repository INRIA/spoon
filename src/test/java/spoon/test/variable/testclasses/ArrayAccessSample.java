package spoon.test.variable.testclasses;

public class ArrayAccessSample {
	public void method(String[] s) {
		s[0] = s[0];
		System.err.println(s[0]);
	}
}
