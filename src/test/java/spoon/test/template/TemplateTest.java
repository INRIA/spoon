package spoon.test.template;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.template.Parameters;
import spoon.template.Substitution;
import spoon.template.TemplateMatcher;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

		CtIf ifStmt = (CtIf) injectedCode;

		// contains the replaced code
		assertEquals("(l.size()) > 10", ifStmt.getCondition().toString());

		// adds the bound check at the beginning of a method
		method.getBody().insertBegin(injectedCode);
		assertEquals(injectedCode, method.getBody().getStatement(0));
	}

	@Test
	public void testTemplateMatcher() throws Exception {
		// contract: the given templates should match the expected elements
		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources("./src/test/java/spoon/test/template/CheckBound.java"),
				SpoonResourceHelper.resources("./src/test/java/spoon/test/template/CheckBoundMatcher.java"))
				.build();

		{// testing matcher1
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher1")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
		}

		{// testing matcher2
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher2")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(1, matcher.find(klass).size());
		}

		{// testing matcher3
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher3")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
		}

		{// testing matcher4
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher4")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(3, matcher.find(klass).size());
		}

		{// testing matcher5
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher5")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(6, matcher.find(klass).size());
		}

		{// testing matcher6
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher6")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
		}
	}

	@Test
	public void testExtensionBlock() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/template/Logger.java");
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/LoggerTemplate.java"));
		launcher.addProcessor(new LoggerTemplateProcessor());
		launcher.getEnvironment().setSourceClasspath(System.getProperty("java.class.path").split(File.pathSeparator));
		try {
			launcher.run();
		} catch (ClassCastException ignored) {
			fail();
		}

		final CtClass<Logger> aLogger = launcher.getFactory().Class().get(Logger.class);
		final CtMethod aMethod = aLogger.getMethodsByName("enter").get(0);
		assertTrue(aMethod.getBody().getStatement(0) instanceof CtTry);
		final CtTry aTry = (CtTry) aMethod.getBody().getStatement(0);
		assertTrue(aTry.getFinalizer().getStatement(0) instanceof CtInvocation);
		assertEquals("spoon.test.template.Logger.exit(\"enter\")", aTry.getFinalizer().getStatement(0).toString());
		assertTrue(aTry.getBody().getStatement(0) instanceof CtInvocation);
		assertEquals("spoon.test.template.Logger.enter(\"Logger\", \"enter\")", aTry.getBody().getStatement(0).toString());
		assertTrue(aTry.getBody().getStatements().size() > 1);
	}
}
