package spoon.test.innerclasses;

public class InnerClasses {
	class A {
		private int a;
		class B {
			private int b;
			class C {
				private int c;
				class D {
					private int d;
					class E {
						private int e;
						class F {
							private int f;
							F(int a, int b, int c, int d, int e, int f) {
								A.this.a = a;
								B.this.b = b;
								C.this.c = c;
								D.this.d = d;
								E.this.e = e;
								this.f = f;
							}
						}
					}
				}
			}
		}
	}
}
