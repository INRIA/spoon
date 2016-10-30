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
package spoon.reflect.code;

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
	 * Get the content of the comment
	 * @return the content of the comment
	 */
	String getContent();

	<E extends CtComment> E setContent(String content);

	/**
	 * Get the type of the comment
 	 * @return the comment type
	 */
	CommentType getCommentType();

	<E extends CtComment> E setCommentType(CommentType commentType);

	@Override
	CtComment clone();
}
