package spoon.test.template.testclasses;

import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class FieldAccessTemplate extends ExtensionTemplate {

	int $field$;
	
	void m() {
		$field$ = 7;
	}
	
	@Local
	public FieldAccessTemplate(String fieldName) {
		this.fieldName = fieldName;
	}

	@Parameter("$field$")
	String fieldName;
}
