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
			public void checkType(Foo type) {
				this.checkType(type);
			}
		}
		return new Foo(0, 0) {
			@Override
			public void m() {
				Foo.this.invStatic();
				this.invStatic();
			}

			public void invStatic() {
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

		void method() {
		}
	}

	public static class Fii {
		public static class Fuu {
			static void m() {
			}
			void method() {
			}
		}
	}
}
