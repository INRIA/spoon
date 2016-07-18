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
package spoon.reflect.visitor;

import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

public class Writer {
	private StringBuilder content = new StringBuilder();
	private int[] offset;
	private String classContent;

	Writer(String content) {
		classContent = content;
		this.content.append(content);
		offset = new int[content.split("\n").length];
	}

	public Writer replace(int start, int end, String content) {
		this.content.delete(start, end + 1);
		this.content.insert(start,  content);
		return this;
	}

	public Writer write(CtElement element, int position) {
		String content = element.toString();
		if (element instanceof CtStatement && !content.endsWith("\n")) {
			content += "\n";
		}
		this.content.insert(position, content);
		return this;
	}

	public Writer remove(CtElement element) {
		SourcePosition position = element.getPosition();
		if (position == null) {
			return this;
		}
		int start = position.getSourceStart();
		int end = position.getSourceStart();
		this.content.delete(start, end + 1);
		return this;
	}


	public void clear() {
		this.content = new StringBuilder();
		this.content.append(classContent);
	}


	@Override
	public String toString() {
		return content.toString();
	}
}
