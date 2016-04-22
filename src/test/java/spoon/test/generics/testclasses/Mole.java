package spoon.test.generics.testclasses;

public class Mole {
	public void cook() {
		class Cook<T> {
		}
		final Cook<String> aClass = new Cook<String>();
	}

	public void prepare() {
		class Prepare<T> {
		}
		new <Integer>Prepare<String>();
	}
}
