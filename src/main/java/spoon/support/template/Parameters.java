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
package spoon.support.template;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.Launcher;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.util.RtHelper;
import spoon.template.Parameter;
import spoon.template.Template;
import spoon.template.TemplateParameter;

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
		Object tparamValue = null;
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
			if (Modifier.isFinal(rtField.getModifiers())) {
				Map<String, Object> m = finals.get(template);
				if (m == null) {
					return null;
				}
				return m.get(parameterName);
			}
			rtField.setAccessible(true);
			tparamValue = rtField.get(template);
			if (rtField.getType().isArray() && (index != null)) {
				tparamValue = ((Object[]) tparamValue)[index];
			}
		} catch (Exception e) {
			throw new UndefinedParameterException();
		}
		return tparamValue;
	}

	static Map<Template<?>, Map<String, Object>> finals = new HashMap<Template<?>, Map<String, Object>>();

	public static CtField<?> getParameterField(CtClass<? extends Template<?>> templateClass, String parameterName) {
		for (CtField<?> f : templateClass.getFields()) {
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
		Object tparamValue = null;
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
					finals.put(template, m = new HashMap<String, Object>());
				}
				m.put(parameterName, value);
				return;
			}
			rtField.setAccessible(true);
			rtField.set(template, value);
			if (rtField.getType().isArray()) {
				// TODO: RP: THIS IS WRONG!!!! tparamValue is never used or
				// set!!
				tparamValue = ((Object[]) tparamValue)[index];
			}
		} catch (Exception e) {
			throw new UndefinedParameterException();
		}
	}

	private static String getParameterName(Field f) {
		String name = f.getName();
		Parameter p = f.getAnnotation(Parameter.class);
		if ((p != null) && !p.value().equals("")) {
			name = p.value();
		}
		return name;
	}

	private static String getParameterName(CtFieldReference<?> f) {
		String name = f.getSimpleName();
		Parameter p = f.getDeclaration().getAnnotation(Parameter.class);
		if ((p != null) && !p.value().equals("")) {
			name = p.value();
		}
		return name;
	}

	/**
	 * Gets the names of all the template parameters of a given template type
	 * (including the ones defined by the super types).
	 */
	public static Collection<String> getNames(CtClass<? extends Template<?>> templateType) {
		Collection<String> params = new ArrayList<String>();
		try {
			for (CtFieldReference<?> f : templateType.getReference().getAllFields()) {
				if (isParameterSource(f)) {
					params.add(getParameterName(f));
				}
			}
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return params;
	}

	/**
	 * Tells if a given field is a template parameter.
	 */
	public static boolean isParameterSource(CtFieldReference<?> ref) {
		try {
			return (ref.getDeclaration() != null // we must have the source of
					// this fieldref
					&& ref.getDeclaration().getAnnotation(Parameter.class) != null) || (!((ref.getType() instanceof CtTypeParameterReference) || ref.getSimpleName().equals("this"))
					&& getTemplateParameterType(ref.getFactory()).isAssignableFrom(ref.getType()));
		} catch (RuntimeException e) {
			// if (e.getCause() instanceof ClassNotFoundException) {
			// return false;
			// } else {
			throw e;
			// }
		}
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
				public Number S() {
					return 0;
				}
			};
		}
		return new TemplateParameter<T>() {
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

		List<Field> result = new ArrayList<Field>();
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

		List<CtField<?>> result = new ArrayList<CtField<?>>();

		for (Field f : getAllTemplateParameterFields(clazz)) {
			result.add(c.getField(f.getName()));
		}

		return result;
	}

}
