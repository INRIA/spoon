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
package spoon.template;

import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.template.Parameters;
import spoon.support.template.SubstitutionVisitor;

/**
 * This class defines the substitution API for templates (see {@link Template}).
 */
public abstract class Substitution {

	private Substitution() {
	}

	/**
	 * Inserts all the methods, fields, constructors, initialization blocks (if
	 * target is a class), inner types, and super interfaces (except
	 * {@link Template}) from a given template by substituting all the template
	 * parameters by their values. Members annotated with
	 * {@link spoon.template.Local} or {@link Parameter} are not inserted.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 */
	public static <T extends Template<?>> void insertAll(CtType<?> targetType, T template) {

		CtClass<T> templateClass = targetType.getFactory().Class().get(template.getClass());
		// insert all the interfaces
		for (CtTypeReference<?> t : templateClass.getSuperInterfaces()) {
			if (!t.equals(targetType.getFactory().Type().createReference(Template.class))) {
				CtTypeReference<?> t1 = t;
				// substitute ref if needed
				if (Parameters.getNames(templateClass).contains(t.getSimpleName())) {
					Object o = Parameters.getValue(template, t.getSimpleName(), null);
					if (o instanceof CtTypeReference) {
						t1 = (CtTypeReference<?>) o;
					} else if (o instanceof Class) {
						t1 = targetType.getFactory().Type().createReference((Class<?>) o);
					} else if (o instanceof String) {
						t1 = targetType.getFactory().Type().createReference((String) o);
					}
				}
				if (!t1.equals(targetType.getReference())) {
					Class<?> c = null;
					try {
						c = t1.getActualClass();
					} catch (Exception e) {
						// swallow it
					}
					if (c != null && c.isInterface()) {
						targetType.addSuperInterface(t1);
					}
					if (c == null) {
						targetType.addSuperInterface(t1);
					}
				}
			}
		}
		// insert all the methods
		for (CtMethod<?> m : templateClass.getMethods()) {
			if (m.getAnnotation(Local.class) != null) {
				continue;
			}
			if (m.getAnnotation(Parameter.class) != null) {
				continue;
			}
			insertMethod(targetType, template, m);
		}
		// insert all the constructors
		if (targetType instanceof CtClass) {
			for (CtConstructor<?> c : templateClass.getConstructors()) {
				if (c.isImplicit()) {
					continue;
				}
				if (c.getAnnotation(Local.class) != null) {
					continue;
				}
				insertConstructor((CtClass<?>) targetType, template, c);
			}
		}
		// insert all the initialization blocks (only for classes)
		if (targetType instanceof CtClass) {
			for (CtAnonymousExecutable e : templateClass.getAnonymousExecutables()) {
				((CtClass<?>) targetType).addAnonymousExecutable(substitute(targetType, template, e));
			}
		}
		// insert all the fields
		for (CtField<?> f : templateClass.getFields()) {
			if (f.getAnnotation(Local.class) != null) {
				continue;
			}
			if (Parameters.isParameterSource(f.getReference())) {
				continue;
			}

			insertField(targetType, template, f);
		}
		// insert all the inner types
		for (CtType<?> t : templateClass.getNestedTypes()) {
			if (t.getAnnotation(Local.class) != null) {
				continue;
			}
			CtType<?> result = substitute(templateClass, template, t);
			targetType.addNestedType(result);
		}

	}

