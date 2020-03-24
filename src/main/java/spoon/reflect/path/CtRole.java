/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import spoon.SpoonException;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtUsedService;
import spoon.support.Internal;

/**
 * Identifies the roles of attributes of spoon model.
 */
public enum CtRole {
	NAME,
	TYPE,
	MULTI_TYPE,
	DECLARING_TYPE,
	DECLARED_TYPE,
	DECLARED_TYPE_REF,
	DECLARED_MODULE,
	DECLARED_MODULE_REF,
	PACKAGE_DECLARATION,
	DECLARED_IMPORT,
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
	EMODIFIER,
	COMMENT,
	ANNOTATION_TYPE,
	INTERFACE,
	ANNOTATION,
	STATEMENT,
	ARGUMENT,
	SUPER_TYPE,
	TYPE_MEMBER,
	NESTED_TYPE(TYPE_MEMBER, obj -> obj instanceof CtType),
	CONSTRUCTOR(TYPE_MEMBER, obj -> obj instanceof CtConstructor),
	METHOD(TYPE_MEMBER, obj -> obj instanceof CtMethod),
	ANNONYMOUS_EXECUTABLE(TYPE_MEMBER, obj -> obj instanceof CtAnonymousExecutable),
	FIELD(TYPE_MEMBER, obj -> obj instanceof CtField),
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
	REQUIRED_MODULE(MODULE_DIRECTIVE, obj -> obj instanceof CtModuleRequirement),
	MODULE_REF,
	EXPORTED_PACKAGE(MODULE_DIRECTIVE, obj -> obj instanceof CtPackageExport && !((CtPackageExport) obj).isOpenedPackage()),
	OPENED_PACKAGE(MODULE_DIRECTIVE, obj -> obj instanceof CtPackageExport && ((CtPackageExport) obj).isOpenedPackage()),
	SERVICE_TYPE(MODULE_DIRECTIVE, obj -> obj instanceof CtUsedService),
	IMPLEMENTATION_TYPE,
	PROVIDED_SERVICE(MODULE_DIRECTIVE, obj -> obj instanceof CtProvidedService),
	IS_INFERRED,
	TYPE_REF,
	LITERAL_BASE,
	CASE_KIND;

	private final CtRole superRole;
	private final List<CtRole> subRoles;
	private final Predicate<Object> predicate;
	private List<CtRole> initSubRoles;

	CtRole() {
		this(null, null);
	}
	CtRole(CtRole superRole, Predicate<Object> predicate) {
		this.superRole = superRole;
		this.initSubRoles = new ArrayList<>(0);
		this.subRoles = Collections.unmodifiableList(this.initSubRoles);
		this.predicate = predicate;
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

	/**
	 * @return sub role of this role, which match `item`.
	 *
	 * <pre><code>
	 * CtMethod method = ...
	 * CtRole role = CtRole.TYPE_MEMBER.getMatchingSubRoleFor(method);
	 * </code></pre>
	 */
	@Internal
	public CtRole getMatchingSubRoleFor(CtElement item) {
		if (item == null) {
			throw new SpoonException("Cannot detect sub role for null.");
		}
		for (CtRole subRole : this.subRoles) {
			if (subRole.predicate.test(item)) {
				return subRole;
			}
		}
		throw new SpoonException("There is no sub role of CtRole." + name() + " for item class " + item.getClass());
	}
}
