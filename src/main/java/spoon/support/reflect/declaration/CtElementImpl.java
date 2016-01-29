/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.reflect.declaration;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.processing.FactoryAccessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.ReferenceFilter;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.support.util.RtHelper;
import spoon.support.visitor.SignaturePrinter;
import spoon.support.visitor.TypeReferenceScanner;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.ANNOTATIONS_CONTAINER_DEFAULT_CAPACITY;

/**
 * Contains the default implementation of most CtElement methods.
 *
 * Implements Comparable for being used in TreeSet
 */
public abstract class CtElementImpl implements CtElement, Serializable, Comparable<CtElement> {
	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER = Logger.getLogger(CtElementImpl.class);
	public static final String ERROR_MESSAGE_TO_STRING = "Error in printing the node. One parent isn't initialized!";

	// we don't use Collections.unmodifiableList and Collections.unmodifiableSet
	// because we need clear() for all set* methods
	// and UnmodifiableList and unmodifiableCollection are not overridable (not visible grrrr)
	private static class UnModifiableCollection extends ArrayList<Object> implements Set<Object> {
		private static final long serialVersionUID = 1L;

		@Override
		public Object set(int index, Object element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, Object element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends Object> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(java.lang.Object e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
	}

	private static final Set<Object> EMPTY_SET = new UnModifiableCollection();
	private static final Set<Object> EMPTY_LIST = new UnModifiableCollection();

	@SuppressWarnings("unchecked")
	public static <T> List<T> emptyList() {
		return (List<T>) EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<T> emptySet() {
		return (Set<T>) EMPTY_SET;
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> emptyCollection() {
		return (Collection<T>) EMPTY_LIST;
	}

	public String getSignature() {
		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature();
	}

	transient Factory factory;

	protected CtElement parent;

	List<CtAnnotation<? extends Annotation>> annotations = emptyList();

	String docComment;

	SourcePosition position;

	public CtElementImpl() {
		super();
	}

	public int compareTo(CtElement o) {
		String current = getSignature();
		String other = o.getSignature();
		if (current.length() <= 0 || other.length() <= 0) {
			throw new ClassCastException("Unable to compare elements");
		}
		return current.compareTo(other);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CtElement)) {
			return false;
		}
		String current = getSignature();
		String other = ((CtElement) o).getSignature();
		return current.equals(other);
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().toString().equals(annotationType.getName().replace('$', '.'))) {
				return ((CtAnnotation<A>) a).getActualAnnotation();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> CtAnnotation<A> getAnnotation(CtTypeReference<A> annotationType) {
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().equals(annotationType)) {
				return (CtAnnotation<A>) a;
			}
		}
		return null;
	}

	public List<CtAnnotation<? extends Annotation>> getAnnotations() {
		return Collections.unmodifiableList(annotations);
	}

	public String getDocComment() {
		return docComment;
	}

	public SourcePosition getPosition() {
		if (position != null) {
			return position;
		}
		return null;
	}

	@Override
	public int hashCode() {
		return getSignature().hashCode();
	}

	public <E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotations) {
		this.annotations.clear();
		for (CtAnnotation<? extends Annotation> annot : annotations) {
			addAnnotation(annot);
		}
		return (E) this;
	}

	@Override
	public void delete() {
		replace(null);
	}

	public <E extends CtElement> E addAnnotation(CtAnnotation<? extends Annotation> annotation) {
		if ((List<?>) this.annotations == (List<?>) emptyList()) {
			this.annotations = new ArrayList<CtAnnotation<? extends Annotation>>(ANNOTATIONS_CONTAINER_DEFAULT_CAPACITY);
		}
		annotation.setParent(this);
		this.annotations.add(annotation);
		return (E) this;
	}

	public boolean removeAnnotation(CtAnnotation<? extends Annotation> annotation) {
		return (List<?>) annotations != (List<?>) emptyList() && this.annotations.remove(annotation);
	}

	public <E extends CtElement> E setDocComment(String docComment) {
		this.docComment = docComment;
		return (E) this;
	}

	public <E extends CtElement> E setPosition(SourcePosition position) {
		this.position = position;
		return (E) this;
	}

	public <E extends CtElement> E setPositions(final SourcePosition position) {
		accept(new CtScanner() {
			@Override
			public void enter(CtElement e) {
				e.setPosition(position);
			}
		});
		return (E) this;
	}