	/**
	 * Inserts all the super interfaces (except {@link Template}) from a given
	 * template by substituting all the template parameters by their values.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 */
	public static void insertAllSuperInterfaces(CtType<?> targetType, Template<?> template) {

		CtClass<? extends Template<?>> sourceClass = targetType.getFactory().Class().get(template.getClass());
		// insert all the interfaces
		for (CtTypeReference<?> t : sourceClass.getSuperInterfaces()) {
			if (!t.equals(targetType.getFactory().Type().createReference(Template.class))) {
				CtTypeReference<?> t1 = t;
				// substitute ref if needed
				if (Parameters.getNames(sourceClass).contains(t.getSimpleName())) {
					Object o = Parameters.getValue(template, t.getSimpleName(), null);
					if (o instanceof CtTypeReference) {
						t1 = (CtTypeReference<?>) o;
					} else if (o instanceof Class) {
						t1 = targetType.getFactory().Type().createReference((Class<?>) o);
					} else if (o instanceof String) {
						t1 = targetType.getFactory().Type().createReference((String) o);
					}
				}
				if (!t1.equals(targetType.getReference())) {
					Class<?> c = t1.getActualClass();
					if (c != null && c.isInterface()) {
						targetType.addSuperInterface(t1);
					}
					if (c == null) {
						targetType.addSuperInterface(t1);
					}
				}
			}
		}
	}

	/**
	 * Inserts all the methods from a given template by substituting all the
	 * template parameters by their values. Members annotated with
	 * {@link spoon.template.Local} or {@link Parameter} are not inserted.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 */
	public static void insertAllMethods(CtType<?> targetType, Template<?> template) {

		CtClass<?> sourceClass = targetType.getFactory().Class().get(template.getClass());
		// insert all the methods
		for (CtMethod<?> m : sourceClass.getMethods()) {
			if (m.getAnnotation(Local.class) != null) {
				continue;
			}
			if (m.getAnnotation(Parameter.class) != null) {
				continue;
			}
			insertMethod(targetType, template, m);
		}
	}

	/**
	 * Inserts all the fields from a given template by substituting all the
	 * template parameters by their values. Members annotated with
	 * {@link spoon.template.Local} or {@link Parameter} are not inserted.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 */
	public static void insertAllFields(CtType<?> targetType, Template<?> template) {

		CtClass<?> sourceClass = targetType.getFactory().Class().get(template.getClass());
		// insert all the fields
		for (CtField<?> f : sourceClass.getFields()) {
			if (f.getAnnotation(Local.class) != null) {
				continue;
			}
			if (Parameters.isParameterSource(f.getReference())) {
				continue;
			}

			insertField(targetType, template, f);
		}
	}

	/**
	 * Inserts all constructors and initialization blocks from a given template
	 * by substituting all the template parameters by their values. Members
	 * annotated with {@link spoon.template.Local} or {@link Parameter} are not
	 * inserted.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 */
	public static void insertAllConstructors(CtType<?> targetType, Template<?> template) {

		CtClass<?> sourceClass = targetType.getFactory().Class().get(template.getClass());
		// insert all the constructors
		if (targetType instanceof CtClass) {
			for (CtConstructor<?> c : sourceClass.getConstructors()) {
				if (c.isImplicit()) {
					continue;
				}
				if (c.getAnnotation(Local.class) != null) {
					continue;
				}
				insertConstructor((CtClass<?>) targetType, template, c);
			}
		}
		// insert all the initialization blocks (only for classes)
		if (targetType instanceof CtClass) {
			for (CtAnonymousExecutable e : sourceClass.getAnonymousExecutables()) {
				CtAnonymousExecutable e2 = substitute(targetType, template, e);
				((CtClass<?>) targetType).addAnonymousExecutable(e2);
				// e2.setParent(targetType);
			}
		}
	}

	/**
	 * Generates a constructor from a template method by substituting all the
	 * template parameters by their values.
	 *
	 * @param targetClass
	 * 		the target class where to insert the generated constructor
	 * @param template
	 * 		the template instance that holds the source template method
	 * 		and that defines the parameter values
	 * @param sourceMethod
	 * 		the source template method
	 * @return the generated method
	 */
	public static <T> CtConstructor<T> insertConstructor(CtClass<T> targetClass, Template<?> template, CtMethod<?> sourceMethod) {

		if (targetClass instanceof CtInterface) {
			return null;
		}
		CtConstructor<T> newConstructor = targetClass.getFactory().Constructor().create(targetClass, sourceMethod);
		newConstructor = substitute(targetClass, template, newConstructor);
		targetClass.addConstructor(newConstructor);
		// newConstructor.setParent(targetClass);
		return newConstructor;
	}

