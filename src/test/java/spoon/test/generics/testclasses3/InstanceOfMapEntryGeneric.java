package spoon.test.generics.testclasses3;

import java.util.Map;

public class InstanceOfMapEntryGeneric {
	public void methode(Object o) {
		boolean b = o instanceof Map.Entry<?, ?>;
	}
}
