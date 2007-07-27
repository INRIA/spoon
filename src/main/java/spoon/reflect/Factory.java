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

package spoon.reflect;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import spoon.processing.Builder;
import spoon.processing.Environment;
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
import spoon.reflect.factory.AnnotationFactory;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.CompilationUnitFactory;
import spoon.reflect.factory.ConstructorFactory;
import spoon.reflect.factory.EnumFactory;
import spoon.reflect.factory.EvalFactory;
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.factory.FieldFactory;
import spoon.reflect.factory.InterfaceFactory;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.factory.PackageFactory;
import spoon.reflect.factory.TemplateFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonBuildingManager;

/**
 * This class defines the entry point API to create and access the program's
 * model. Element-specific methods are defined in the sub-factories. An instance
 * of the current factory can be retrieved at any place where a
 * {@link spoon.processing.FactoryAccessor} instance is available (that is to
 * say, any processor or model element). Factory methods ensure the model's
 * consistency and should be always used when creating model elements. The
 * {@link spoon.reflect.CoreFactory} is used to create raw elements, but model
 * consistency has to be maintained manually.
 * 
 * @see spoon.processing.Processor
 * @see spoon.reflect.declaration.CtElement
 * @see spoon.reflect.CoreFactory
 * @see #Core()
 */
public class Factory implements Serializable {

	private static final long serialVersionUID = 1L;

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

	private TemplateFactory Template;

	/**
	 * The {@link CtPackage} sub-factory.
	 */
	public TemplateFactory Template() {
		if (Template == null) {
			Template = new TemplateFactory(this);
		}
		return Template;
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

	private Factory() {
		super();
		if (launchingFactory == null)
			launchingFactory = this;
	}

	static Factory launchingFactory;

	/**
	 * Gets the factory that was created at launching time (the firstly created
	 * factory). This factory is automatically initialized when a factory is
	 * contructed for the first time. Any subsequent constructions will not
	 * affect the launching factory and references to other factories have to be
	 * handled manually, if ever needed.
	 */
	public static Factory getLauchingFactory() {
		return launchingFactory;
	}

	/**
	 * The constructor.
	 */
	public Factory(CoreFactory coreFactory, Environment environment) {
		this();
		this.Environment = environment;
		environment.setFactory(this);
		this.Core = coreFactory;
		this.Core.setMainFactory(this);
	}

	/**
	 * The builder associated to this factory.
	 */
	protected transient Builder builder;

	/**
	 * Returns a builder for this factory (creates it if not existing yet).
	 */
	public Builder getBuilder() {
		if (builder == null)
			builder = new SpoonBuildingManager(this);
		return builder;
	}

	/**
	 * Converts an object <code>o</code> into an object or a
	 * {@link CtReference} of type </code>type<code>.
	 * 
	 * @param <T>
	 *            the actual type of the object
	 * @param type
	 *            the type to convert the object into
	 * @param o
	 *            the object to be converted
	 * @return a primitive object of type T, or a reference
	 */
	@SuppressWarnings("unchecked")
	public <T> T convert(Class<T> type, Object o) {
		if (o == null)
			return null;
		if (type == boolean.class)
			return (T) new Boolean(o.toString());
		if (type == byte.class)
			return (T) new Byte(o.toString());
		if (type == char.class)
			return (T) new Character(o.toString().charAt(0));
		if (type == double.class)
			return (T) new Double(o.toString());
		if (type == float.class)
			return (T) new Float(o.toString());
		if (type == int.class)
			return (T) new Integer(o.toString());
		if (type == long.class)
			return (T) new Long(o.toString());
		if (CtTypeReference.class.isAssignableFrom(type)) {
			return (T) Type().createReference(o.toString());
		}
		if (CtExecutableReference.class.isAssignableFrom(type)) {
			return (T) Executable().createReference(o.toString());
		}
		if (CtFieldReference.class.isAssignableFrom(type)) {
			return (T) Field().createReference(o.toString());
		}
		if (CtPackageReference.class.isAssignableFrom(type)) {
			return (T) Package().createReference(o.toString());
		}
		if (type.isEnum()) {
			return (T) java.lang.Enum.valueOf((Class<Enum>) type, o.toString());
		}
		return (T) o.toString();
	}

	/**
	 * Converts a collection of object into an array of type </code>type<code>.
	 * 
	 * @param <T>
	 *            the actual type of the array
	 * @param type
	 *            the type to convert the object into
	 * @param val
	 *            the collection to be converted
	 * @return an array of type T
	 */
	@SuppressWarnings("unchecked")
	public <T> T convertArray(Class<T> type, Collection<Object> val) {
		if (type.equals(boolean.class)) {
			boolean[] ret = new boolean[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(boolean.class, o);
			}
			return (T) ret;
		} else if (type.equals(byte.class)) {
			byte[] ret = new byte[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(byte.class, o);
			}
			return (T) ret;
		} else if (type.equals(char.class)) {
			char[] ret = new char[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(char.class, o);
			}
			return (T) ret;
		} else if (type.equals(double.class)) {
			double[] ret = new double[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(double.class, o);
			}
			return (T) ret;
		} else if (type.equals(float.class)) {
			float[] ret = new float[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(float.class, o);
			}
			return (T) ret;
		} else if (type.equals(int.class)) {
			int[] ret = new int[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(int.class, o);
			}
			return (T) ret;
		} else if (type.equals(long.class)) {
			long[] ret = new long[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(long.class, o);
			}
			return (T) ret;
		} else if (type.equals(String.class)) {
			String[] ret = new String[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(String.class, o);
			}
			return (T) ret;
		} else if (CtPackageReference.class.isAssignableFrom(type)) {
			CtPackageReference[] ret = new CtPackageReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtPackageReference.class, o);
			}
			return (T) ret;
		} else if (CtTypeReference.class.isAssignableFrom(type)) {
			CtTypeReference[] ret = new CtTypeReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtTypeReference.class, o);
			}
			return (T) ret;
		} else if (CtFieldReference.class.isAssignableFrom(type)) {
			CtFieldReference[] ret = new CtFieldReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtFieldReference.class, o);
			}
			return (T) ret;
		} else if (CtExecutableReference.class.isAssignableFrom(type)) {
			CtExecutableReference[] ret = new CtExecutableReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtExecutableReference.class, o);
			}
			return (T) ret;
		} else if (type.isEnum()) {
			Collection<Enum<?>> ret = new ArrayList<Enum<?>>();
			for (Object o : val) {
				ret.add((Enum) convert(type, o));
			}
			return (T) ret.toArray((Enum[]) Array.newInstance(type, 0));
		}
		return null;
	}

}
