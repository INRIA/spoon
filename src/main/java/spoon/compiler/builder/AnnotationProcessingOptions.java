/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

public class AnnotationProcessingOptions<T extends AnnotationProcessingOptions<T>> extends Options<T> {
	public AnnotationProcessingOptions() {
		super(AnnotationProcessingOptions.class);
	}

	public T processors(String processors) {
		if (processors == null || processors.isEmpty()) {
			return myself;
		}
		args.add("-processor");
		args.add(processors);
		return myself;
	}

	public T processors(String... processors) {
		if (processors == null || processors.length == 0) {
			return myself;
		}
		return processors(join(COMMA_DELIMITER, processors));
	}

	public T runProcessors() {
		args.add("-proc:only");
		return myself;
	}

	public T compileProcessors() {
		args.add("-proc:none");
		return myself;
	}
}
