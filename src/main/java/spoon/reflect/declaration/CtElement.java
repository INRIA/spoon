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

package spoon.reflect.declaration;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import spoon.processing.FactoryAccessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.ReferenceFilter;
import spoon.reflect.visitor.Root;

/**
 * This interface is the root interface for the metamodel elements (any program
 * element).
 */
@Root
public interface CtElement extends FactoryAccessor, Comparable<CtElement> {
	/**
	 * Accepts a visitor.
	 * 
	 * @param visitor
	 *            to accept
	 */
	void accept(CtVisitor visitor);

	/**
	 * Searches for an annotation (proxy) of the given class that annotates the
	 * current element.
	 * 
	 * <p>
	 * NOTE: before using an annotation proxy, you have to make sure that all
	 * the types referenced by the annotation have been compiled and are in the
	 * classpath so that accessed values can be converted into the actual types.
	 * Otherwise, use {@link #getAnnotation(CtTypeReference)}.
	 * 
	 * @param <A>
	 *            the annotation's type
	 * @param annotationType
	 *            the annotation's class
	 * @return if found, returns a proxy for this annotation
	 */
	<A extends Annotation> A getAnnotation(Class<A> annotationType);

	/**
	 * Gets the annotation element for a given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return the annotation if this element is annotated by one annotation of
	 *         the given type
	 */
	<A extends Annotation> CtAnnotation<A> getAnnotation(
			CtTypeReference<A> annotationType);

	/**
	 * Returns the annotations that are present on this element.
	 */
	List<CtAnnotation<? extends Annotation>> getAnnotations();

	/**
	 * Returns the text of the documentation ("javadoc") comment of this
	 * element.
	 */
	String getDocComment();

	/**
	 * Gets the parent of current element, which can be null. Elements that are
	 * manually added to the tree may have a null parent if not manually set.
	 * Note that the parents of an entire tree of elements can be automatically
	 * set by using the {@link #updateAllParentsBelow()}.
	 * 
	 * @throws ParentNotInitializedException
	 *             when the parent of this element is not initialized
	 */
	CtElement getParent() throws ParentNotInitializedException;

	/**
	 * Tells if this parent has been initialized.
	 */
	boolean isParentInitialized();

	/**
	 * Tells if this element is a root of the element tree (and thus has no
	 * parent, which is different from being not initialized). When an element
	 * is a root element, {@link #getParent()} will return null without throwing
	 * an exception.
	 */
	boolean isRootElement();

	/**
	 * Sets the root element flag to this element.
	 */
	void setRootElement(boolean rootElement);

	/**
	 * Gets the signature of the element.
	 */
	String getSignature();

	/**
	 * Gets the first parent that matches the given type.
	 */
	<P extends CtElement> P getParent(Class<P> parentType)
			throws ParentNotInitializedException;

	/**
	 * Tells if the given element is a direct or indirect parent.
	 */
	boolean hasParent(CtElement candidate) throws ParentNotInitializedException;

	/**
	 * Gets the position of this element in input source files
	 * 
	 * @return Source file and line number of this element or null
	 */
	SourcePosition getPosition();

	/**
	 * Replaces this element by another one.
	 */
	void replace(CtElement element) throws ParentNotInitializedException;

	/**
	 * Add an annotation for this element
	 * 
	 * @param annotation
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean addAnnotation(CtAnnotation<? extends Annotation> annotation);

	/**
	 * Remove an anntation for this element
	 * 
	 * @param annotation
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeAnnotation(CtAnnotation<? extends Annotation> annotation);

	/**
	 * Sets the text of the documentation ("javadoc") comment of this
	 * declaration.
	 */
	void setDocComment(String docComment);

	/**
	 * Manually sets the parent element of the current element. Note that the
	 * parents of an entire tree of elements can be automatically set by using
	 * the {@link #updateAllParentsBelow()}.
	 * 
	 * @param element
	 *            parent
	 */
	void setParent(CtElement element);

	/**
	 * Calculates and sets all the parents below this element. This function can
	 * be called to check and fix parents after manipulating the model.
	 */
	void updateAllParentsBelow();

	/**
	 * Sets the position in the Java source file. Note that this information is
	 * used to feed the line numbers in the generated bytecode if any (which is
	 * useful for debugging).
	 * 
	 * @param position
	 *            of this element in the input source files
	 */
	void setPosition(SourcePosition position);

	/**
	 * Gets the child elements annotated with the given annotation type's
	 * instances.
	 * 
	 * @param <E>
	 *            the element's type
	 * @param annotationType
	 *            the annotation type
	 * @return all the child elements annotated with an instance of the given
	 *         annotation type
	 */
	<E extends CtElement> List<E> getAnnotatedChildren(
			Class<? extends Annotation> annotationType);

	/**
	 * Returns true if this element is implicit and automatically added by the
	 * Java compiler.
	 */
	boolean isImplicit();

	/**
	 * Sets this element to be implicit (will not be printed).
	 */
	void setImplicit(boolean b);

	/**
	 * Calculates and returns the set of all the types referenced by this
	 * element (and sub-elements in the AST).
	 */
	Set<CtTypeReference<?>> getReferencedTypes();

	/**
	 * @param filter
	 * @return
	 */
	<E extends CtElement> List<E> getElements(Filter<E> filter);

	/**
	 * @param filter
	 * @return
	 */
	<T extends CtReference> List<T> getReferences(ReferenceFilter<T> filter);

	/**
	 * Sets the position of this element and all its children element. Note that
	 * this information is used to feed the line numbers in the generated
	 * bytecode if any (which is useful for debugging).
	 * 
	 * @param position
	 *            of this element and all children in the input source file
	 */
	void setPositions(SourcePosition position);

	/**
	 * Sets the annotations for this element.
	 */
	void setAnnotations(List<CtAnnotation<? extends Annotation>> annotation);

}