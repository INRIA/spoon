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

import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtJavaDocTagImpl extends CtElementImpl implements CtJavaDocTag {

	private CtJavaDocTag.TagType name;
	private String content;
	private String param;

	@Override
	public TagType getName() {
		return name;
	}

	@Override
	public <E extends CtJavaDocTag> E setName(String name) {
		this.setName(CtJavaDocTag.TagType.fromName(name));
		return (E) this;
	}

	@Override
	public <E extends CtJavaDocTag> E setName(TagType name) {
		this.name = name;
		return (E) this;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public <E extends CtJavaDocTag> E setContent(String content) {
		if (this.name != null && this.name.hasParam()) {
			int firstWord = content.indexOf(" ");
			int firstLine = content.indexOf("\n");
			if (firstLine < firstWord && firstLine >= 0) {
				firstWord = firstLine;
			}
			if (firstWord == -1) {
				firstWord = content.length();
			}
			this.param = content.substring(0, firstWord);
			content = content.substring(firstWord);
		}
		this.content = content.trim();
		return (E) this;
	}

	@Override
	public String getParam() {
		return param;
	}

	@Override
	public <E extends CtJavaDocTag> E setParam(String param) {
		this.param = param;
		return (E) this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(CtJavaDocTag.JAVADOC_TAG_PREFIX);
		sb.append(name.getKeyword());
		sb.append(" ");
		if (name.hasParam()) {
			sb.append(param);
			if (!content.isEmpty() && !content.startsWith("\n")) {
				sb.append(" ");
			}
		}
		sb.append(content);
		return sb.toString();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtJavaDocTag(this);
	}

	@Override
	public CtJavaDocTag clone() {
		return (CtJavaDocTag) super.clone();
	}
}
