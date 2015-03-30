package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.support.template.Parameters;
import spoon.template.Substitution;

public class TemplateTest {

	@Test
	public void testTemplateInheritance() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/template/SubClass.java",
						"./src/test/java/spoon/test/template/SuperClass.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/SubTemplate.java",
								"./src/test/java/spoon/test/template/SuperTemplate.java"))
				.build();

		CtClass<?> superc = factory.Class().get(SuperClass.class);
		new SuperTemplate().apply(superc);

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
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/template/C1.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/TemplateWithConstructor.java",
								"./src/test/java/spoon/test/template/TemplateWithFieldsAndMethods.java"))
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
				"java.util.List<java.util.Date> toBeInserted = new java.util.ArrayList<java.util.Date>();",
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

	@Test
	public void testCheckBoundTemplate() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/template/FooBound.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/CheckBoundTemplate.java"))
				.build();

		CtClass<?> c = factory.Class().get(FooBound.class);
		
		CtMethod<?> method = c.getMethodsByName("method").get(0);
	
		assertEquals(1, Parameters.getAllTemplateParameterFields(CheckBoundTemplate.class).size());
		assertEquals(1, Parameters.getAllTemplateParameterFields(CheckBoundTemplate.class, factory).size());
		
		// creating a template instance
		CheckBoundTemplate t = new CheckBoundTemplate();
		assertTrue(t.isWellFormed());
		assertFalse(t.isValid());
		CtParameter<?> param = method.getParameters().get(0);
		t.setVariable(param);
		assertTrue(t.isValid());
		
		// getting the final AST
		CtStatement injectedCode = (t.apply(null));

		assertTrue(injectedCode instanceof CtIf);
		
		CtIf ifStmt = (CtIf)injectedCode;
		
		// contains the replaced code
		assertEquals("(l.size()) > 10", ifStmt.getCondition().toString());
		
		// adds the bound check at the beginning of a method
		method.getBody().insertBegin(injectedCode);		
		assertEquals(injectedCode, method.getBody().getStatement(0));
	}
}
