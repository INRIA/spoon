package spoon.test.generics;

import java.util.Map;

public class InstanceOfMapEntryGeneric {
	public void methode(Object o) {
		boolean b = o instanceof Map.Entry<?, ?>;
	}
}
