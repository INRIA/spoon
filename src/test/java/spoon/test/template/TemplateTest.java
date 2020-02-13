/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.template;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonResourceHelper;
import spoon.pattern.Match;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.ModelConsistencyChecker;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.template.Parameters;
import spoon.template.ExpressionTemplate;
import spoon.template.Substitution;
import spoon.template.TemplateMatcher;
import spoon.template.TemplateParameter;
import spoon.test.template.testclasses.AnExpressionTemplate;
import spoon.test.template.testclasses.AnotherFieldAccessTemplate;
import spoon.test.template.testclasses.ArrayAccessTemplate;
import spoon.test.template.testclasses.FieldAccessOfInnerClassTemplate;
import spoon.test.template.testclasses.FieldAccessTemplate;
import spoon.test.template.testclasses.Flow;
import spoon.test.template.testclasses.FlowMatcher;
import spoon.test.template.testclasses.InnerClassTemplate;
import spoon.test.template.testclasses.InvocationTemplate;
import spoon.test.template.testclasses.NtonCodeTemplate;
import spoon.test.template.testclasses.ObjectIsNotParamTemplate;
import spoon.test.template.testclasses.SecurityCheckerTemplate;
import spoon.test.template.testclasses.SimpleTemplate;
import spoon.test.template.testclasses.SubStringTemplate;
import spoon.test.template.testclasses.SubstituteLiteralTemplate;
import spoon.test.template.testclasses.SubstituteRootTemplate;
import spoon.test.template.testclasses.TypeReferenceClassAccessTemplate;
import spoon.test.template.testclasses.bounds.CheckBound;
import spoon.test.template.testclasses.bounds.CheckBoundMatcher;
import spoon.test.template.testclasses.bounds.CheckBoundTemplate;
import spoon.test.template.testclasses.bounds.FooBound;
import spoon.test.template.testclasses.constructors.C1;
import spoon.test.template.testclasses.constructors.TemplateWithConstructor;
import spoon.test.template.testclasses.constructors.TemplateWithFieldsAndMethods;
import spoon.test.template.testclasses.constructors.TemplateWithFieldsAndMethods_Wrong;
import spoon.test.template.testclasses.inheritance.InterfaceTemplate;
import spoon.test.template.testclasses.inheritance.SubClass;
import spoon.test.template.testclasses.inheritance.SubTemplate;
import spoon.test.template.testclasses.inheritance.SuperClass;
import spoon.test.template.testclasses.inheritance.SuperTemplate;
import spoon.test.template.testclasses.logger.Logger;
import spoon.test.template.testclasses.logger.LoggerTemplateProcessor;
import spoon.test.template.testclasses.types.AClassModel;
import spoon.test.template.testclasses.types.AnEnumModel;
import spoon.test.template.testclasses.types.AnIfaceModel;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.io.Serializable;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.getOptimizedString;

public class TemplateTest {

	private String newLine = System.getProperty("line.separator");

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

		//Collects type members which should contain generated by comment
		final Map<CtElement, String> elementToGeneratedByMember = new IdentityHashMap<>();
		
		CtClass<?> superc = factory.Class().get(SuperClass.class);
		new SuperTemplate().addGeneratedBy(true).apply(superc);

		CtMethod<?> addedMethod = superc.getElements(
				new NamedElementFilter<>(CtMethod.class,"toBeOverriden")).get(0);
		assertEquals("toBeOverriden", addedMethod.getSimpleName());
		elementToGeneratedByMember.put(addedMethod, "#toBeOverriden");

