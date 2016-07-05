/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.path.impl.CtNamedPathElement;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.reflect.path.impl.CtTypedNameElement;

import java.util.regex.Matcher;
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
					String.format("Unable to locate element with name $s in Spoon model", name));
		}
	}

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
		Matcher matcher = pathPattern.matcher(pathStr);

		CtPathImpl path = new CtPathImpl();
		while (matcher.find()) {
			String kind = matcher.group(1);

			CtPathElement pathElement = null;
			if (CtNamedPathElement.STRING.equals(kind)) {
				pathElement = new CtNamedPathElement(matcher.group(2));
			} else if (CtTypedNameElement.STRING.equals(kind)) {
				pathElement = new CtTypedNameElement(load(matcher.group(2)));
			} else if (CtRolePathElement.STRING.equals(kind)) {
				pathElement = new CtRolePathElement(CtPathRole.fromName(matcher.group(2)));
			}

			String args = matcher.group(4);
			if (args != null) {
				for (String arg : args.split(";")) {
					Matcher argmatcher = argumentPattern.matcher(arg);
					if (argmatcher.matches()) {
						pathElement.addArgument(argmatcher.group(1), argmatcher.group(2));
					}
				}
			}

			path.addLast(pathElement);
		}
		return path;
	}

}
