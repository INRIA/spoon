package spoon.test.template;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.filter.NameFilter;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thomas on 19/12/15.
 */
public class TemplateFromByteCode {

	@Test
	public void testTemplateC1() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/template/C1.java"),
				SpoonResourceHelper
						.resources())
				.build();

		CtClass<?> c1 = factory.Class().get(C1.class);

		// before template: 1 constructor
		assertEquals(1, // this is the default implicit constructor
				c1.getConstructors().size());

		// the actual substitution
		new TemplateWithConstructor(factory.Type()
				.createReference(Date.class))
				.apply(c1);

		// after template: 3 constructors
		// System.out.println("==>"+c1.getConstructors());
		assertEquals(3, c1.getConstructors().size());

		CtField<?> toBeInserted = c1.getElements(
				new NameFilter<CtField<?>>("toBeInserted")).get(0);
		assertEquals(Date.class, toBeInserted.getType()
				.getActualTypeArguments().get(0).getActualClass());
		assertEquals(
				"java.util.List<java.util.Date> toBeInserted = new java.util.ArrayList();",
				toBeInserted.toString());

		new TemplateWithFieldsAndMethods(
				"testparam", factory.Code().createLiteral("testparam2")).apply(c1);

		assertEquals(3, c1.getConstructors().size());
		assertNotNull(c1.getField("fieldToBeInserted"));

		CtMethod<?> m = c1.getMethod("methodToBeInserted");
		assertNotNull(m);
		assertEquals("return \"testparam\"", m.getBody().getStatement(0)
				.toString());

		CtMethod<?> m2 = c1.getMethod("methodToBeInserted2");
		assertNotNull(m2);
		assertEquals("return \"testparam2\"", m2.getBody().getStatement(0)
				.toString());

		new ModelConsistencyChecker(factory.getEnvironment(), false, true).scan(c1);

		assertEquals(0, factory.getEnvironment().getErrorCount());
		assertEquals(0, factory.getEnvironment().getWarningCount());

	}
}
