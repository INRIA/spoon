package spoon.test.casts;

import java.util.ArrayList;
import java.util.List;

public class Castings {
	public void test(double a) {
	}

	public void foo() {
		List<Integer> list = new ArrayList<Integer>(1);
		list.add(1);
		test(getValue(list));
	}

	public final <T> T getValue(List<T> list) {
		return list.get(0);
	}
}
