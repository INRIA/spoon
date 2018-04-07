package spoon.test.template.testclasses.match;

import spoon.pattern.ParametersBuilder;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.template.TemplateParameter;

import static java.lang.System.out;

import java.util.List;
import java.util.function.Consumer;

public class MatchMultiple2 {

	public static Pattern createPattern(Factory factory, Consumer<ParametersBuilder> cfgParams) {
		CtType<?> type = factory.Type().get(MatchMultiple2.class);
		return PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
			.configureTemplateParameters()
			.configureParameters(pb -> {
				pb.parameter("statements1").setContainerKind(ContainerKind.LIST);
				pb.parameter("statements2").setContainerKind(ContainerKind.LIST);
				pb.parameter("printedValue").byVariable("something").matchInlinedStatements();
				cfgParams.accept(pb);
			})
			.build();
	}
	
	public void matcher1(List<String> something) {
		statements1.S();
		statements2.S();
		for (String v : something) {
			System.out.println(v);
		}
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
