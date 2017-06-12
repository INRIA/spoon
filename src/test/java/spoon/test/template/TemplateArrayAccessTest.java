package spoon.test.template;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.SubstituteArrayAccessTemplate;

public class TemplateArrayAccessTest {

	@Test
	public void testArrayAccess() throws Exception {
		//contract: the template engine supports variable access, typed as Array substitution
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubstituteArrayAccessTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtStatement result = new SubstituteArrayAccessTemplate(new String[]{"a","b"}).apply(resultKlass);
		assertEquals("new java.lang.String[]{ \"a\" , \"b\" }.toString()", result.toString());
	}

}
