package spoon.test.template.testclasses.match;

import spoon.template.TemplateParameter;

import static java.lang.System.out;

public class MatchMultiple3 {

	public void matcher1() {
		statements1.S();
		statements2.S();
		System.out.println("something");
	}
	
	TemplateParameter<Void> statements1;
	TemplateParameter<Void> statements2;
	
	public void testMatch1() {
		int i = 0;
		i++;
		System.out.println(i);
		out.println("Xxxx");
		System.out.println((String) null);
		System.out.println("last one");
	}

}
