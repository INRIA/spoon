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
package spoon.reflect.path;

/**
 * Created by nicolas on 27/08/2015.
 */
public enum CtPathRole {
	/**
	 * Default value for a field
	 */
	DEFAULT_VALUE("defaultValue"),
	/**
	 * Then part of a CtIf
	 */
	THEN("then"),
	/**
	 * Else part of a CtIf
	 */
	ELSE("else"),
	/**
	 * Body of CtExecutable.
	 */
	BODY("body");

	private final String[] names;

	CtPathRole(String... names) {
		this.names = names;
	}

	public static CtPathRole fromName(String name) {
		for (CtPathRole role : values()) {
			for (String roleName : role.names) {
				if (roleName.equals(name)) {
					return role;
				}
			}
		}
		throw new IllegalArgumentException("no role found with name :" + name);
	}

	@Override
	public String toString() {
		return names[0];
	}
}
