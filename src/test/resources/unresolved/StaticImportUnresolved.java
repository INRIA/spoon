package spoon.test.prettyprinter.testclasses;

import java.util.ArrayList;
import java.util.List;
import static org.Bar.*;

public class StaticImportUnresolved extends org.Foo {
	int i = f;
	public UnresolvedExtend() {
		super();
		m();
	}

	public List<?> aMethod() {
		return new ArrayList<>();
	}

	public List<? extends ArrayList> aMethodWithGeneric() {
		return new ArrayList<>();
	}
}
