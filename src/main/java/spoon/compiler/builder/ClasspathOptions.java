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
