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

	public T sources(String sources) {
		if (sources == null || sources.isEmpty()) {
			return myself;
		}
		return sources(sources.split(File.pathSeparator));
	}

	public T sources(String... sources) {
		if (sources == null || sources.length == 0) {
			args.add(".");
			return myself;
		}
		args.addAll(Arrays.asList(sources));
		return myself;
	}

	public T sources(List<SpoonFile> sources) {
		if (sources == null || sources.size() == 0) {
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
					IOUtils.copy(source.getContent(), new FileOutputStream(file));
					args.add(file.toString());
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
		return myself;
	}
}
