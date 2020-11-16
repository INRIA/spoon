package examples.spoon.blocks;

public class Blocks {

	public Blocks() {

	}
	public int bar() {
		if (false) {
		return 3;
		}
		throw new ArithmeticException();
	}
}
