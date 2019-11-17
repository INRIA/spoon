package spoon.test.prettyprinter.testclasses;

import java.util.ArrayList;
import java.util.List;
import static org.Bar.m;

public class UnresolvedExtend extends org.Foo {
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
