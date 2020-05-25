/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.reference.CtExecutableReference;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This enum defines the Java keywords and some helper method to determine if
 * some strings are Java identifiers.
 */
public enum JavaIdentifiers {

	IMPORT, PACKAGE, INTERFACE, CLASS, ENUM, SUPER, THIS, ASSERT, EXTENDS, BREAK, CASE, CATCH, CONTINUE, DO, FOR, IF,
	NEW, RETURN, SWITCH, THROW, TRY, WHILE,

	PUBLIC, PROTECTED, PRIVATE, ABSTRACT, STATIC, FINAL, TRANSIENT, VOLATILE, SYNCHRONIZED, NATIVE, STRICTFP;

	/**
	 * Contains the Java keywords.
	 */
	public static final Set<String> KEYWORDS;

	static {
		KEYWORDS = new HashSet<>();
		for (JavaIdentifiers kw : JavaIdentifiers.values()) {
			KEYWORDS.add(kw.name().toLowerCase());
		}
	}

	static boolean isJavaIdentifier(String s) {
		if (s.isEmpty() || !Character.isJavaIdentifierStart(s.charAt(0))) {
			return false;
		}
		for (int i = 1; i < s.length(); i++) {
			if (!Character.isJavaIdentifierPart(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a string is a valid Java identifier and not a Java keyword.
	 *
	 * @param string
	 * 		the string to be tested
	 */
	public static boolean isLegalJavaIdentifier(String string) {
		if (string == null) {
			return false;
		}
		return !KEYWORDS.contains(string) && isJavaIdentifier(string);
	}

	/**
	 * Checks if a string is a valid Java package identifier.
	 *
	 * @param string
	 * 		the string to be tested
	 */
	public static boolean isLegalJavaPackageIdentifier(String string) {
		if (string == null) {
			return false;
		}
		StringTokenizer st = new StringTokenizer(string, ".");
		while (st.hasMoreElements()) {
			String s = st.nextToken();
			if (!isLegalJavaIdentifier(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a string is a valid Java package identifier.
	 *
	 * @param string
	 * 		the string to be tested
	 */
	public static boolean isLegalJavaExecutableIdentifier(String string) {
		if (string == null) {
			return false;
		}
		if (string.equals(CtExecutableReference.CONSTRUCTOR_NAME)) {
			return true;
		}
		return isLegalJavaIdentifier(string);
	}
}
