package spoon.test.imports.testclasses;

import spoon.test.imports.testclasses.internal3.Foo;

import java.io.File;

public class NotImportExecutableType {

	void m() {
		Foo foo = new Foo();
		Object o = foo.<File>bar();
	}
}
