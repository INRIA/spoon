/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

import java.io.File;

public class ClasspathOptions<T extends ClasspathOptions<T>> extends Options<T> {
	public ClasspathOptions() {
		super(ClasspathOptions.class);
	}

	public T classpath(String classpath) {
		if (classpath == null) {
			return myself;
		}
		args.add("-cp");
		args.add(classpath);
		return myself;
	}

	public T classpath(String... classpaths) {
		if (classpaths == null || classpaths.length == 0) {
			return myself;
		}
		return classpath(join(File.pathSeparator, classpaths));
	}

	public T bootclasspath(String bootclasspath) {
		if (bootclasspath == null) {
			return myself;
		}
		args.add("-bootclasspath");
		args.add(bootclasspath);
		return myself;
	}

	public T bootclasspath(String... bootclasspaths) {
		if (bootclasspaths == null || bootclasspaths.length == 0) {
			return myself;
		}
		return bootclasspath(join(File.pathSeparator, bootclasspaths));
	}

	public T binaries(String directory) {
		if (directory == null) {
			return binaries((File) null);
		}
		return binaries(new File(directory));
	}

	public T binaries(File directory) {
		if (directory == null) {
			args.add("-d");
			args.add("none");
		} else {
			args.add("-d");
			args.add(directory.getAbsolutePath());
		}
		return myself;
	}

	public T encoding(String encoding) {
		if (encoding == null || encoding.isEmpty()) {
			return myself;
		}
		args.add("-encoding");
		args.add(encoding);
		return myself;
	}
}
