package spoon.test.template.testclasses;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;

public class ClassAccessTemplate extends StatementTemplate {

	@Override
	public void statement() throws Throwable {
		aClass.getName();
	}
	
	@Parameter
	Class aClass;

	@Local
	public ClassAccessTemplate(Class aClass) {
		this.aClass = aClass;
	}
	
	@Local
	class _AClass_ {}
}