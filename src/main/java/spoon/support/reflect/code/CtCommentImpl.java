/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import java.util.Objects;

import static spoon.support.compiler.jdt.JDTCommentBuilder.cleanComment;

public class CtCommentImpl extends CtStatementImpl implements CtComment {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.COMMENT_CONTENT)
	protected String content;

	@MetamodelPropertyField(role = CtRole.COMMENT_TYPE)
	private CommentType type;

	public CtCommentImpl() {
	}

	protected CtCommentImpl(CommentType type) {
		this.type = type;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getRawContent() {
		SourcePosition pos = getPosition();
		CtCompilationUnit cu = pos.getCompilationUnit();
		if (cu != null) {
			String source = cu.getOriginalSourceCode();
			if (source != null) {
				return source.substring(pos.getSourceStart(), pos.getSourceEnd() + 1);
			}
		}
		return "";
	}

	@Override
	public <E extends CtComment> E setContent(String content) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.COMMENT_CONTENT, content, this.content);
		this.content = cleanComment(content);
		return (E) this;
	}

	/**
	 * FOR ADVANCED USAGE ONLY
	 * Set the comment content, without cleaning the comment, if the cleaning behavior to get a canonical version does not work for you.
	 * Does not ensure any AST contract such as calling the change listener
	 * You have to cast your comment to CtCommentImpl, it's not beautiful, but it's known :-)
	 */
	public <E extends CtComment> E _setRawContent(String content) {
		this.content = content;
		return (E) this;
	}

	@Override
	public CommentType getCommentType() {
		return type;
	}

	@Override
	public <E extends CtComment> E setCommentType(CommentType commentType) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, commentType, this.type);
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

		if (!Objects.equals(content, ctComment.content)) {
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

	@Override
	public CtJavaDoc asJavaDoc() {
		if (this instanceof CtJavaDoc) {
			return (CtJavaDoc) this;
		}
		throw new IllegalStateException("not a javadoc comment");
	}
}
