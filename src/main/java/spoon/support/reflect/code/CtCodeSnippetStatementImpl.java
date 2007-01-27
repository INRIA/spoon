package spoon.support.reflect.code;

import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.visitor.CtVisitor;

public class CtCodeSnippetStatementImpl extends CtStatementImpl implements
		CtCodeSnippetStatement {

	private static final long serialVersionUID = 1L;

	public void accept(CtVisitor visitor) {
		visitor.visitCtCodeSnippetStatement(this);
	}
	
	String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
