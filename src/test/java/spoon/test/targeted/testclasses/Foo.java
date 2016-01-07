package spoon.test.targeted.testclasses;

import static spoon.test.targeted.testclasses.Bar.FIELD;

public class Foo {
	private int i;
	private int j;
	static int k;

	public void m() {
		int x;
		x= this.k;
		x= Foo.k;
		x= k;
		this.k = x;
		k=x;
		Foo.k=x;
		x = Bar.FIELD;
		x = FIELD;
		Bar.FIELD = x;
		FIELD = x;
	}

	public Foo(int i, int k) {
		this.i = i;
		j = k;
	}
}
