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
package spoon.reflect.factory;

import spoon.compiler.Environment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.support.DefaultCoreFactory;
import spoon.support.DefaultInternalFactory;
import spoon.support.StandardEnvironment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implements {@link Factory}
 */
public class FactoryImpl implements Factory, Serializable {

	private static final long serialVersionUID = 1L;

	private transient Factory parentFactory;

	/**
	 * Returns the parent of this factory. When an element is not found in a
	 * factory, it can be looked up in its parent factory using a delegation
	 * model.
	 */
	public Factory getParentFactory() {
		return parentFactory;
	}

	private transient AnnotationFactory annotation;

	/**
	 * The {@link CtAnnotationType} sub-factory.
	 */
	@Override
	public AnnotationFactory Annotation() {
		if (annotation == null) {
			annotation = new AnnotationFactory(this);
		}
		return annotation;
	}

	private transient ClassFactory clazz;

	/**
	 * The {@link CtClass} sub-factory.
	 */
	@Override
	public ClassFactory Class() {
		if (clazz == null) {
			clazz = new ClassFactory(this);
		}
		return clazz;
	}

	private transient CodeFactory code;

	/**
	 * The {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 */
	@Override
	public CodeFactory Code() {
		if (code == null) {
			code = new CodeFactory(this);
		}
		return code;
	}

	private transient ConstructorFactory constructor;

	/**
	 * The {@link CtConstructor} sub-factory.
	 */
	@Override
	public ConstructorFactory Constructor() {
		if (constructor == null) {
			constructor = new ConstructorFactory(this);
		}
		return constructor;
	}

	private transient CoreFactory core;

	/**
	 * The core factory.
	 */
	@Override
	public CoreFactory Core() {
		if (core == null) {
			core = new DefaultCoreFactory();
		}
		return core;
	}

	private transient EnumFactory enumF;

	/**
	 * The {@link CtEnum} sub-factory.
	 */
	@Override
	public EnumFactory Enum() {
		if (enumF == null) {
			enumF = new EnumFactory(this);
		}
		return enumF;
	}

	private transient Environment environment;

	/**
	 * Gets the Spoon environment that encloses this factory.
	 */
	@Override
	public Environment getEnvironment() {
		if (environment == null) {
			environment = new StandardEnvironment();
		}
		return environment;
	}

	private transient ExecutableFactory executable;

	/**
	 * The {@link CtExecutable} sub-factory.
	 */
	@Override
	public ExecutableFactory Executable() {
		if (executable == null) {
			executable = new ExecutableFactory(this);
		}
		return executable;
	}

	private transient EvalFactory eval;

	/**
	 * The evaluators sub-factory.
	 */
	@Override
	public EvalFactory Eval() {
		if (eval == null) {
			eval = new EvalFactory(this);
		}
		return eval;
	}

	private transient FieldFactory field;

	/**
	 * The {@link CtField} sub-factory.
	 */
	@Override
	public FieldFactory Field() {
		if (field == null) {
			field = new FieldFactory(this);
		}
		return field;
	}

	/**
	 * The {@link CtInterface} sub-factory.
	 */
	private transient InterfaceFactory interfaceF;

	/**
	 * The {@link CtInterface} sub-factory.
	 */
	@Override
	public InterfaceFactory Interface() {
		if (interfaceF == null) {
			interfaceF = new InterfaceFactory(this);
		}
		return interfaceF;
	}

	private transient MethodFactory methodF;

	/**
	 * The {@link CtMethod} sub-factory.
	 */
	@Override
	public MethodFactory Method() {
		if (methodF == null) {
			methodF = new MethodFactory(this);
		}
		return methodF;
	}

	private PackageFactory packageF;

	/**
	 * The {@link CtPackage} sub-factory.
	 */
	@Override
	public PackageFactory Package() {
		if (packageF == null) {
			packageF = new PackageFactory(this);
		}
		return packageF;
	}

	private CompilationUnitFactory compilationUnit;

	/**
	 * The {@link CompilationUnit} sub-factory.
	 */
	@Override
	public CompilationUnitFactory CompilationUnit() {
		if (compilationUnit == null) {
			compilationUnit = new CompilationUnitFactory(this);
		}
		return compilationUnit;
	}

	private transient TypeFactory type;

	/**
	 * The {@link CtType} sub-factory.
	 */
	@Override
	public TypeFactory Type() {
		if (type == null) {
			type = new TypeFactory(this);
		}
		return type;
	}

	private transient InternalFactory internal;

	@Override
	public InternalFactory Internal() {
		if (internal == null) {
			internal = new DefaultInternalFactory(this);
		}
		return internal;
	}

	/**
	 * A constructor that takes the parent factory
	 */
	public FactoryImpl(CoreFactory coreFactory, Environment environment, Factory parentFactory) {
		this.environment = environment;
		this.core = coreFactory;
		this.core.setMainFactory(this);
		this.parentFactory = parentFactory;
	}

	/**
	 * Should not be called directly. Use {@link spoon.Launcher#createFactory()} instead.
	 */
	public FactoryImpl(CoreFactory coreFactory, Environment environment) {
		this(coreFactory, environment, null);
	}

	// Deduplication
	// See http://shipilev.net/talks/joker-Oct2014-string-catechism.pdf

	private static class Dedup {
		Map<String, String> cache = new HashMap<String, String>();
		// TODO replace with ThreadLocalRandom when Spoon drops Java 6 compat
		Random random = new Random();
	}

	/**
	 * Note this is an instance field. To avoid memory leaks and dedup being
	 * targeted to each Spoon Launching, that could differ a lot by
	 * frequently used symbols.
	 */
	private transient ThreadLocal<Dedup> threadLocalDedup = new ThreadLocal<Dedup>() {
		@Override
		protected Dedup initialValue() {
			return new Dedup();
		}
	};

	/**
	 * Returns a String equal to the given symbol. Performs probablilistic
	 * deduplication.
	 */
	public String dedup(String symbol) {
		Dedup dedup = threadLocalDedup.get();
		Map<String, String> cache = dedup.cache;
		String cached;
		if ((cached = cache.get(symbol)) != null) {
			return cached;
		} else {
			// Puts the symbol into cache with 20% probability
			int prob = (int) (Integer.MIN_VALUE + (0.2 * (1L << 32)));
			if (dedup.random.nextInt() < prob) {
				cache.put(symbol, symbol);
			}
			return symbol;
		}
	}
}
