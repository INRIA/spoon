package spoon.test.template.testclasses.match;

import java.util.List;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.TemplateModelBuilder;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.test.template.testclasses.replace.OldPattern;

import static java.lang.System.out;

public class MatchForEach {

	public static Pattern createPattern(Factory factory) {
		CtType<?> type = factory.Type().get(MatchForEach.class);

		return PatternBuilder.create(new TemplateModelBuilder(type).setBodyOfMethod("matcher1").getTemplateModels())
			.configureParameters(pb -> {
				pb.parameter("values").byVariable("values").setContainerKind(ContainerKind.LIST);
			})
			.configureInlineStatements(lsb -> lsb.byVariableName("values"))
			.build();
	}
	
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
