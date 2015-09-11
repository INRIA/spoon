package spoon.test.prettyprinter.testclasses;

import java.util.ArrayList;
import java.util.List;

public class AClass {
	public List<?> aMethod() {
		return new ArrayList<>();
	}

	public List<? extends ArrayList> aMethodWithGeneric() {
		return new ArrayList<>();
	}
}
