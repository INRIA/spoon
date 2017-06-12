package spoon.test.template.testclasses;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;

public class SubstituteArrayAccessTemplate extends StatementTemplate {

	@Override
	public void statement() throws Throwable {
		anArray.toString();
	}
	
	@Parameter
	String[] anArray;

	@Local
	public SubstituteArrayAccessTemplate(String[] anArray) {
		this.anArray = anArray;
	}
}