package spoon.support.reflect.declaration;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.support.sniper.internal.ElementSourceFragment;

/**
 * The null object implementation for {@link spoon.reflect.declaration.CtPackage}.
 */
public class NoPackage implements CtPackage {
	private static final long serialVersionUID = 1L;
	private String NO_PACKAGE_NAME = "<no package>";

	@Override
	public void accept(CtVisitor visitor) {
		// do nothing
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtPackage> T addPackage(CtPackage pack) {
		return (T) this;
	}

	@Override
	public boolean removePackage(CtPackage pack) {
		return false;
	}

	@Override
	public CtModule getDeclaringModule() {
		return null;
	}

	@Override
	public CtPackage getDeclaringPackage() {
		return this;
	}

	@Override
	public CtPackage getPackage(String name) {
		return this;
	}

	@Override
	public Set<CtPackage> getPackages() {
		return Collections.singleton(this);
	}

	@Override
	public String getQualifiedName() {
		return getSimpleName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtType<?>> T getType(String simpleName) {
		return (T) this;
	}

	@Override
	public Set<CtType<?>> getTypes() {
		return Collections.emptySet();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtPackage> T setPackages(Set<CtPackage> pack) {
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtPackage> T setTypes(Set<CtType<?>> types) {
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtPackage> T addType(CtType<?> type) {
		return (T) this;
	}

	@Override
	public void removeType(CtType<?> type) {
		// do nothing
	}

	@Override
	public CtPackageReference getReference() {
		return null;
	}

	@Override
	public String getSimpleName() {
		return NO_PACKAGE_NAME;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtNamedElement> T setSimpleName(String simpleName) {
		return (T) this;
	}

	@Override
	public String getShortRepresentation() {
		return super.toString();
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}

	@Override
	public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
		return false;
	}

	@Override
	public <A extends Annotation> CtAnnotation<A> getAnnotation(CtTypeReference<A> annotationType) {
		return null;
	}

	@Override
	public List<CtAnnotation<? extends Annotation>> getAnnotations() {
		return Collections.emptyList();
	}

	@Override
	public String getDocComment() {
		return "";
	}

	@Override
	public SourcePosition getPosition() {
		return SourcePosition.NOPOSITION;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotation) {
		return (E) this;
	}

	@Override
	public void delete() {
		// do nothing
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E addAnnotation(CtAnnotation<? extends Annotation> annotation) {
		return (E) this;
	}

	@Override
	public boolean removeAnnotation(CtAnnotation<? extends Annotation> annotation) {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setDocComment(String docComment) {
		return (E) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setPosition(SourcePosition position) {
		return (E) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setPositions(SourcePosition position) {
		return (E) this;
	}

	@Override
	public String prettyprint() {
		return "";
	}

	@Override
	public <E extends CtElement> List<E> getAnnotatedChildren(Class<? extends Annotation> annotationType) {
		return Collections.emptyList();
	}

	@Override
	public boolean isImplicit() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setImplicit(boolean b) {
		return (E) this;
	}

	@Override
	public Set<CtTypeReference<?>> getReferencedTypes() {
		return Collections.emptySet();
	}

	@Override
	public <E extends CtElement> List<E> getElements(Filter<E> filter) {
		return Collections.emptyList();
	}

	@Override
	public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
		return null;
	}

	@Override
	public <I, R> CtQuery map(CtFunction<I, R> function) {
		return null;
	}

	@Override
	public <R extends CtElement> CtQuery filterChildren(Filter<R> filter) {
		return null;
	}

	@Override
	public CtElement getParent() throws ParentNotInitializedException {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setParent(CtElement parent) {
		return (E) this;
	}

	@Override
	public boolean isParentInitialized() {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends CtElement> P getParent(Class<P> parentType) {
		return (P) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E getParent(Filter<E> filter) {
		return (E) this;
	}

	@Override
	public boolean hasParent(CtElement candidate) {
		return false;
	}

	@Override
	public CtRole getRoleInParent() {
		return null;
	}

	@Override
	public void updateAllParentsBelow() {
		// do nothing
	}

	@Override
	public Factory getFactory() {
		return null;
	}

	@Override
	public void setFactory(Factory factory) {
		// do nothing
	}

	@Override
	public void replace(CtElement element) {
		// do nothing
	}

	@Override
	public <E extends CtElement> void replace(Collection<E> elements) {
		// do nothing
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setAllMetadata(Map<String, Object> metadata) {
		return (E) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E putMetadata(String key, Object val) {
		return (E) this;
	}

	@Override
	public Object getMetadata(String key) {
		return null;
	}

	@Override
	public Map<String, Object> getAllMetadata() {
		return Collections.emptyMap();
	}

	@Override
	public Set<String> getMetadataKeys() {
		return Collections.emptySet();
	}

	@Override
	public List<CtComment> getComments() {
		return Collections.emptyList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E addComment(CtComment comment) {
		return (E) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E removeComment(CtComment comment) {
		return (E) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement> E setComments(List<CtComment> comments) {
		return (E) this;
	}

	@Override
	public <T> T getValueByRole(CtRole role) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtElement, T> E setValueByRole(CtRole role, T value) {
		return (E) this;
	}

	@Override
	public CtPath getPath() {
		return null;
	}

	@Override
	public Iterator<CtElement> descendantIterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Iterable<CtElement> asIterable() {
		return this::descendantIterator;
	}

	@Override
	public List<CtElement> getDirectChildren() {
		return Collections.emptyList();
	}

	@Override
	public ElementSourceFragment getOriginalSourceFragment() {
		return ElementSourceFragment.NO_SOURCE_FRAGMENT;
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

	@Override
	public String toStringDebug() {
		return toString();
	}

	@Override
	public boolean isShadow() {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		return (E) this;
	}

	@Override
	public CtPackage clone() {
		throw new UnsupportedOperationException("can't clone NoPackage");
	}

	@Override
	public boolean isUnnamedPackage() {
		return false;
	}

	@Override
	public boolean hasPackageInfo() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean hasTypes() {
		return false;
	}

	@Override
	public boolean hasPackages() {
		return false;
	}
}
