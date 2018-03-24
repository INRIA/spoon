package spoon.test.template.testclasses.match;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.TemplateModelBuilder;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import static java.lang.System.out;

public class MatchIfElse {

	public static Pattern createPattern(Factory factory) {
		CtType<?> type = factory.Type().get(MatchIfElse.class);
		return PatternBuilder.create(new TemplateModelBuilder(type).setBodyOfMethod("matcher1").getTemplateModels())
			.configureParameters(pb -> {
				pb.parameter("option").byVariable("option");
				pb.parameter("option2").byVariable("option2");
				pb.parameter("value").byFilter(new TypeFilter(CtLiteral.class));
			})
			.configureInlineStatements(lsb -> lsb.byVariableName("option"))
			.build();
	}
	
	public void matcher1(boolean option, boolean option2) {
		if (option) {
			//matches String argument
			System.out.println("string");
		} else if (option2) {
			//matches int argument
			System.out.println(1);
		} else {
			//matches double argument
			System.out.println(4.5);
		}
	}
	
	public void testMatch1() {
		int i = 0;
		System.out.println(i);
		System.out.println("a");
		out.println("Xxxx");
		System.out.println((String) null);
		System.out.println((Integer) null);
		System.out.println(2018);
		System.out.println(Long.class.toString());
		System.out.println(3.14);
	}

}
