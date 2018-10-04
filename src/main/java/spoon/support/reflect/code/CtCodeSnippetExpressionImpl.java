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
