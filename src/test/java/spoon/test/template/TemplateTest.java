package spoon.test.template;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import spoon.Spoon;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.template.Substitution;

public class TemplateTest {

	@Test
	public void testTemplateInheritance() throws Exception {
		Factory factory = Spoon.createFactory();
		Spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/template/SubClass.java",
						"./src/test/java/spoon/test/template/SuperClass.java"),
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/template/SubTemplate.java",
								"./src/test/java/spoon/test/template/SuperTemplate.java"))
				.build();

		CtClass<?> superc = factory.Class().get(SuperClass.class);
		// superc.updateAllParentsBelow();
		Substitution.insertAll(superc, new SuperTemplate());

		CtMethod<?> addedMethod = superc.getElements(
				new NameFilter<CtMethod<?>>("toBeOverriden")).get(0);
		assertEquals("toBeOverriden", addedMethod.getSimpleName());

		CtClass<?> subc = factory.Class().get(SubClass.class);
		Substitution.insertAll(subc, new SubTemplate());
		CtMethod<?> addedMethod2 = subc.getElements(
				new NameFilter<CtMethod<?>>("toBeOverriden")).get(0);
		assertEquals("toBeOverriden", addedMethod2.getSimpleName());
		assertEquals("super.toBeOverriden()", addedMethod2.getBody()
				.getStatements().get(0).toString());

	}

	@Test
	public void testTemplateC1() throws Exception {
		Factory factory = Spoon.createFactory();
		Spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/template/C1.java"),
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/template/TemplateWithConstructor.java"))
				.build();

		CtClass<?> c1 = factory.Class().get(C1.class);

		// before template: 1 constructor
		assertEquals(1, // this is the default implicit constructor
				c1.getConstructors().size());

		// the actual substitution
		Substitution.insertAll(c1, new TemplateWithConstructor(factory.Type()
				.createReference(Date.class)));

		// after template: 3 constructors
		// System.out.println("==>"+c1.getConstructors());
		assertEquals(3, c1.getConstructors().size());

		CtField<?> toBeInserted = c1.getElements(
				new NameFilter<CtField<?>>("toBeInserted")).get(0);
		assertEquals(Date.class, toBeInserted.getType()
				.getActualTypeArguments().get(0).getActualClass());
		assertEquals(
				"java.util.List<java.util.Date> toBeInserted = new java.util.ArrayList<java.util.Date>();",
				toBeInserted.toString());

	}

}
