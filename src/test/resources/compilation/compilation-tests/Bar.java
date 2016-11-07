package compilation;

public class Bar implements IBar {

	@Override
	public int m() {
		return 1;
	}
}

class FooEx extends RuntimeException {}
