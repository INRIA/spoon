package spoon.test.template;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.annotation.ElementType;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.EnumAccessTemplate;
import spoon.testing.utils.ModelUtils;

public class TemplateEnumAccessTest {

	@Test
	public void testEnumAccessTest() throws Exception {
		//contract: the template engine supports enum value access substitution
		Launcher launcher = new Launcher();
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/EnumAccessTemplate.java"));

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		CtClass<?> resultKlass = factory.Class().create(factory.Package().getOrCreate("spoon.test.template"), "EnumAccessResult");
		new EnumAccessTemplate(ElementType.FIELD, launcher.getFactory()).apply(resultKlass);
		assertEquals("java.lang.annotation.ElementType.FIELD.name()", resultKlass.getMethod("method").getBody().getStatement(0).toString());
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		ModelUtils.canBeBuilt(new File("./target/spooned/spoon/test/template/EnumAccessResult.java"), 8);
	}

}
