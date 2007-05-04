package spoon.support.reflect.code;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.builder.CtSnippetCompilationError;
import spoon.support.builder.SnippetCompiler;

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

	public void setValue(String value) {
		this.value = value;
	}
	
	public CtExpression<T> compile() throws CtSnippetCompilationError {
		return SnippetCompiler.compileExpression(this);
	}
	
}
