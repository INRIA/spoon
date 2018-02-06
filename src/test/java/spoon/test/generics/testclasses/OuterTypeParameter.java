package spoon.test.generics.testclasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OuterTypeParameter {
	public <T> List<T> method() {
		return new ArrayList<T>() {
			@Override
			public Iterator<T> iterator() {
				return super.iterator();
			}
		};
	}
}
