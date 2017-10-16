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
	NAME,
	TYPE,
	DECLARING_TYPE,
	BODY,
	IS_SHADOW,
	BOUND, // in reference only
	BOUNDING_TYPE, // in reference only
	IS_FINAL, // in reference only
	IS_STATIC, // in reference only
	IS_UPPER, // in reference only
	IS_IMPLICIT,
	IS_DEFAULT,
	IS_VARARGS,
	DEFAULT_EXPRESSION,
	THEN,
	ELSE,
	PACKAGE_REF,
	SUB_PACKAGE,
	CONDITION,
	RIGHT_OPERAND,
	LEFT_OPERAND,
	LABEL,
	CASE,
	OPERATOR_KIND,
	PARAMETER,
	EXPRESSION,
	TARGET,
	VARIABLE,
	FINALIZER,
	THROWN,
	ASSIGNMENT,
	ASSIGNED,
	MODIFIER,
	COMMENT,
	ANNOTATION_TYPE,
	INTERFACE,
	ANNOTATION,
	STATEMENT,
	ARGUMENT,
	SUPER_TYPE,
	NESTED_TYPE,
	CONSTRUCTOR,
	EXECUTABLE,
	FIELD,
	CAST,
	VALUE,
	FOR_UPDATE,
	FOR_INIT,
	TRY_RESOURCE,
	DIMENSION,
	CATCH,
	TARGET_LABEL,
	TYPE_PARAMETER,
	COMMENT_TAG,
	COMMENT_CONTENT,
	COMMENT_TYPE,
	POSITION;

	/**
	 * Get the {@link CtRole} associated to the field name
	 * @param name
	 * @return
	 */
	public static CtRole fromName(String name) {
		for (int i = 0; i < CtRole.values().length; i++) {
			if (CtRole.values()[i].name().equals(name) || CtRole.values()[i].getCamelCaseName().toLowerCase().equals(name.toLowerCase())) {
				return CtRole.values()[i];
			}
		}
		name = name.toLowerCase();
		if ("implicit".equals(name)) {
			return IS_IMPLICIT;
		}
		if ("varargs".equals(name)) {
			return IS_VARARGS;
		}
		if ("defaultmethod".equals(name)) {
			return IS_DEFAULT;
		}
		if ("catchers".equals(name)) {
			return CATCH;
		}
		if ("tags".equals(name)) {
			return COMMENT_TAG;
		}
		if ("content".equals(name)) {
			return COMMENT_CONTENT;
		}
		if ("upper".equals(name)) {
			return IS_UPPER;
		}
		if ("block".equals(name)) {
			return BODY;
		}
		if ("param".equals(name)) {
			return PARAMETER;
		}
		if ("dimensionexpressions".equals(name)) {
			return DIMENSION;
		}
		if ("actualtypearguments".equals(name)) {
			return TYPE_PARAMETER;
		}
		if ("formalcttypeparameters".equals(name)) {
			return TYPE_PARAMETER;
		}
		if ("typecasts".equals(name)) {
			return CAST;
		}
		if ("kind".equals(name)) {
			return OPERATOR_KIND;
		}
		if ("resources".equals(name)) {
			return TRY_RESOURCE;
		}
		if ("cases".equals(name)) {
			return CASE;
		}
		if ("enumvalues".equals(name) || "elementvalues".equals(name) || "valuesmethod".equals(name)) {
			return VALUE;
		}
		if ("throwntypes".equals(name)) {
			return THROWN;
		}
		if ("value".equals(name) || "returnedexpression".equals(name) || "expressions".equals(name) || "anonymousclass".equals(name) || "operand".equals(name)) {
			return EXPRESSION;
		}
		if ("asserted".equals(name)) {
			return CONDITION;
		}
		if ("parameters".equals(name)) {
			return PARAMETER;
		}
		if ("typemembers".equals(name)) {
			return FIELD;
		}
		if ("throwexpression".equals(name)) {
			return THROWN;
		}
		if ("declaringtype".equals(name)) {
			return DECLARING_TYPE;
		}
		if ("boundingtype".equals(name)) {
			return BOUNDING_TYPE;
		}
		if ("returntype".equals(name)
				|| "componenttype".equals(name)) {
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
			return SUB_PACKAGE;
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

	public String getTitleName() {
		String s = getCamelCaseName();
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	@Override
	public String toString() {
		return getCamelCaseName();
	}
}
