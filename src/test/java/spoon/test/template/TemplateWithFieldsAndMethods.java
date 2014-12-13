package spoon.test.template;

import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.TemplateParameter;

public class TemplateWithFieldsAndMethods extends ExtensionTemplate {

	@Parameter
	public String PARAM;

	public TemplateParameter<String> PARAM2;

	@Local
	public TemplateWithFieldsAndMethods(String PARAM,
			TemplateParameter<String> PARAM2) {
		this.PARAM = PARAM;
		this.PARAM2 = PARAM2;
	}

	public String methodToBeInserted() {
		return PARAM;
	}

	public String fieldToBeInserted;

	public String methodToBeInserted2() {
		return PARAM2.S();
	}

}
