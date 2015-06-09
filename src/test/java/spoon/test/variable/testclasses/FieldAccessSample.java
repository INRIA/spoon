package spoon.test.variable.testclasses;

public class FieldAccessSample {
	private static int I;
	private String s;
	private int i;

	public void method() {
		s = "tacos";
		System.out.println(s);
		i = 3;
		FieldAccessSample.I = 42;
	}
}
