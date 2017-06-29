package spoon.test.template.testclasses;

import spoon.reflect.code.CtExpression;
import spoon.template.BlockTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class InvocationSubstitutionByExpressionTemplate extends BlockTemplate {

	@Override
	public void block() throws Throwable {
		System.out.println(_expression_().substring(1));
		System.out.println(_expression_.S().substring(1));
	}
	
	@Parameter
	CtExpression<String> _expression_;

	@Local
	public InvocationSubstitutionByExpressionTemplate(CtExpression<String> expr) {
		this._expression_ = expr;
	}
	
	@Local
	String _expression_() {
		return null;
	}
}