package spoon.test.generics.testclasses;

public class Panini {
	public Subscriber<? super Object> apply(Subscriber<? extends Long> t) {
		return null;
	}

	public interface Subscriber<T> {
	}
}
