package spoon.support.util;

import java.util.ArrayList;
import java.util.Collection;

public class NotDirectlyModifiableList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	public NotDirectlyModifiableList() {
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
	public void add(int index, E element) {
		throw new RuntimeException("operation not allowed");
	}

	public void forceAdd(int index, E element) {
		super.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new RuntimeException("operation not allowed");
	}

	public boolean forceAddAll(int index, Collection<? extends E> c) {
		return super.addAll(index, c);
	}

	@Override
	public E remove(int index) {
		throw new RuntimeException("operation not allowed");
	}

	public E forceRemove(int index) {
		return super.remove(index);
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
