package spoon.test.template.testclasses;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;

public class SubstituteArrayLengthTemplate extends StatementTemplate {

	@Override
	public void statement() throws Throwable {
		if (anArray.length > 0);
	}
	
	@Parameter
	String[] anArray;

	@Local
	public SubstituteArrayLengthTemplate(String[] anArray) {
		this.anArray = anArray;
	}
}