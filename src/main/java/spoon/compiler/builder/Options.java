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
package spoon.compiler.builder;

import java.util.ArrayList;
import java.util.List;

public abstract class Options<T extends Options<T>> {
	final String COMMA_DELIMITER = ",";
	final List<String> args = new ArrayList<String>();
	final T myself;

	public Options(Class<?> type) {
		this.myself = (T) type.cast(this);
	}

	public String[] build() {
		return args.toArray(new String[args.size()]);
	}

	protected String join(String delimiter, String[] classpath) {
		if (classpath == null || classpath.length == 0) {
			return "";
		}
		final StringBuilder builder = new StringBuilder();
		for (String entry : classpath) {
			builder.append(entry);
			builder.append(delimiter);
		}
		return builder.toString();
	}
}
