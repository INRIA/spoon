package spoon.test.template;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.ReturnReplaceTemplate;
import spoon.testing.utils.ModelUtils;

public class TemplateReplaceReturnTest {

	@Test
	public void testReturnReplaceTemplate() throws Exception {
		//contract: the template engine supports replace of `return _param_.S()` by `<CtBlock>`
		Launcher launcher = new Launcher();
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/ReturnReplaceTemplate.java"));

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		CtBlock<String> model = (CtBlock) factory.Class().get(ReturnReplaceTemplate.class).getMethod("sample").getBody();
		
		CtClass<?> resultKlass = factory.Class().create(factory.Package().getOrCreate("spoon.test.template"), "ReturnReplaceResult");
		new ReturnReplaceTemplate(model).apply(resultKlass);
		assertEquals("{ if (((java.lang.System.currentTimeMillis()) % 2L) == 0) { return \"Panna\"; }else { return \"Orel\"; }}", resultKlass.getMethod("method").getBody().toString().replaceAll("[\\r\\n\\t]+", "").replaceAll("\\s{2,}", " "));
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		ModelUtils.canBeBuilt(new File("./target/spooned/spoon/test/template/ReturnReplaceResult.java"), 8);
	}

}
