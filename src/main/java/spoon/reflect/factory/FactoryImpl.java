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

package spoon.reflect.factory;

import java.io.Serializable;

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
import spoon.support.StandardEnvironment;

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

	private transient AnnotationFactory Annotation;

	/**
	 * The {@link CtAnnotationType} sub-factory.
	 */
	public AnnotationFactory Annotation() {
		if (Annotation == null) {
			Annotation = new AnnotationFactory(this);
		}
		return Annotation;
	}

	private transient ClassFactory Class;

	/**
	 * The {@link CtClass} sub-factory.
	 */
	public ClassFactory Class() {
		if (Class == null) {
			Class = new ClassFactory(this);
		}
		return Class;
	}

	private transient CodeFactory Code;

	/**
	 * The {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 */
	public CodeFactory Code() {
		if (Code == null) {
			Code = new CodeFactory(this);
		}
		return Code;
	}

	private transient ConstructorFactory Constructor;

	/**
	 * The {@link CtConstructor} sub-factory.
	 */
	public ConstructorFactory Constructor() {
		if (Constructor == null) {
			Constructor = new ConstructorFactory(this);
		}
		return Constructor;
	}

	private transient CoreFactory Core;

	/**
	 * The core factory.
	 */
	public CoreFactory Core() {
		if (Core == null) {
			Core = new DefaultCoreFactory();
		}
		return Core;
	}

	private transient EnumFactory Enum;

	/**
	 * The {@link CtEnum} sub-factory.
	 */
	public EnumFactory Enum() {
		if (Enum == null) {
			Enum = new EnumFactory(this);
		}
		return Enum;
	}

	private transient Environment Environment;

	/**
	 * Gets the Spoon environment that encloses this factory.
	 */
	public Environment getEnvironment() {
		if (Environment == null) {
			Environment = new StandardEnvironment();
		}
		return Environment;
	}

	private transient ExecutableFactory Executable;

	/**
	 * The {@link CtExecutable} sub-factory.
	 */
	public ExecutableFactory Executable() {
		if (Executable == null) {
			Executable = new ExecutableFactory(this);
		}
		return Executable;
	}

	private transient EvalFactory Eval;

	/**
	 * The evaluators sub-factory.
	 */
	public EvalFactory Eval() {
		if (Eval == null) {
			Eval = new EvalFactory(this);
		}
		return Eval;
	}

	private transient FieldFactory Field;

	/**
	 * The {@link CtField} sub-factory.
	 */
	public FieldFactory Field() {
		if (Field == null) {
			Field = new FieldFactory(this);
		}
		return Field;
	}

	/**
	 * The {@link CtInterface} sub-factory.
	 */
	private transient InterfaceFactory Interface;

	/**
	 * The {@link CtInterface} sub-factory.
	 */
	public InterfaceFactory Interface() {
		if (Interface == null) {
			Interface = new InterfaceFactory(this);
		}
		return Interface;
	}

	private transient MethodFactory Method;

	/**
	 * The {@link CtMethod} sub-factory.
	 */
	public MethodFactory Method() {
		if (Method == null) {
			Method = new MethodFactory(this);
		}
		return Method;
	}

	private PackageFactory Package;

	/**
	 * The {@link CtPackage} sub-factory.
	 */
	public PackageFactory Package() {
		if (Package == null) {
			Package = new PackageFactory(this);
		}
		return Package;
	}

	private CompilationUnitFactory CompilationUnit;

	/**
	 * The {@link CompilationUnit} sub-factory.
	 */
	public CompilationUnitFactory CompilationUnit() {
		if (CompilationUnit == null) {
			CompilationUnit = new CompilationUnitFactory(this);
		}
		return CompilationUnit;
	}

	private transient TypeFactory Type;

	/**
	 * The {@link CtType} sub-factory.
	 */
	public TypeFactory Type() {
		if (Type == null) {
			Type = new TypeFactory(this);
		}
		return Type;
	}

	private FactoryImpl() {
		if (launchingFactory == null)
			launchingFactory = this;
	}

	static Factory launchingFactory;

	/**
	 * Gets the factory that was created at launching time (the firstly created
	 * factory). This factory is automatically initialized when a factory is
	 * constructed for the first time. Any subsequent constructions will not
	 * affect the launching factory and references to other factories have to be
	 * handled manually, if ever needed.
	 */
	public static Factory getLauchingFactory() {
		return launchingFactory;
	}

	/**
	 * A constructor that takes the parent factory
	 */
	public FactoryImpl(CoreFactory coreFactory, Environment environment,
			Factory parentFactory) {
		this();
		this.Environment = environment;
		this.Core = coreFactory;
		this.Core.setMainFactory(this);
		this.parentFactory = parentFactory;
	}

	/**
	 * The constructor.
	 */
	public FactoryImpl(CoreFactory coreFactory, Environment environment) {
		this(coreFactory, environment, null);
	}


}
