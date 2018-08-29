package spoon.test.generics.testclasses3;

import java.util.ArrayList;
import java.util.List;

public class GenericConstructor {
	public <E> GenericConstructor() {
		List<Integer> l = new ArrayList<>();
		l.size();
	}
}
