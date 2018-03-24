package spoon.test.template.testclasses.match;

import java.util.List;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.TemplateModelBuilder;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;

import static java.lang.System.out;

public class MatchForEach2 {

	public static Pattern createPattern(Factory factory) {
		CtType<?> type = factory.Type().get(MatchForEach2.class);

		return PatternBuilder.create(new TemplateModelBuilder(type).setBodyOfMethod("matcher1").getTemplateModels())
			.configureParameters(pb -> {
				pb.parameter("values").byVariable("values").setContainerKind(ContainerKind.LIST);
				pb.parameter("varName").byString("var");
			})
			.configureInlineStatements(lsb -> lsb.byVariableName("values"))
			.build();
	}
	
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
