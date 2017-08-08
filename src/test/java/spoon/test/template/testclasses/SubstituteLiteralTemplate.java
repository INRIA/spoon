package spoon.test.template.testclasses;

import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class SubstituteLiteralTemplate extends ExtensionTemplate{

	String stringField1 = "$param1$";
	String stringField2 = "Substring $param1$ is substituted too - $param1$";

	void m1() {
		System.out.println(Params.$param1$());
	}
	
	@Parameter
	Object $param1$;
	
	@Local
	public SubstituteLiteralTemplate(Object param) {
		this.$param1$ = param;
	}
}

class Params {
	static int $param1$() {
		return 0;
	}
}
