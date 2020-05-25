/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JDTBuilderImpl implements JDTBuilder {
	private final List<String> args = new ArrayList<>();
	private boolean hasSources = false;

	@Override
	public JDTBuilder classpathOptions(ClasspathOptions<?> options) {
		checkSources();
		args.addAll(Arrays.asList(options.build()));
		return this;
	}

	@Override
	public JDTBuilder complianceOptions(ComplianceOptions<?> options) {
		checkSources();
		args.addAll(Arrays.asList(options.build()));
		return this;
	}

	@Override
	public JDTBuilder annotationProcessingOptions(AnnotationProcessingOptions<?> options) {
		checkSources();
		args.addAll(Arrays.asList(options.build()));
		return this;
	}

	@Override
	public JDTBuilder advancedOptions(AdvancedOptions<?> options) {
		checkSources();
		args.addAll(Arrays.asList(options.build()));
		return this;
	}

	@Override
	public JDTBuilder sources(SourceOptions<?> options) {
		hasSources = true;
		args.addAll(Arrays.asList(options.build()));
		return this;
	}

	@Override
	public String[] build() {
		return args.toArray(new String[0]);
	}

	private void checkSources() {
		if (hasSources) {
			throw new RuntimeException("Please, specify sources at the end.");
		}
	}
}
