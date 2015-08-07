package spoon.support.reflect.code;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.compiler.SnippetCompilationError;
import spoon.support.compiler.SnippetCompilationHelper;

public class CtCodeSnippetExpressionImpl<T> extends CtExpressionImpl<T> implements
		CtCodeSnippetExpression<T> {

	private static final long serialVersionUID = 1L;

	public void accept(CtVisitor visitor) {
		visitor.visitCtCodeSnippetExpression(this);
	}
	
	String value;

	public String getValue() {
		return value;
	}

	public <C extends CtCodeSnippet> C setValue(String value) {
		this.value = value;
		return (C) this;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends CtExpression<T>> E compile() throws SnippetCompilationError {
		return (E)SnippetCompilationHelper.compileExpression(this);
	}
	
}
