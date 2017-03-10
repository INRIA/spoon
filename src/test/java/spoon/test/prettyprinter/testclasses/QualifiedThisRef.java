package spoon.test.prettyprinter.testclasses;

public class QualifiedThisRef<T> {

	private Sub sub;
	class Sub {
		void foo() {
			Object o = spoon.test.prettyprinter.testclasses.QualifiedThisRef.Sub.this;
		}
		void foo2() {
			Object o2 = spoon.test.prettyprinter.testclasses.QualifiedThisRef.this;
		}
	}

	void bla() { System.out.println(sub); }
}
