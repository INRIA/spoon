package spoon.test.refactoring.testclasses;

public class MethodGenericRenaming {
	public <T> T[] sort(T[] array) {
		return sort(array, false);
	}

	public <T> T[] sort(T[] array, boolean order) {
		return array;
	}
}
