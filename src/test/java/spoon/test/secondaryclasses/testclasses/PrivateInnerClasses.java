package spoon.test.secondaryclasses.testclasses;

import java.util.Comparator;

public class PrivateInnerClasses<P> {

	private class DataClassComparator<T> implements Comparator<Class<T>> {

		public DataClassComparator() {
		}

		@Override
		public int compare(Class<T> c1, Class<T> c2) {
			return c1.getName().compareTo(c2.getName());
		}
	};

	public PrivateInnerClasses() {
		DataClassComparator<Object> c1 = new DataClassComparator<>();
		DataClassComparator<Class<P>> c2 = new DataClassComparator<Class<P>>();
		System.out.println(" - " + c1 + "," + c2);
	}
	
}
