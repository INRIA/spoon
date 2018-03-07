package spoon.test.template.testclasses.match;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.factory.Factory;
import static java.lang.System.out;

public class MatchWithParameterType {

	public static Pattern createPattern(Factory factory, Class valueType) {
		return PatternBuilder.create(factory, MatchWithParameterType.class, tmb -> tmb.setBodyOfMethod("matcher1"))
			.configureParameters(pb -> {
				pb.parameter("value").byVariable("value");
				if (valueType != null) {
					pb.setValueType(valueType);
				}
			})
			.configureLiveStatements(lsb -> lsb.byVariableName("values"))
			.build();
	}
	
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
