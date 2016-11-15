package spoon.test.ctBodyHolder.testclasses;

import spoon.template.Parameter;
import spoon.template.StatementTemplate;

public class CWBStatementTemplate extends StatementTemplate
{
	@Parameter
	String value;

	public CWBStatementTemplate(String val)
	{
		value = val;
	}

	@Override
	public void statement() throws Throwable
	{
		System.out.println(value);
	}

}
