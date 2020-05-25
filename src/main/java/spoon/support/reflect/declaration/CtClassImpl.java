/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.SpoonException;
import spoon.SpoonModelBuilder.InputType;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.support.util.SignatureBasedSortedSet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.reflect.path.CtRole.CONSTRUCTOR;
import static spoon.reflect.path.CtRole.ANNONYMOUS_EXECUTABLE;
import static spoon.reflect.path.CtRole.SUPER_TYPE;

/**
 * The implementation for {@link spoon.reflect.declaration.CtClass}.
 *
 * @author Renaud Pawlak
 */
public class CtClassImpl<T> extends CtTypeImpl<T> implements CtClass<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = SUPER_TYPE)
	CtTypeReference<?> superClass;

	@Override
	public void accept(CtVisitor v) {
		v.visitCtClass(this);
	}

	@Override
	public List<CtAnonymousExecutable> getAnonymousExecutables() {
		List<CtAnonymousExecutable> anonymousExecutables = new ArrayList<>();
		for (CtTypeMember typeMember : typeMembers) {
			if (typeMember instanceof CtAnonymousExecutable) {
				anonymousExecutables.add((CtAnonymousExecutable) typeMember);
			}
		}
		return Collections.unmodifiableList(anonymousExecutables);
	}

	@Override
	public CtConstructor<T> getConstructor(CtTypeReference<?>... parameterTypes) {
		for (CtTypeMember typeMember : getTypeMembers()) {
			if (!(typeMember instanceof CtConstructor)) {
				continue;
			}
			CtConstructor<T> c = (CtConstructor<T>) typeMember;
			if (hasSameParameters(c, parameterTypes)) {
				return c;
			}
		}
		return null;
	}

	@Override
	public Set<CtConstructor<T>> getConstructors() {
		Set<CtConstructor<T>> constructors = new SignatureBasedSortedSet<>();
		for (CtTypeMember typeMember : typeMembers) {
			if (typeMember instanceof CtConstructor) {
				constructors.add((CtConstructor<T>) typeMember);
			}
		}
		return Collections.unmodifiableSet(constructors);
	}

	@Override
	public <C extends CtClass<T>> C addAnonymousExecutable(CtAnonymousExecutable e) {
		if (e == null) {
			return (C) this;
		}
		e.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, ANNONYMOUS_EXECUTABLE, typeMembers, e);
		return addTypeMember(e);
	}

	@Override
	public boolean removeAnonymousExecutable(CtAnonymousExecutable e) {
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, ANNONYMOUS_EXECUTABLE, typeMembers, typeMembers.indexOf(e), e);
		return removeTypeMember(e);
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		return superClass;
	}

	@Override
	public <C extends CtClass<T>> C setAnonymousExecutables(List<CtAnonymousExecutable> anonymousExecutables) {
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, ANNONYMOUS_EXECUTABLE, typeMembers, new ArrayList<>(getAnonymousExecutables()));
		if (anonymousExecutables == null || anonymousExecutables.isEmpty()) {
			this.typeMembers.removeAll(getAnonymousExecutables());
			return (C) this;
		}
		typeMembers.removeAll(getAnonymousExecutables());
		for (CtAnonymousExecutable exec : anonymousExecutables) {
			addAnonymousExecutable(exec);
		}
		return (C) this;
	}

	@Override
	public <C extends CtClass<T>> C setConstructors(Set<CtConstructor<T>> constructors) {
		Set<CtConstructor<T>> oldConstructor = getConstructors();
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CONSTRUCTOR, typeMembers, oldConstructor);
		if (constructors == null || constructors.isEmpty()) {
			this.typeMembers.removeAll(oldConstructor);
			return (C) this;
		}
		typeMembers.removeAll(oldConstructor);
		for (CtConstructor<T> constructor : constructors) {
			addConstructor(constructor);
		}
		return (C) this;
	}

	@Override
	public <C extends CtClass<T>> C addConstructor(CtConstructor<T> constructor) {
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CONSTRUCTOR, typeMembers, constructor);
		return addTypeMember(constructor);
	}

	@Override
	public void removeConstructor(CtConstructor<T> constructor) {
		removeTypeMember(constructor);
	}

	@Override
	public <C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass) {
		if (superClass != null) {
			superClass.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, SUPER_TYPE, superClass, this.superClass);
		this.superClass = superClass;
		return (C) this;
	}

	@Override
	public boolean isClass() {
		return true;
	}

	@Override
	public boolean isAnonymous() {
		try {
			Integer.parseInt(getSimpleName());
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return getReference().isSubtypeOf(type);
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatementList statements) {
		CtStatementImpl.insertAfter(this, statements);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatementList statements) {
		CtStatementImpl.insertBefore(this, statements);
		return (C) this;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	@UnsettableProperty
	public <C extends CtStatement> C setLabel(String label) {
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends CtCodeElement> R partiallyEvaluate() {
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		return eval.evaluate((R) this);
	}

	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		Collection<CtExecutableReference<?>> declaredExecutables = super.getDeclaredExecutables();
		List<CtExecutableReference<?>> l = new ArrayList<>(declaredExecutables.size() + getConstructors().size());
		l.addAll(declaredExecutables);
		for (CtExecutable<?> c : getConstructors()) {
			l.add(c.getReference());
		}
		return Collections.unmodifiableList(l);
	}

	@Override
	public CtClass<T> clone() {
		return (CtClass<T>) super.clone();
	}

	@Override
	public T newInstance() {
		try {
			JDTBasedSpoonCompiler spooner = new JDTBasedSpoonCompiler(getFactory());
			spooner.compile(InputType.CTTYPES); // compiling the types of the factory
			try (NewInstanceClassloader classloader = new NewInstanceClassloader(spooner.getBinaryOutputDirectory())) {
				Class<?> klass = classloader.loadClass(getQualifiedName());
				return (T) klass.newInstance();
			}
		} catch (Exception e) {
			throw new SpoonException(e);
		}
	}

	private class NewInstanceClassloader extends URLClassLoader {
		NewInstanceClassloader(File binaryOutputDirectory) throws MalformedURLException {
			super(new URL[] { binaryOutputDirectory.toURI().toURL()});
		}

		@Override
		public Class<?> loadClass(String s) throws ClassNotFoundException {
			try {
				return findClass(s);
			} catch (Exception e) {
				return super.loadClass(s);
			}
		}
	}

	/** adding the constructors and static executables */
	@Override
	public Collection<CtExecutableReference<?>> getAllExecutables() {
		Set<CtExecutableReference<?>> l = (Set<CtExecutableReference<?>>) super.getAllExecutables();
		for (CtConstructor c : getConstructors()) {
			l.add(c.getReference());
		}
		for (CtExecutable<?> anon : getAnonymousExecutables()) {
			l.add(anon.getReference());
		}
		return l;
	}
}
