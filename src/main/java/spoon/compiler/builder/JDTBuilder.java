/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

/**
 * Helper to build arguments for the JDT compiler.
 */
public interface JDTBuilder {
	/**
	 * Classpath options for the compiler.
	 */
	JDTBuilder classpathOptions(ClasspathOptions<?> options);

	/**
	 * Compliance options for the compiler.
	 */
	JDTBuilder complianceOptions(ComplianceOptions<?> options);

	/**
	 * Annotation processing options for the compiler.
	 */
	JDTBuilder annotationProcessingOptions(AnnotationProcessingOptions<?> options);

	/**
	 * Advanced options for the compiler.
	 */
	JDTBuilder advancedOptions(AdvancedOptions<?> options);

	/**
	 * Sources for the compiler.
	 */
	JDTBuilder sources(SourceOptions<?> options);

	/**
	 * Builds arguments.
	 */
	String[] build();
}
