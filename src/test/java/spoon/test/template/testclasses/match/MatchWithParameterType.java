package spoon.test.template.testclasses.match;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import static java.lang.System.out;

public class MatchWithParameterType {

	public static Pattern createPattern(Factory factory, Class valueType) {
		CtType<?> type = factory.Type().get(MatchWithParameterType.class);
		return PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
			.configureParameters(pb -> {
				pb.parameter("value").byVariable("value");
				if (valueType != null) {
					pb.setValueType(valueType);
				}
			})
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
