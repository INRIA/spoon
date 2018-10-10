/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import spoon.reflect.ModelElementContainerDefaultCapacities;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtElementPathBuilder;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtIterator;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.DerivedProperty;
import spoon.support.StandardEnvironment;
import spoon.support.sniper.internal.ElementSourceFragment;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains the default implementation of most CtElement methods.
 *
 */
public abstract class CtElementImpl implements CtElement, Serializable {
	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER = Logger.getLogger(CtElementImpl.class);
	public static final String ERROR_MESSAGE_TO_STRING = "Error in printing the node. One parent isn't initialized!";
	private static final Factory DEFAULT_FACTORY = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());


	public static <T> List<T> emptyList() {
		return EmptyClearableList.instance();
	}

	public static <T> Set<T> emptySet() {
		return EmptyClearableSet.instance();
	}

	public static <T> List<T> unmodifiableList(List<T> list) {
		return list.isEmpty() ? Collections.<T>emptyList() : Collections.unmodifiableList(list);
	}

	Factory factory;

	protected CtElement parent;

	@MetamodelPropertyField(role = CtRole.ANNOTATION)
	List<CtAnnotation<? extends Annotation>> annotations = emptyList();

	@MetamodelPropertyField(role = CtRole.COMMENT)
	private List<CtComment> comments = emptyList();

	@MetamodelPropertyField(role = CtRole.POSITION)
	SourcePosition position = SourcePosition.NOPOSITION;

	Map<String, Object> metadata;

	public CtElementImpl() {
	}

	@Override
	public String getShortRepresentation() {
		return super.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CtElementImpl)) {
			return false;
		}
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
	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		CtType annot = getFactory().Annotation().get(annotationType);
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().getQualifiedName().equals(annot.getQualifiedName())) {
				return ((CtAnnotation<A>) a).getActualAnnotation(); // warning, here we do heavy and costly work with proxy
			}
		}
		return null;
	}

	@Override
	public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
		CtType annot = getFactory().Annotation().get(annotationType);
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().getQualifiedName().equals(annot.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Annotation> CtAnnotation<A> getAnnotation(CtTypeReference<A> annotationType) {
		for (CtAnnotation<? extends Annotation> a : getAnnotations()) {
			if (a.getAnnotationType().equals(annotationType)) {
				return (CtAnnotation<A>) a;
			}
		}
		return null;
	}

	@Override
	public List<CtAnnotation<? extends Annotation>> getAnnotations() {
		if (this instanceof CtShadowable) {
			CtShadowable shadowable = (CtShadowable) this;
			if (shadowable.isShadow()) {
				Launcher.LOGGER.debug("Some annotations might be unreachable from the shadow element: " + this.getShortRepresentation());
			}
		}
		return unmodifiableList(annotations);
	}

	@Override
	public String getDocComment() {
		for (CtComment ctComment : comments) {
			if (ctComment.getCommentType() == CtComment.CommentType.JAVADOC) {
				StringBuilder result = new StringBuilder();
				result.append(ctComment.getContent() + System.lineSeparator());
				for (CtJavaDocTag tag: ((CtJavaDoc) ctComment).getTags()) {
					result.append(tag.toString()); // the tag already contains a new line
				}
				return result.toString();
			}
		}
		return "";
	}

	@Override
	public SourcePosition getPosition() {
		if (position != null) {
			return position;
		}
		return SourcePosition.NOPOSITION;
	}

	@Override
	public int hashCode() {
		HashcodeVisitor pr = new HashcodeVisitor();
		pr.scan(this);
		return pr.getHasCode();
	}

	@Override
	public <E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotations) {
		if (annotations == null || annotations.isEmpty()) {
			this.annotations = emptyList();
			return (E) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.ANNOTATION, this.annotations, new ArrayList<>(this.annotations));
		this.annotations.clear();
		for (CtAnnotation<? extends Annotation> annot : annotations) {
			addAnnotation(annot);
		}
		return (E) this;
	}

	@Override
	public void delete() {
		//delete is implemented as replace by no element (empty list of elements)
		replace(Collections.<CtElement>emptyList());
	}

	@Override
	public <E extends CtElement> E addAnnotation(CtAnnotation<? extends Annotation> annotation) {
		if (annotation == null) {
			return (E) this;
		}
		if (this.annotations == CtElementImpl.<CtAnnotation<? extends Annotation>>emptyList()) {
			this.annotations = new ArrayList<>(ModelElementContainerDefaultCapacities.ANNOTATIONS_CONTAINER_DEFAULT_CAPACITY);
		}
		annotation.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.ANNOTATION, this.annotations, annotation);
		this.annotations.add(annotation);
		return (E) this;
	}

	@Override
	public boolean removeAnnotation(CtAnnotation<? extends Annotation> annotation) {
		if (this.annotations == CtElementImpl.<CtAnnotation<? extends Annotation>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.ANNOTATION, annotations, annotations.indexOf(annotation), annotation);
		return this.annotations.remove(annotation);
	}

	@Override
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

	@Override
	public <E extends CtElement> E setPosition(SourcePosition position) {
		if (position == null) {
			position = SourcePosition.NOPOSITION;
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.POSITION, position, this.position);
		this.position = position;
		return (E) this;
	}

	@Override
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
			// we do not want to compute imports of for CtImport and CtReference
			// as it may change the print of a reference
			if (!(this instanceof CtImport) && !(this instanceof CtReference)) {
				printer.getImportsContext().computeImports(this);
			}
			printer.scan(this);
		} catch (ParentNotInitializedException ignore) {
			LOGGER.error(ERROR_MESSAGE_TO_STRING, ignore);
			errorMessage = ERROR_MESSAGE_TO_STRING;
		}
		// in line-preservation mode, newlines are added at the beginning to matches the lines
		// removing them from the toString() representation
		return printer.toString().replaceFirst("^\\s+", "") + errorMessage;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> List<E> getAnnotatedChildren(Class<? extends Annotation> annotationType) {
		return (List<E>) Query.getElements(this, new AnnotationFilter<>(CtElement.class, annotationType));
	}

	@MetamodelPropertyField(role = CtRole.IS_IMPLICIT)
	boolean implicit = false;

	@Override
	public boolean isImplicit() {
		return implicit;
	}

	@Override
	public <E extends CtElement> E setImplicit(boolean implicit) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_IMPLICIT, implicit, this.implicit);
		this.implicit = implicit;
		return (E) this;
	}

	@Override
	@DerivedProperty
	public Set<CtTypeReference<?>> getReferencedTypes() {
		TypeReferenceScanner s = new TypeReferenceScanner();
		s.scan(this);
		return s.getReferences();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return filterChildren(filter).list();
	}

	@Override
	public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
		return factory.Query().createQuery(this).map(queryStep);
	}

	@Override
	public <I, R> CtQuery map(CtFunction<I, R> function) {
		return factory.Query().createQuery(this).map(function);
	}

	@Override
	public <P extends CtElement> CtQuery filterChildren(Filter<P> predicate) {
		return factory.Query().createQuery(this).filterChildren(predicate);
	}

	@Override
	public CtElement getParent() throws ParentNotInitializedException {
		if (parent == null) {
			String exceptionMsg;
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
		if (parent == null) {
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
			return this != getFactory().getModel().getUnnamedModule() && (getParent() == candidate || getParent().hasParent(candidate));
		} catch (ParentNotInitializedException e) {
			return false;
		}
	}

	@Override
	public CtRole getRoleInParent() {
		if (isParentInitialized()) {
			EarlyTerminatingScanner<CtRole> ets = new EarlyTerminatingScanner<CtRole>() {
				@Override
				public void scan(CtRole role, CtElement element) {
					if (element == CtElementImpl.this) {
						setResult(role);
						terminate();
					}
					//do not call super.scan, because we do not want scan children
				}
			};
			getParent().accept(ets);
			return ets.getResult();
		}
		return null;
	}

	@Override
	public void updateAllParentsBelow() {
		new ModelConsistencyChecker(getFactory().getEnvironment(), true, true).scan(this);
	}

	@Override
	public Factory getFactory() {
		if (this.factory == null) {
			return DEFAULT_FACTORY;
		}
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
	public <E extends CtElement> void replace(Collection<E> elements) {
		ReplacementVisitor.replace(this, elements);
	}

	@Override
	public <E extends CtElement> E setAllMetadata(Map<String, Object> metadata) {
		if (metadata == null || metadata.isEmpty()) {
			this.metadata = null;
			return (E) this;
		}
		if (this.metadata == null) {
			this.metadata = new HashMap<>();
		} else {
			this.metadata.clear();
		}
		this.metadata.putAll(metadata);
		return (E) this;
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
	public Map<String, Object> getAllMetadata() {
		if (this.metadata == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(this.metadata);
	}

	@Override
	public Set<String> getMetadataKeys() {
		if (metadata == null) {
			return Collections.EMPTY_SET;
		}
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
		if (this.comments == CtElementImpl.<CtComment>emptyList()) {
			comments = new ArrayList<>(ModelElementContainerDefaultCapacities.COMMENT_CONTAINER_DEFAULT_CAPACITY);
		}
		comment.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.COMMENT, this.comments, comment);
		comments.add(comment);
		return (E) this;
	}

	@Override
	public <E extends CtElement> E removeComment(CtComment comment) {
		if (this.comments == CtElementImpl.<CtComment>emptyList()) {
			return (E) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.COMMENT, comments, comments.indexOf(comment), comment);
		this.comments.remove(comment);
		return (E) this;
	}

	@Override
	public <E extends CtElement> E setComments(List<CtComment> comments) {
		if (comments == null || comments.isEmpty()) {
			this.comments = emptyList();
			return (E) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.COMMENT, this.comments, new ArrayList<>(this.comments));
		this.comments.clear();
		for (CtComment comment : comments) {
			addComment(comment);
		}
		return (E) this;
	}

	@Override
	public CtElement clone() {
		return CloneHelper.INSTANCE.clone(this);
	}

	@Override
	public <T> T getValueByRole(CtRole role) {
		RoleHandler rh = RoleHandlerHelper.getRoleHandler(this.getClass(), role);
		return rh.getValue(this);
	}

	@Override
	public <E extends CtElement, T> E setValueByRole(CtRole role, T value) {
		RoleHandler rh = RoleHandlerHelper.getRoleHandler(this.getClass(), role);
		rh.setValue(this, value);
		return (E) this;
	}

	@Override
	public CtPath getPath() {
		return new CtElementPathBuilder().fromElement(this);
	}

	@Override
	public Iterator<CtElement> descendantIterator() {
		return new CtIterator(this);
	}

	@Override
	public Iterable<CtElement> asIterable() {
		return this::descendantIterator;
	}

	@Override
	public ElementSourceFragment getOriginalSourceFragment() {
		SourcePosition sp = this.getPosition();
		CompilationUnit compilationUnit = sp.getCompilationUnit();
		if (compilationUnit != null) {
			ElementSourceFragment rootFragment = compilationUnit.getOriginalSourceFragment();
			return rootFragment.getSourceFragmentOf(this, sp.getSourceStart(), sp.getSourceEnd() + 1);
		} else {
			return ElementSourceFragment.NO_SOURCE_FRAGMENT;
		}
	}
}