	/**
	 * Generates a method from a template method by substituting all the
	 * template parameters by their values.
	 *
	 * @param targetType
	 * 		the target type where to insert the generated method
	 * @param template
	 * 		the template instance that holds the source template method
	 * 		and that defines the parameter values
	 * @param sourceMethod
	 * 		the source template method
	 * @return the generated method
	 */
	public static <T> CtMethod<T> insertMethod(CtType<?> targetType, Template<?> template, CtMethod<T> sourceMethod) {

		CtMethod<T> newMethod = substitute(targetType, template, sourceMethod);
		if (targetType instanceof CtInterface) {
			newMethod.setBody(null);
		}
		targetType.addMethod(newMethod);
		// newMethod.setParent(targetType);
		return newMethod;
	}

	/**
	 * Generates a constructor from a template constructor by substituting all
	 * the template parameters by their values.
	 *
	 * @param targetClass
	 * 		the target class where to insert the generated constructor
	 * @param template
	 * 		the template instance that holds the source template
	 * 		constructor and that defines the parameter values
	 * @param sourceConstructor
	 * 		the source template constructor
	 * @return the generated constructor
	 */
	@SuppressWarnings("unchecked")
	public static <T> CtConstructor<T> insertConstructor(CtClass<T> targetClass, Template<?> template, CtConstructor<?> sourceConstructor) {

		CtConstructor<T> newConstrutor = substitute(targetClass, template, (CtConstructor<T>) sourceConstructor);
		// remove the implicit constructor if clashing
		if (newConstrutor.getParameters().isEmpty()) {
			CtConstructor<?> c = targetClass.getConstructor();
			if (c != null && c.isImplicit()) {
				targetClass.getConstructors().remove(c);
			}
		}
		targetClass.addConstructor(newConstrutor);
		// newConstrutor.setParent(targetClass);
		return newConstrutor;
	}

	/**
	 * Gets a body from a template executable with all the template parameters
	 * substituted.
	 *
	 * @param targetClass
	 * 		the target class
	 * @param template
	 * 		the template that holds the executable
	 * @param executableName
	 * 		the source executable template
	 * @param parameterTypes
	 * 		the parameter types of the source executable
	 * @return the body expression of the source executable template with all
	 * the template parameters substituted
	 */
	public static CtBlock<?> substituteMethodBody(CtClass<?> targetClass, Template<?> template, String executableName, CtTypeReference<?>... parameterTypes) {
		CtClass<?> sourceClass = targetClass.getFactory().Class().get(template.getClass());
		CtExecutable<?> sourceExecutable = executableName.equals(template.getClass().getSimpleName())
				? sourceClass.getConstructor(parameterTypes)
				: sourceClass.getMethod(executableName, parameterTypes);
		return substitute(targetClass, template, sourceExecutable.getBody());
	}

	/**
	 * Gets a statement from a template executable with all the template
	 * parameters substituted.
	 *
	 * @param targetClass
	 * 		the target class
	 * @param template
	 * 		the template that holds the executable
	 * @param statementIndex
	 * 		the statement index in the executable's body
	 * @param executableName
	 * 		the source executable template
	 * @param parameterTypes
	 * 		the parameter types of the source executable
	 * @return the body expression of the source executable template with all
	 * the template parameters substituted
	 */
	public static CtStatement substituteStatement(CtClass<?> targetClass, Template<?> template, int statementIndex, String executableName, CtTypeReference<?>... parameterTypes) {
		CtClass<?> sourceClass = targetClass.getFactory().Class().get(template.getClass());
		CtExecutable<?> sourceExecutable = executableName.equals(template.getClass().getSimpleName())
				? sourceClass.getConstructor(parameterTypes)
				: sourceClass.getMethod(executableName, parameterTypes);
		return substitute(targetClass, template, sourceExecutable.getBody().getStatement(statementIndex));
	}

