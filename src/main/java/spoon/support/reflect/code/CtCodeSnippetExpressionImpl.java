package spoon.support.reflect.code;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.visitor.CtVisitor;

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
	
	
}
