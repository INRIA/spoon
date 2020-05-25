/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

import org.apache.commons.io.IOUtils;
import spoon.compiler.SpoonFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SourceOptions<T extends SourceOptions<T>> extends Options<T> {
	public SourceOptions() {
		super(SourceOptions.class);
	}

	/** adds the given paths as concatenated string with File.pathSeparator as sources */
	public T sources(String sources) {
		if (sources == null || sources.isEmpty()) {
			return myself;
		}
		return sources(sources.split(File.pathSeparator));
	}

	/** adds the given paths as sources */
	public T sources(String... sources) {
		if (sources == null || sources.length == 0) {
			args.add(".");
			return myself;
		}
		args.addAll(Arrays.asList(sources));
		return myself;
	}

	/** adds the given {@link spoon.compiler.SpoonFile} as sources */
	public T sources(List<SpoonFile> sources) {
		if (sources == null || sources.isEmpty()) {
			args.add(".");
			return myself;
		}
		for (SpoonFile source : sources) {
			if (source.isActualFile()) {
				args.add(source.toString());
			} else {
				try {
					File file = File.createTempFile(source.getName(), ".java");
					file.deleteOnExit();
					try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
						IOUtils.copy(source.getContent(), fileOutputStream);
					}
					args.add(file.toString());
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
		return myself;
	}
}
