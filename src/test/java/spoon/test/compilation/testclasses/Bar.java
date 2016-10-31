package spoon.test.compilation.testclasses;

public class Bar implements IBar {

	@Override
	public int m() {
		return 1;
	}
}

class FooEx extends RuntimeException {}