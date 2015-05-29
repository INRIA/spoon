package spoon.test.visibility.testclasses;

public class Tacos {
	int[] x;
}

class Burritos extends Tacos {
	void foo() {
		System.out.println(x.length);
	}
}
