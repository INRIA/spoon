package spoon.test.prettyprinter.testclasses;

public class QualifiedThisRef<T> {

	class Sub {
		void foo() {
			Object o = spoon.test.prettyprinter.testclasses.QualifiedThisRef.Sub.this;
		}
		void foo2() {
			Object o2 = spoon.test.prettyprinter.testclasses.QualifiedThisRef.this;
		}
	}
}
