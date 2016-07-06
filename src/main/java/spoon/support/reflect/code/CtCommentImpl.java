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
package spoon.support.reflect.code;

import spoon.reflect.code.CtComment;
import spoon.reflect.visitor.CtVisitor;

public class CtCommentImpl extends CtStatementImpl implements CtComment {
	private static final long serialVersionUID = 1L;

	private String content;

	private CommentType type;

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public <E extends CtComment> E setContent(String content) {
		this.content = content;
		return (E) this;
	}

	@Override
	public CommentType getCommentType() {
		return type;
	}

	@Override
	public <E extends CtComment> E setCommentType(CommentType commentType) {
		type = commentType;
		return (E) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtComment(this);
	}

	/**
	 * The comments are not printed during the CtElement equality.
	 * The method is this overridden for CtComment.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CtCommentImpl ctComment = (CtCommentImpl) o;

		if (content != null
				? !content.equals(ctComment.content)
				: ctComment.content != null) {
			return false;
		}
		return type == ctComment.type;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (content != null ? content.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	@Override
	public CtComment clone() {
		return (CtComment) super.clone();
	}
}