		CtClass<?> subc = factory.Class().get(SubClass.class);
		SubTemplate template = new SubTemplate();
		template.addGeneratedBy(true);
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
				new NamedElementFilter<>(CtMethod.class,"toBeOverriden")).get(0);
		assertEquals("toBeOverriden", addedMethod2.getSimpleName());
		assertEquals("super.toBeOverriden()", addedMethod2.getBody()
				.getStatements().get(0).toString());
		elementToGeneratedByMember.put(addedMethod2, "#toBeOverriden");

		// contract; method parameter templates are handled
		CtMethod<?> methodWithTemplatedParameters = subc.getElements(
				new NamedElementFilter<>(CtMethod.class,"methodWithTemplatedParameters")).get(0);
		assertEquals("methodWithTemplatedParameters", methodWithTemplatedParameters.getSimpleName());
		assertEquals("x", methodWithTemplatedParameters.getParameters().get(0).getSimpleName());
		assertEquals("int x", methodWithTemplatedParameters.getParameters().get(0).toString());
		elementToGeneratedByMember.put(methodWithTemplatedParameters, "#methodWithTemplatedParameters");

		// contract: nested types of the templates are copied
		assertEquals(1, subc.getNestedTypes().size());
		elementToGeneratedByMember.put(subc.getNestedTypes().iterator().next(), "$InnerClass");
		
		// contract: methods are renamed
		assertEquals(1, subc.getMethodsByName("newVarName").size());
		CtMethod<?> varMethod = subc.getMethodsByName("newVarName").get(0);
		elementToGeneratedByMember.put(varMethod, "#var");
		// contract: parameters are replaced in comments too. The Class parameter value is converted to String
		assertEquals("newVarName", varMethod.getComments().get(0).getContent().split("[\\n\\r]+")[0]);
		assertEquals("{@link LinkedList}", varMethod.getComments().get(1).getContent());
		assertEquals("{@link SuperClass#toBeOverriden()}", varMethod.getComments().get(2).getContent());

		// contract: variable are renamed
		assertEquals("java.util.List newVarName = null// will be replaced by List newVarName = null;" + newLine, methodWithTemplatedParameters.getBody().getStatement(0).toString());

		// contract: types are replaced by other types
		assertEquals("java.util.LinkedList l = null// will be replaced by LinkedList l = null;" + newLine, methodWithTemplatedParameters.getBody().getStatement(1).toString());

		// contract: casts are replaced by substitution types
		assertEquals("java.util.List o = ((java.util.LinkedList) (new java.util.LinkedList()))// will be replaced by List o = (LinkedList) new LinkedList();" + newLine, methodWithTemplatedParameters.getBody().getStatement(2).toString());

		// contract: invocations are replaced by actual invocations
		assertEquals("toBeOverriden()", methodWithTemplatedParameters.getBody().getStatement(3).toString());

		// contract: foreach in block is inlined into that wrapping block
		CtBlock templatedForEach = methodWithTemplatedParameters.getBody().getStatement(4);
		assertEquals("java.lang.System.out.println(0)", templatedForEach.getStatement(0).toString());
		assertEquals("java.lang.System.out.println(1)", templatedForEach.getStatement(1).toString());
		// contract: foreach with single body block are inlined without extra block
		assertEquals("java.lang.System.out.println(0)", methodWithTemplatedParameters.getBody().getStatement(5).toString());
		assertEquals("java.lang.System.out.println(1)", methodWithTemplatedParameters.getBody().getStatement(6).toString());
		// contract: foreach with double body block are inlined with one extra block for each inlined statement
		assertEquals("java.lang.System.out.println(0)", ((CtBlock) methodWithTemplatedParameters.getBody().getStatement(7)).getStatement(0).toString());
		assertEquals("java.lang.System.out.println(1)", ((CtBlock) methodWithTemplatedParameters.getBody().getStatement(8)).getStatement(0).toString());
		// contract: foreach with statement are inlined without extra (implicit) block
		assertFalse(methodWithTemplatedParameters.getBody().getStatement(9) instanceof CtBlock);
		assertEquals("java.lang.System.out.println(0)", methodWithTemplatedParameters.getBody().getStatement(9).toString());
		assertFalse(methodWithTemplatedParameters.getBody().getStatement(10) instanceof CtBlock);
		assertEquals("java.lang.System.out.println(1)", methodWithTemplatedParameters.getBody().getStatement(10).toString());
		//contract: for each whose expression is not a template parameter is not inlined
		assertTrue(methodWithTemplatedParameters.getBody().getStatement(11) instanceof CtForEach);

		// contract: local variable write are replaced by local variable write with modified local variable name
		assertEquals("newVarName = o// will be replaced by newVarName = o" + newLine, methodWithTemplatedParameters.getBody().getStatement(12).toString());

		// contract: local variable read are replaced by local variable read with modified local variable name
		assertEquals("l = ((java.util.LinkedList) (newVarName))// will be replaced by l = (LinkedList) newVarName" + newLine, methodWithTemplatedParameters.getBody().getStatement(13).toString());
		
		// contract; field access is handled same like local variable access
		CtMethod<?> methodWithFieldAccess = subc.getElements(
				new NamedElementFilter<>(CtMethod.class, "methodWithFieldAccess")).get(0);
		elementToGeneratedByMember.put(methodWithFieldAccess, "#methodWithFieldAccess");
		elementToGeneratedByMember.put(subc.getField("newVarName"), "#var");

		// contract: field write are replaced by field write with modified field name
		assertEquals("newVarName = o", methodWithFieldAccess.getBody().getStatement(2).toString());

		// contract: field read are replaced by field read with modified field name
		assertEquals("l = ((java.util.LinkedList) (newVarName))// will be replaced by l = (LinkedList) newVarName" + newLine, methodWithFieldAccess.getBody().getStatement(3).toString());
		

		class Context {
			int nrTypeMembers = 0;
			int nrOthers = 0;
		}
		final Context ctx = new Context();
		//contract: each CtTypeMember has `Generated by ` with appropriate template class name, member name and line number
		superc.filterChildren(null).forEach((CtElement e)->{
			if (e instanceof CtTypeMember) {
				ctx.nrTypeMembers++;
				assertCommentHasGeneratedBy(e, SuperTemplate.class.getName(),elementToGeneratedByMember);
			} else {
				ctx.nrOthers++;
				assertTrue(e.getDocComment()==null || !e.getDocComment().contains("Generated by"));
			}
		});
		assertTrue(ctx.nrTypeMembers>0);
		assertTrue(ctx.nrOthers>0);
		ctx.nrTypeMembers = 0;
		ctx.nrOthers = 0;
		subc.filterChildren(null).forEach((CtElement e)->{
			if (e instanceof CtTypeMember) {
				ctx.nrTypeMembers++;
				assertCommentHasGeneratedBy(e, SubTemplate.class.getName(),elementToGeneratedByMember);
			} else {
				ctx.nrOthers++;
				assertTrue(e.getDocComment()==null || !e.getDocComment().contains("Generated by"));
			}
		});
		assertTrue(ctx.nrTypeMembers>0);
		assertTrue(ctx.nrOthers>0);
	}

	/*
	 * Matches comments like:
	 * Generated by spoon.test.template.testclasses.inheritance.SuperTemplate#toBeOverriden(SuperTemplate.java:7)
	 * Generated by spoon.test.template.testclasses.inheritance.SubTemplate#methodWithTemplatedParameters(SubTemplate.java:20)
	 * Generated by spoon.test.template.testclasses.inheritance.SubTemplate#toBeOverriden(SubTemplate.java:16)
	 * Generated by spoon.test.template.testclasses.inheritance.SubTemplate#var(SubTemplate.java:51)
	 * Generated by spoon.test.template.testclasses.inheritance.SubTemplate$InnerClass(SubTemplate.java:76)
	 */
	private static final Pattern generatedByRE = Pattern.compile(".*Generated by ([^$#\\(]+)([^\\(]*)\\(([^\\.]+)\\.java:(\\d+)\\).*", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern typeMemberRE = Pattern.compile("[^\\.$#]+$");
	
	private void assertCommentHasGeneratedBy(CtElement e, String templateQName, Map<CtElement, String> elementToGeneratedByMember) {
		String docComment = e.getDocComment();
		String generatedByMember = elementToGeneratedByMember.get(e);
		if (generatedByMember == null) {
			assertEquals("", docComment);
//			assertTrue(e.getPosition().isValidPosition() == false);
		} else {
			assertNotNull("Javadoc comment is missing for "+e.toString(), docComment);
			int idx = docComment.indexOf("Generated by");
			assertTrue("Javadoc comment doesn't contain Generated by. There is:\n"+docComment, idx>=0);
			Matcher m = generatedByRE.matcher(docComment);
			assertTrue("Unexpected Generated by:\n"+docComment, m.matches());
			assertEquals(templateQName, m.group(1));
			assertEquals(generatedByMember, m.group(2));
			assertTrue(templateQName.endsWith("."+m.group(3)));
			int lineNr = Integer.parseInt(m.group(4))-1;
			assertTrue(lineNr>=0);
			String[] sourceLines = e.getPosition().getCompilationUnit().getOriginalSourceCode().split("\\r\\n|\\r|\\n");
			assertTrue(sourceLines.length>lineNr);
			String sourceLine = sourceLines[lineNr];
			Matcher m2 = typeMemberRE.matcher(generatedByMember);
			assertTrue(m2.find());
			String memberSimpleName = m2.group();
			assertTrue(memberSimpleName.length()>2);
			assertTrue("Source file "+e.getPosition().getFile().getAbsolutePath()+":"+lineNr+" doesn't contain member name "+memberSimpleName, sourceLine.contains(memberSimpleName));
		}
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
				new NamedElementFilter<>(CtField.class,"toBeInserted")).get(0);
		assertSame(Date.class, toBeInserted.getType()
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
	public void testTemplateWithWrongUsedStringParam() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/template/testclasses/constructors/C1.java"),
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/template/testclasses/constructors/TemplateWithFieldsAndMethods_Wrong.java"))
				.build();

		CtClass<?> c1 = factory.Class().get(C1.class);

		new TemplateWithFieldsAndMethods_Wrong(
				"testparam").apply(c1);

		CtMethod<?> m = c1.getMethod("methodToBeInserted");
		assertNotNull(m);
		//contract: printing of code which contains invalid field reference, fails with nice exception
		try {
			m.getBody().getStatement(0).toString();
		} catch (SpoonException e) {
			assertTrue("The error description doesn't contain name of invalid field. There is:\n" + e.getMessage(), e.getMessage().contains("testparam"));
		}
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
		assertEquals("l.size() > 10", ifStmt.getCondition().toString());

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
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher1")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
			assertThat(asList("foo","fbar"), is(klass.filterChildren(matcher).map((CtElement e)->getMethodName(e)).list())) ;
			matcher.forEachMatch(klass, (match) -> {
				assertTrue(checkParameters("foo", match, "_col_", "new java.util.ArrayList<>()")
						|| checkParameters("fbar", match, "_col_", "l")
				);
			});
		}

		{// testing matcher2
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher2")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
			assertThat(asList("bou", "bov"), is(klass.filterChildren(matcher).map((CtElement e)->getMethodName(e)).list())) ;
			matcher.forEachMatch(klass, (match) -> {
				assertTrue(checkParameters("bov", match, "_col_", "new java.util.ArrayList<>()")
						|| checkParameters("bou", match, "_col_", "new java.util.ArrayList<>()"));
			});
		}

		{// testing matcher3
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher3")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(2, matcher.find(klass).size());
			assertThat(asList("foo","fbar"), is(klass.filterChildren(matcher).map((CtElement e)->getMethodName(e)).list())) ;
			matcher.forEachMatch(klass, (match) -> {
				assertTrue(checkParameters("foo", match, "_x_", "new java.util.ArrayList<>().size()")
						||checkParameters("fbar", match, "_x_", "l.size()")
				);
			});
		}

		{// testing matcher4
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher4")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(3, matcher.find(klass).size());
			assertThat(asList("foo","foo2","fbar"), is(klass.filterChildren(matcher).map((CtElement e)->getMethodName(e)).list())) ;
			matcher.forEachMatch(klass, (match) -> {
				assertTrue(
						checkParameters("foo", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10")
						||checkParameters("foo2", match, 
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "11")
						||checkParameters("fbar", match,
								"_x_", "l.size()",
								"_y_", "10")
				);
			});
		}

		{// testing matcher5
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher5")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(6, matcher.find(klass).size());
			assertThat(asList("foo","foo2","fbar","baz","bou","bov"), is(klass.filterChildren(matcher).map((CtElement e)->getMethodName(e)).list())) ;
			matcher.forEachMatch(klass, (match) -> {
				assertTrue(
						checkParameters("foo", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_block_", "{ throw new java.lang.IndexOutOfBoundsException();}")
						||checkParameters("foo2", match, 
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "11",
								"_block_", "{ throw new java.lang.IndexOutOfBoundsException();}")
						||checkParameters("fbar", match,
								"_x_", "l.size()",
								"_y_", "10",
								"_block_", "{ throw new java.lang.IndexOutOfBoundsException();}")
						||checkParameters("baz", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_block_", "{}")
						||checkParameters("bou", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_block_", "{ java.lang.System.out.println();}")
						||checkParameters("bov", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_block_", "{ java.lang.System.out.println();}")
				);
			});
		}

		{// testing matcher6
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher6")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(6, matcher.find(klass).size());
			assertThat(asList("foo","foo2","fbar","baz","bou","bov"), is(klass.filterChildren(matcher).map((CtElement e)->getMethodName(e)).list())) ;
			matcher.forEachMatch(klass, (match) -> {
				assertTrue(
						checkParameters("foo", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_stmt_", "throw new java.lang.IndexOutOfBoundsException()")
						||checkParameters("foo2", match, 
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "11",
								"_stmt_", "throw new java.lang.IndexOutOfBoundsException()")
						||checkParameters("fbar", match,
								"_x_", "l.size()",
								"_y_", "10",
								"_stmt_", "throw new java.lang.IndexOutOfBoundsException()")
						||checkParameters("baz", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_stmt_", "null")
						||checkParameters("bou", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_stmt_", "java.lang.System.out.println()")
						||checkParameters("bov", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_y_", "10",
								"_stmt_", "java.lang.System.out.println()")
				);
			});
		}

		{// testing matcher7
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher7")).get(0)).getBody().getStatement(0);
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);
			assertEquals(1, matcher.find(klass).size());
			assertThat(asList("bos"), is(klass.filterChildren(matcher).map((CtElement e)->getMethodName(e)).list())) ;
			matcher.forEachMatch(klass, (match) -> {
				assertTrue(
						checkParameters("bos", match,
								"_x_", "new java.util.ArrayList<>().size()",
								"_block_", "{ java.lang.System.out.println();}")
				);
			});
		}

		// testing with named elements, at the method level
		{
			CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
			CtClass<?> klass = factory.Class().get(CheckBound.class);
			CtMethod meth = (CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher3")).get(0);

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
			CtMethod meth = (CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher5")).get(0);

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
	
	private boolean checkParameters(String methodName, Match match, String... keyValues) {
		if (methodName.equals(getMethodName(match.getMatchingElement()))) {
			assertEquals("The arguments of keyValues must be in pairs", 0, keyValues.length % 2);
			Map<String, Object> allParams = new HashMap<>(match.getParameters().asMap());
			int count = keyValues.length / 2;
			for (int i = 0; i < count; i++) {
				String key = keyValues[i * 2];
				String expectedValue = keyValues[i * 2 + 1];
				Object realValue = allParams.remove(key);
				assertEquals("parameter " + key, expectedValue, getOptimizedString(realValue));
			}
			assertTrue("Unexpected parameter values: " + allParams, allParams.isEmpty());
			return true;
		}
		return false;
	}

	private String getMethodName(CtElement e) {
		return e.getParent(CtMethod.class).getSimpleName();
	}

	@Test
	public void testExtensionBlock() {
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
		assertEquals("java.lang.System.out.println(((\"enter: \" + className) + \" - \") + methodName)", aTry.getBody().getStatement(1).toString());
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
	public void testTemplateMatcherWithWholePackage() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/template/testclasses/ContextHelper.java");
		spoon.addInputResource("./src/test/java/spoon/test/template/testclasses/BServiceImpl.java");

		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SecurityCheckerTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> templateKlass = factory.Class().get(SecurityCheckerTemplate.class);
		CtMethod templateMethod = (CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher1")).get(0);
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
	public void testTemplateMatcherMatchTwoSnippets() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/template/testclasses/TwoSnippets.java");
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SecurityCheckerTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> templateKlass = factory.Class().get(SecurityCheckerTemplate.class);
		CtMethod templateMethod = (CtMethod) templateKlass.getElements(new NamedElementFilter<>(CtMethod.class,"matcher1")).get(0);
		CtIf templateRoot = (CtIf) templateMethod.getBody().getStatement(0);
		TemplateMatcher matcher = new TemplateMatcher(templateRoot);

		//match byFieldAccessOnVariableusing legacy TemplateMatcher#find method
		List<CtElement> matches = matcher.find(factory.getModel().getRootPackage());

		assertEquals(2, matches.size());

		CtElement match1 = matches.get(0);
		CtElement match2 = matches.get(1);

		assertTrue(match1.equals(match2));
		
		//match using TemplateMatcher#matches method and query filter
		matches = factory.getModel().filterChildren(matcher).list();

		assertEquals(2, matches.size());

		match1 = matches.get(0);
		match2 = matches.get(1);

		assertTrue(match1.equals(match2));
	}
	@Test
	public void testTemplateInvocationSubstitution() {
		//contract: the template engine supports substitution of method names in method calls.
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/InvocationTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> resultKlass = factory.Class().create("Result");
		new InvocationTemplate(factory.Type().OBJECT, "hashCode").apply(resultKlass);
		CtMethod<?> templateMethod = (CtMethod<?>) resultKlass.getElements(new NamedElementFilter<>(CtMethod.class,"invoke")).get(0);
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
		//whitespace seems wrong here
		new SimpleTemplate("HelloWorld").apply(testSimpleTpl);

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
	public void testTemplateArrayAccess() {
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
		//check that both TemplateParameter usages were replaced by appropriate parameter value and that substitution which miss the value is silently removed
		assertEquals(2, m.getBody().getStatements().size());
		assertTrue(m.getBody().getStatements().get(0) instanceof CtBlock);
		assertEquals("int i = 0", ((CtBlock)m.getBody().getStatements().get(0)).getStatement(0).toString());
		assertTrue(m.getBody().getStatements().get(1) instanceof CtBlock);
		assertEquals("java.lang.String s = \"Spoon is cool!\"", ((CtBlock)m.getBody().getStatements().get(1)).getStatement(0).toString());
		//check that both @Parameter usage was replaced by appropriate parameter value
		CtMethod<?> m2 = resultKlass.getMethod("method2");
		assertEquals("java.lang.System.out.println(\"second\")", m2.getBody().getStatement(0).toString());
		//check that substitution by missing value correctly produces empty expression
		assertEquals("java.lang.System.out.println(null)", m2.getBody().getStatement(1).toString());
	}

	@Test
	public void testSubstituteInnerClass() {
		//contract: the inner class is substituted well too and references to target class are substituted well
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/InnerClassTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> result = factory.Class().create("x.Result");
		new InnerClassTemplate().apply(result);
		assertEquals(1, result.getNestedTypes().size());
		CtType<?> innerType = result.getNestedType("Inner");
		assertNotNull(innerType);
		CtField<?> innerField = innerType.getField("innerField");
		assertNotNull(innerField);
		assertSame(innerType, innerField.getDeclaringType());
		CtFieldReference<?> fr = innerType.filterChildren((CtFieldReference<?> e)->true).first();
		//check that reference to declaring type is correctly substituted
		assertEquals("x.Result$Inner",fr.getDeclaringType().getQualifiedName());
	}

	@Test
	public void testStatementTemplateRootSubstitution() {
		//contract: the template engine supports substitution of root element
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubstituteRootTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<?> templateClass = factory.Class().get(SubstituteRootTemplate.class);
		CtBlock<Void> templateParam = (CtBlock) templateClass.getMethod("sampleBlock").getBody();
		
		CtClass<?> resultKlass = factory.Class().create("Result");
		CtStatement result = new SubstituteRootTemplate(templateParam).apply(resultKlass);
		assertEquals("java.lang.String s = \"Spoon is cool!\"", ((CtBlock)result).getStatement(0).toString());
	}

	@Test
	public void testExpressionTemplate() {
		//contract: the template engine supports expression templates
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/AnExpressionTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		CtClass<? extends ExpressionTemplate<?>> templateClass = factory.Class().get(AnExpressionTemplate.class);

		assertEquals("new java.lang.String(exp.S())", ExpressionTemplate.getExpression(templateClass).toString());


		CtClass<?> resultKlass = factory.Class().create("Result");
		CtExpression result = new AnExpressionTemplate(factory.createCodeSnippetExpression("\"Spoon is cool!\"")).apply(resultKlass);
		assertFalse(result.isParentInitialized());
		assertEquals("new java.lang.String(\"Spoon is cool!\")", result.toString());
	}

	@Test
	public void createTypeFromTemplate() {
		//contract: the Substitution API provides a method createTypeFromTemplate
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addTemplateResource(new FileSystemFolder("./src/test/java/spoon/test/template/testclasses/types"));

		launcher.buildModel();
		Factory factory = launcher.getFactory();
		
		Map<String, Object> parameters = new HashMap<>();
		//replace someMethod with genMethod
		parameters.put("someMethod", "genMethod");
		
		//contract: we can generate interface
		final CtType<?> aIfaceModel = launcher.getFactory().Interface().get(AnIfaceModel.class);
		CtType<?> genIface = Substitution.createTypeFromTemplate("generated.GenIface", aIfaceModel, parameters);
		assertNotNull(genIface);
		assertSame(genIface, factory.Type().get("generated.GenIface"));
		CtMethod<?> generatedIfaceMethod = genIface.getMethod("genMethod");
		assertNotNull(generatedIfaceMethod);
		assertNull(genIface.getMethod("someMethod"));

		//add new substitution request - replace AnIfaceModel by GenIface
		parameters.put("AnIfaceModel", genIface.getReference());
		//contract: we can generate class
		final CtType<?> aClassModel = launcher.getFactory().Class().get(AClassModel.class);
		CtType<?> genClass = Substitution.createTypeFromTemplate("generated.GenClass", aClassModel, parameters);
		assertNotNull(genClass);
		assertSame(genClass, factory.Type().get("generated.GenClass"));
		CtMethod<?> generatedClassMethod = genClass.getMethod("genMethod");
		assertNotNull(generatedClassMethod);
		assertNull(genClass.getMethod("someMethod"));
		assertNotSame(generatedIfaceMethod, generatedClassMethod);
		assertTrue(generatedClassMethod.isOverriding(generatedIfaceMethod));

		//contract: we can generate enum
		parameters.put("case1", "GOOD");
		parameters.put("case2", "BETTER");
		final CtType<?> aEnumModel = launcher.getFactory().Type().get(AnEnumModel.class);
		CtEnum<?> genEnum = (CtEnum<?>) Substitution.createTypeFromTemplate("generated.GenEnum", aEnumModel, parameters);
		assertNotNull(genEnum);
		assertSame(genEnum, factory.Type().get("generated.GenEnum"));
		assertEquals(2, genEnum.getEnumValues().size());
		assertEquals("GOOD", genEnum.getEnumValues().get(0).getSimpleName());
		assertEquals("BETTER", genEnum.getEnumValues().get(1).getSimpleName());
	}
	
	@Test
	public void substituteStringLiteral() {
		//contract: the substitution of literals is possible too
		//contract: the template engine supports substitution of root element
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubstituteLiteralTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		{
			//contract: String value is substituted in String literal
			final CtClass<?> result = (CtClass<?>) new SubstituteLiteralTemplate("value1").apply(factory.createClass());
			assertEquals("java.lang.String stringField1 = \"value1\";", result.getField("stringField1").toString());
			assertEquals("java.lang.String stringField2 = \"Substring value1 is substituted too - value1\";", result.getField("stringField2").toString());
			//contract: the parameter of type string replaces only method name
			assertEquals("java.lang.System.out.println(spoon.test.template.testclasses.Params.value1())", result.getMethodsByName("m1").get(0).getBody().getStatement(0).toString());
		}
		{
			//contract: String Literal value is substituted in String literal
			final CtClass<?> result = (CtClass<?>) new SubstituteLiteralTemplate(factory.createLiteral("value2")).apply(factory.createClass());
			assertEquals("java.lang.String stringField1 = \"value2\";", result.getField("stringField1").toString());
			assertEquals("java.lang.String stringField2 = \"Substring value2 is substituted too - value2\";", result.getField("stringField2").toString());
			//contract: the parameter of type String literal replaces whole invocation
			assertEquals("java.lang.System.out.println(\"value2\")", result.getMethodsByName("m1").get(0).getBody().getStatement(0).toString());
		}
		{
			//contract: simple name of type reference is substituted in String literal 
			final CtClass<?> result = (CtClass<?>) new SubstituteLiteralTemplate(factory.Type().createReference("some.ignored.foo.TypeName")).apply(factory.createClass());
			assertEquals("java.lang.String stringField1 = \"TypeName\";", result.getField("stringField1").toString());
			assertEquals("java.lang.String stringField2 = \"Substring TypeName is substituted too - TypeName\";", result.getField("stringField2").toString());
			//contract type reference is substituted in invocation as class access
			assertEquals("java.lang.System.out.println(some.ignored.foo.TypeName.class)", result.getMethodsByName("m1").get(0).getBody().getStatement(0).toString());
		}
		{
			//contract: number literal is substituted in String literal as number converted to string
			final CtClass<?> result = (CtClass<?>) new SubstituteLiteralTemplate(factory.createLiteral(7)).apply(factory.createClass());
			assertEquals("java.lang.String stringField1 = \"7\";", result.getField("stringField1").toString());
			assertEquals("java.lang.String stringField2 = \"Substring 7 is substituted too - 7\";", result.getField("stringField2").toString());
			//contract number literal is substituted in invocation as number literal
			assertEquals("java.lang.System.out.println(7)", result.getMethodsByName("m1").get(0).getBody().getStatement(0).toString());
		}
	}
	@Test
	public void substituteSubString() {
		//contract: the substitution of substrings works on named elements and references too
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/SubStringTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		{
			//contract: String value is substituted in substring of literal, named element and reference
			final CtClass<?> result = (CtClass<?>) new SubStringTemplate("A").apply(factory.createClass());
			assertEquals("java.lang.String m_A = \"A is here more times: A\";", result.getField("m_A").toString());
			//contract: the parameter of type string replaces substring in method name
			CtMethod<?> method1 = result.getMethodsByName("setA").get(0);
			assertEquals("setA", method1.getSimpleName());
			assertEquals("java.lang.String p_A", method1.getParameters().get(0).toString());
			assertEquals("this.m_A = p_A", method1.getBody().getStatement(0).toString());
			assertEquals("setA(\"The A is here too\")", result.getMethodsByName("m").get(0).getBody().getStatements().get(0).toString());
		}
		{
			//contract: Type value name is substituted in substring of literal, named element and reference
			final CtClass<?> result = (CtClass<?>) new SubStringTemplate(factory.Type().OBJECT.getTypeDeclaration()).apply(factory.createClass());
			assertEquals("java.lang.String m_Object = \"Object is here more times: Object\";", result.getField("m_Object").toString());
			//contract: the parameter of type string replaces substring in method name
			CtMethod<?> method1 = result.getMethodsByName("setObject").get(0);
			assertEquals("setObject", method1.getSimpleName());
			assertEquals("java.lang.String p_Object", method1.getParameters().get(0).toString());
			assertEquals("this.m_Object = p_Object", method1.getBody().getStatement(0).toString());
			assertEquals("setObject(\"The Object is here too\")", result.getMethodsByName("m").get(0).getBody().getStatements().get(0).toString());
		}
		{
			//contract: Type reference value name is substituted in substring of literal, named element and reference
			final CtClass<?> result = (CtClass<?>) new SubStringTemplate(factory.Type().OBJECT).apply(factory.createClass());
			assertEquals("java.lang.String m_Object = \"Object is here more times: Object\";", result.getField("m_Object").toString());
			//contract: the parameter of type string replaces substring in method name
			CtMethod<?> method1 = result.getMethodsByName("setObject").get(0);
			assertEquals("setObject", method1.getSimpleName());
			assertEquals("java.lang.String p_Object", method1.getParameters().get(0).toString());
			assertEquals("this.m_Object = p_Object", method1.getBody().getStatement(0).toString());
			assertEquals("setObject(\"The Object is here too\")", result.getMethodsByName("m").get(0).getBody().getStatements().get(0).toString());
		}
		{
			//contract: String literal value name is substituted in substring of literal, named element and reference
			final CtClass<?> result = (CtClass<?>) new SubStringTemplate(factory.createLiteral("Xxx")).apply(factory.createClass());
			assertEquals("java.lang.String m_Xxx = \"Xxx is here more times: Xxx\";", result.getField("m_Xxx").toString());
			//contract: the parameter of type string replaces substring in method name
			CtMethod<?> method1 = result.getMethodsByName("setXxx").get(0);
			assertEquals("setXxx", method1.getSimpleName());
			assertEquals("java.lang.String p_Xxx", method1.getParameters().get(0).toString());
			assertEquals("this.m_Xxx = p_Xxx", method1.getBody().getStatement(0).toString());
			assertEquals("setXxx(\"The Xxx is here too\")", result.getMethodsByName("m").get(0).getBody().getStatements().get(0).toString());
		}
		{
			//contract: The elements which cannot be converted to String should throw exception
			SubStringTemplate template = new SubStringTemplate(factory.createSwitch());
			try {
				template.apply(factory.createClass());
				fail();
			} catch(SpoonException e) {
				//OK
			}
		}
	}

	@Test
	public void testObjectIsNotParamTemplate() {
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/ObjectIsNotParamTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();
		//contract: String value is substituted in substring of literal, named element and reference
		final CtClass<?> result = (CtClass<?>) new ObjectIsNotParamTemplate().apply(factory.createClass());
		assertEquals(0, result.getMethodsByName("methXXXd").size());
		assertEquals(1, result.getMethodsByName("method").size());
	}

	@Test
	public void testFieldAccessNameSubstitution() {
		//contract: the substitution of name of whole field is possible
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/FieldAccessTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		{
			//contract: String value is substituted in String literal
			final CtClass<?> result = (CtClass<?>) new FieldAccessTemplate("value").apply(factory.Class().create("x.X"));
			assertEquals("int value;", result.getField("value").toString());
			
			assertEquals("value = 7", result.getMethodsByName("m").get(0).getBody().getStatement(0).toString());
		}
	}

	@Test
	public void testFieldAccessNameSubstitutionInInnerClass() {
		//contract: the substitution of name of whole field is possible in inner class too
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/FieldAccessOfInnerClassTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		{
			//contract: String value is substituted in String literal
			final CtClass<?> result = (CtClass<?>) new FieldAccessOfInnerClassTemplate("value").apply(factory.Class().create("x.X"));
			final CtClass<?> innerClass = result.getNestedType("Inner");
			assertEquals("int value;", innerClass.getField("value").toString());
			
			assertEquals("value = 7", innerClass.getMethodsByName("m").get(0).getBody().getStatement(0).toString());
		}
	}

	@Test
	public void testAnotherFieldAccessNameSubstitution() {
		//contract: the substitution of name of whole field is possible
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/AnotherFieldAccessTemplate.java"));

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		{
			//contract: String value is substituted in String literal
			final CtClass<?> result = (CtClass<?>) new AnotherFieldAccessTemplate().apply(factory.Class().create("x.X"));
			assertEquals("int x;", result.getField("x").toString());
			assertEquals("int m_x;", result.getField("m_x").toString());

			assertEquals("java.lang.System.out.println(x + m_x)", result.getAnonymousExecutables().get(0).getBody().getStatement(0).toString());
		}
	}

	@Test
	public void substituteTypeAccessReference() {
		//contract: the substitution of CtTypeAccess expression ignores actual type arguments if it have to
		Launcher spoon = new Launcher();
		spoon.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/TypeReferenceClassAccessTemplate.java"));
		String outputDir = "./target/spooned/test/template/testclasses";
		spoon.setSourceOutputDirectory(outputDir);

		spoon.buildModel();
		Factory factory = spoon.getFactory();

		//contract: String value is substituted in substring of literal, named element and reference
		CtTypeReference<?> typeRef = factory.Type().createReference("spoon.test.template.TypeReferenceClassAccess$Example");
		typeRef.addActualTypeArgument(factory.Type().DATE);
		
		final CtClass<?> result = (CtClass<?>) new TypeReferenceClassAccessTemplate(typeRef).apply(factory.Class().create("spoon.test.template.TypeReferenceClassAccess"));
		spoon.prettyprint();
		ModelUtils.canBeBuilt(outputDir, 8);
		CtMethod<?> method = result.getMethodsByName("someMethod").get(0);
		assertEquals("spoon.test.template.TypeReferenceClassAccess.Example<java.util.Date>", method.getType().toString());
		assertEquals("spoon.test.template.TypeReferenceClassAccess.Example<java.util.Date>", method.getParameters().get(0).getType().toString());
		assertEquals("o = spoon.test.template.TypeReferenceClassAccess.Example.out", method.getBody().getStatement(0).toString());
		assertEquals("spoon.test.template.TypeReferenceClassAccess.Example<java.util.Date> ret = new spoon.test.template.TypeReferenceClassAccess.Example<java.util.Date>()", method.getBody().getStatement(1).toString());
		assertEquals("o = spoon.test.template.TypeReferenceClassAccess.Example.currentTimeMillis()", method.getBody().getStatement(2).toString());
		assertEquals("o = spoon.test.template.TypeReferenceClassAccess.Example.class", method.getBody().getStatement(3).toString());
		assertEquals("o = o instanceof spoon.test.template.TypeReferenceClassAccess.Example<?>", method.getBody().getStatement(4).toString());
		assertEquals("java.util.function.Supplier<java.lang.Long> p = spoon.test.template.TypeReferenceClassAccess.Example::currentTimeMillis", method.getBody().getStatement(5).toString());
	}

	@Test
	public void testMethodCast() throws Exception {
		//contract: matcher should match methods with cast too
		//https://github.com/INRIA/spoon/issues/3026
		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources("./src/test/java/spoon/test/template/testclasses/Flow.java"),
				SpoonResourceHelper.resources("./src/test/java/spoon/test/template/testclasses/FlowMatcher.java"))
				.build();

		CtClass<?> templateKlass = factory.Class().get(FlowMatcher.class);
		CtClass<?> klass = factory.Class().get(Flow.class);

		CtMethod<?> method = (CtMethod<?>) templateKlass.getElements(new NamedElementFilter(CtMethod.class, "subFlowMatcher")).get(0);
		CtElement templateRoot = method.getBody().getStatement(0);
		TemplateMatcher myMatcher = new TemplateMatcher(templateRoot);
		assertEquals(2, myMatcher.find(klass).size());
	}
}
