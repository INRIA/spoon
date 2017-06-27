package spoon.test.template.testclasses;

import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class SubStringTemplate extends ExtensionTemplate{

	String m_$name$ = "$name$ is here more times: $name$";

	void set$name$(String p_$name$) {
		this.m_$name$ = p_$name$;
	}
	
	void m() {
		set$name$("The $name$ is here too");
	}
	
	@Parameter("$name$")
	Object name;
	
	@Local
	public SubStringTemplate(Object name) {
		this.name = name;
	}
}
