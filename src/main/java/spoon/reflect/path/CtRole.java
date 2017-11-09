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
 * Identifies the roles of attributes of spoon model.
 */
public enum CtRole {
	NAME,
	TYPE,
	DECLARING_TYPE,
	CONTAINED_TYPE,
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
	ARGUMENT_TYPE,
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
	EXECUTABLE_REF,
	METHOD,
	ANNONYMOUS_EXECUTABLE,
	FIELD,
	TYPE_MEMBER,
	CAST,
	VALUE,
	FOR_UPDATE,
	FOR_INIT,
	FOREACH_VARIABLE,
	TRY_RESOURCE,
	DIMENSION,
	CATCH,
	TARGET_LABEL,
	TYPE_PARAMETER,
	TYPE_ARGUMENT,
	COMMENT_TAG,
	COMMENT_CONTENT,
	COMMENT_TYPE,
	DOCUMENTATION_TYPE,
	JAVADOC_TAG_VALUE,
	POSITION,
	SNIPPET,
	ACCESSED_TYPE;

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