	@Override
	public String toString() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(getFactory().getEnvironment());
		String errorMessage = "";
		try {
			printer.computeImports(this);
			printer.scan(this);
		} catch (ParentNotInitializedException ignore) {
			errorMessage = ERROR_MESSAGE_TO_STRING;
		}
		return printer.toString() + errorMessage;
	}

	@SuppressWarnings("unchecked")
	public <E extends CtElement> List<E> getAnnotatedChildren(Class<? extends Annotation> annotationType) {
		return (List<E>) Query.getElements(this, new AnnotationFilter<CtElement>(CtElement.class, annotationType));
	}

	boolean implicit = false;

	public boolean isImplicit() {
		return implicit;
	}

	public <E extends CtElement> E setImplicit(boolean implicit) {
		this.implicit = implicit;
		return (E) this;
	}

	public Set<CtTypeReference<?>> getReferencedTypes() {
		TypeReferenceScanner s = new TypeReferenceScanner();
		s.scan(this);
		return s.getReferences();
	}

	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return Query.getElements(this, filter);
	}

	public <T extends CtReference> List<T> getReferences(ReferenceFilter<T> filter) {
		return Query.getReferences(this, filter);
	}

	@Override
	public CtElement getParent() throws ParentNotInitializedException {
		if (parent == null) {
			String exceptionMsg = "";
			if (this instanceof CtReference) {
				exceptionMsg = "parent not initialized for " + ((CtReference) this).getSimpleName() + "(" + this.getClass() + ")";
			} else {
				SourcePosition pos = getPosition();
				if (this instanceof CtNamedElement) {
					exceptionMsg = ("parent not initialized for " + ((CtNamedElement) this).getSimpleName() + "(" + this.getClass() + ")" + (pos != null ? " " + pos : " (?)"));
				} else {
					exceptionMsg = ("parent not initialized for " + this.getClass() + (pos != null ? " " + pos : " (?)"));
				}
			}
			throw new ParentNotInitializedException(exceptionMsg);
		}
		return parent;
	}

	@Override
	public <E extends CtElement> E setParent(E parent) {
		this.parent = parent;
		return (E) this;
	}

	@Override
	public boolean isParentInitialized() {
		return parent != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends CtElement> P getParent(Class<P> parentType) throws ParentNotInitializedException {
		if (getParent() == null) {
			return null;
		}
		if (parentType.isAssignableFrom(getParent().getClass())) {
			return (P) getParent();
		}
		return getParent().getParent(parentType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E getParent(Filter<E> filter) throws ParentNotInitializedException {
		E current = (E) this;
		while (true) {
			try {
				while (current != null && !filter.matches(current)) {
					current = (E) current.getParent();
				}
				break;
			} catch (ClassCastException e) {
				// expected, some elements are not of type
				current = (E) current.getParent();
			}
		}

		if (current != null && filter.matches(current)) {
			return current;
		}
		return null;
	}

	@Override
	public boolean hasParent(CtElement candidate) throws ParentNotInitializedException {
		return getParent() == candidate || getParent().hasParent(candidate);
	}

	@Override
	public void updateAllParentsBelow() {
		new ModelConsistencyChecker(getFactory().getEnvironment(), true, true).scan(this);
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
		LOGGER.setLevel(factory.getEnvironment().getLevel());
	}

	@Override
	public void replace(CtElement element) {
		try {
			replaceIn(this, element, getParent());
		} catch (CtUncomparableException e1) {
			// do nothing
		} catch (Exception e1) {
			Launcher.LOGGER.error(e1.getMessage(), e1);
		}
	}

	private <T extends FactoryAccessor> void replaceIn(Object toReplace, T replacement, Object parent) throws IllegalArgumentException, IllegalAccessException {

		for (Field f : RtHelper.getAllFields(parent.getClass())) {
			f.setAccessible(true);
			Object tmp = f.get(parent);

			if (tmp != null) {
				if (tmp instanceof List) {
					@SuppressWarnings("unchecked") List<T> lst = (List<T>) tmp;
					for (int i = 0; i < lst.size(); i++) {
						if (lst.get(i) != null && compare(lst.get(i), toReplace)) {
							lst.remove(i);
							if (replacement != null) {
								lst.add(i, getReplacement(replacement, parent));
							}
						}
					}
				} else if (tmp instanceof Collection) {
					@SuppressWarnings("unchecked") Collection<T> collect = (Collection<T>) tmp;
					Object[] array = collect.toArray();
					for (Object obj : array) {
						if (compare(obj, toReplace)) {
							collect.remove(obj);
							collect.add(getReplacement(replacement, parent));
						}
					}
				} else if (compare(tmp, toReplace)) {
					f.set(parent, getReplacement(replacement, parent));
				}
			}
		}
	}

	private <T extends FactoryAccessor> T getReplacement(T replacement, Object parent) {
		// T ret = replacement.getFactory().Core().clone(replacement);
		if (replacement instanceof CtElement && parent instanceof CtElement) {
			((CtElement) replacement).setParent((CtElement) parent);
		}
		return replacement;
	}

	private boolean compare(Object o1, Object o2) {
		return o1 == o2;
	}
}
