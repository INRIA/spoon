/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.path.CtRole.COMMENT_TAG;

public class CtJavaDocImpl extends CtCommentImpl implements CtJavaDoc {

	@MetamodelPropertyField(role = CtRole.COMMENT_TAG)
	List<CtJavaDocTag> tags = new ArrayList<>();

	public CtJavaDocImpl() {
		super(CommentType.JAVADOC);
	}

	@Override
	public List<CtJavaDocTag> getTags() {
		return new ArrayList<>(tags);
	}

	@Override
	public <E extends CtJavaDoc> E setTags(List<CtJavaDocTag> tags) {
		if (tags == null) {
			return (E) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, COMMENT_TAG, this.tags, new ArrayList<>(this.tags));
		this.tags = new ArrayList<>();
		for (CtJavaDocTag tag : tags) {
			this.addTag(tag);
		}
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E addTag(CtJavaDocTag tag) {
		if (tag != null) {
			tag.setParent(this);
			getFactory().getEnvironment().getModelChangeListener().onListAdd(this, COMMENT_TAG, tags, tag);
			tags.add(tag);
		}
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E addTag(int index, CtJavaDocTag tag) {
		tag.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, COMMENT_TAG, tags, index, tag);
		tags.add(index, tag);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E removeTag(int index) {
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, COMMENT_TAG, tags, index, tags.get(index));
		tags.remove(index);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E removeTag(CtJavaDocTag tag) {
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, COMMENT_TAG, tags, tags.indexOf(tag), tag);
		tags.remove(tag);
		return (E) this;
	}

	@Override
	public String getShortDescription() {
		int indexEndSentence = this.getContent().indexOf(".");
		if (indexEndSentence == -1) {
			indexEndSentence = this.getContent().indexOf("\n");
		}
		if (indexEndSentence == -1) {
			indexEndSentence = this.getContent().length();
		}
		if (indexEndSentence != -1) {
			return this.getContent().substring(0, indexEndSentence + 1).trim();
		}
		return "";
	}

	@Override
	public String getLongDescription() {
		int indexStartLongDescription = getShortDescription().length();

		return this.getContent().substring(indexStartLongDescription).trim();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtJavaDoc(this);
	}

	@Override
	public CtJavaDoc clone() {
		return (CtJavaDoc) super.clone();
	}
}
