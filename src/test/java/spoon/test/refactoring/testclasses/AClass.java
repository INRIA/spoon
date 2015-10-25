package spoon.test.refactoring.testclasses;

public class AClass extends AbstractClass {
	private final String string;

	public AClass() {
		this("");
	}

	public AClass(String string) {
		super();
		this.string = string;
	}

	public boolean isMySubclass(Object o) {
	    return o instanceof AClass;
	}
}
