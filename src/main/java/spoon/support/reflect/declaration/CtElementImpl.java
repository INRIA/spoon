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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.Factory;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.support.visitor.ElementReplacer;
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

	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().toString().equals(
					annotationType.getName())) {
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
		return position;
	}

	@Override
	public int hashCode() {
		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature().hashCode();
	}

	public void replace(CtElement element) {
		ElementReplacer<CtElement> translator = new ElementReplacer<CtElement>(
				this, element);
		getParent().accept(translator);
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
		TypeReferenceScanner s=new TypeReferenceScanner();
		s.scan(this);
		return s.getReferences();
	}
	
}