/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

import java.util.ArrayList;
import java.util.List;

public abstract class Options<T extends Options<T>> {
	static final String COMMA_DELIMITER = ",";
	final List<String> args = new ArrayList<>();
	final T myself;

	public Options(Class<?> type) {
		this.myself = (T) type.cast(this);
	}

	public String[] build() {
		return args.toArray(new String[0]);
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
