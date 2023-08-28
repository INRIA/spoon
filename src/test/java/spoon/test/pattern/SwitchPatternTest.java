package spoon.test.pattern;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.test.SpoonTestHelpers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SwitchPatternTest {

	private static CtModel createModelFromString(String code) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(20);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	@Test
	void test() {
		CtModel model = createModelFromString(
						"package spoon.test.pattern;\n" +
										"public class Foo {\n" +
										"    public void foo(Number n) {\n" +
										"        switch (n) {\n" +
										"            case Integer i -> System.out.println(1);\n" +
										"            case Float f when f > 5 -> System.out.println(2);\n" +
										"            case null, default -> System.out.println(3);\n" +
										"        }\n" +
										"    }\n" +
										"}");
		List<CtCasePattern> elements = model.getElements(new TypeFilter<>(CtCasePattern.class));
		assertThat(elements).hasSize(2);
	}
}
