package spoon.test.template.testclasses.constructors;

import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class TemplateWithFieldsAndMethods_Wrong extends ExtensionTemplate {

	@Parameter
	public String PARAM;

	@Local
	public TemplateWithFieldsAndMethods_Wrong(String PARAM) {
		this.PARAM = PARAM;
	}

	public String methodToBeInserted() {
		return PARAM;
	}
}
