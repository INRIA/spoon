package spoon.test.prettyprinter.testclasses;

public class Throw {
	void foo(int x) {
		new IllegalArgumentException("x must be nonnegative");
	}
}