package spoon.test.thisconstructor.testclasses;

public class AClass  extends AbstractClass{
	private final String string;

	public AClass() {
		this("");
	}

	public AClass(String string) {
		super();
		this.string = string;
	}
}