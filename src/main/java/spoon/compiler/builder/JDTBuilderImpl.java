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
		return args.toArray(new String[args.size()]);
	}

	private void checkSources() {
		if (hasSources) {
			throw new RuntimeException("Please, specify sources at the end.");
		}
	}
}
