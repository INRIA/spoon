/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.cu.position;

/**
 * This interface represents the position of a program element in a source file.
 * The start/end represents range of whole program element including children element and comments
 * The nameStart/End represents range of name of program element.
 * The modifierSourceStart/End represents range of modifiers and annotations of the element
 */
public interface DeclarationSourcePosition extends CompoundSourcePosition {

	/** returns the first char of the first modifier */
	int getModifierSourceStart();

	/** returns the last char of the last modifier */
	int getModifierSourceEnd();

	/** returns the end of the default value
	 * int i = 0, j =1 => returns the comma
	 */
	int getDefaultValueEnd();

	/** sets the position of the end of the default value declaration */
	DeclarationSourcePosition setDefaultValueEnd(int endDefaultValueDeclaration);

}
