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

import spoon.SpoonException;
import spoon.SpoonModelBuilder.InputType;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
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

/**
 * The implementation for {@link spoon.reflect.declaration.CtClass}.
 *
 * @author Renaud Pawlak
 */
public class CtClassImpl<T extends Object> extends CtTypeImpl<T> implements CtClass<T> {
	private static final long serialVersionUID = 1L;

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
		return anonymousExecutables;
	}

	@Override
	public CtConstructor<T> getConstructor(CtTypeReference<?>... parameterTypes) {
		for (CtTypeMember typeMember : getTypeMembers()) {
			if (!(typeMember instanceof CtConstructor)) {
				continue;
			}
			CtConstructor<T> c = (CtConstructor<T>) typeMember;
			boolean cont = c.getParameters().size() == parameterTypes.length;
			for (int i = 0; cont && (i < c.getParameters().size()) && (i < parameterTypes.length); i++) {
				if (!parameterTypes[i].equals(c.getParameters().get(i).getType())) {
					cont = false;
				}
			}
			if (cont) {
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
		return constructors;
	}

	@Override
	public <C extends CtClass<T>> C addAnonymousExecutable(CtAnonymousExecutable e) {
		return addTypeMember(e);
	}

	@Override
	public boolean removeAnonymousExecutable(CtAnonymousExecutable e) {
		return removeTypeMember(e);
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		return superClass;
	}

	@Override
	public <C extends CtClass<T>> C setAnonymousExecutables(List<CtAnonymousExecutable> anonymousExecutables) {
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
		if (constructors == null || constructors.isEmpty()) {
			this.typeMembers.removeAll(getConstructors());
			return (C) this;
		}
		typeMembers.removeAll(getConstructors());
		for (CtConstructor<T> constructor : constructors) {
			addConstructor(constructor);
		}
		return (C) this;
	}

	@Override
	public <C extends CtClass<T>> C addConstructor(CtConstructor<T> constructor) {
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
		this.superClass = superClass;
		return (C) this;
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
		if ((getSuperclass() != null) && getSuperclass().isSubtypeOf(type)) {
			return true;
		}
		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.isSubtypeOf(type)) {
				return true;
			}
		}
		return false;
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
	public <C extends CtStatement> C setLabel(String label) {
		throw new UnsupportedOperationException("cannot set a label on a class declaration");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends CtCodeElement> R partiallyEvaluate() {
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		return eval.evaluate(getParent(), (R) this);
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
	public void replace(CtStatement element) {
		replace((CtElement) element);
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
			Class<?> klass = new NewInstanceClassloader(spooner.getBinaryOutputDirectory()).loadClass(getQualifiedName());
			return (T) klass.newInstance();
		} catch (Exception e) {
			throw new SpoonException(e);
		}
	}

	private class NewInstanceClassloader extends URLClassLoader {
		NewInstanceClassloader(File binaryOutputDirectory) throws MalformedURLException {
			super(new URL[] { binaryOutputDirectory.toURL()});
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
}
