package spoon.test.template;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.template.testclasses.replace.DPPSample1;
import spoon.test.template.testclasses.replace.NewPattern;
import spoon.test.template.testclasses.replace.OldPattern;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.*;

import java.io.File;

public class CodeReplaceTest {
	/**
	 * @param factory a to be used factory
	 * @return a Pattern instance of this Pattern
	 */
	public static Pattern createPattern(Factory factory) {
		return PatternBuilder
				//Create a pattern from all statements of OldPattern_ParamsInNestedType#patternModel
				.create(factory, OldPattern.class, model->model.setBodyOfMethod("patternModel"))
				.configureParameters(pb->pb
						.parametersByVariable("params", "item")
						.parameter("statements").setContainerKind(ContainerKind.LIST)
				)
				.configureAutomaticParameters()
				.configureInlineStatements(ls -> ls.byVariableName("useStartKeyword"))
				.build();
	}



	@Test
	public void testMatchSample1() throws Exception {
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		CtClass<?> classDJPP = f.Class().get(DPPSample1.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());
		Pattern p = createPattern(f);
		class Context {
			int count = 0;
		}
		Context context = new Context();
		p.forEachMatch(classDJPP, (match) -> {
			ParameterValueProvider params = match.getParameters();
			if (context.count == 0) {
				assertEquals("\"extends\"", params.getValue("startKeyword").toString());
				assertEquals(Boolean.TRUE, params.getValue("useStartKeyword"));
			} else {
				assertEquals(null, params.getValue("startKeyword"));
				assertEquals(Boolean.FALSE, params.getValue("useStartKeyword"));
			}
			assertEquals("false", params.getValue("startPrefixSpace").toString());
			assertEquals("null", params.getValue("start").toString());
			assertEquals("false", params.getValue("startSuffixSpace").toString());
			assertEquals("false", params.getValue("nextPrefixSpace").toString());
			assertEquals("\",\"", params.getValue("next").toString());
			assertEquals("true", params.getValue("nextSuffixSpace").toString());
			assertEquals("false", params.getValue("endPrefixSpace").toString());
			assertEquals("\";\"", params.getValue("end").toString());
			assertEquals("ctEnum.getEnumValues()", params.getValue("getIterable").toString());
			assertEquals("[scan(enumValue)]", params.getValue("statements").toString());
			context.count++;
		});
		assertEquals(2, context.count);
	}

	@Test
	public void testTemplateReplace() throws Exception {
		Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setComplianceLevel(8);
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setCommentEnabled(true);
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.addInputSource(new File("./src/main/java/spoon/reflect/visitor"));
		compiler.addInputSource(new File("./src/test/java/spoon/test/template/testclasses/replace"));
		compiler.build();
		CtClass<?> classDJPP = factory.Class().get(DefaultJavaPrettyPrinter.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());
		NewPattern.replaceOldByNew(classDJPP);

		launcher.setSourceOutputDirectory(new File("./target/spooned-template-replace/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
	}

}
