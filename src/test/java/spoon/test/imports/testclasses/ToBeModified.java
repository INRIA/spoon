package spoon.test.imports.testclasses;

import java.util.List;

public class ToBeModified {

	/**
	 * This method contains List, but it will be removed during refactoring
	 */
	public void m() {
		List<?> x;
	}

}
