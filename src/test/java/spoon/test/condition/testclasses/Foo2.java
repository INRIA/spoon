package spoon.test.condition.testclasses;

public class Foo2 {
	// bug case kindly provided by @Egor18
	// in https://github.com/INRIA/spoon/pull/2733
	void bug() {
		if (false);
		else System.out.println();
	}

	void bug2() {
		if (false)
			System.out.println("valid");
		else if (false);
			// Do nothing...
		else
			System.out.println("invalid");
	}

	void bug3() {
		if (false) {} // some comment
		else if (false) {}
	}
}
