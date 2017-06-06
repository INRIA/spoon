package spoon.test.template;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.template.Parameters;
import spoon.template.TemplateMatcher;
import spoon.template.TemplateParameter;
import spoon.test.template.testclasses.ArrayAccessTemplate;
import spoon.test.template.testclasses.InvocationTemplate;
import spoon.test.template.testclasses.NtonCodeTemplate;
import spoon.test.template.testclasses.SecurityCheckerTemplate;
import spoon.test.template.testclasses.SimpleTemplate;
import spoon.test.template.testclasses.bounds.CheckBound;
import spoon.test.template.testclasses.bounds.CheckBoundMatcher;
import spoon.test.template.testclasses.bounds.CheckBoundTemplate;
import spoon.test.template.testclasses.bounds.FooBound;
import spoon.test.template.testclasses.constructors.C1;
import spoon.test.template.testclasses.constructors.TemplateWithConstructor;
import spoon.test.template.testclasses.constructors.TemplateWithFieldsAndMethods;
import spoon.test.template.testclasses.inheritance.InterfaceTemplate;
import spoon.test.template.testclasses.inheritance.SubClass;
import spoon.test.template.testclasses.inheritance.SubTemplate;
import spoon.test.template.testclasses.inheritance.SuperClass;
import spoon.test.template.testclasses.inheritance.SuperTemplate;
import spoon.test.template.testclasses.logger.Logger;
import spoon.test.template.testclasses.logger.LoggerTemplateProcessor;

import java.io.File;
import java.io.Serializable;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static java.util.Arrays.asList;

public class TemplateTest {

