/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.compiler.SnippetCompilationError;
import spoon.support.compiler.SnippetCompilationHelper;

import static spoon.reflect.path.CtRole.SNIPPET;

public class CtCodeSnippetStatementImpl extends CtStatementImpl implements CtCodeSnippetStatement {

	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCodeSnippetStatement(this);
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
	public <S extends CtStatement> S compile() throws SnippetCompilationError {
		return (S) SnippetCompilationHelper.compileStatement(this);
	}

	@Override
	public CtCodeSnippetStatement clone() {
		return (CtCodeSnippetStatement) super.clone();
	}
}
