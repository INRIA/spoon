package spoon.test.eval.testclasses;

import java.io.File;

public class Foo {
	final boolean b0 = true && false;
	boolean b2 = true; // not final so not considered
	boolean b3 = true & false; // exec BITAND
	boolean b4 = true | false; // exec BITOR
	boolean b5 = true ^ false; // exec BITXOR
	final boolean b6 = b0;
	final int i1 = Integer.MAX_VALUE; // access to static field
	final String str1 = File.pathSeparator;
	void foo() {
		boolean b1 = true ? false || b0 : b2; // will be simplified to "false"
	}

	int bar() {
		final int x = 0;
		do { // do
		} while (x != 0);
		if (x > 1 - 2) { // if
		}
		int y = 0;
		y = x;// assignment
		while (1 - 4 < 0) { // while
			return x; // return
		}
	}
}
