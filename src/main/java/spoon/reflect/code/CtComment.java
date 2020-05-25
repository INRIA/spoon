/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.DerivedProperty;

import static spoon.reflect.path.CtRole.COMMENT_TYPE;
import static spoon.reflect.path.CtRole.COMMENT_CONTENT;

/**
 * This code element defines a comment
 *
 * Example:
 * <code>
 * int x = 0;
 * // a comment
 * </code>
 */
public interface CtComment extends CtStatement {
	enum CommentType {
		/**
		 * before the package line (typically the license)
		 */
		FILE,
		/**
		 * JavaDoc comment: before methods, fields, types
		 */
		JAVADOC,
		/**
		 * Inline comment (//)
		 */
		INLINE,
		/**
		 * Block comment (/* *\/)
		 */
		BLOCK
	}

	/**
	 * This line separator is used in comments returned by {@link #getContent()}.
	 * It is OS independent.
	 * It has no influence to pretty printed comments, which uses by default OS dependent line separator
	 */
	String LINE_SEPARATOR = "\n";

	/**
	 * Get the content of the comment
	 * @return the content of the comment
	 */
	@PropertyGetter(role = COMMENT_CONTENT)
	String getContent();

	@PropertySetter(role = COMMENT_CONTENT)
	<E extends CtComment> E setContent(String content);

	/**
	 * @return the original raw comment from the source file including comment prefix and suffix, indentation (including TABs) original EOLs,
	 * based on the attached position object (the returned value is "derived" from the position).
	 * If the file pointed to in the position object does not exist on disk anymore,
	 * then the empty string "" is returned
	 * Note: the call of {@link #setContent(String)} doesn't influence the returned value, only the value of the position object.
	 */
	@DerivedProperty
	String getRawContent();

	/**
	 * Get the type of the comment
 	 * @return the comment type
	 */
	@PropertyGetter(role = COMMENT_TYPE)
	CommentType getCommentType();

	@PropertySetter(role = COMMENT_TYPE)
	<E extends CtComment> E setCommentType(CommentType commentType);

	@Override
	CtComment clone();

	/** Utility method to for casting the object, throws an exception if not of the correct type */
	CtJavaDoc asJavaDoc();
}
