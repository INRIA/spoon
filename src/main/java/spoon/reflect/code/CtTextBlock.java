/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines a Text Block String Literal.
 *
 * <pre>
 *     String example = """
 *		      Test String
 *        	      """;
 * </pre>
 *
 * The example above shows a TextBlock literal, in which the string is represented by CtTextBlock
 */
public interface CtTextBlock extends CtLiteral<String> {
	/** Overriding return type, a clone of a CtTextBlock returns a CtTextBlock */
	@Override
	CtTextBlock clone();
}
