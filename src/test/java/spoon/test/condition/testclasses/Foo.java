package spoon.test.condition.testclasses;

public class Foo {
	public boolean m() {
		boolean x;
		int a = 0;
		return x = (a == 18) ? true : false;
	}

	public boolean m2() {
		int a = 0;
		return a == 18 ? true : false;
	}

	void m3() {
		if (true) {
			System.out.println();
		} else if (true) {
			System.out.println();
		} else {
			System.out.println();
		}
		if (true)
			System.out.println();
		else if (true)
			System.out.println();
		else
			System.out.println();
		for (int i = 0; i < 10; i++) {
			System.out.println();
		}
		for (int i = 0; i < 10; i++)
			System.out.println();
		while(true) {
			break;
		}
		while(true)
			break;
		do
			break;
		while (true);
		do {
			break;
		} while (true);
	}

	void m4() {
		if (false) {}

		if (false);
	}
}
