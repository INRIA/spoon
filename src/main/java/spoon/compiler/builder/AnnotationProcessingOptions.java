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
