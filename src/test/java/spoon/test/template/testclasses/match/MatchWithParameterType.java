package spoon.test.template.testclasses.match;

import static java.lang.System.out;

public class MatchWithParameterType {

	public void matcher1(String value) {
		System.out.println(value);
	}
	
	public void testMatch1() {
		
		System.out.println("a");
		out.println("Xxxx");
		System.out.println((String) null);
		System.out.println(Long.class.toString());
	}

}
