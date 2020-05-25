/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.javadoc.internal.Javadoc;
import spoon.javadoc.internal.JavadocBlockTag;
import spoon.javadoc.internal.JavadocDescriptionElement;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.ModelList;

import java.util.List;

import static spoon.support.compiler.jdt.JDTCommentBuilder.cleanComment;

public class CtJavaDocImpl extends CtCommentImpl implements CtJavaDoc {

	/** the structured object resulting from parsing */
	private transient Javadoc javadoc;

	@MetamodelPropertyField(role = CtRole.COMMENT_TAG)
	private final ModelList<CtJavaDocTag> tags = new ModelList<CtJavaDocTag>() {
		@Override
		protected CtElement getOwner() {
			return CtJavaDocImpl.this;
		}
		@Override
		protected CtRole getRole() {
			return CtRole.COMMENT_TAG;
		}
		@Override
		protected int getDefaultCapacity() {
			return 2;
		}
	};

	public CtJavaDocImpl() {
		super(CommentType.JAVADOC);
	}

	@Override
	public List<CtJavaDocTag> getTags() {
		return tags;
	}

	@Override
	public <E extends CtJavaDoc> E setTags(List<CtJavaDocTag> tags) {
		this.tags.set(tags);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E addTag(CtJavaDocTag tag) {
		this.tags.add(tag);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E addTag(int index, CtJavaDocTag tag) {
		this.tags.add(index, tag);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E removeTag(int index) {
		this.tags.remove(index);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E removeTag(CtJavaDocTag tag) {
		this.tags.remove(tag);
		return (E) this;
	}

	@Override
	public String getShortDescription() {
		int indexEndSentence = this.getContent().indexOf('.');
		if (indexEndSentence == -1) {
			indexEndSentence = this.getContent().indexOf('\n');
		}
		if (indexEndSentence != -1) {
			return this.getContent().substring(0, indexEndSentence + 1).trim();
		} else {
			return this.getContent().trim();
		}
	}

	/**
	 * Parses the content string to split in two: the description and the Javadoc tags
	 */
	@Override
	public <E extends CtComment> E setContent(String content) {
		tags.clear();

		// avoiding NPE later
		if (content == null) {
			content = "";
		}

		String longDescription = "";
		String currentTagContent = "";
		CtJavaDocTag.TagType currentTag = null;

		javadoc = Javadoc.parse(cleanComment(content));
		for (JavadocBlockTag tag: javadoc.getBlockTags()) {
			addTag(getFactory().createJavaDocTag(tag.getContent().toText(), CtJavaDocTag.TagType.tagFromName(tag.getTagName())));
		}

		// we cannot call super.setContent because it calls cleanComment (which has already been done above)
		// and we don't want to clean the comment twice
		String contentWithTags = javadoc.getDescription().toText().trim(); // trim is required for backward compatibility

		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.COMMENT_CONTENT, contentWithTags, this.content);
		this.content = contentWithTags;

		return (E) this;
	}



	@Override
	public String getLongDescription() {
		int indexStartLongDescription = getShortDescription().length();

		if (indexStartLongDescription < this.getContent().trim().length()) {
			return this.getContent().substring(indexStartLongDescription).trim();
		} else {
			return this.getContent().trim();
		}

	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtJavaDoc(this);
	}

	@Override
	public CtJavaDoc clone() {
		return (CtJavaDoc) super.clone();
	}

	@Override
	public List<JavadocDescriptionElement> getJavadocElements() {
		return javadoc.getDescription().getElements();
	}
}
