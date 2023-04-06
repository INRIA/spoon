/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.cu.position;

import spoon.reflect.cu.SourcePosition;

/**
 * This interface represents the position of a program element like an expression in a source file.
 * The start/end represents range of whole program element including children element and comments
 * The nameStart/End represents range of core part of program element.
 */
public interface CompoundSourcePosition extends SourcePosition {

	/** returns the start of everything incl. type, name and default value */
	int getDeclarationStart();

	/** returns the end of everything incl. type, name and default value */
	int getDeclarationEnd();

	/** returns the start of name (int foo = 0 ➡ pos('f')) */
	int getNameStart();

	/** returns the end of name (int bar = 0 ➡ pos('r')) */
	int getNameEnd();

}
