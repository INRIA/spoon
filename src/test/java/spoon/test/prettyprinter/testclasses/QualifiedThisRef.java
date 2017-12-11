package spoon.test.prettyprinter.testclasses;

public class QualifiedThisRef<T> {

	private Sub sub;
	class Sub {
		void foo() {
			Object o = this;
		}
		void foo2() {
			Object o2 = QualifiedThisRef.this;
		}
	}

	void bla() { System.out.println(sub); }
}
