package spoon.test.limits;

import java.util.Map;

public class InstanceOfMapEntryGeneric {
	public void methode(Object o) {
		if (o instanceof Map.Entry<?, ?>) {
			return;
		}
	}
}
