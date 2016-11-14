/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
import spoon.reflect.code.CtComment;
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
import spoon.support.reflect.cu.SourcePositionImpl;
import spoon.support.util.EmptyClearableList;
import spoon.support.util.EmptyClearableSet;
import spoon.support.visitor.HashcodeVisitor;
import spoon.support.visitor.TypeReferenceScanner;
import spoon.support.visitor.equals.CloneHelper;
import spoon.support.visitor.equals.EqualsVisitor;
import spoon.support.visitor.replace.ReplacementVisitor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.ANNOTATIONS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.ModelElementContainerDefaultCapacities.COMMENT_CONTAINER_DEFAULT_CAPACITY;

/**
 * Contains the default implementation of most CtElement methods.
 *
 */
public abstract class CtElementImpl implements CtElement, Serializable {
	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER = Logger.getLogger(CtElementImpl.class);
	public static final String ERROR_MESSAGE_TO_STRING = "Error in printing the node. One parent isn't initialized!";

	public static <T> List<T> emptyList() {
		return EmptyClearableList.instance();
	}

	public static <T> Set<T> emptySet() {
		return EmptyClearableSet.instance();
	}

	public static <T> List<T> unmodifiableList(List<T> list) {
		return list.isEmpty() ? Collections.<T>emptyList() : Collections.unmodifiableList(list);
	}

	transient Factory factory;

	protected CtElement parent;

	List<CtAnnotation<? extends Annotation>> annotations = emptyList();

	private List<CtComment> comments = emptyList();

	public final SourcePosition DEFAULT_POSITION = new SourcePositionImpl(null, -1, -1, -1, null);

	SourcePosition position = DEFAULT_POSITION;

	Map<String, Object> metadata;

	public CtElementImpl() {
		super();
	}


	@Override
	public String getShortRepresentation() {
		return super.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		boolean ret = EqualsVisitor.equals(this, (CtElement) o);
		// neat online testing of core Java contract
		if (ret && !factory.getEnvironment().checksAreSkipped() && this.hashCode() != o.hashCode()) {
			throw new IllegalStateException("violation of equal/hashcode contract between \n" + this.toString() + "\nand\n" + o.toString() + "\n");
		}
		return ret;
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
		return unmodifiableList(annotations);
	}

	public String getDocComment() {
		for (CtComment ctComment : comments) {
			if (ctComment.getCommentType() == CtComment.CommentType.JAVADOC) {
				return ctComment.getContent();
			}
		}
		return null;
	}

	public SourcePosition getPosition() {
		if (position != null) {
			return position;
		}
		return null;
	}

	@Override
	public int hashCode() {
		HashcodeVisitor pr = new HashcodeVisitor();
		pr.scan(this);
		return pr.getHasCode();
	}

	public <E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotations) {
		if (annotations == null || annotations.isEmpty()) {
			this.annotations = CtElementImpl.emptyList();
			return (E) this;
		}
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
		if (annotation == null) {
			return (E) this;
		}
		if ((List<?>) this.annotations == (List<?>) emptyList()) {
			this.annotations = new ArrayList<>(ANNOTATIONS_CONTAINER_DEFAULT_CAPACITY);
		}
		annotation.setParent(this);
		this.annotations.add(annotation);
		return (E) this;
	}

	public boolean removeAnnotation(CtAnnotation<? extends Annotation> annotation) {
		return (List<?>) annotations != (List<?>) emptyList() && this.annotations.remove(annotation);
	}

	public <E extends CtElement> E setDocComment(String docComment) {
		for (CtComment ctComment : comments) {
			if (ctComment.getCommentType() == CtComment.CommentType.JAVADOC) {
				ctComment.setContent(docComment);
				return (E) this;
			}
		}
		this.addComment(factory.Code().createComment(docComment, CtComment.CommentType.JAVADOC));
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
			LOGGER.error(ERROR_MESSAGE_TO_STRING, ignore);
			errorMessage = ERROR_MESSAGE_TO_STRING;
		}
		return printer.toString() + errorMessage;
	}

	@SuppressWarnings("unchecked")
	public <E extends CtElement> List<E> getAnnotatedChildren(Class<? extends Annotation> annotationType) {
		return (List<E>) Query.getElements(this, new AnnotationFilter<>(CtElement.class, annotationType));
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
		E current = (E) getParent();
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
	public boolean hasParent(CtElement candidate) {
		try {
			return this != getFactory().getModel().getRootPackage() && (getParent() == candidate || getParent().hasParent(candidate));
		} catch (ParentNotInitializedException e) {
			return false;
		}
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
		ReplacementVisitor.replace(this, element);
	}

	@Override
	public <E extends CtElement> E putMetadata(String key, Object val) {
		if (metadata == null) {
			metadata = new HashMap<>();
		}
		metadata.put(key, val);
		return (E) this;
	}

	@Override
	public Object getMetadata(String key) {
		if (metadata == null) {
			return null;
		}
		return metadata.get(key);
	}

	@Override
	public Set<String> getMetadataKeys() {
		return metadata.keySet();
	}

	@Override
	public List<CtComment> getComments() {
		return unmodifiableList(comments);
	}

	@Override
	public <E extends CtElement> E addComment(CtComment comment) {
		if (comment == null) {
			return (E) this;
		}
		if ((List<?>) comments == emptyList()) {
			comments = new ArrayList<>(COMMENT_CONTAINER_DEFAULT_CAPACITY);
		}
		comments.add(comment);
		comment.setParent(this);
		return (E) this;
	}

	// TODO return boolean
	@Override
	public <E extends CtElement> E removeComment(CtComment comment) {
		if ((List<?>) comments != emptyList()) {
			comments.remove(comment);
		}
		return (E) this;
	}

	@Override
	public <E extends CtElement> E setComments(List<CtComment> comments) {
		if (comments == null || comments.isEmpty()) {
			this.comments = CtElementImpl.emptyList();
			return (E) this;
		}
		this.comments.clear();
		for (CtComment comment : comments) {
			addComment(comment);
		}
		return (E) this;
	}

	@Override
	public CtElement clone() {
		return CloneHelper.clone(this);
	}
}
