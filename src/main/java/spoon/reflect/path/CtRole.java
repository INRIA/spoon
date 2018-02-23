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
import java.util.Collections;
import java.util.List;

/**
 * Identifies the roles of attributes of spoon model.
 */
public enum CtRole {
	NAME,
	TYPE,
	MULTI_TYPE,
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
	TYPE_MEMBER,
	NESTED_TYPE(TYPE_MEMBER),
	CONSTRUCTOR(TYPE_MEMBER),
	METHOD(TYPE_MEMBER),
	ANNONYMOUS_EXECUTABLE(TYPE_MEMBER),
	FIELD(TYPE_MEMBER),
	EXECUTABLE_REF,
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
	ACCESSED_TYPE,
	IMPORT_REFERENCE,
	MODULE_DIRECTIVE,
	REQUIRED_MODULE(MODULE_DIRECTIVE),
	MODULE_REF,
	EXPORTED_PACKAGE(MODULE_DIRECTIVE),
	OPENED_PACKAGE(MODULE_DIRECTIVE),
	SERVICE_TYPE(MODULE_DIRECTIVE),
	IMPLEMENTATION_TYPE,
	PROVIDED_SERVICE(MODULE_DIRECTIVE);

	private final CtRole superRole;
	private final List<CtRole> subRoles;
	private List<CtRole> initSubRoles;

	CtRole() {
		this(null);
	}
	CtRole(CtRole superRole) {
		this.superRole = superRole;
		this.initSubRoles = new ArrayList<>(0);
		this.subRoles = Collections.unmodifiableList(this.initSubRoles);
		if (superRole != null) {
			superRole.initSubRoles.add(this);
		}
	}

	static {
		//after all are initialized, avoid further modification
		for (CtRole role : CtRole.values()) {
			role.initSubRoles = null;
		}
	}

	/**
	 * Get the {@link CtRole} associated to the field name
	 * @param name
	 * @return
	 */
	public static CtRole fromName(String name) {
		for (CtRole role : CtRole.values()) {
			if (role.getCamelCaseName().toLowerCase().equals(name.toLowerCase()) || role.name().equals(name)) {
				return role;
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
		String[] tokens = s.split("_");
		if (tokens.length == 1) {
			return s;
		} else {
			StringBuilder buffer = new StringBuilder(tokens[0]);
			for (int i = 1; i < tokens.length; i++) {
				String t = tokens[i];
				buffer.append(Character.toUpperCase(t.charAt(0)));
				buffer.append(t.substring(1));
			}
			return buffer.toString();
		}
	}

	@Override
	public String toString() {
		return getCamelCaseName();
	}

	/**
	 * @return the CtRole which is the real holder of this virtual CtRole or null if there is no super role.
	 * 	For example {@link #TYPE_MEMBER} is super role of {@link #CONSTRUCTOR}, {@link #FIELD}, {@link #METHOD}, {@link #NESTED_TYPE}
	 */
	public CtRole getSuperRole() {
		return superRole;
	}

	/**
	 * @return sub roles of this super role or empty array if there is no sub role.
	 * 	For example {@link #TYPE_MEMBER} is super role of {@link #CONSTRUCTOR}, {@link #FIELD}, {@link #METHOD}, {@link #NESTED_TYPE}
	 *
	 */
	public List<CtRole> getSubRoles() {
		return subRoles;
	}
}
