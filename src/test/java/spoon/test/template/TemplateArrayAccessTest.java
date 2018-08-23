package spoon.test.template;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.SubstituteArrayAccessTemplate;
import spoon.test.template.testclasses.SubstituteArrayLengthTemplate;

public class TemplateArrayAccessTest {

	@Test
	public void testArrayAccess() {
		//contract: the template engine supports variable access, typed as Array substitution
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubstituteArrayAccessTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtStatement result = new SubstituteArrayAccessTemplate(new String[]{"a",null,"b"}).apply(resultKlass);
		assertEquals("new java.lang.String[]{ \"a\", null, \"b\" }.toString()", result.toString());
	}

	@Test
	public void testArrayLengthAccess() {
		//contract: the template engine replaces length of collection of parameter values by number
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubstituteArrayLengthTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtStatement result = new SubstituteArrayLengthTemplate(new String[]{"a",null,"b"}).apply(resultKlass);
		assertEquals("if (3 > 0);", result.toString());
	}
}
