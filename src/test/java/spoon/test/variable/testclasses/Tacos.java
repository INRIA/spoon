package spoon.test.variable.testclasses;

public class Tacos {
	Burritos burritos;
	final class Burritos {
		int i;
	}

	public void makeIt() {
		burritos.i = 4;

		final Tacos tacos = new Tacos();
		tacos.burritos.i = 3;
	}
}
