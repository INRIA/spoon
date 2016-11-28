package spoon.test.targeted.testclasses;

import static spoon.test.targeted.testclasses.Bar.FIELD;
import static spoon.test.targeted.testclasses.Bar.staticMethodBar;

public class Foo<T> extends SuperClass {
	private int i;
	private int j;
	static int k;
	Foo foo;
	Bar bar;
	Fii.Fuu fuu;
	final static int p;

	static {
		p = 0;
	}

	public void m() {
		int x;
		// checking that this is correct Java and is correctly parsed
		x= spoon.test.targeted.testclasses.Foo.this.k;
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

	public void field() {
		int x = this.i;
		x = i;
		x = this.bar.i;
		x = bar.i;
		x = this.o;
		x = o;
		x = fuu.p;
	}

	public void inv() {
		new Foo(0, 0).method();
		foo.method();
		this.method();
		method();
		bar.methodBar();
		fuu.method();
		superMethod();
	}

	public void invStatic() {
		new Foo(0, 0).staticMethod();
		foo.staticMethod();
		this.staticMethod();
		Foo.staticMethod();
		staticMethod();
		Bar.staticMethodBar();
		staticMethodBar();
		Fii.Fuu.m();
	}

	private Foo method() {
		class NestedTypeScanner {
			Foo type;
			public void checkType(Foo type) {
				this.checkType(type);
			}
			public void checkField() {
				Foo inner = this.type;
				inner = type;
			}
		}
		return new Foo(0, 0) {
			int i;

			@Override
			public void m() {
				Foo.this.invStatic();
				this.invStatic();
			}

			public void invStatic() {
				int inner = Foo.this.i;
				inner = this.i;
				inner = i;
			}
		};
	}

	private static void staticMethod() {
	}

	public Foo(int i, int k) {
		this.i = i;
		j = k;
	}
	class InnerClass {
		int i;
		public void innerInv() {
			inv();
			Foo.this.inv();
			staticMethod();
			Foo.staticMethod();
			superMethod();
			Foo.this.superMethod();
			method();
			this.method();
		}

		public void innerField() {
			int x = this.i;
			x = i;
			x = Foo.this.i;
			x = Foo.k;
			x = Foo.this.o;
			x = o;
		}

		void method() {
		}
	}

	public static class Fii {
		public static class Fuu {
			int p;
			static void m() {
			}
			void method() {
			}
		}
	}
}