	@Test
	public void testTemplateInheritance() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();
		spoon.getEnvironment().setCommentEnabled(true);
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/template/testclasses/inheritance/SubClass.java",
						"./src/test/java/spoon/test/template/testclasses/inheritance/SuperClass.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/testclasses/inheritance/SubTemplate.java",
								"./src/test/java/spoon/test/template/testclasses/inheritance/SuperTemplate.java"))
				.build();

		CtClass<?> superc = factory.Class().get(SuperClass.class);
		new SuperTemplate().apply(superc);

		CtMethod<?> addedMethod = superc.getElements(
				new NameFilter<CtMethod<?>>("toBeOverriden")).get(0);
		assertEquals("toBeOverriden", addedMethod.getSimpleName());

		CtClass<?> subc = factory.Class().get(SubClass.class);
		SubTemplate template = new SubTemplate();
		template.params = new ArrayList<>();
		CtParameter<Integer> parameter = factory.Core().createParameter();
		parameter.setSimpleName("x");
		parameter.setType(factory.Type().createReference(int.class));
		template.params.add(parameter);

		// templating the invocation
		template.invocation = factory.Code().createInvocation(null, addedMethod.getReference());

		// templating the foreach
		template.intValues = new CtExpression[2];
		template.intValues[0] = factory.Code().createLiteral(0);
		template.intValues[1] = factory.Code().createLiteral(1);

		// we apply the extension template to subc
		template.apply(subc);

		CtMethod<?> addedMethod2 = subc.getElements(
				new NameFilter<CtMethod<?>>("toBeOverriden")).get(0);
		assertEquals("toBeOverriden", addedMethod2.getSimpleName());
		assertEquals("super.toBeOverriden()", addedMethod2.getBody()
				.getStatements().get(0).toString());

		// contract; method parameter templates are handled
		CtMethod<?> methodWithTemplatedParameters = subc.getElements(
				new NameFilter<CtMethod<?>>("methodWithTemplatedParameters")).get(0);
		assertEquals("methodWithTemplatedParameters", methodWithTemplatedParameters.getSimpleName());
		assertEquals("x", methodWithTemplatedParameters.getParameters().get(0).getSimpleName());
		assertEquals("int x", methodWithTemplatedParameters.getParameters().get(0).toString());

		// contract: nested types of the templates are copied
		assertEquals(1, subc.getNestedTypes().size());

		// contract: variable are renamed
		assertEquals("java.util.List newVarName = null", methodWithTemplatedParameters.getBody().getStatement(0).toString());

		// contract: types are replaced by other types
		assertEquals("java.util.LinkedList l = null", methodWithTemplatedParameters.getBody().getStatement(1).toString());

		// contract: casts are replaced by substitution types
		assertEquals("java.util.List o = ((java.util.LinkedList) (new java.util.LinkedList()))", methodWithTemplatedParameters.getBody().getStatement(2).toString());

		// contract: invocations are replaced by actual invocations
		assertEquals("toBeOverriden()", methodWithTemplatedParameters.getBody().getStatement(3).toString());

		// contract: foreach are inlined
		CtBlock templatedForEach = methodWithTemplatedParameters.getBody().getStatement(4);
		assertEquals("java.lang.System.out.println(0)", templatedForEach.getStatement(0).toString());
		assertEquals("java.lang.System.out.println(1)", templatedForEach.getStatement(1).toString());
	}

	@Test
	public void testTemplateC1() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/template/testclasses/constructors/C1.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/testclasses/constructors/TemplateWithConstructor.java",
								"./src/test/java/spoon/test/template/testclasses/constructors/TemplateWithFieldsAndMethods.java"))
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
						"./src/test/java/spoon/test/template/testclasses/bounds/FooBound.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/testclasses/bounds/CheckBoundTemplate.java"))
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
				SpoonResourceHelper.resources("./src/test/java/spoon/test/template/testclasses/bounds/CheckBound.java"),
				SpoonResourceHelper.resources("./src/test/java/spoon/test/template/testclasses/bounds/CheckBoundMatcher.java"))
				.build();

		{// testing matcher1
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher1")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
			assertThat(asList("foo","fbar"), is(klass.filterChildren(matcher).map((CtElement e)->e.getParent(CtMethod.class).getSimpleName()).list())) ;
		}

		{// testing matcher2
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher2")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(1, matcher.find(klass).size());
			assertThat(asList("bov"), is(klass.filterChildren(matcher).map((CtElement e)->e.getParent(CtMethod.class).getSimpleName()).list())) ;
		}

		{// testing matcher3
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher3")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
			assertThat(asList("foo","fbar"), is(klass.filterChildren(matcher).map((CtElement e)->e.getParent(CtMethod.class).getSimpleName()).list())) ;
		}

		{// testing matcher4
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher4")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(3, matcher.find(klass).size());
			assertThat(asList("foo","foo2","fbar"), is(klass.filterChildren(matcher).map((CtElement e)->e.getParent(CtMethod.class).getSimpleName()).list())) ;
		}

		{// testing matcher5
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher5")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(6, matcher.find(klass).size());
			assertThat(asList("foo","foo2","fbar","baz","bou","bov"), is(klass.filterChildren(matcher).map((CtElement e)->e.getParent(CtMethod.class).getSimpleName()).list())) ;
		}

		{// testing matcher6
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher6")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
			assertThat(asList("baz","bou"), is(klass.filterChildren(matcher).map((CtElement e)->e.getParent(CtMethod.class).getSimpleName()).list())) ;
		}


		// testing with named elements, at the method level
		{
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtMethod meth = (CtMethod) templateKlass.getElements(new NameFilter("matcher3")).get(0);

			// exact match
			meth.setSimpleName("foo");
			TemplateMatcher matcher = new TemplateMatcher(meth);
			List<CtMethod> ctElements = matcher.find(klass);
			assertEquals(1, ctElements.size());
			assertEquals("foo", ctElements.get(0).getSimpleName());
		}

		{
			// contract: the name to be matched does not have to be an exact match
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtMethod meth = (CtMethod) templateKlass.getElements(new NameFilter("matcher5")).get(0);

			// together with the appropriate @Parameter _w_, this means
			// we match all methods with name f*, because parameter _w_ acts as a wildcard
			meth.setSimpleName("f_w_");
			TemplateMatcher matcher = new TemplateMatcher(meth);
			List<CtMethod> ctElements = matcher.find(klass);
			assertEquals(3, ctElements.size());
			assertEquals("foo", ctElements.get(0).getSimpleName());
			assertEquals("foo2", ctElements.get(1).getSimpleName());
			assertEquals("fbar", ctElements.get(2).getSimpleName());
		}
	}

	@Test
	public void testExtensionBlock() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/template/testclasses/logger/Logger.java");
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/logger/LoggerTemplate.java"));
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
		assertEquals("spoon.test.template.testclasses.logger.Logger.exit(\"enter\")", aTry.getFinalizer().getStatement(0).toString());
		assertTrue(aTry.getBody().getStatement(0) instanceof CtInvocation);
		assertEquals("spoon.test.template.testclasses.logger.Logger.enter(\"Logger\", \"enter\")", aTry.getBody().getStatement(0).toString());
		assertTrue(aTry.getBody().getStatements().size() > 1);
	}

	@Test
	public void testTemplateInterfaces() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/template/testclasses/inheritance/SubClass.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/testclasses/inheritance/InterfaceTemplate.java")
				)
				.build();

		CtClass<?> superc = factory.Class().get(SuperClass.class);
		InterfaceTemplate interfaceTemplate = new InterfaceTemplate(superc.getFactory());
		interfaceTemplate.apply(superc);

		assertEquals(3, superc.getSuperInterfaces().size());
		assertTrue(superc.getSuperInterfaces().contains(factory.Type().createReference(Comparable.class)));
		assertTrue(superc.getSuperInterfaces().contains(factory.Type().createReference(Serializable.class)));
		assertTrue(superc.getSuperInterfaces().contains(factory.Type().createReference(Remote.class)));
	}

	@Test
	public void testTemplateMatcherWithWholePackage() throws Exception {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/template/testclasses/ContextHelper.java");
		spoon.addInputResource("./src/test/java/spoon/test/template/testclasses/BServiceImpl.java");

		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SecurityCheckerTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> templateKlass = factory.Class().get(SecurityCheckerTemplate.class);
		CtMethod templateMethod = (CtMethod) templateKlass.getElements(new NameFilter("matcher1")).get(0);
		CtIf templateRoot = (CtIf) templateMethod.getBody().getStatement(0);
		TemplateMatcher matcher = new TemplateMatcher(templateRoot);

		List<CtElement> matches = matcher.find(factory.getModel().getRootPackage());

		assertEquals(1, matches.size());

		CtElement match = matches.get(0);

		assertTrue("Match is not a if", match instanceof CtIf);

		CtElement matchParent = match.getParent();

		assertTrue("Match parent is not a block", matchParent instanceof CtBlock);

		CtElement matchParentParent = matchParent.getParent();

		assertTrue("Match grand parent is not a method", matchParentParent instanceof CtMethod);

		CtMethod methodHello = (CtMethod)matchParentParent;

		assertEquals("Match grand parent is not a method called hello", "hello", methodHello.getSimpleName());

		CtElement methodParent = methodHello.getParent();

		assertTrue("Parent of the method is not a class",methodParent instanceof CtClass);

		CtClass bservice = (CtClass) methodParent;

		assertEquals("Parent of the method is not a class called BServiceImpl", "BServiceImpl", bservice.getSimpleName());
	}

	@Test
	public void testTemplateMatcherMatchTwoSnippets() throws Exception {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/template/testclasses/TwoSnippets.java");
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SecurityCheckerTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> templateKlass = factory.Class().get(SecurityCheckerTemplate.class);
		CtMethod templateMethod = (CtMethod) templateKlass.getElements(new NameFilter("matcher1")).get(0);
		CtIf templateRoot = (CtIf) templateMethod.getBody().getStatement(0);
		TemplateMatcher matcher = new TemplateMatcher(templateRoot);

		//match using legacy TemplateMatcher#find method
		List<CtElement> matches = matcher.find(factory.getModel().getRootPackage());

		assertEquals(2, matches.size());

		CtElement match1 = matches.get(0);
		CtElement match2 = matches.get(1);

		assertTrue(match1.equals(match2));
		
		//match using TemplateMatcher#matches method and query filter
		matches = factory.getModel().getRootPackage().filterChildren(matcher).list();

		assertEquals(2, matches.size());

		match1 = matches.get(0);
		match2 = matches.get(1);

		assertTrue(match1.equals(match2));
	}
	@Test
	public void testTemplateInvocationSubstitution() throws Exception {
		//contract: the template engine supports substitution of method names in method calls.
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/InvocationTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		new InvocationTemplate(factory.Type().OBJECT, "hashCode").apply(resultKlass);
		CtMethod<?> templateMethod = (CtMethod<?>) resultKlass.getElements(new NameFilter("invoke")).get(0);
		CtStatement templateRoot = (CtStatement) templateMethod.getBody().getStatement(0);
		//iface.$method$() becomes iface.hashCode()
		assertEquals("iface.hashCode()", templateRoot.toString());
	}

	@Test
	public void testSimpleTemplate() {
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SimpleTemplate.java"));
		spoon.buildModel();

		Factory factory = spoon.getFactory();

		CtClass<?> testSimpleTpl = factory.Class().create("TestSimpleTpl");
		new SimpleTemplate("Hello world").apply(testSimpleTpl);

		Set<CtMethod<?>> listMethods = testSimpleTpl.getMethods();
		assertEquals(0, testSimpleTpl.getMethodsByName("apply").size());
		assertEquals(1, listMethods.size());
	}

	@Test
	public void testSubstitutionInsertAllNtoN() {
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/NtonCodeTemplate.java"));
		spoon.addInputResource("./src/test/java/spoon/test/template/testclasses/C.java");
		spoon.buildModel();

		Factory factory = spoon.getFactory();

		CtClass<?> cclass = factory.Class().get("spoon.test.template.testclasses.C");
		new NtonCodeTemplate(cclass.getReference(), 5).apply(cclass);

		Set<CtMethod<?>> listMethods = cclass.getMethods();
		assertEquals(0, cclass.getMethodsByName("apply").size());
		assertEquals(4, listMethods.size());
	}

	@Test
	public void testTemplateArrayAccess() throws Exception {
		//contract: the template engine supports substitution of arrays of parameters.
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/ArrayAccessTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		CtClass<?> templateClass = factory.Class().get(ArrayAccessTemplate.class);
		//create array of template parameters, which contains CtBlocks
		TemplateParameter[] params = templateClass.getMethod("sampleBlocks").getBody().getStatements().toArray(new TemplateParameter[0]);
		new ArrayAccessTemplate(params).apply(resultKlass);
		CtMethod<?> m = resultKlass.getMethod("method");
		//check that both TemplateParameter usages were replaced by appropriate parameter value
		assertEquals(2, m.getBody().getStatements().size());
		assertTrue(m.getBody().getStatements().get(0) instanceof CtBlock);
		assertEquals("int i = 0", ((CtBlock)m.getBody().getStatements().get(0)).getStatement(0).toString());
		assertTrue(m.getBody().getStatements().get(1) instanceof CtBlock);
		assertEquals("java.lang.String s = \"Spoon is cool!\"", ((CtBlock)m.getBody().getStatements().get(1)).getStatement(0).toString());
		//check that both @Parameter usage was replaced by appropriate parameter value
		CtMethod<?> m2 = resultKlass.getMethod("method2");
		assertEquals("java.lang.System.out.println(\"second\")", m2.getBody().getStatement(0).toString());
	}
}
