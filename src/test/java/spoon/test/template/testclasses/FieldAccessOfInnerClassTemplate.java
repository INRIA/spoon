package spoon.test.template.testclasses;

import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class FieldAccessOfInnerClassTemplate extends ExtensionTemplate {

	class Inner {
		int $field$;

		void m() {
			$field$ = 7;
		}
	}
	
	@Local
	public FieldAccessOfInnerClassTemplate(String fieldName) {
		this.$field$ = fieldName;
	}

	@Parameter
	String $field$;
}
