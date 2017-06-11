package spoon.test.template.testclasses;

import spoon.reflect.code.CtStatement;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;

public class InvocationSubstitutionByStatementTemplate extends StatementTemplate {

	@Override
	public void statement() throws Throwable {
		_statement_();
	}
	
	@Parameter("_statement_")
	CtStatement statement;

	@Local
	public InvocationSubstitutionByStatementTemplate(CtStatement statement) {
		this.statement = statement;
	}
	
	@Local
	void _statement_() {
	}
	
	@Local
	void sample() {
		throw new RuntimeException("Failed");
	}
}