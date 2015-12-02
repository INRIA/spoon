package spoon.test.replace.testclasses;

public class Tacos<T> {
	String field;

	public int m() {
		return 1;
	}

	public Tacos<Integer> m2() {
		return new Tacos<>();
	}

	public void m3(String param) {
		System.err.println(param);
	}
}
