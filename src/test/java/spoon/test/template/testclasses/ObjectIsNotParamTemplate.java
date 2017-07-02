package spoon.test.template.testclasses;

import spoon.template.ExtensionTemplate;
import spoon.template.Local;

public class ObjectIsNotParamTemplate extends ExtensionTemplate {

	//this is normal field of type Object - it must not be considered as template parameter automatically
	Object o = "XXX";
	
	//the "o" in the method name must not be substituted
	void method() {}
	
	@Local
	public ObjectIsNotParamTemplate() {
	}
}
