package spoon.test.position.testclasses;

public class FooField {

	public final int field1 = 0;

	int field2 =
			0;

	static FooField f = null;

	public void m() {
		FooField.f.field2 = 0;
	}
}