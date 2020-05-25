/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.compiler.SnippetCompilationError;
import spoon.support.compiler.SnippetCompilationHelper;

import static spoon.reflect.path.CtRole.SNIPPET;

public class CtCodeSnippetExpressionImpl<T> extends CtExpressionImpl<T> implements CtCodeSnippetExpression<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCodeSnippetExpression(this);
	}

	@MetamodelPropertyField(role = SNIPPET)
	String value;

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public <C extends CtCodeSnippet> C setValue(String value) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, SNIPPET, value, this.value);
		this.value = value;
		return (C) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends CtExpression<T>> E compile() throws SnippetCompilationError {
		return (E) SnippetCompilationHelper.compileExpression(this);
	}

	@Override
	public CtCodeSnippetExpression<T> clone() {
		return (CtCodeSnippetExpression<T>) super.clone();
	}
}
