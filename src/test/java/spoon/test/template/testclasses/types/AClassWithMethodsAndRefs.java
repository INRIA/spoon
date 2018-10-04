package spoon.test.template.testclasses.types;

public class AClassWithMethodsAndRefs {

	public AClassWithMethodsAndRefs() {
		someMethod(0);
	}
	
	Local local = () -> {sameTypeStatic.foo();};
	AClassWithMethodsAndRefs sameType = new AClassWithMethodsAndRefs();
	static AClassWithMethodsAndRefs sameTypeStatic;

	public AClassWithMethodsAndRefs anotherMethod() {
		someMethod(0);
		// instantiate itself
		return new AClassWithMethodsAndRefs();
	}

	public void someMethod(int i) {
		// call recursivelly itself
		if (i == 0) {
			someMethod(1);
		}
	}

	interface Local {
		void bar();
	}

	Local foo() {
		class Bar implements Local {
			public void bar() {
				bar();
				foo();
			}
		}
		return new Bar();
	}

}
