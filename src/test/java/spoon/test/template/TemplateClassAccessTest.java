package spoon.test.template;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.ClassAccessTemplate;

public class TemplateClassAccessTest {

	@Test
	public void testClassAccessTest() throws Exception {
		//contract: the template engine supports class access substitution
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/ClassAccessTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtStatement result = new ClassAccessTemplate(String.class).apply(resultKlass);
		assertEquals("java.lang.String.class.getName()", result.toString());
	}

}
