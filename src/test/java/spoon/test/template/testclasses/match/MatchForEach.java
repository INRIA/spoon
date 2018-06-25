package spoon.test.template.testclasses.match;

import java.util.List;

import static java.lang.System.out;

public class MatchForEach {

	public void matcher1(List<String> values) {
		for (String value : values) {
			System.out.println(value);
		}
	}
	
	public void testMatch1() {
		
		System.out.println("a");
		out.println("Xxxx");
		System.out.println((String) null);
		System.out.println(Long.class.toString());
	}

}
