/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import spoon.SpoonException;
import spoon.pattern.PatternBuilder;
import spoon.processing.FactoryAccessor;
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
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.template.Parameters;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		CtClass<T> templateClass = getTemplateCtClass(targetType, template);
		// insert all the interfaces
		insertAllSuperInterfaces(targetType, template);
		// insert all the methods
		insertAllMethods(targetType, template);
		// insert all the constructors and all the initialization blocks (only for classes)
		insertAllConstructors(targetType, template);
		for (CtTypeMember typeMember : templateClass.getTypeMembers()) {
			if (typeMember instanceof CtField) {
				// insert all the fields
				insertGeneratedField(targetType, template, (CtField<?>) typeMember);
			} else if (typeMember instanceof CtType) {
				// insert all the inner types
				insertGeneratedNestedType(targetType, template, (CtType) typeMember);
			}
		}
	}

	/**
	 * Generates a type (class, interface, enum, ...) from the template model `templateOfType`
	 * by by substituting all the template parameters by their values.
	 *
	 * Inserts all the methods, fields, constructors, initialization blocks (if
	 * target is a class), inner types, super class and super interfaces.
	 *
	 * Note!
	 * This algorithm does NOT handle interfaces or annotations
	 * {@link Template}, {@link spoon.template.Local}, {@link TemplateParameter} or {@link Parameter}
	 * in a special way, it means they all will be added to the generated type too.
	 * If you do not want to add them then clone your templateOfType and remove these nodes from that model before.
	 *
	 * @param qualifiedTypeName
	 * 		the qualified name of the new type
	 * @param templateOfType
	 * 		the model used as source of generation.
	 * @param templateParameters
	 * 		the substitution parameters
	 */
	public static <T extends CtType<?>> T createTypeFromTemplate(String qualifiedTypeName, CtType<?> templateOfType, Map<String, Object> templateParameters) {
		return PatternBuilder
				.create(templateOfType)
				.configurePatternParameters(pc -> {
					pc.byTemplateParameter(templateParameters);
					pc.byParameterValues(templateParameters);
				})
				.build()
				.generator()
				.generateType(qualifiedTypeName, templateParameters);
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

		CtClass<? extends Template<?>> sourceClass = getTemplateCtClass(targetType, template);
		insertAllSuperInterfaces(targetType, template, sourceClass);
	}
	/**
	 * Inserts all the super interfaces (except {@link Template}) from a given
	 * template by substituting all the template parameters by their values.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 * @param sourceClass
	 * 		the model of source template
	 */
	static void insertAllSuperInterfaces(CtType<?> targetType, Template<?> template, CtClass<? extends Template<?>> sourceClass) {

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

		CtClass<?> sourceClass = getTemplateCtClass(targetType, template);
		insertAllMethods(targetType, template, sourceClass);
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
	 * @param sourceClass
	 * 		the model of source template
	 */
	static void insertAllMethods(CtType<?> targetType, Template<?> template, CtClass<?> sourceClass) {
		Set<CtMethod<?>> methodsOfTemplate = sourceClass.getFactory().Type().get(Template.class).getMethods();
		// insert all the methods
		for (CtMethod<?> m : sourceClass.getMethods()) {
			if (m.getAnnotation(Local.class) != null) {
				continue;
			}
			if (m.getAnnotation(Parameter.class) != null) {
				continue;
			}

			boolean isOverridingTemplateItf = false;
			for (CtMethod m2 : methodsOfTemplate) {
				if (m.isOverriding(m2)) {
					isOverridingTemplateItf = true;
				}
			}

			if (isOverridingTemplateItf) {
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

		CtClass<?> sourceClass = getTemplateCtClass(targetType, template);
		// insert all the fields
		for (CtTypeMember typeMember: sourceClass.getTypeMembers()) {
			if (typeMember instanceof CtField) {
				insertGeneratedField(targetType, template, (CtField<?>) typeMember);
			}
		}
	}

	/**
	 * Inserts the field by substituting all the
	 * template parameters by their values. Field annotated with
	 * {@link spoon.template.Local} or {@link Parameter} is not inserted.

	 * @param targetType
	 * @param template
	 * @param field
	 */
	static void insertGeneratedField(CtType<?> targetType, Template<?> template, CtField<?> field) {

		if (field.getAnnotation(Local.class) != null) {
			return;
		}
		if (Parameters.isParameterSource(field.getReference())) {
			return;
		}

		insertField(targetType, template, field);
	}

	/**
	 * Inserts all the nested types from a given template by substituting all the
	 * template parameters by their values. Members annotated with
	 * {@link spoon.template.Local} are not inserted.
	 *
	 * @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 */
	public static void insertAllNestedTypes(CtType<?> targetType, Template<?> template) {

		CtClass<?> sourceClass = getTemplateCtClass(targetType, template);
		// insert all the fields
		for (CtTypeMember typeMember: sourceClass.getTypeMembers()) {
			if (typeMember instanceof CtType) {
				insertGeneratedNestedType(targetType, template, (CtType<?>) typeMember);
			}
		}
	}

	/**
	 * Inserts the nestedType by substituting all the
	 * template parameters by their values. Nested type annotated with
	 * {@link spoon.template.Local} is not inserted.
	 *  @param targetType
	 * 		the target type
	 * @param template
	 * 		the source template
	 * @param nestedType
 * 		to be insterted nested type
	 */
	static void insertGeneratedNestedType(CtType<?> targetType, Template<?> template, CtType<?> nestedType) {

		if (nestedType.getAnnotation(Local.class) != null) {
			return;
		}
		CtType<?> result = substitute(targetType, template, (CtType) nestedType);
		targetType.addNestedType(result);
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

		CtClass<?> sourceClass = getTemplateCtClass(targetType, template);
		insertAllConstructors(targetType, template, sourceClass);
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
	 * @param sourceClass
	 * 		the model of source template
	 */
	static void insertAllConstructors(CtType<?> targetType, Template<?> template, CtClass<?> sourceClass) {

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
				((CtClass<?>) targetType).addAnonymousExecutable(substitute(targetType, template, e));
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
				targetClass.removeConstructor((CtConstructor<T>) c);
			}
		}
		targetClass.addConstructor(newConstrutor);
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
		CtClass<?> sourceClass = getTemplateCtClass(targetClass, template);
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
		CtClass<?> sourceClass = getTemplateCtClass(targetClass, template);
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
		CtClass<?> sourceClass = getTemplateCtClass(targetType, template);
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
	@SuppressWarnings("unchecked")
	public static <E extends CtElement> E substitute(CtType<?> targetType, Template<?> template, E code) {
		if (code == null) {
			return null;
		}
		if (targetType == null) {
			throw new RuntimeException("target is null in substitution");
		}
		TemplateBuilder tb = TemplateBuilder.createPattern(code, template);
		if (template instanceof AbstractTemplate) {
			tb.setAddGeneratedBy(((AbstractTemplate) template).isAddGeneratedBy());
		}
		return (E) tb.substituteSingle(targetType, CtElement.class);
	}

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
	@SuppressWarnings("unchecked")
	public static <T extends CtType<?>> T substitute(Template<?> template, T templateType) {
		// result.setParent(templateType.getParent());
		CtType<?> result = TemplateBuilder.createPattern(templateType, template).substituteSingle(null, CtType.class);
		//TODO check if it is still needed
		result.setPositions(null);
		return (T) result;
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
		return field;
	}

	/**
	 * A helper method that recursively redirects all the type references from a
	 * source type to a target type in the given element.
	 */
	public static void redirectTypeReferences(CtElement element, CtTypeReference<?> source, CtTypeReference<?> target) {

		List<CtTypeReference<?>> refs = Query.getReferences(element, new ReferenceTypeFilter<>(CtTypeReference.class));

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

	/**
	 * @param targetType - the element which is going to receive the model produced by the template.
	 * It is needed here just to provide the spoon factory, which contains the model of the template
	 *
	 * @param template - java instance of the template
	 *
	 * @return - CtClass from the already built spoon model, which represents the template
	 */
	static <T> CtClass<T> getTemplateCtClass(CtType<?> targetType, Template<?> template) {
		Factory factory;
		// we first need a factory
		if (targetType != null) {
			// if it's template with reference replacement
			factory = targetType.getFactory();
		} else {
			// else we have at least one template parameter with a factory
			factory = getFactory(template);
		}
		return getTemplateCtClass(factory, template);
	}

	/**
	 * @param factory - the factory, which contains the model of the template
	 *
	 * @param template - java instance of the template
	 *
	 * @return - CtClass from the already built spoon model, which represents the template
	 */
	public static <T> CtClass<T> getTemplateCtClass(Factory factory, Template<?> template) {
		CtClass<T> c = factory.Class().get(template.getClass());
		if (c.isShadow()) {
			throw new SpoonException("The template " + template.getClass().getName() + " is not part of model. Add template sources to spoon template path.");
		}
		checkTemplateContracts(c);
		return c;
	}

	private static <T> void checkTemplateContracts(CtClass<T> c) {
		for (CtField f : c.getFields()) {
			Parameter templateParamAnnotation = f.getAnnotation(Parameter.class);
			if (templateParamAnnotation != null && !templateParamAnnotation.value().isEmpty()) {
				String proxyName = templateParamAnnotation.value();
				// contract: if value, then the field type must be String or CtTypeReference
				String fieldTypeQName = f.getType().getQualifiedName();
				if (fieldTypeQName.equals(String.class.getName())) {
					// contract: the name of the template parameter must correspond to the name of the field
					// as found, by Pavel, this is not good contract because it prevents easy refactoring of templates
					// we remove it but keep the commented code in case somebody would come up with this bad idea
//					if (!f.getSimpleName().equals("_" + f.getAnnotation(Parameter.class).value())) {
//						throw new TemplateException("the field name of a proxy template parameter must be called _" + f.getSimpleName());
//					}

					// contract: if a proxy parameter is declared and named "x" (@Parameter("x")), then a type member named "x" must exist.
					boolean found = false;
					for (CtTypeMember member: c.getTypeMembers()) {
						if (member.getSimpleName().equals(proxyName)) {
							found = true;
						}
					}
					if (!found) {
						throw new TemplateException("if a proxy parameter is declared and named \"" + proxyName + "\", then a type member named \"\" + proxyName + \"\" must exist.");
					}
				} else if (fieldTypeQName.equals(CtTypeReference.class.getName())) {
					//OK it is CtTypeReference
				} else {
					throw new TemplateException("proxy template parameter must be typed as String or CtTypeReference, but it is " + fieldTypeQName);
				}
			}
		}
	}

	/**
	 * returns a Spoon factory object from the first template parameter that contains one
	 */
	static Factory getFactory(Template<?> template) {
		try {
			for (Field f : Parameters.getAllTemplateParameterFields(template.getClass())) {
				if (f.get(template) != null && f.get(template) instanceof FactoryAccessor) {
					return ((FactoryAccessor) f.get(template)).getFactory();
				}
			}
		} catch (Exception e) {
			throw new SpoonException(e);
		}
		throw new TemplateException("no factory found in template " + template.getClass().getName());
	}
}
