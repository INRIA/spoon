/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.ModelList;

import java.util.List;

public class CtJavaDocImpl extends CtCommentImpl implements CtJavaDoc {

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
}
