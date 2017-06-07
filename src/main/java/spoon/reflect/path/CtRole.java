/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.path;

/**
 * Created by nicolas on 27/08/2015.
 */
public enum CtRole {
	PARENT,
	NAME,
	DECLARING_TYPE,
	TYPE,
	BODY,
	IS_FINAL,
	IS_SHADOW,
	IS_STATIC,
	IS_IMPLICIT,
	IS_DEFAULT,
	IS_VARARGS,
	IS_UPPER,
	DEFAULT_EXPRESSION,
	THEN,
	ELSE,
	PACKAGE,
	CONDITION,
	SUPER_TYPE,
	POSITION,
	RIGHT_OPERAND,
	LEFT_OPERAND,
	LABEL,
	CASE,
	KIND,
	PARAMETER,
	EXPRESSION,
	TARGET,
	OPERAND,
	VARIABLE,
	FINALIZER,
	THROW,
	EXECUTABLE,
	ASSIGNMENT,
	ASSIGNED,
	MODIFIERS,
	COMMENTS,
	TYPES,
	INTERFACES,
	ANNOTATIONS,
	STATEMENTS,
	ARGUMENTS,
	MEMBERS,
	CASTS,
	VALUES,
	FOR_UPDATE,
	FOR_INIT,
	RESOURCES,
	DIMENSIONS,
	BOUNDS,
	CATCHERS,
	ANONYMOUS_CLASS,
	TARGET_LABEL,
	TYPE_PARAMETERS,
	CONTENT,
	TAGS;

	/**
	 * Get the {@link CtRole} associated to the field name
	 * @param name
	 * @return
	 */
	public static CtRole fromName(String name) {
		name = name.toLowerCase();
		for (int i = 0; i < CtRole.values().length; i++) {
			if (CtRole.values()[i].getCamelCaseName().toLowerCase()
					.equals(name)) {
				return CtRole.values()[i];
			}
		}
		if ("implicit".equals(name)) {
			return IS_IMPLICIT;
		}
		if ("fina".equals(name)) {
			return IS_FINAL;
		}
		if ("stat".equals(name)) {
			return IS_STATIC;
		}
		if ("varargs".equals(name)) {
			return IS_VARARGS;
		}
		if ("defaultmethod".equals(name)) {
			return IS_DEFAULT;
		}
		if ("block".equals(name)) {
			return BODY;
		}
		if ("param".equals(name)) {
			return PARAMETER;
		}
		if ("dimensionexpressions".equals(name)) {
			return DIMENSIONS;
		}
		if ("actualtypearguments".equals(name)) {
			return TYPE_PARAMETERS;
		}
		if ("formalcttypeparameters".equals(name)) {
			return TYPE_PARAMETERS;
		}
		if ("typecasts".equals(name)) {
			return CASTS;
		}
		if ("cases".equals(name)) {
			return CASE;
		}
		if ("labelledstatement".equals(name)) {
			return LABEL;
		}
		if ("enumvalues".equals(name) || "elementvalues".equals(name)) {
			return VALUES;
		}
		if ("throwntypes".equals(name)) {
			return THROW;
		}
		if ("value".equals(name) || "returnedexpression".equals(name) || "expressions".equals(name)) {
			return EXPRESSION;
		}
		if ("asserted".equals(name)) {
			return CONDITION;
		}
		if ("parameters".equals(name)) {
			return PARAMETER;
		}
		if ("typemembers".equals(name)) {
			return MEMBERS;
		}
		if ("throwexpression".equals(name)) {
			return THROW;
		}
		if ("returntype".equals(name)
				|| "componenttype".equals(name)
				|| "annotationtype".equals(name)) {
			return TYPE;
		}
		if ("caseexpression".equals(name)) {
			return CASE;
		}
		if ("elseexpression".equals(name) || "elsestatement".equals(name)) {
			return ELSE;
		}
		if ("thenexpression".equals(name) || "thenstatement".equals(name)) {
			return THEN;
		}
		if ("righthandoperand".equals(name)) {
			return RIGHT_OPERAND;
		}
		if ("lefthandoperand".equals(name)) {
			return LEFT_OPERAND;
		}
		if ("pack".equals(name) || "packs".equals(name)) {
			return PACKAGE;
		}
		if ("superclass".equals(name)) {
			return SUPER_TYPE;
		}
		if ("name".equals(name) || "simplename".equals(name)) {
			return NAME;
		}
		return null;
	}

	/**
	 * Get the camel case representation of the name
	 * @return the name in camel case
	 */
	public String getCamelCaseName() {
		String s = name().toLowerCase();
		int i = s.indexOf("_");
		if (i != -1) {
			s = s.substring(0, i) + Character.toUpperCase(s.charAt(i + 1)) + s.substring(i + 2);
		}
		return s;
	}

	@Override
	public String toString() {
		return getCamelCaseName();
	}
}
