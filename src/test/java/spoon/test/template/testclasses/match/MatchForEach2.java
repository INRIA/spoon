package spoon.test.template.testclasses.match;

import java.util.List;

import static java.lang.System.out;

public class MatchForEach2 {

	public void matcher1(List<String> values) {
		int var = 0;
		for (String value : values) {
			System.out.println(value);
			var++;
		}
	}

	public void testMatch1() {
		System.out.println("a");
		int cc = 0;
		out.println("Xxxx");
		cc++;
		System.out.println((String) null);
		cc++;
		int dd = 0;
		System.out.println(Long.class.toString());
		dd++;
	}

}