	/**
	 * Gets a default expression from a template field with all the template
	 * parameters substituted.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the template that holds the field
	 * @param fieldName
	 * 		the template source field
	 * @return the expression of the template source field with all the template
	 * parameters substituted
	 */

	public static CtExpression<?> substituteFieldDefaultExpression(CtType<?> targetType, Template<?> template, String fieldName) {
		CtClass<?> sourceClass = targetType.getFactory().Class().get(template.getClass());
		CtField<?> sourceField = sourceClass.getField(fieldName);
		return substitute(targetType, template, sourceField.getDefaultExpression());
	}

	/**
	 * Substitutes all the template parameters in a random piece of code.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the template instance
	 * @param code
	 * 		the code
	 * @return the code where all the template parameters has been substituted
	 * by their values
	 */
	public static <E extends CtElement> E substitute(CtType<?> targetType, Template<?> template, E code) {
		if (code == null) {
			return null;
		}
		if (targetType == null) {
			throw new RuntimeException("target is null in substitution");
		}
		E result = targetType.getFactory().Core().clone(code);
		new SubstitutionVisitor(targetType.getFactory(), targetType, template).scan(result);
		return result;
	}

	/**
	 * Substitutes all the template parameters in the first template element
	 * annotated with an instance of the given annotation type.
	 *
	 * @param targetType
	 *            the target type
	 * @param template
	 *            the template instance
	 * @param annotationType
	 *            the annotation type
	 * @return the element where all the template parameters has be substituted
	 *         by their values
	 */
	// public static <E extends CtElement> E substitute(
	// CtSimpleType<?> targetType, Template template,
	// Class<? extends Annotation> annotationType) {
	// CtClass<? extends Template> c = targetType.getFactory().Class
	// .get(template.getClass());
	// E element = (E) c.getAnnotatedChildren(annotationType).get(0);
	// if (element == null)
	// return null;
	// if (targetType == null)
	// throw new RuntimeException("target is null in substitution");
	// E result = CtCloner.clone(element);
	// new SubstitutionVisitor(targetType.getFactory(), targetType, template)
	// .scan(result);
	// return result;
	// }

	/**
	 * Substitutes all the template parameters in a given template type and
	 * returns the resulting type.
	 *
	 * @param template
	 * 		the template instance (holds the parameter values)
	 * @param templateType
	 * 		the template type
	 * @return a copy of the template type where all the parameters has been
	 * substituted
	 */
	public static <T extends CtType<?>> T substitute(Template<?> template, T templateType) {
		T result = templateType.getFactory().Core().clone(templateType);
		result.setPositions(null);
		// result.setParent(templateType.getParent());
		new SubstitutionVisitor(templateType.getFactory(), result, template).scan(result);
		return result;
	}

	/**
	 * Generates a field (and its initialization expression) from a template
	 * field by substituting all the template parameters by their values.
	 *
	 * @param <T>
	 * 		the type of the field
	 * @param targetType
	 * 		the target type where the field is inserted
	 * @param template
	 * 		the template that defines the source template field
	 * @param sourceField
	 * 		the source template field
	 * @return the inserted field
	 */
	public static <T> CtField<T> insertField(CtType<?> targetType, Template<?> template, CtField<T> sourceField) {
		CtField<T> field = substitute(targetType, template, sourceField);
		targetType.addField(field);
		// field.setParent(targetType);
		return field;
	}

	/**
	 * A helper method that recursively redirects all the type references from a
	 * source type to a target type in the given element.
	 */
	public static void redirectTypeReferences(CtElement element, CtTypeReference<?> source, CtTypeReference<?> target) {

		List<CtTypeReference<?>> refs = Query.getReferences(element, new ReferenceTypeFilter<CtTypeReference<?>>(CtTypeReference.class));

		String srcName = source.getQualifiedName();
		String targetName = target.getSimpleName();
		CtPackageReference targetPackage = target.getPackage();

		for (CtTypeReference<?> ref : refs) {
			if (ref.getQualifiedName().equals(srcName)) {
				ref.setSimpleName(targetName);
				ref.setPackage(targetPackage);
			}
		}
	}

}
