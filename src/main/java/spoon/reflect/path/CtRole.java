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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Identifies the roles of attributes of spoon model.
 */
public enum CtRole {
	NAME("name", "simplename"),
	TYPE("returntype", "componenttype"),
	DECLARING_TYPE("declaringtype"),
	BODY("block"),
	IS_SHADOW,
	BOUND, // in reference only
	BOUNDING_TYPE("boundingtype"), // in reference only
	IS_FINAL, // in reference only
	IS_STATIC, // in reference only
	IS_UPPER, // in reference only
	IS_IMPLICIT("implicit"),
	IS_DEFAULT("defaultmethod"),
	IS_VARARGS("varargs"),
	DEFAULT_EXPRESSION,
	THEN("thenexpression", "thenstatement"),
	ELSE("elseexpression", "elsestatement"),
	PACKAGE_REF,
	SUB_PACKAGE("pack", "packs"),
	CONDITION("asserted"),
	RIGHT_OPERAND("righthandoperand"),
	LEFT_OPERAND("lefthandoperand"),
	LABEL,
	CASE("cases", "caseexpression"),
	OPERATOR_KIND,
	PARAMETER("param", "parameters"),
	EXPRESSION("value", "returnedexpression", "expressions"),
	ARGUMENT_TYPE,
	TARGET,
	VARIABLE,
	FINALIZER,
	THROWN("throwntypes", "throwexpression"),
	ASSIGNMENT,
	ASSIGNED,
	MODIFIER,
	COMMENT,
	ANNOTATION_TYPE,
	INTERFACE,
	ANNOTATION,
	STATEMENT,
	ARGUMENT,
	SUPER_TYPE("superclass"),
	NESTED_TYPE,
	CONSTRUCTOR,
	EXECUTABLE,
	FIELD("typemembers"),
	CAST("typecasts"),
	VALUE("enumvalues", "elementvalues"),
	EXECUTABLE_REF,
	METHOD,
	ANNONYMOUS_EXECUTABLE,
	TYPE_MEMBER,
	FOR_UPDATE,
	FOR_INIT,
	FOREACH_VARIABLE,
	TRY_RESOURCE,
	DIMENSION("dimensionexpressions"),
	CATCH,
	TARGET_LABEL,
	TYPE_PARAMETER("actualtypearguments", "formalcttypeparameters"),
	TYPE_ARGUMENT,
	COMMENT_TAG,
	COMMENT_CONTENT,
	COMMENT_TYPE,
	DOCUMENTATION_TYPE,
	JAVADOC_TAG_VALUE,
	POSITION,
	SNIPPET,
	ACCESSED_TYPE;

	private List<String> names = new ArrayList<>();

	CtRole(String... names) {
		this.names = Arrays.asList(names);
	}

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
		name = name.toLowerCase();
		for (CtRole ctrole : CtRole.values()) {
			if (ctrole.names.contains(name)) {
				return ctrole;
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
