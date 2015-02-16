package spoon.test.imports.testclasses;

import java.security.AccessControlException;

/**
 * Created by nicolas on 11/02/2015.
 */
public class MultiCatch {

	public void test() {

		try {

		} catch (ArithmeticException | AccessControlException e) {

		}
	}

}
