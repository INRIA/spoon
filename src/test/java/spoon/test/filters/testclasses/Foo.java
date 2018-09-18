package spoon.test.filters.testclasses;

@SuppressWarnings("bar")
public class Foo {
	int i;
	void foo() {
		int x = 3;
		int z;
		z= x+i;
		System.out.println(z);
	}
	
	@SuppressWarnings("foo")
	int bar () {
		if (0==1) {
			throw new RuntimeException();
		}
		return i;
	}
}

