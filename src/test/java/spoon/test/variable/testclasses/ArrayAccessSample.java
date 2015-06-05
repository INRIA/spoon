package spoon.test.variable.testclasses;

public class ArrayAccessSample {
	public void method(String[] s) {
		s[0] = "tacos";
		System.err.println(s[0]);
	}
}
