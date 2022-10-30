package test;

// static field import
import static java.lang.Integer.MAX_VALUE;

// static method import
import static java.lang.System.lineSeparator;

public class Test {
	public int test() {
		test();
		lineSeparator();
		return MAX_VALUE;
	}
}
