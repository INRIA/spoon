package spoon.test.imports.testclasses;

public class SubClass extends SuperClass {
	public void aMethod() {
		new SubClass.Item("");
	}

	public static class Item extends SuperClass.Item {
		public Item(String s) {
			super(1, s);
		}
	}
}
