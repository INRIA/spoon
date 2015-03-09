package spoon.test.trycatch.testclasses;

import spoon.test.trycatch.testclasses.internal.MyException;
import spoon.test.trycatch.testclasses.internal.MyException2;

public class Foo {
	public void m() throws Exception{
		try {
		} catch (MyException | MyException2 ignore) {
		}
	}
}
