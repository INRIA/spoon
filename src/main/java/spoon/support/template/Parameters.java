/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.template;

import spoon.SpoonException;
import spoon.pattern.PatternBuilder;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.util.RtHelper;
import spoon.template.Parameter;
import spoon.template.Template;
import spoon.template.TemplateParameter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class defines an API to manipulate template parameters.
 */
public abstract class Parameters {

	private Parameters() {
	}

	/**
	 * The prefix "_FIELD_" for a parameter that represents a fields in order to
	 * avoid name clashes.
	 */
	protected static final String fieldPrefix = "_FIELD_";

	/**
	 * Gets the index of a one-dimension array (helper).
	 */
	@SuppressWarnings("unchecked")
	public static Integer getIndex(CtExpression<?> e) {
		if (e.getParent() instanceof CtArrayAccess) {
			CtExpression<Integer> indexExpression = ((CtArrayAccess<?, CtExpression<Integer>>) e.getParent()).getIndexExpression();
			return ((CtLiteral<Integer>) indexExpression).getValue();
		}
		return null;
	}

	/**
	 * Gets a template field parameter value.
	 */
	public static Object getValue(Template<?> template, String parameterName, Integer index) {
		Field rtField = null;
		try {
			for (Field f : RtHelper.getAllFields(template.getClass())) {
				if (isParameterSource(f)) {
					if (parameterName.equals(getParameterName(f))) {
						rtField = f;
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new UndefinedParameterException(e);
		}
		Object tparamValue = getValue(template, parameterName, rtField);
		if (rtField.getType().isArray() && (index != null)) {
			tparamValue = ((Object[]) tparamValue)[index];
		}
		return tparamValue;
	}
	private static Object getValue(Template<?> template, String parameterName, Field rtField) {
		if (rtField == null) {
			throw new UndefinedParameterException();
		}
		try {
			if (Modifier.isFinal(rtField.getModifiers())) {
				Map<String, Object> m = finals.get(template);
				if (m == null) {
					//BUG: parameters marked as final will always return null, even if they have a value!
					return null;
				}
				return m.get(parameterName);
			}
			rtField.setAccessible(true);
			return rtField.get(template);
		} catch (Exception e) {
			throw new UndefinedParameterException(e);
		}
	}

	static Map<Template<?>, Map<String, Object>> finals = new HashMap<>();

	public static CtField<?> getParameterField(CtClass<? extends Template<?>> templateClass, String parameterName) {
		for (CtTypeMember typeMember : templateClass.getTypeMembers()) {
			if (!(typeMember instanceof CtField)) {
				continue;
			}
			CtField<?> f = (CtField<?>) typeMember;
			Parameter p = f.getAnnotation(Parameter.class);
			if (p == null) {
				continue;
			}
			if (f.getSimpleName().equals(parameterName)) {
				return f;
			}
			if (parameterName.equals(p.value())) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Sets a template field parameter value.
	 */
	@SuppressWarnings("null")
	public static void setValue(Template<?> template, String parameterName, Integer index, Object value) {
		try {
			Field rtField = null;
			for (Field f : RtHelper.getAllFields(template.getClass())) {
				if (isParameterSource(f)) {
					if (parameterName.equals(getParameterName(f))) {
						rtField = f;
						break;
					}
				}
			}
			if (rtField == null) {
				return;
			}
			if (Modifier.isFinal(rtField.getModifiers())) {
				Map<String, Object> m = finals.get(template);
				if (m == null) {
					finals.put(template, m = new HashMap<>());
				}
				m.put(parameterName, value);
				return;
			}
			rtField.setAccessible(true);
			rtField.set(template, value);
			if (rtField.getType().isArray()) {
				// TODO: RP: THIS IS WRONG!!!! tparamValue is never used or set!
			}
		} catch (Exception e) {
			throw new UndefinedParameterException();
		}
	}

	private static String getParameterName(Field f) {
		String name = f.getName();
		Parameter p = f.getAnnotation(Parameter.class);
		if ((p != null) && !p.value().isEmpty()) {
			name = p.value();
		}
		return name;
	}

	private static String getParameterName(CtFieldReference<?> f) {
		String name = f.getSimpleName();
		Parameter p = f.getDeclaration().getAnnotation(Parameter.class);
		if ((p != null) && !p.value().isEmpty()) {
			name = p.value();
		}
		return name;
	}

	/**
	 * Gets the names of all the template parameters of a given template type
	 * (including the ones defined by the super types).
	 */
	public static List<String> getNames(CtClass<? extends Template<?>> templateType) {
		List<String> params = new ArrayList<>();
		try {
			for (CtFieldReference<?> f : templateType.getAllFields()) {
				if (isParameterSource(f)) {
					params.add(getParameterName(f));
				}
			}
		} catch (Exception e) {
			throw new SpoonException("Getting of template parameters failed", e);
		}
		return params;
	}
	/**
	 * Gets the Map of names to template parameter value for all the template parameters of a given template type
	 * (including the ones defined by the super types).
	 */
	public static Map<String, Object> getNamesToValues(Template<?> template, CtClass<? extends Template<?>> templateType) {
		//use linked hash map to assure same order of parameter names. There are cases during substitution of parameters when substitution order matters. E.g. SubstitutionVisitor#substituteName(...)
		Map<String, Object> params = new LinkedHashMap<>();
		try {
			for (CtFieldReference<?> f : templateType.getAllFields()) {
				if (isParameterSource(f)) {
					String parameterName = getParameterName(f);
					params.put(parameterName, getValue(template, parameterName, (Field) f.getActualField()));
				}
			}
		} catch (Exception e) {
			throw new SpoonException("Getting of template parameters failed", e);
		}
		return params;
	}

	/**
	 * Gets the Map of names to template parameter values for all the template parameters of a given template type
	 * + adds mapping of template model reference to target type as parameter too
	 * @param f
	 * 		the factory
	 * @param targetType
	 * 		the target type of the substitution (can be null), which will be done with result parameters
	 * @param template
	 * 		the template that holds the parameter values
	 */
	public static Map<String, Object> getTemplateParametersAsMap(Factory f, CtType<?> targetType, Template<?> template) {
		Map<String, Object> params = new HashMap<>(getNamesToValues(template, (CtClass) f.Class().get(template.getClass())));
		//detect reference to to be generated type
		CtTypeReference<?> targetTypeRef = targetType == null ? null : targetType.getReference();
		if (targetType == null) {
			//legacy templates has target type stored under variable whose name was equal to simple name of template type
			Object targetTypeObject = params.get(template.getClass().getSimpleName());
			if (targetTypeObject != null) {
				if (targetTypeObject instanceof CtTypeReference<?>) {
					targetTypeRef = (CtTypeReference<?>) targetTypeObject;
				} else if (targetTypeObject instanceof String) {
					targetTypeRef = f.Type().createReference((String) targetTypeObject);
				} else if (targetTypeObject instanceof Class) {
					targetTypeRef = f.Type().createReference((Class<?>) targetTypeObject);
				} else  {
					throw new SpoonException("Unsupported definition of target type by value of class " + targetTypeObject.getClass());
				}
			}
		}
		/*
		 * there is required to replace all template model references by target type reference.
		 * Handle that request as template parameter too
		 */
		if (targetTypeRef != null) {
			params.put(PatternBuilder.TARGET_TYPE, targetTypeRef);
		}
		return params;
	}

	/**
	 * Tells if a given field is a template parameter.
	 */
	public static boolean isParameterSource(CtFieldReference<?> ref) {
		CtField<?> field = ref.getDeclaration();
		if (field == null) {
			// we must have the source of this fieldref, otherwise we cannot use it as template parameter
			return false;
		}
		if (field.getAnnotation(Parameter.class) != null) {
			//it is the template field which represents template parameter, because of "Parameter" annotation
			return true;
		}
		if (ref.getType() instanceof CtTypeParameterReference) {
			//the template fields, which are using generic type like <T>, are not template parameters
			return false;
		}
		if ("this".equals(ref.getSimpleName())) {
			//the reference to this is not template parameter
			return false;
		}
		//the type of template field is TemplateParameter.
		return ref.getType().isSubtypeOf(getTemplateParameterType(ref.getFactory()));
	}

	/**
	 * Tells if a given field is a template parameter.
	 */
	public static boolean isParameterSource(Field field) {
		return (field.getAnnotation(Parameter.class) != null) || TemplateParameter.class.isAssignableFrom(field.getType());
	}

	static CtTypeReference<TemplateParameter<?>> templateParameterType;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static synchronized CtTypeReference<TemplateParameter<?>> getTemplateParameterType(Factory factory) {
		if (templateParameterType == null) {
			templateParameterType = (CtTypeReference) factory.Type().createReference(TemplateParameter.class);
		}
		return templateParameterType;
	}

	/**
	 * Creates an empty template parameter of the <code>T</code> type where
	 * {@link TemplateParameter#S()} does not return <code>null</code> in case
	 * the template code needs to be executed such as in static initializers.
	 */
	@SuppressWarnings("unchecked")
	public static <T> TemplateParameter<T> NIL(Class<? extends T> type) {
		if (Number.class.isAssignableFrom(type)) {
			return (TemplateParameter<T>) new TemplateParameter<Number>() {
				@Override
				public Number S() {
					return 0;
				}
			};
		}
		return new TemplateParameter<T>() {
			@Override
			public T S() {
				return null;
			}
		};
	}

	/**
	 * returns all the runtime fields of a template representing a template parameter
	 */
	public static List<Field> getAllTemplateParameterFields(Class<? extends Template> clazz) {
		if (!Template.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException();
		}

		List<Field> result = new ArrayList<>();
		for (Field f : RtHelper.getAllFields(clazz)) {
			if (isParameterSource(f)) {
				result.add(f);
			}
		}

		return result;
	}

	/**
	 * returns all the compile_time fields of a template representing a template parameter
	 */
	public static List<CtField<?>> getAllTemplateParameterFields(Class<? extends Template<?>> clazz, Factory factory) {
		CtClass<?> c = factory.Class().get(clazz);
		if (c == null) {
			throw new IllegalArgumentException("Template not in template classpath");
		}

		List<CtField<?>> result = new ArrayList<>();

		for (Field f : getAllTemplateParameterFields(clazz)) {
			result.add(c.getField(f.getName()));
		}

		return result;
	}

}
