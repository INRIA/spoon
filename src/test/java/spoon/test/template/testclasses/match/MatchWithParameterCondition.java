package spoon.test.template.testclasses.match;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.factory.Factory;
import static java.lang.System.out;

import java.util.function.Predicate;

public class MatchWithParameterCondition {

	public static Pattern createPattern(Factory factory, Predicate<Object> condition) {
		return PatternBuilder.create(factory, MatchWithParameterCondition.class, tmb -> tmb.setBodyOfMethod("matcher1"))
			.configureParameters(pb -> {
				pb.parameter("value").byVariable("value");
				if (condition != null) {
					pb.matchCondition(null, condition);
				}
			})
			.configureInlineStatements(lsb -> lsb.byVariableName("values"))
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
