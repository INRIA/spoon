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
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.path.CtRole.COMMENT_CONTENT;
import static spoon.reflect.path.CtRole.JAVADOC_TAG_VALUE;
import static spoon.reflect.path.CtRole.DOCUMENTATION_TYPE;

public class CtJavaDocTagImpl extends CtElementImpl implements CtJavaDocTag {

	@MetamodelPropertyField(role = DOCUMENTATION_TYPE)
	private CtJavaDocTag.TagType type;
	@MetamodelPropertyField(role = COMMENT_CONTENT)
	private String content;
	@MetamodelPropertyField(role = JAVADOC_TAG_VALUE)
	private String param;

	@Override
	public TagType getType() {
		return type;
	}

	@Override
	public <E extends CtJavaDocTag> E setType(String type) {
		this.setType(CtJavaDocTag.TagType.tagFromName(type));
		return (E) this;
	}

	@Override
	public <E extends CtJavaDocTag> E setType(TagType type) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, DOCUMENTATION_TYPE, type, this.type);
		this.type = type;
		return (E) this;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public <E extends CtJavaDocTag> E setContent(String content) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, COMMENT_CONTENT, content, this.content);
		this.content = content;
		return (E) this;
	}

	@Override
	public String getParam() {
		return param;
	}

	@Override
	public <E extends CtJavaDocTag> E setParam(String param) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, JAVADOC_TAG_VALUE, param, this.param);
		this.param = param;
		return (E) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtJavaDocTag(this);
	}

	@Override
	public CtJavaDocTag clone() {
		return (CtJavaDocTag) super.clone();
	}

	@Override
	public String toString()	{
		return this.getType().toString()	//Tag type, with @ sign included
				+ " "	//Space required between tag type and parameter
				+ this.param + System.lineSeparator()	//Tag parameter
				+ "\t\t" + this.content + System.lineSeparator();
	}
}
