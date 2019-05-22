package spoon.test.prettyprinter.testclasses;

import org.Bar;
import org.foo.*;

public class Unresolved {
	public Unresolved() {
		Bar b = new Bar();
		Foo f = new Foo();
	}
}
