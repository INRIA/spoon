package spoon.support.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import spoon.reflect.declaration.CtElement;

public class ChildList<E extends CtElement> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	private CtElement parent;

	public ChildList(CtElement parent) {
		this.parent = parent;
	}

	public ChildList(List<E> statements, CtElement parent) {
		this.parent = parent;
		this.addAll(statements);
	}

	@Override
	public boolean add(E e) {
		boolean res = super.add(e);
		if (res)
			e.setParent(parent);
		return res;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean res = super.addAll(c);
		if (res)
			for (CtElement stat : c) {
				stat.setParent(parent);
			}
		return res;
	}

	@Override
	public boolean addAll(int i, Collection<? extends E> c) {
		boolean res = super.addAll(i, c);
		if (res)
			for (CtElement stat : c) {
				stat.setParent(parent);
			}
		return res;
	}

	@Override
	public E set(int i, E e) {
		E res = super.set(i, e);
		e.setParent(parent);
		return res;
	}

	@Override
	public void add(int i, E e) {
		super.add(i, e);
		e.setParent(parent);
	}

	public void setParent(CtElement parent) {
		this.parent = parent;
		resetParent();
	}

	private void resetParent() {
		for (E e : this)
			e.setParent(parent);
	}

}
