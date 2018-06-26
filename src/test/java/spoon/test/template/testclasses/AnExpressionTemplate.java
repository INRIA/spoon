package spoon.test.template.testclasses;

import spoon.reflect.code.CtExpression;
import spoon.template.ExpressionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.TemplateParameter;

public class AnExpressionTemplate extends ExpressionTemplate {

	@Override
	public String expression() throws Throwable {
		return new String(exp.S());
	}

	@Parameter
	TemplateParameter<String> exp;

	@Local
	public AnExpressionTemplate(CtExpression<String> block) {
		this.exp = block;
	}
	
}