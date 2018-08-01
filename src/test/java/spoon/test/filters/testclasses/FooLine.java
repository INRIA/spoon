package spoon.test.filters.testclasses;

public class FooLine {
	void simple() {
		int x = 3;
		int z = 0;
		System.out.println(z);
	}

	void loopBlock() {
		for(int i = 0; i < 10; i++) {
			System.out.println(i);
		}
	}

	void loopNoBlock() {
		for(int i = 0; i < 10; i++)
			System.out.println(i);
	}

	void loopNoBody() {
		for(int i = 0; i < 10; i++);
	}

	void ifBlock() {
		if (3 < 4) {
			System.out.println("if");
		}
	}

	void ifNoBlock() {
		if (3 < 4)
			System.out.println("if");
	}

	void switchBlock() {
		switch ("test") {
		case "test":
			break;
		default:
			System.out.println("switch");
		}
	}
}

