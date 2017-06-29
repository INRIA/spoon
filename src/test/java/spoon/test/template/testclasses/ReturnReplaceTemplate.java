package spoon.test.template.testclasses;

import spoon.reflect.code.CtBlock;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.TemplateParameter;

public class ReturnReplaceTemplate extends ExtensionTemplate {

	public String method() throws Throwable {
		return _statement_.S();
	}
	
	TemplateParameter<String> _statement_;

	@Local
	public ReturnReplaceTemplate(CtBlock<String> statement) {
		this._statement_ = statement;
	}
	
	@Local
	String sample() {
		if(System.currentTimeMillis()%2L==0) {
			return "Panna";
		} else {
			return "Orel";
		}
	}
}