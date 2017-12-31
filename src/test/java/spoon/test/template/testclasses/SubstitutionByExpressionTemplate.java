package spoon.test.template.testclasses;

import spoon.reflect.code.CtExpression;
import spoon.template.BlockTemplate;
import spoon.template.Local;

public class SubstitutionByExpressionTemplate extends BlockTemplate {

	@Override
	public void block() throws Throwable {
		System.out.println(_expression_.S().substring(1));
	}
	
	//note that there is no @Parameter annotation! This field is detected as template parameter, because it's type extends TemplateParameter
	CtExpression<String> _expression_;

	@Local
	public SubstitutionByExpressionTemplate(CtExpression<String> expr) {
		this._expression_ = expr;
	}
}