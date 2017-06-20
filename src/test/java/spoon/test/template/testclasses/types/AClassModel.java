package spoon.test.template.testclasses.types;

import java.util.AbstractList;

public class AClassModel<E> extends AbstractList<E> implements AnIfaceModel {

	public AClassModel() {
	}
	
	@Override
	public E get(int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public void someMethod() {
	}
}
