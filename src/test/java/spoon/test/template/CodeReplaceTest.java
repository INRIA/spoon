package spoon.test.template;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.pattern.ParameterValueProvider;
import spoon.pattern.Pattern;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.template.testclasses.replace.DPPSample1;
import spoon.test.template.testclasses.replace.NewPattern;
import spoon.test.template.testclasses.replace.OldPattern;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.*;

import java.io.File;

public class CodeReplaceTest {
	
	@Test
	public void testMatchSample1() throws Exception {
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		CtClass<?> classDJPP = f.Class().get(DPPSample1.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());
		Pattern p = OldPattern.createPattern(f);
		class Context {
			int count = 0;
		}
		Context context = new Context();
		p.forEachMatch(classDJPP, (match) -> {
			ParameterValueProvider params = match.getParameters();
			if (context.count == 0) {
				assertEquals("\"extends\"", params.get("startKeyword").toString());
				assertEquals(Boolean.TRUE, params.get("useStartKeyword"));
			} else {
				assertEquals(null, params.get("startKeyword"));
				assertEquals(Boolean.FALSE, params.get("useStartKeyword"));
			}
			assertEquals("false", params.get("startPrefixSpace").toString());
			assertEquals("null", params.get("start").toString());
			assertEquals("false", params.get("startSuffixSpace").toString());
			assertEquals("false", params.get("nextPrefixSpace").toString());
			assertEquals("\",\"", params.get("next").toString());
			assertEquals("true", params.get("nextSuffixSpace").toString());
			assertEquals("false", params.get("endPrefixSpace").toString());
			assertEquals("\";\"", params.get("end").toString());
			assertEquals("ctEnum.getEnumValues()", params.get("getIterable").toString());
			assertEquals("[scan(enumValue)]", params.get("statements").toString());
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
