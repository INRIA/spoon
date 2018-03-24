package spoon.test.template.testclasses.match;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.TemplateModelBuilder;
import spoon.pattern.matcher.Quantifier;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;

import static java.lang.System.out;

public class MatchMultiple {

	public static Pattern createPattern(Factory factory, Quantifier matchingStrategy, Integer minCount, Integer maxCount) {
		CtType<?> type = factory.Type().get(MatchMultiple.class);
		return PatternBuilder.create(new TemplateModelBuilder(type).setBodyOfMethod("matcher1").getTemplateModels())
			.configureParameters(pb -> {
				pb.parameter("statements").bySimpleName("statements").setContainerKind(ContainerKind.LIST);
				if (matchingStrategy != null) {
					pb.setMatchingStrategy(matchingStrategy);
				}
				if (minCount != null) {
					pb.setMinOccurence(minCount);
				}
				if (maxCount != null) {
					pb.setMaxOccurence(maxCount);
				}
				pb.parameter("printedValue").byFilter((CtLiteral<?> literal) -> "something".equals(literal.getValue()));
			})
			.build();
	}
	
	public void matcher1() {
		statements();
		System.out.println("something");
	}
	
	void statements() {}
	
	public void testMatch1() {
		int i = 0;
		i++;
		System.out.println(i);
		out.println("Xxxx");
		System.out.println((String) null);
		System.out.println("last one");
	}

}
