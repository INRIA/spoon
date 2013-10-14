/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.processing.FactoryAccessor;
import spoon.reflect.Factory;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.ReferenceFilter;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.support.util.RtHelper;
import spoon.support.visitor.SignaturePrinter;
import spoon.support.visitor.TypeReferenceScanner;

/**
 * The implementation for {@link spoon.test.spoon.reflect.Declaration}.
 * 
 * @author Renaud Pawlak
 * @see spoon.test.spoon.reflect.Declaration
 */
public abstract class CtElementImpl implements CtElement, Serializable {

	transient Factory factory;

	public String getSignature() {
		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature();
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	Set<CtAnnotation<? extends Annotation>> annotations = new TreeSet<CtAnnotation<? extends Annotation>>();

	String docComment;

	CtElement parent;

	SourcePosition position;

	public CtElementImpl() {
		super();
	}

	public int compareTo(CtElement o) {
		String current = getSignature();
		String other = o.getSignature();
		if (current.length() <= 0 || other.length() <= 0)
			throw new ClassCastException("Unable to compare elements");
		return current.compareTo(other);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CtElement))
			return false;
		String current = getSignature();
		String other = ((CtElement) o).getSignature();
		if (current.length() <= 0 || other.length() <= 0)
			throw new CtUncomparableException("Unable to compare elements");
		return current.equals(other);
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().toString()
					.equals(annotationType.getName())) {
				return ((CtAnnotation<A>) a).getActualAnnotation();
			}
		}
		return null;
	}

	// TODO remove the warning
	@SuppressWarnings("unchecked")
	public <A extends Annotation> CtAnnotation<A> getAnnotation(
			CtTypeReference<A> annotationType) {
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().equals(annotationType)) {
				return (CtAnnotation<A>) a;
			}
		}
		return null;
	}

	public Set<CtAnnotation<? extends Annotation>> getAnnotations() {
		return annotations;
	}

	public String getDocComment() {
		return docComment;
	}

	public CtElement getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	public <P extends CtElement> P getParent(Class<P> parentType) {
		if (getParent() == null)
			return null;
		if (parentType.isAssignableFrom(getParent().getClass()))
			return (P) getParent();
		return getParent().getParent(parentType);
	}

	public boolean hasParent(CtElement candidate) {
		if (getParent() == null)
			return false;
		if (getParent() == candidate)
			return true;
		return getParent().hasParent(candidate);
	}

	public SourcePosition getPosition() {
		if (position!=null) {
		  return position;
		}
		if (getParent()!=null) {
			return getParent().getPosition();
		}
		return null;
	}

	@Override
	public int hashCode() {
		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature().hashCode();
	}

	public void replace(CtElement element) {
//		ElementReplacer<CtElement> translator = new ElementReplacer<CtElement>(
//				this, element);	
//		getParent().accept(translator);
		try {
			replaceIn(this, element, getParent());
		} catch (CtUncomparableException e1) {
			// do nothing
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	private <T extends FactoryAccessor> void replaceIn(Object toReplace,T replacement, Object parent) throws IllegalArgumentException, IllegalAccessException {

		for (Field f : RtHelper.getAllFields(parent.getClass())) {
			f.setAccessible(true);
			Object tmp = f.get(parent);

			if (tmp != null) {
				if (tmp instanceof List) {
					List<T> lst = (List<T>) tmp;

					for (int i = 0; i < lst.size(); i++) {
						if (lst.get(i) != null && compare(lst.get(i), toReplace)) {
							lst.remove(i);
							if (replacement != null)
								lst.add(i, getReplacement(replacement, parent));
						}
					}
				} else if (tmp instanceof Collection) {
					Collection<T> collect = (Collection<T>) tmp;
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
	
	public void replace(Filter<? extends CtElement> replacementPoints,
			CtElement element) {
		List<? extends CtElement> l = Query
				.getElements(this, replacementPoints);
		for (CtElement e : l) {
			e.replace(element);
		}
	}

	public void setAnnotations(
			Set<CtAnnotation<? extends Annotation>> annotations) {
		this.annotations = annotations;
	}

	public boolean addAnnotation(CtAnnotation<? extends Annotation> annotation) {
		return this.annotations.add(annotation);
	}
	
	public boolean removeAnnotation(
			CtAnnotation<? extends Annotation> annotation) {
		return this.annotations.remove(annotation);
	}
	
	public void setDocComment(String docComment) {
		this.docComment = docComment;
	}

	public void setParent(CtElement parentElement) {
		this.parent = parentElement;
	}

	public void setPosition(SourcePosition position) {
		this.position = position;
	}

	public void setPositions(final SourcePosition position) {
		accept(new CtScanner() {
			@Override
			public void enter(CtElement e) {
				e.setPosition(position);
			}
		});
	}

	@Override
	public String toString() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(
				getFactory().getEnvironment());
		printer.scan(this);
		return printer.toString();
	}

	@SuppressWarnings("unchecked")
	public <E extends CtElement> List<E> getAnnotatedChildren(
			Class<? extends Annotation> annotationType) {
		return Query.getElements(this, new AnnotationFilter(CtElement.class,
				annotationType));
	}

	boolean implicit = false;

	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}

	public Set<CtTypeReference<?>> getReferencedTypes() {
		TypeReferenceScanner s = new TypeReferenceScanner();
		s.scan(this);
		return s.getReferences();
	}

	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return Query.getElements(this, filter);
	}

	public <T extends CtReference> List<T> getReferences(
			ReferenceFilter<T> filter) {
		return Query.getReferences(this, filter);
	}

}