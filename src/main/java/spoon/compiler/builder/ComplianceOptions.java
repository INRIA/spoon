/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

public class ComplianceOptions<T extends ComplianceOptions<T>> extends Options<T> {
	public ComplianceOptions() {
		super(ComplianceOptions.class);
	}

	public T compliance(int version) {
		if (version < 10) {
			args.add("-1." + version);
		} else {
			args.add("-" + version);
		}

		return myself;
	}

	public T enablePreview() {
		args.add("--enable-preview");
		return myself;
	}
}
