package spoon.support.util;

import java.util.Collection;
import java.util.TreeSet;

public class NotDirectlyModifiableSet<E> extends TreeSet<E> {

	private static final long serialVersionUID = 1L;

	public NotDirectlyModifiableSet() {
	}
	
	@Override
	public boolean add(E e) {
		throw new RuntimeException("operation not allowed");
	}

	public boolean forceAdd(E e) {
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new RuntimeException("operation not allowed");
	}

	public boolean forceAddAll(Collection<? extends E> c) {
		return super.addAll(c);
	}

	@Override
	public boolean remove(Object o) {
		throw new RuntimeException("operation not allowed");
	}

	public boolean forceRemove(Object o) {
		return super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new RuntimeException("operation not allowed");
	}

	public boolean forceRemoveAll(Collection<?> c) {
		return super.removeAll(c);
	}
}
