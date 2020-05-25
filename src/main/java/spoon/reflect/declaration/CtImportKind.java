/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

public enum CtImportKind {
	TYPE, // import my.package.Type;
	ALL_TYPES, // import my.package.*;
	ALL_STATIC_MEMBERS, // import static my.package.Type.*;
	FIELD, // import static my.package.Type.f;
	METHOD, // import static my.package.Type.m;
	UNRESOLVED // Any of the above when in mode no classpath and the reference cannot be resolved.
	// It is then stored as a pure String that will be printed as is when pretty printed.
}
