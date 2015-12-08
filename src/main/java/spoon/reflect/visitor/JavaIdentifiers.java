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
package spoon.reflect.visitor;

import java.util.Arrays;
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
	public static Set<String> KEYWORDS;

	static {
		KEYWORDS = new HashSet<String>();
		for (JavaIdentifiers kw : Arrays.asList(JavaIdentifiers.values())) {
			KEYWORDS.add(kw.name().toLowerCase());
		}
	}

	static boolean isJavaIdentifier(String s) {
		if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
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
		if (string.equals("<init>")) {
			return true;
		}
		return isLegalJavaIdentifier(string);
	}

}
