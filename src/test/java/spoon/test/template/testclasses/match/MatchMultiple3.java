package spoon.test.template.testclasses.match;

import spoon.pattern.ParametersBuilder;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.template.TemplateParameter;

import static java.lang.System.out;

import java.util.function.Consumer;

public class MatchMultiple3 {

	public static Pattern createPattern(Factory factory, Consumer<ParametersBuilder> cfgParams) {
		CtType<?> type = factory.Type().get(MatchMultiple3.class);
		return PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
			.configureTemplateParameters()
			.configureParameters(pb -> {
				pb.parameter("statements1").setContainerKind(ContainerKind.LIST);
				pb.parameter("statements2").setContainerKind(ContainerKind.LIST);
				pb.parameter("printedValue").byFilter((CtLiteral<?> literal) -> "something".equals(literal.getValue()));
				cfgParams.accept(pb);
			})
			.build();
	}
	
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
