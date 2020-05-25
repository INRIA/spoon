/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path;


import spoon.SpoonException;
import spoon.reflect.path.impl.AbstractPathElement;
import spoon.reflect.path.impl.CtNamedPathElement;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.path.impl.CtTypedNameElement;
import spoon.reflect.path.impl.CtRolePathElement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by nicolas on 27/08/2015.
 */
public class CtPathStringBuilder {

	private final Pattern pathPattern = Pattern.compile("([/.#])([^/.#\\[]+)(\\[([^/.#]*)\\])?");
	private final Pattern argumentPattern = Pattern.compile("(\\w+)=([^=\\]]+)");


	private Class load(String name) throws CtPathException {
		// try without name
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ex) {
		}

		// search in spoon.reflect.declaration
		try {
			return Class.forName("spoon.reflect.declaration." + name);
		} catch (ClassNotFoundException ex) {
		}
		// search in
		try {
			return Class.forName("spoon.reflect.code." + name);
		} catch (ClassNotFoundException ex) {
			throw new CtPathException(
					String.format("Unable to locate element with type %s in Spoon model", name));
		}
	}

	private static final String MAIN_DELIMITERS = ".#/";
	private static final String PATH_DELIMITERS = ".#/[";
	private static final String ARG_NAME_DELIMITERS = "=";

	private static final Pattern NAME_MATCHER = Pattern.compile("\\w+");

	/**
	 * Build path from a string representation.
	 *
	 * for example:
	 * new CtPathBuilder().fromString(".spoon.test.path.Foo.foo#statement[index=0]")
	 * Match the first statement of method foo from class spoon.test.path.Foo.
	 *
	 * Some specials characters
	 * . :  match with the given name
	 * # : match with a CtPathRole
	 * / : match with a element type (for example, to match all classes, use /CtClass
	 */
	public CtPath fromString(String pathStr) throws CtPathException {
		CtPathImpl path = new CtPathImpl();

		Tokenizer tokenizer = new Tokenizer(pathStr);
		String token = tokenizer.getNextToken(MAIN_DELIMITERS);
		while (token != null) {
			String kind = token;
			CtPathElement pathElement;
			token = tokenizer.getNextToken(PATH_DELIMITERS);
			if (token != null && token.length() == 1 && PATH_DELIMITERS.contains(token)) {
				//nextToken is again path delimiter. It means there is no token value in between
				throw new CtPathException("Path value is missing");
			}
			if (CtNamedPathElement.STRING.equals(kind)) {
				//reg exp cannot be used in string, because `.` and `[` are reserved characters for CtPath
				pathElement = new CtNamedPathElement(token, false);
			} else if (CtTypedNameElement.STRING.equals(kind)) {
				pathElement = new CtTypedNameElement(load(token));
			} else if (CtRolePathElement.STRING.equals(kind)) {
				pathElement = new CtRolePathElement(CtRole.fromName(token));
			} else {
				throw new CtPathException("Unexpected token " + kind);
			}
			token = tokenizer.getNextToken(PATH_DELIMITERS);
			if (AbstractPathElement.ARGUMENT_START.equals(token)) {
				while (true) {
					String argName = tokenizer.getNextToken(ARG_NAME_DELIMITERS);
					if (!NAME_MATCHER.matcher(argName).matches()) {
						throw new CtPathException("Argument name must be a word, but is: " + argName);
					}
					token = tokenizer.getNextToken(ARG_NAME_DELIMITERS);
					if (!AbstractPathElement.ARGUMENT_NAME_SEPARATOR.equals(token)) {
						throw new CtPathException("Expects " + AbstractPathElement.ARGUMENT_NAME_SEPARATOR);
					}
					token = parseArgumentValue(tokenizer, argName, pathElement);
					if ("]".equals(token)) {
						break;
					}
					//read next argument
				}
				token = tokenizer.getNextToken(MAIN_DELIMITERS);
			}
			path.addLast(pathElement);
		}
		return path;
	}

	private static final String ARG_VALUE_DELIMITERS = "[];()";

	/**
	 * @return last token
	 */
	private String parseArgumentValue(Tokenizer tokenizer, String argName, CtPathElement pathElement) {
		StringBuilder argValue = new StringBuilder();
		Deque<String> stack = new ArrayDeque<>();
		while (true) {
			String token = tokenizer.getNextToken(ARG_VALUE_DELIMITERS);
			if ("(".equals(token) || "[".equals(token)) {
				//starts bracket
				stack.push(token);
			} else if (stack.size() > 0) {
				//we are in some brackets. Just wait for end of bracket
				if (")".equals(token)) {
					//closing bracket
					String kind = stack.pop();
					if (!"(".equals(kind)) {
						throw new CtPathException("Unexpected end of bracket " + token);
					}
				} else if ("]".equals(token)) {
					//closing bracket
					String kind = stack.pop();
					if (!"[".equals(kind)) {
						throw new CtPathException("Unexpected end of bracket " + token);
					}
				}
			} else if ("]".equals(token) || ";".equals(token)) {
				//finished reading of argument value
				pathElement.addArgument(argName, argValue.toString());
				return token;
			}
			argValue.append(token);
		}
	}

	private static class Tokenizer {
		StringTokenizer tokenizer;
		int length;
		int off;
		Tokenizer(String str) {
			length = str.length();
			off = 0;
			tokenizer = new StringTokenizer(str, MAIN_DELIMITERS, true);
		}

		String getNextToken(String delimiters) {
			try {
				if (off >= length) {
					return null;
				}
				String token = tokenizer.nextToken(delimiters);
				off += token.length();
				return token;
			} catch (NoSuchElementException e) {
				throw new SpoonException("Unexpected error", e);
			}
		}
	}
}
