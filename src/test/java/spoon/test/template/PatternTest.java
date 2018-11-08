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
import spoon.pattern.ConflictResolutionMode;
import spoon.pattern.Match;
import spoon.pattern.PatternParameterConfigurator;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.pattern.Quantifier;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.util.ImmutableMap;
import spoon.support.util.ImmutableMapImpl;
import spoon.test.template.testclasses.LoggerModel;
import spoon.test.template.testclasses.ToBeMatched;
import spoon.test.template.testclasses.logger.Logger;
import spoon.test.template.testclasses.match.GenerateIfElse;
import spoon.test.template.testclasses.match.MatchForEach;
import spoon.test.template.testclasses.match.MatchForEach2;
import spoon.test.template.testclasses.match.MatchIfElse;
import spoon.test.template.testclasses.match.MatchMap;
import spoon.test.template.testclasses.match.MatchModifiers;
import spoon.test.template.testclasses.match.MatchMultiple;
import spoon.test.template.testclasses.match.MatchMultiple2;
import spoon.test.template.testclasses.match.MatchMultiple3;
import spoon.test.template.testclasses.match.MatchThrowables;
import spoon.test.template.testclasses.match.MatchWithParameterCondition;
import spoon.test.template.testclasses.match.MatchWithParameterType;
import spoon.test.template.testclasses.replace.DPPSample1;
import spoon.test.template.testclasses.replace.OldPattern;
import spoon.test.template.testclasses.types.AClassWithMethodsAndRefs;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


// main test of Spoon's patterns
public class PatternTest {

	@Test
	public void testMatchForeach() throws Exception {
		//contract: a foreach template can also match inlined lists of statements
		CtType<?> ctClass = ModelUtils.buildClass(MatchForEach.class);

		CtType<?> type = ctClass.getFactory().Type().get(MatchForEach.class);

		// create one pattern from matcher1, with one parameter "values"
//		public void matcher1(List<String> values) {
//			for (String value : values) {
//				System.out.println(value);
//			}
//		}
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						pb.parameter("values").byVariable("values").setContainerKind(ContainerKind.LIST).matchInlinedStatements();
					})
				.build();

		List<Match> matches = pattern.getMatches(ctClass);

		assertEquals(2, matches.size());
		{
			Match match = matches.get(0);
			assertEquals(Arrays.asList("java.lang.System.out.println(value)"), toListOfStrings(match.getMatchingElements()));
			//FIX IT
//			assertEquals(Arrays.asList(""), listToListOfStrings((List) match.getParameters().getValue("values")));
		}
		{
			Match match = matches.get(1);
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(\"a\")",
					"java.lang.System.out.println(\"Xxxx\")",
					"java.lang.System.out.println(((java.lang.String) (null)))",
					"java.lang.System.out.println(java.lang.Long.class.toString())"), toListOfStrings(match.getMatchingElements()));
			assertEquals(Arrays.asList(
					"\"a\"",
					"\"Xxxx\"",
					"((java.lang.String) (null))",
					"java.lang.Long.class.toString()"), toListOfStrings((List) match.getParameters().getValue("values")));
		}
	}

	@Test
	public void testMatchForeachWithOuterSubstitution() throws Exception {
		//contract: inline foreach templates can also match outer parameters
		CtType<?> ctClass = ModelUtils.buildClass(MatchForEach2.class);

		CtType<?> type = ctClass.getFactory().Type().get(MatchForEach2.class);

		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
				.configurePatternParameters(pb -> {
					pb.parameter("values").byVariable("values").setContainerKind(ContainerKind.LIST).matchInlinedStatements();
					// the variable "var" of the template is a parameter
					pb.parameter("varName").byString("var");
				})
				.build();

		List<Match> matches = pattern.getMatches(ctClass);

		assertEquals(3, matches.size());
		{
			Match match = matches.get(0);
			assertEquals(Arrays.asList("int var = 0"), toListOfStrings(match.getMatchingElements()));
			//FIX IT
//			assertEquals(Arrays.asList(""), listToListOfStrings((List) match.getParameters().getValue("values")));
		}
		{
			Match match = matches.get(1);
			assertEquals(Arrays.asList(
					"int cc = 0",
					"java.lang.System.out.println(\"Xxxx\")",
					"cc++",
					"java.lang.System.out.println(((java.lang.String) (null)))",
					"cc++"), toListOfStrings(match.getMatchingElements()));

			// correctly matching the outer parameter
			assertEquals("cc", match.getParameters().getValue("varName"));
		}
		{
			Match match = matches.get(2);
			assertEquals(Arrays.asList(
					"int dd = 0",
					"java.lang.System.out.println(java.lang.Long.class.toString())",
					"dd++"), toListOfStrings(match.getMatchingElements()));

			// correctly matching the outer parameter
			assertEquals("dd", match.getParameters().getValue("varName"));
		}
	}

	@Test
	public void testMatchIfElse() throws Exception {
		//contract: if statements can be inlined
		// meaning, either the if branch or the else branch can be matched independently

		// in this example the main if statement starting with "if (option) {"
		// is inlined, and any of the branch can be matched
		CtType<?> ctClass = ModelUtils.buildClass(MatchIfElse.class);

		CtType<?> type = ctClass.getFactory().Type().get(MatchIfElse.class);
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
				.configurePatternParameters(pb -> {
					pb.parameter("option").byVariable("option");
					pb.parameter("value").byFilter(new TypeFilter(CtLiteral.class));
				})
				//we have to configure inline statements after all expressions
				//of combined if statement are marked as pattern parameters
				.configureInlineStatements(lsb -> lsb.inlineIfOrForeachReferringTo("option"))
				.build();

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		// we only match the calls having a string literal as parameter or a float
		assertEquals(5, matches.size());
		{
			Match match = matches.get(0);
			assertEquals(Arrays.asList("java.lang.System.out.println(\"a\")"), toListOfStrings(match.getMatchingElements()));
			assertEquals(true, match.getParameters().getValue("option"));
			assertEquals("\"a\"", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(1);
			assertEquals(Arrays.asList("java.lang.System.out.println(\"Xxxx\")"), toListOfStrings(match.getMatchingElements()));
			assertEquals(true, match.getParameters().getValue("option"));
			assertEquals("\"Xxxx\"", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(2);
			assertEquals(Arrays.asList("java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings(match.getMatchingElements()));
			assertEquals(true, match.getParameters().getValue("option"));
			assertEquals("((java.lang.String) (null))", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(4);
			assertEquals(Arrays.asList("java.lang.System.out.println(3.14)"), toListOfStrings(match.getMatchingElements()));
			assertEquals(false, match.getParameters().getValue("option"));
			assertEquals("3.14", match.getParameters().getValue("value").toString());
		}
	}

	@Test
	public void testGenerateIfElse() throws Exception {
		//contract: it is possible to generate code using optional targets 
		CtType<?> type = ModelUtils.buildClass(GenerateIfElse.class);
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("generator").getPatternElements())
				.configurePatternParameters(pb -> {
					pb.parameter("option").byVariable("option");
				})
				//we have to configure inline statements after all expressions
				//of combined if statement are marked as pattern parameters
				.configureInlineStatements(lsb -> lsb.inlineIfOrForeachReferringTo("option"))
				//contract: it is possible to configure pattern parameters after their parent is inlined
				.configurePatternParameters(pb -> {
					pb.parameter("value").byFilter(new TypeFilter(CtLiteral.class));
				})
				.build();

		{
			List<CtStatement> statements = pattern.generator().generate(CtStatement.class, 
					new ImmutableMapImpl().putValue("option", true).putValue("value", "spoon"));
			assertEquals(1, statements.size());
			assertEquals("java.lang.System.out.print(\"spoon\")", statements.get(0).toString());
		}
		{
			List<CtStatement> statements = pattern.generator().generate(CtStatement.class, 
					new ImmutableMapImpl().putValue("option", false).putValue("value", 2.1));
			assertEquals(1, statements.size());
			assertEquals("java.lang.System.out.println(2.1)", statements.get(0).toString());
		}
	}

	@Test
	public void testGenerateMultiValues() throws Exception {
		// contract: the pattern parameter (in this case 'statements')
		// can have type List and can be replaced by list of elements
		//(in this case by list of statements)

		// here, in particular, we test method "substitute"


		// setup of the test
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Factory factory = ctClass.getFactory();

		Pattern pattern = MatchMultiple.createPattern(null, null, null);
		ImmutableMap params = new ImmutableMapImpl();

		// created in "MatchMultiple.createPattern", matching a literal "something"
		// so "something" is replaced by "does it work?"
		params = params.putValue("printedValue", "does it work?");

		List<CtStatement> statementsToBeAdded = Arrays.asList(new CtStatement[] {factory.createCodeSnippetStatement("int foo = 0"), factory.createCodeSnippetStatement("foo++")});
		//statementsToBeAdded = ctClass.getMethodsByName("testMatch1").get(0).getBody().getStatements().subList(0, 3); // we don't use this in order not to mix the matching and the transformation

		// created in "MatchMultiple.createPattern", matching a method "statements"
		params = params.putValue("statements", statementsToBeAdded);

		List<CtStatement> generated = pattern.generator().generate(CtStatement.class, params);
		assertEquals(Arrays.asList(
				//these statements comes from `statements` parameter value
				"int foo = 0",
				"foo++",
				//this statement comes from pattern model, just the string literal comes from parameter `printedValue`
				"java.lang.System.out.println(\"does it work?\")"), generated.stream().map(Object::toString).collect(Collectors.toList()));
	}

	@Test
	public void testMatchGreedyMultiValueUnlimited() throws Exception {
		//contract: there is a way to match absolutely any kind of statement, in an unlimited list

		//explanation: multivalue parameter (setContainerKind(ContainerKind.LIST) can match multiple nodes in a row.
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);

		Pattern pattern = MatchMultiple.createPattern(null, null, null);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		assertEquals(1, matches.size());
		Match match = matches.get(0);
		//check that absolutely all statements from "testMatch1" are matched
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"Xxxx\")",
				"java.lang.System.out.println(((java.lang.String) (null)))",
				"java.lang.System.out.println(\"last one\")"), toListOfStrings(match.getMatchingElements()));

		//check all statements excluding last are stored as value of "statements" parameter
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"Xxxx\")",
				"java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings((List) match.getParameters().getValue("statements")));

		//last statement is matched by last template, which saves printed value
		assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
		assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
	}

	@Test
	public void testMatchGreedyMultiValueMaxCountLimit() throws Exception {
		//contract: it is possible to stop matching after a specific number of times
		// This is done with method parameterBuilder.setMaxOccurrence(maxCount)

		// explanation: greedy matching eats everything until max count = 3
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Pattern pattern = MatchMultiple.createPattern(null, null, 3);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		assertEquals(2, matches.size());
		{
			Match match = matches.get(0);
			//check 3 + 1 statements are matched
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)",
					"java.lang.System.out.println(\"Xxxx\")"
			), toListOfStrings(match.getMatchingElements()));

			//check 3 statements are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)"), toListOfStrings((List) match.getParameters().getValue("statements")));
			//4th statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(1);
			//check remaining next 2 statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))",
					"java.lang.System.out.println(\"last one\")"), toListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
		}
	}

	@Test
	public void testMatchReluctantMultivalue() throws Exception {
		//contract: reluctant matching (Quantifier.RELUCTANT) matches only the minimal amount of time
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);

		Pattern pattern = MatchMultiple.createPattern(Quantifier.RELUCTANT, null, null);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		assertEquals(3, matches.size());
		{
			Match match = matches.get(0);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)",	//this is println(int), but last temple matches println(String) - it is question if it is wanted or not ...
					"java.lang.System.out.println(\"Xxxx\")"), toListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)"), toListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(1);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(), toListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("((java.lang.String) (null))", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(2);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(\"last one\")"), toListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(), toListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
		}
	}

	@Test
	public void testMatchReluctantMultivalueMinCount1() throws Exception {
		//contract: one can do reluctant matches with a minCount of 1 node
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);

		Pattern pattern = MatchMultiple.createPattern(Quantifier.RELUCTANT, 1, null);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		assertEquals(2, matches.size());
		{
			Match match = matches.get(0);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)",	//this is println(int), but last temple matches println(String) - it is question if it is wanted or not ...
					"java.lang.System.out.println(\"Xxxx\")"), toListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)"), toListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(1);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))",
					"java.lang.System.out.println(\"last one\")"), toListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
		}
	}

	@Test
	public void testMatchReluctantMultivalueExactly2() throws Exception {
		//contract: one can do reluctant matches min 2 nodes and max 2 nodes
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);

		Pattern pattern = MatchMultiple.createPattern(Quantifier.RELUCTANT, 2, 2);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		assertEquals(1, matches.size());
		{
			Match match = matches.get(0);
			//check only 2 statements are matched + next one
			assertEquals(Arrays.asList(
					"i++",
					"java.lang.System.out.println(i)",	//this is println(int), but last temple matches println(String) - it is question if it is wanted or not ...
					"java.lang.System.out.println(\"Xxxx\")"), toListOfStrings(match.getMatchingElements()));

			//check 2 statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"i++",
					"java.lang.System.out.println(i)"), toListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
	}

	@Test
	public void testMatchPossesiveMultiValueUnlimited() throws Exception {
		//contract: possessive matching eats everything
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Pattern pattern = MatchMultiple.createPattern(Quantifier.POSSESSIVE, null, null);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());

		// template:
//		public void matcher1() {
//			statements();
//			System.out.println("something");
//		}

		// Quantifier.POSSESSIVE matches all elements by statements() and there remains no element for mandatory single match of System.out.println("something");,
		// consequently, no match of the full template
		assertEquals(0, matches.size());
	}

	@Test
	public void testMatchPossesiveMultiValueMaxCount4() throws Exception {
		//contract: maxCount (#setMaxOccurrence) can be used to stop Quantifier.POSSESSIVE for matching too much
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);

		// note that if we set maxCount = 3, it fails because there is one dangling statement before System.out.println("something")
		Pattern pattern = MatchMultiple.createPattern(Quantifier.POSSESSIVE, null, 4);

		List<Match> matches = pattern.getMatches(ctClass);

		// only testMatch1 is matched
		// method matcher1, from which the template has been built, is not matched
		// because the possessive quantifier eats its two statements, here remains nothing for the second template statement, which cannot match then.
		assertEquals(1, matches.size());
		Match match = matches.get(0);

		//check 4 statements are matched + last template
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"Xxxx\")",
				"java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings(match.getMatchingElements()));

		//check 4 statements excluding last are stored as value of "statements" parameter
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"Xxxx\")"), toListOfStrings((List) match.getParameters().getValue("statements")));
		//last statement is matched by last template, which saves printed value
		assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
		assertEquals("((java.lang.String) (null))", match.getParameters().getValue("printedValue").toString());
	}

	@Test
	public void testMatchPossesiveMultiValueMinCount() throws Exception {
		//contract: there is a correct interplay between for possessive matching with min count limit and with GREEDY matching

		// pattern
//		public void matcher1() {
//			statements1.S(); // Quantifier.GREEDY
//			statements2.S(); // Quantifier.POSSESSIVE with setMinOccurrence and setMaxOccurrence set
//			System.out.println("something"); // "something" -> anything
//		}

		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple3.class);

		// trying with all values of "count"
		for (int count = 0; count < 6; count++) {
			final int countFinal = count;
			CtType<?> type = ctClass.getFactory().Type().get(MatchMultiple3.class);
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
					.configurePatternParameters()
					.configurePatternParameters(pb -> {
						pb.parameter("statements1").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.GREEDY);
						pb.parameter("statements2").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.POSSESSIVE).setMinOccurrence(countFinal).setMaxOccurrence(countFinal);
						pb.parameter("printedValue").byFilter((CtLiteral<?> literal) -> "something".equals(literal.getValue()));
					})
					.build();

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());

			// Quantifier.POSSESSIVE matches exactly the right number of times
			assertEquals("count="+countFinal, countFinal, getCollectionSize(matches.get(0).getParameters().getValue("statements2")));

			// Quantifier.GREEDY gets the rest
			assertEquals("count="+countFinal, 5-countFinal, getCollectionSize(matches.get(0).getParameters().getValue("statements1")));
		}
	}

	@Test
	public void testMatchPossesiveMultiValueMinCount2() throws Exception {
		//contract: there is a correct interplay between for possessive matching with min count limit and with GREEDY matching

		// pattern:
//		public void matcher1(List<String> something) {
//			statements1.S(); // Quantifier.GREEDY
//			statements2.S(); // Quantifier.POSSESSIVE with setMinOccurrence and setMaxOccurrence set
//			for (String v : something) {
//				System.out.println(v); // can be inlined
//			}
//		}
//

		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple2.class);

		// trying with all values of "count"
		for (int count = 0; count < 5; count++) {
			final int countFinal = count;
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
			.configurePatternParameters(pb -> {
				pb.byTemplateParameter();
				pb.parameter("statements1").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.GREEDY);
				pb.parameter("statements2").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.POSSESSIVE).setMinOccurrence(countFinal).setMaxOccurrence(countFinal);
				pb.parameter("inlinedSysOut").byVariable("something").setMatchingStrategy(Quantifier.POSSESSIVE).setContainerKind(ContainerKind.LIST).setMinOccurrence(2).matchInlinedStatements();
			})
			.build();

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
			//the last template has nothing to match -> no match
			assertEquals("count="+countFinal, 1, matches.size());
			assertEquals("count="+countFinal, 4-countFinal, getCollectionSize(matches.get(0).getParameters().getValue("statements1")));
			assertEquals("count="+countFinal, countFinal, getCollectionSize(matches.get(0).getParameters().getValue("statements2")));
			assertEquals("count="+countFinal, 2, getCollectionSize(matches.get(0).getParameters().getValue("inlinedSysOut")));
		}

		for (int count = 5; count < 7; count++) {
			final int countFinal = count;
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
					.configurePatternParameters().build();

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
			//the possessive matcher eats too much. There is no target element for last `printedValue` variable
			assertEquals("count="+countFinal, 0, matches.size());
		}
	}

	@Test
	public void testMatchGreedyMultiValueMinCount2() throws Exception {
		//contract: check possessive matching with min count limit and GREEDY back off
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple2.class);
		for (int i = 0; i < 7; i++) {
			final int count = i;
			CtType<?> type = ctClass.getFactory().Type().get(MatchMultiple2.class);
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						pb.byTemplateParameter();
						pb.parameter("statements1").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.RELUCTANT);
						pb.parameter("statements2").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.GREEDY).setMaxOccurrence(count);
						pb.parameter("printedValue").byVariable("something").matchInlinedStatements();
						pb.parameter("printedValue").setMatchingStrategy(Quantifier.GREEDY).setContainerKind(ContainerKind.LIST).setMinOccurrence(2);
					})
					.build();
			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());


			if (count < 7) {
				//the last template has nothing to match -> no match
				assertEquals("count=" + count, 1, matches.size());
				assertEquals("count=" + count, Math.max(0, 3 - count), getCollectionSize(matches.get(0).getParameters().getValue("statements1")));
				assertEquals("count=" + count, count - Math.max(0, count - 4), getCollectionSize(matches.get(0).getParameters().getValue("statements2")));
				assertEquals("count=" + count, Math.max(2, 3 - Math.max(0, count - 3)), getCollectionSize(matches.get(0).getParameters().getValue("printedValue")));
			} else {
				//the possessive matcher eats too much. There is no target element for last `printedValue` variable
				assertEquals("count=" + count, 0, matches.size());
			}
		}
	}

	/** returns the size of the list of 0 is list is null */
	private int getCollectionSize(Object list) {
		if (list instanceof Collection) {
			return ((Collection) list).size();
		}
		if (list == null) {
			return 0;
		}
		fail("Unexpected object of type " + list.getClass());
		return -1;
	}

	@Test
	public void testMatchParameterValue() throws Exception {
		//contract: if matching on the pattern itself, the matched parameter value is the original AST node from the pattern
		// see last assertSame of this test
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterType.class);

		// pattern is System.out.println(value);

		// pattern: a call to System.out.println with anything as parameter
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
				.configurePatternParameters(pb -> {
					// anything in place of the variable reference value can be matched
					pb.parameter("value").byVariable("value");
				})
				.build();

		List<Match> matches = pattern.getMatches(ctClass);

		// specifying Match#toString
		assertEquals("{\n" +
				"value=value\n" +
				"}\n" +
				"----------\n" +
				"1) java.lang.System.out.println(value)", matches.get(0).toString());

		// we match in the whole class, which means the original matcher statements and the ones from testMatcher1

		assertEquals(5, matches.size());
		{
			Match match = matches.get(0);
			assertEquals(Arrays.asList("java.lang.System.out.println(value)"), toListOfStrings(match.getMatchingElements()));
			Object value = match.getParameters().getValue("value");
			assertTrue(value instanceof CtVariableRead);
			assertEquals("value", value.toString());
			//contract: the value is reference to found node (not a clone)
			assertTrue(((CtElement)value).isParentInitialized());
			assertSame(CtRole.ARGUMENT, ((CtElement)value).getRoleInParent());
		}
	}

	@Test
	public void testMatchParameterValueType() throws Exception {
		// contract: pattern parameters can be restricted to only certain types
		// in this test case, we only match CtLiteral
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterType.class);
		{
			// now we match only the ones with a literal as parameter
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						pb.parameter("value").byVariable("value");
						pb.setValueType(CtLiteral.class);
					})
					.build();

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

			// there are 3 System.out.println with a literal as parameter
			assertEquals(3, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"a\")"), toListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"a\"", match.getParameters().getValue("value").toString());
			}
			{
				Match match = matches.get(1);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"Xxxx\")"), toListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"Xxxx\"", match.getParameters().getValue("value").toString());
			}
			{
				// in Java, null is considered as a literal
				Match match = matches.get(2);
				assertEquals(Arrays.asList("java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("((java.lang.String) (null))", match.getParameters().getValue("value").toString());
			}
		}
		{
			// now we match a System.out.println with an invocation as paramter
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						pb.parameter("value").byVariable("value");
						pb.setValueType(CtInvocation.class);
					})
					.build();

			List<Match> matches = pattern.getMatches(ctClass);

			assertEquals(1, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(Arrays.asList("java.lang.System.out.println(java.lang.Long.class.toString())"), toListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtInvocation);
				assertEquals("java.lang.Long.class.toString()", match.getParameters().getValue("value").toString());
			}

		}
	}

	@Test
	public void testMatchParameterCondition() throws Exception {
		//contract: pattern parameters support conditions passed as lambda
		//if the value isn't matching then node is not matched
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterCondition.class);
		{
			// matching a System.out.println with a literal
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						pb.parameter("value").byVariable("value");
						pb.byCondition(null, (Object value) -> value instanceof CtLiteral);
					})
					.build();

			List<Match> matches = pattern.getMatches(ctClass);

			assertEquals(3, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"a\")"), toListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"a\"", match.getParameters().getValue("value").toString());
			}
			{
				Match match = matches.get(1);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"Xxxx\")"), toListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"Xxxx\"", match.getParameters().getValue("value").toString());
			}
			{
				Match match = matches.get(2);
				assertEquals(Arrays.asList("java.lang.System.out.println(((java.lang.String) (null)))"), toListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("((java.lang.String) (null))", match.getParameters().getValue("value").toString());
			}
		}
	}

	@Test
	public void testMatchOfAttribute() throws Exception {
		// contract: it is possible to match nodes based on their roles
		// tested methods: ParameterBuilder#byRole and ParameterBuilder#byString
		CtType<?> ctClass = ModelUtils.buildClass(MatchModifiers.class);
		{
			//match all methods with arbitrary name, modifiers, parameters, but with empty body and return type void
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setTypeMember("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						pb.parameter("modifiers").byRole(CtRole.MODIFIER, new TypeFilter(CtMethod.class));
						pb.parameter("methodName").byString("matcher1");
						pb.parameter("parameters").byRole(CtRole.PARAMETER, new TypeFilter(CtMethod.class));
					})
					.build();
			List<Match> matches = pattern.getMatches(ctClass);

			// three methods are matched
			assertEquals(3, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("matcher1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(3, match.getParametersMap().size());
				assertEquals("matcher1", match.getParametersMap().get("methodName"));
				assertEquals(new HashSet<>(Arrays.asList(ModifierKind.PUBLIC)), match.getParametersMap().get("modifiers"));
				assertEquals(Arrays.asList(), match.getParametersMap().get("parameters"));
			}
			{
				Match match = matches.get(1);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("publicStaticMethod", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(3, match.getParametersMap().size());
				assertEquals("publicStaticMethod", match.getParametersMap().get("methodName"));
				assertEquals(new HashSet<>(Arrays.asList(ModifierKind.PUBLIC, ModifierKind.STATIC)), match.getParametersMap().get("modifiers"));
				assertEquals(Arrays.asList(), match.getParametersMap().get("parameters"));
			}
			{
				Match match = matches.get(2);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("packageProtectedMethodWithParam", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(3, match.getParametersMap().size());
				assertEquals("packageProtectedMethodWithParam", match.getParametersMap().get("methodName"));
				assertEquals(new HashSet<>(), match.getParametersMap().get("modifiers"));
				assertEquals(2, ((List) match.getParametersMap().get("parameters")).size());
			}
		}
		{
			//match all methods with arbitrary name, modifiers, parameters and body, but with return type void
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setTypeMember("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						pb.parameter("modifiers").byRole(CtRole.MODIFIER, new TypeFilter(CtMethod.class));
						pb.parameter("methodName").byString("matcher1");
						pb.parameter("parameters").byRole(CtRole.PARAMETER, new TypeFilter(CtMethod.class));
						pb.parameter("statements").byRole(CtRole.STATEMENT, new TypeFilter(CtBlock.class));
					})
					.build();
			List<Match> matches = pattern.getMatches(ctClass);
			// same as before + one more method: withBody
			assertEquals(4, matches.size());
			{
				Match match = matches.get(3);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("withBody", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(4, match.getParametersMap().size());
				assertEquals("withBody", match.getParametersMap().get("methodName"));
				assertEquals(new HashSet<>(Arrays.asList(ModifierKind.PRIVATE)), match.getParametersMap().get("modifiers"));
				assertEquals(0, ((List) match.getParametersMap().get("parameters")).size());
				assertEquals(2, ((List) match.getParametersMap().get("statements")).size());
				assertEquals("this.getClass()", ((List) match.getParametersMap().get("statements")).get(0).toString());
				assertEquals("java.lang.System.out.println()", ((List) match.getParametersMap().get("statements")).get(1).toString());
			}
		}
	}

	@Test
	public void testMatchOfMapAttribute() throws Exception {
		//contract: there is support for matching annotations with different annotation values
		CtType<?> matchMapClass = ModelUtils.buildClass(MatchMap.class);
		{
			CtType<?> type = matchMapClass.getFactory().Type().get(MatchMap.class);
			// create a pattern from method matcher1
			// match all methods with arbitrary name, and annotation @Check, parameters, but with empty body and return type void
//			@Check()
//			void matcher1() {
//			}
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						//match any value of @Check annotation to parameter `testAnnotations`
						pb.parameter("__pattern_param_annot").byRole(CtRole.VALUE, new TypeFilter(CtAnnotation.class)).setContainerKind(ContainerKind.MAP);
						//match any method name
						pb.parameter("__pattern_param_method_name").byString("matcher1");
					})
					.build();

			// we apply the pattern on MatchMap
			List<Match> matches = pattern.getMatches(matchMapClass);
			assertEquals(3, matches.size());
			{
				// first match: matcher1 itself (normal)
				Match match = matches.get(0);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("matcher1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("matcher1", match.getParametersMap().get("__pattern_param_method_name"));
				Map<String, Object> values = getMap(match, "__pattern_param_annot");
				assertEquals(0, values.size());
			}
			{
				// first match: m1 itself (normal)
//				@Check(value = "xyz")
//				void m1() {
//				}
				Match match = matches.get(1);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("m1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("m1", match.getParametersMap().get("__pattern_param_method_name"));
				Map<String, Object> values = getMap(match, "__pattern_param_annot");
				assertEquals(1, values.size());
				assertEquals("\"xyz\"", values.get("value").toString());
			}
			{
				// second match: m2, it also contains a timeout value
				Match match = matches.get(2);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("m2", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("m2", match.getParametersMap().get("__pattern_param_method_name"));
				Map<String, Object> values = getMap(match, "__pattern_param_annot");
				assertEquals(2, values.size());
				assertEquals("\"abc\"", values.get("value").toString());
				assertEquals("123", values.get("timeout").toString());
			}
		}
	}

	@Test
	public void testMatchOfMapAttributeAndOtherAnnotations() throws Exception {
		//contract: match a pattern with an "open" annotation (different annotations can be matched)
		// same test but with one more pattern parameter: allAnnotations
		CtType<?> ctClass = ModelUtils.buildClass(MatchMap.class);
		{
			CtType<?> type = ctClass.getFactory().Type().get(MatchMap.class);
			// create a pattern from method matcher1
			// match all methods with arbitrary name, with any annotation set, Test modifiers, parameters, but with empty body and return type void
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("matcher1").getPatternElements())
					.configurePatternParameters(pb -> {
						//match any value of @Check annotation to parameter `testAnnotations`
						//match any method name
						pb.parameter("methodName").byString("matcher1");
						// match on any annotation
						pb.parameter("allAnnotations")
								.setConflictResolutionMode(ConflictResolutionMode.APPEND)
								.byRole(CtRole.ANNOTATION, new TypeFilter<>(CtMethod.class))
						;
						pb.parameter("CheckAnnotationValues").byRole(CtRole.VALUE, new TypeFilter(CtAnnotation.class)).setContainerKind(ContainerKind.MAP);
					})
					.build();
			List<Match> matches = pattern.getMatches(ctClass);

			// we match the same methods in MatchMap as testMatchOfMapAttribute
			assertEquals(4, matches.size());
			// the new one is the one with deprecated
			{
				Match match = matches.get(3);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("deprecatedTestAnnotation2", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(3, match.getParametersMap().size());
				assertEquals("deprecatedTestAnnotation2", match.getParametersMap().get("methodName"));
				assertEquals("{timeout=4567}", getMap(match, "CheckAnnotationValues").toString());
				assertEquals("@java.lang.Deprecated", match.getParameters().getValue("allAnnotations").toString());
			}

		}
	}

	@Test
	public void testMatchOfMapKeySubstring() throws Exception {
		//contract: one can capture in parameters a key in an annotation key -> value map
		CtType<?> ctClass = ModelUtils.buildClass(MatchMap.class);
		{
			// match all methods with arbitrary name, and Annotation Test modifiers, parameters, but with empty body and return type void
			CtType<?> type = ctClass.getFactory().Type().get(MatchMap.class);
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("m1").getPatternElements())
					.configurePatternParameters(pb -> {
						//match any value of @Check annotation to parameter `testAnnotations`
						pb.parameter("CheckKey").bySubstring("value");
						pb.parameter("CheckValue").byFilter((CtLiteral lit) -> true);
						//match any method name
						pb.parameter("methodName").byString("m1");
						//match on all annotations of method
						pb.parameter("allAnnotations")
								.setConflictResolutionMode(ConflictResolutionMode.APPEND)
								.byRole(CtRole.ANNOTATION, new TypeFilter<>(CtMethod.class));
					})
					.build();
			List<Match> matches = pattern.getMatches(ctClass);
			assertEquals(2, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("m1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(3, match.getParametersMap().size());
				assertEquals("m1", match.getParametersMap().get("methodName"));
				assertEquals("value", match.getParameters().getValue("CheckKey").toString());
				assertEquals("\"xyz\"", match.getParameters().getValue("CheckValue").toString());
			}
			{
				Match match = matches.get(1);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("deprecatedTestAnnotation2", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(4, match.getParametersMap().size());
				assertEquals("deprecatedTestAnnotation2", match.getParametersMap().get("methodName"));
				assertEquals("timeout", match.getParameters().getValue("CheckKey").toString());
				assertEquals("4567", match.getParameters().getValue("CheckValue").toString());
				assertEquals("@java.lang.Deprecated", match.getParameters().getValue("allAnnotations").toString());
			}
		}
	}

	@Test
	public void testMatchInSet() throws Exception {
		// contract: the container type "Set" is supported to match set-related AST nodes (e.g. the throws clause)
		// tested method: setContainerKind(ContainerKind.SET)
		CtType<?> ctClass = ModelUtils.buildClass(MatchThrowables.class);
		Factory f = ctClass.getFactory();

		// we match a method with any "throws" clause
		// and the match "throws" are captured in the parameter
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setTypeMember("matcher1").getPatternElements())
				.configurePatternParameters(pb -> {
					pb.parameter("otherThrowables")
							//add matcher for other arbitrary throwables
							.setConflictResolutionMode(ConflictResolutionMode.APPEND)
							.setContainerKind(ContainerKind.SET)
							.setMinOccurrence(0)
							.byRole(CtRole.THROWN, new TypeFilter(CtMethod.class));
				})
				.configurePatternParameters(pb -> {
					//define other parameters too to match all kinds of methods
					pb.parameter("modifiers").byRole(CtRole.MODIFIER, new TypeFilter(CtMethod.class));
					pb.parameter("methodName").byString("matcher1");
					pb.parameter("parameters").byRole(CtRole.PARAMETER, new TypeFilter(CtMethod.class));
					pb.parameter("statements").byRole(CtRole.STATEMENT, new TypeFilter(CtBlock.class));
				})
				.build();
		String str = pattern.toString();
		List<Match> matches = pattern.getMatches(ctClass);
		assertEquals(4, matches.size());
		{
			Match match = matches.get(0);
			assertEquals(1, match.getMatchingElements().size());
			assertEquals("matcher1", match.getMatchingElement(CtMethod.class).getSimpleName());
		}
		{
			Match match = matches.get(1);
			assertEquals(1, match.getMatchingElements().size());
			assertEquals("sample2", match.getMatchingElement(CtMethod.class).getSimpleName());
			assertEquals(new HashSet(Arrays.asList(
					"java.lang.UnsupportedOperationException",
					"java.lang.IllegalArgumentException")),
					((Set<CtTypeReference<?>>) match.getParameters().getValue("otherThrowables"))
							.stream().map(e->e.toString()).collect(Collectors.toSet()));
		}
		{
			Match match = matches.get(2);
			assertEquals(1, match.getMatchingElements().size());
			assertEquals("sample3", match.getMatchingElement(CtMethod.class).getSimpleName());
			assertEquals(new HashSet(Arrays.asList(
					"java.lang.IllegalArgumentException")),
					((Set<CtTypeReference<?>>) match.getParameters().getValue("otherThrowables"))
							.stream().map(e->e.toString()).collect(Collectors.toSet()));
		}
		{
			// now looking at sample4
			Match match = matches.get(3);
			assertEquals(1, match.getMatchingElements().size());
			assertEquals("sample4", match.getMatchingElement(CtMethod.class).getSimpleName());

			// sample4 has exactly the expected exceptions. But there are no other exceptions, so match.getParameters().getValue("otherThrowables") is null
			assertNull(match.getParameters().getValue("otherThrowables"));
		}
	}

	private List<String> toListOfStrings(List<?> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		List<String> strings = new ArrayList<>(list.size());
		for (Object obj : list) {
			strings.add(obj == null ? "null" : obj.toString());
		}
		return strings;
	}

	private MapBuilder map() {
		return new MapBuilder();
	}

	class MapBuilder extends LinkedHashMap<String, Object> {
		public MapBuilder put(String key, Object value) {
			super.put(key, value);
			return this;
		}
	}

	@Test
	public void testPatternParameters() {
		//contract: all the parameters of Pattern are available through getParameterInfos
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPatternFromMethodPatternModel(f);
		Map<String, ParameterInfo> parameterInfos = p.getParameterInfos();
		// the pattern has all usages of variable "params" and "item"
		assertEquals(15, parameterInfos.size());
		// .. which are
		assertEquals(new HashSet<>(Arrays.asList("next","item","startPrefixSpace","printer","start",
				"statements","nextPrefixSpace","startSuffixSpace","elementPrinterHelper",
				"endPrefixSpace","startKeyword","useStartKeyword","end","nextSuffixSpace","getIterable"
				)), parameterInfos.keySet());

		// the map from getParameterInfos is consistent
		for (Map.Entry<String, ParameterInfo> e : parameterInfos.entrySet()) {
			assertEquals(e.getKey(), e.getValue().getName());
		}
	}

	@Test
	public void testPatternToString() {
		//contract: Pattern can be printed to String and each parameter is defined there
		String nl = System.getProperty("line.separator");
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPatternFromMethodPatternModel(f);
		assertEquals("if (/* CtInvocation"+nl+"" +
				"    / <= ${useStartKeyword}"+nl+"" +
				" */"+nl+"" +
				"useStartKeyword()) {"+nl+"" +
				"    /* CtInvocation"+nl+"" +
				"        /argument/ <= ${startKeyword}"+nl+"" +
				"     */"+nl+"" +
				"    /* CtInvocation"+nl+"" +
				"        /target/ <= ${printer}"+nl+"" +
				"     */"+nl+"" +
				"    /* CtInvocation"+nl+"" +
				"        / <= ${printer}"+nl+"" +
				"     */"+nl+"" +
				"    printer().writeSpace().writeKeyword(/* CtInvocation"+nl+"" +
				"        / <= ${startKeyword}"+nl+"" +
				"     */"+nl+"" +
				"    startKeyword()).writeSpace();"+nl+"" +
				"}"+nl+"" +
				"try (final spoon.reflect.visitor.ListPrinter lp = /* CtInvocation"+nl+"" +
				"    /argument/ <= ${end}"+nl+"" +
				"    /target/ <= ${elementPrinterHelper}"+nl+"" +
				" */"+nl+"" +
				"/* CtInvocation"+nl+"" +
				"    / <= ${elementPrinterHelper}"+nl+"" +
				" */"+nl+"" +
				"elementPrinterHelper().createListPrinter(/* CtInvocation"+nl+"" +
				"    / <= ${startPrefixSpace}"+nl+"" +
				" */"+nl+"" +
				"startPrefixSpace(), /* CtInvocation"+nl+"" +
				"    / <= ${start}"+nl+"" +
				" */"+nl+"" +
				"start(), /* CtInvocation"+nl+"" +
				"    / <= ${startSuffixSpace}"+nl+"" +
				" */"+nl+"" +
				"startSuffixSpace(), /* CtInvocation"+nl+"" +
				"    / <= ${nextPrefixSpace}"+nl+"" +
				" */"+nl+"" +
				"nextPrefixSpace(), /* CtInvocation"+nl+"" +
				"    / <= ${next}"+nl+"" +
				" */"+nl+"" +
				"next(), /* CtInvocation"+nl+"" +
				"    / <= ${nextSuffixSpace}"+nl+"" +
				" */"+nl+"" +
				"nextSuffixSpace(), /* CtInvocation"+nl+"" +
				"    / <= ${endPrefixSpace}"+nl+"" +
				" */"+nl+"" +
				"endPrefixSpace(), /* CtInvocation"+nl+"" +
				"    / <= ${end}"+nl+"" +
				" */"+nl+"" +
				"end())) {"+nl+"" +
				"    /* CtForEach"+nl+"" +
				"        /expression/ <= ${getIterable}"+nl+"" +
				"        /foreachVariable/ <= ${item}"+nl+"" +
				"     */"+nl+"" +
				"    for (/* CtLocalVariable"+nl+"" +
				"        / <= ${item}"+nl+"" +
				"     */"+nl+"" +
				"    java.lang.Object item : /* CtInvocation"+nl+"" +
				"        / <= ${getIterable}"+nl+"" +
				"     */"+nl+"" +
				"    getIterable()) /* CtBlock"+nl+"" +
				"        /statement/ <= ${statements}"+nl+"" +
				"     */"+nl+"" +
				"    {"+nl+"" +
				"        lp.printSeparatorIfAppropriate();"+nl+"" +
				"        /* CtInvocation"+nl+"" +
				"            / <= ${statements}"+nl+"" +
				"         */"+nl+"" +
				"        statements();"+nl+"" +
				"    }"+nl+"" +
				"}"+nl, p.print(true));
	}

	@Test
	public void testPatternToStringNoComments() {
		//contract: Pattern can be printed to String without parameters
		String nl = System.getProperty("line.separator");
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPatternFromMethodPatternModel(f);
		assertEquals("if (useStartKeyword()) {" + nl + 
				"    printer().writeSpace().writeKeyword(startKeyword()).writeSpace();" + nl + 
				"}" + nl + 
				"try (final spoon.reflect.visitor.ListPrinter lp = elementPrinterHelper().createListPrinter(startPrefixSpace(), start(), startSuffixSpace(), nextPrefixSpace(), next(), nextSuffixSpace(), endPrefixSpace(), end())) {" + nl + 
				"    for (java.lang.Object item : getIterable()) {" + nl + 
				"        lp.printSeparatorIfAppropriate();" + nl + 
				"        statements();" + nl + 
				"    }" + nl + 
				"}" + nl, p.print(false));
	}

	@Test
	public void testMatchSample1() {
		// contract: a super complex pattern is well matched
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
		);
		CtClass<?> classDJPP = f.Class().get(DPPSample1.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());

		CtType<Object> type = f.Type().get(OldPattern.class);
		// Create a pattern from all statements of OldPattern#patternModel
		Pattern p = PatternBuilder
				.create(new PatternBuilderHelper(type).setBodyOfMethod("patternModel").getPatternElements())
				.configurePatternParameters((PatternParameterConfigurator pb) -> pb
						// creating patterns parameters for all references to "params" and "items"
						.byFieldAccessOnVariable("params").byFieldAccessOnVariable("item")
						.parameter("statements").setContainerKind(ContainerKind.LIST)
				)
				.configurePatternParameters()
				.configureInlineStatements(ls -> ls.inlineIfOrForeachReferringTo("useStartKeyword"))
				.build();

		// so let's try to match this complex pattern on DJPP
		List<Match> matches = p.getMatches(classDJPP);

		// there are two results (the try-with-resource in each method)
		assertEquals(2, matches.size());
		ImmutableMap params = matches.get(0).getParameters();
		assertEquals("\"extends\"", params.getValue("startKeyword").toString());
		assertEquals(Boolean.TRUE, params.getValue("useStartKeyword"));
		assertEquals("false", params.getValue("startPrefixSpace").toString());
		assertEquals("null", params.getValue("start").toString());
		assertEquals("false", params.getValue("startSuffixSpace").toString());
		assertEquals("false", params.getValue("nextPrefixSpace").toString());
		assertEquals("\",\"", params.getValue("next").toString());
		assertEquals("true", params.getValue("nextSuffixSpace").toString());
		assertEquals("false", params.getValue("endPrefixSpace").toString());
		assertEquals("\";\"", params.getValue("end").toString());
		assertEquals("ctEnum.getEnumValues()", params.getValue("getIterable").toString());
		assertEquals("[scan(enumValue)]", params.getValue("statements").toString());

		params = matches.get(1).getParameters();
		// all method arguments to createListPrinter have been matched
		assertNull(params.getValue("startKeyword"));
		assertEquals(Boolean.FALSE, params.getValue("useStartKeyword"));
		assertEquals("false", params.getValue("startPrefixSpace").toString());
		assertEquals("null", params.getValue("start").toString());
		assertEquals("false", params.getValue("startSuffixSpace").toString());
		assertEquals("false", params.getValue("nextPrefixSpace").toString());
		assertEquals("\",\"", params.getValue("next").toString());
		assertEquals("true", params.getValue("nextSuffixSpace").toString());
		assertEquals("false", params.getValue("endPrefixSpace").toString());
		assertEquals("\";\"", params.getValue("end").toString());
		assertEquals("ctEnum.getEnumValues()", params.getValue("getIterable").toString());
		assertEquals("[scan(enumValue)]", params.getValue("statements").toString());

		// additional test for ImmutableMap
		assertEquals(params.asMap(), params.checkpoint().asMap());
	}

	@Test
	public void testAddGeneratedBy() throws Exception {
		//contract: by default "generated by" comments are not generated
		//contract: generated by comments can be switched ON/OFF later

		// creating a pattern from AClassWithMethodsAndRefs
		CtType templateModel = ModelUtils.buildClass(AClassWithMethodsAndRefs.class);
		Factory factory = templateModel.getFactory();
		Pattern pattern = PatternBuilder.create(templateModel).setAddGeneratedBy(true).build();
	}

	@Test
	public void testGenerateClassWithSelfReferences() throws Exception {
		// main contract: a class with methods and fields can be generated with method #createType
		// in particular, all the references to the origin class are replace by reference to the new class cloned class

		// creating a pattern from AClassWithMethodsAndRefs
		CtType templateModel = ModelUtils.buildClass(AClassWithMethodsAndRefs.class);
		Factory factory = templateModel.getFactory();
		Pattern pattern = PatternBuilder.create(templateModel).setAddGeneratedBy(true).build();

		final String newQName = "spoon.test.generated.ACloneOfAClassWithMethodsAndRefs";
		CtClass<?> generatedType = pattern.generator().generateType(newQName, Collections.emptyMap());
		assertNotNull(generatedType);

		// sanity check that the new type contains all the expected methods
		assertEquals(Arrays.asList("<init>","local","sameType","sameTypeStatic","anotherMethod","someMethod","Local","foo"),
				generatedType.getTypeMembers().stream().map(CtTypeMember::getSimpleName).collect(Collectors.toList()));

		// contract: one can generated the type in a new package, with a fully-qualified name
		assertEquals(newQName, generatedType.getQualifiedName());

		//contract: all the type references points to new type
		Set<String> usedTypeRefs = new HashSet<>();
		generatedType.filterChildren(new TypeFilter<>(CtTypeReference.class))
			.forEach((CtTypeReference ref) -> usedTypeRefs.add(ref.getQualifiedName()));
		assertEquals(new HashSet<>(Arrays.asList(
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs","void","boolean",
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$1Bar",
				"java.lang.Object","int","spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$Local")),
				usedTypeRefs);

		//contract: all executable references points to the executables in cloned type
		generatedType.filterChildren(new TypeFilter<>(CtExecutableReference.class)).forEach((CtExecutableReference execRef) -> {
			CtTypeReference declTypeRef = execRef.getDeclaringType();
			if(declTypeRef.getQualifiedName().startsWith("spoon.test.generated.ACloneOfAClassWithMethodsAndRefs")) {
				//OK
				return;
			}
			if(declTypeRef.getQualifiedName().equals(Object.class.getName())) {
				return;
			}
			fail("Unexpected declaring type " + declTypeRef.getQualifiedName());
		});
	}

	@Test
	public void testGenerateMethodWithSelfReferences() throws Exception {
		//contract: a method with self references can be used as a template to generate a clone

		CtType templateModel = ModelUtils.buildClass(AClassWithMethodsAndRefs.class);
		Factory factory = templateModel.getFactory();

		// create a template from method foo
		Pattern pattern = PatternBuilder.create(
				(CtMethod) templateModel.getMethodsByName("foo").get(0)
				)
				.setAddGeneratedBy(true) //switch ON: generate by comments
				.build();

		CtClass<?> generatedType = factory.createClass("spoon.test.generated.ACloneOfAClassWithMethodsAndRefs");

		pattern.generator().addToType(CtMethod.class, Collections.emptyMap(), generatedType);

		//contract: the foo method has been added
		assertEquals(Arrays.asList("foo"),
				generatedType.getTypeMembers().stream().map(CtTypeMember::getSimpleName).collect(Collectors.toList()));
		assertEquals(1, generatedType.getMethodsByName("foo").size());

		//contract: generate by comments are appended
		assertEquals("Generated by spoon.test.template.testclasses.types.AClassWithMethodsAndRefs#foo(AClassWithMethodsAndRefs.java:30)",
				generatedType.getMethodsByName("foo").get(0).getDocComment().trim());

		//contract: all the type references points to new type
		Set<String> usedTypeRefs = new HashSet<>();
		generatedType.filterChildren(new TypeFilter<>(CtTypeReference.class))
			.forEach((CtTypeReference ref) -> usedTypeRefs.add(ref.getQualifiedName()));
		assertEquals(new HashSet<>(Arrays.asList(
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs","void",
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$1Bar",
				"java.lang.Object","spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$Local")),
				usedTypeRefs);
		//contract: all executable references points to executables in cloned type
		generatedType.filterChildren(new TypeFilter<>(CtExecutableReference.class)).forEach((CtExecutableReference execRef) -> {
			CtTypeReference declTypeRef = execRef.getDeclaringType();
			if(declTypeRef.getQualifiedName().startsWith("spoon.test.generated.ACloneOfAClassWithMethodsAndRefs")) {
				//OK
				return;
			}
			if(declTypeRef.getQualifiedName().equals(Object.class.getName())) {
				return;
			}
			fail("Unexpected declaring type " + declTypeRef.getQualifiedName());
		});
	}

	@Test
	public void testPatternMatchOfMultipleElements() throws Exception {
		// contract: one can match list of elements in hard-coded arrays (CtNewArray)
		CtType toBeMatchedtype = ModelUtils.buildClass(ToBeMatched.class);

		// getting the list of literals defined in method match1
		List<CtLiteral<String>> literals1 = getFirstStmt(toBeMatchedtype, "match1", CtInvocation.class).getArguments();
		List<CtLiteral<String>> literals2 = getFirstStmt(toBeMatchedtype, "match2", CtInvocation.class).getArguments();
		assertEquals("a", literals1.get(0).getValue());

		Factory f = toBeMatchedtype.getFactory();

		{	//contract: matches one exact literal
			List<CtElement> found = new ArrayList<>();

			// creating a Pattern from a Literal, with zero pattern parameters
			// The pattern model consists of one CtLIteral only
			// there is not needed any type reference, because CtLiteral has no reference to a type where it is defined
			spoon.pattern.Pattern p = PatternBuilder.create(f.createLiteral("a")).build();

			//The pattern has no parameters. There is just one constant CtLiteral
			assertEquals (0, p.getParameterInfos().size());

			// when we match the pattern agains AST of toBeMatchedtype, we find three instances of "a",
			//because there are 3 instances of CtLiteral "a" in toBeMatchedtype
			p.forEachMatch(toBeMatchedtype, (match) -> {
				found.add(match.getMatchingElement());
			});

			assertEquals(3, found.size());
			assertSame(literals1.get(0)/* first "a" in match1 */, found.get(0));
			assertSame(literals1.get(6)/* 2nd "a" in match1 */, found.get(1));
			assertSame(literals2.get(0)/* 1st "a" in match 2 */, found.get(2));
		}
		{	//contract: matches sequence of elements
			List<List<CtElement>> found = new ArrayList<>();
			// now we match a sequence of "a", "b", "c"
			spoon.pattern.Pattern pattern = patternOfStringLiterals(toBeMatchedtype.getFactory(), "a", "b", "c");
			pattern.forEachMatch(toBeMatchedtype, (match) -> {
				found.add(match.getMatchingElements());
			});
			assertEquals(2, found.size());

			assertEquals(3, found.get(1).size());
			// it starts with the first "a" in the match1
			assertEquals("\"a\"", found.get(0).get(0).toString());
			assertEquals(17, found.get(0).get(0).getPosition().getColumn());
			assertEquals("\"b\"", found.get(0).get(1).toString());
			assertEquals(22, found.get(0).get(1).getPosition().getColumn());
			assertEquals("\"c\"", found.get(0).get(2).toString());
			assertEquals(27, found.get(0).get(2).getPosition().getColumn());

			// more generic asserts
			assertSequenceOn(literals1, 0, 3, found.get(0));
			assertSequenceOn(literals1, 6, 3, found.get(1));
		}
		{	//contract: matches sequence of elements not starting at the beginning
			List<List<CtElement>> found = new ArrayList<>();
			patternOfStringLiterals(toBeMatchedtype.getFactory(), "b", "c").forEachMatch(toBeMatchedtype, (match) -> {
				found.add(match.getMatchingElements());
			});
			// we have three times a sequence ["b", "c"]
			assertEquals(3, found.size());
			assertSequenceOn(literals1, 1, 2, found.get(0));
			assertSequenceOn(literals1, 7, 2, found.get(1));
			assertSequenceOn(literals2, 3, 2, found.get(2));
		}
		{	//contract: matches sequence of repeated elements, but match each element only once
			List<List<CtElement>> found = new ArrayList<>();
			// we search for ["d", "d"]
			patternOfStringLiterals(toBeMatchedtype.getFactory(), "d", "d").forEachMatch(toBeMatchedtype, (match) -> {
				found.add(match.getMatchingElements());
			});
			// in ToBeMatched there is ["d", "d", "d", "d", "d]
			// so there are only two sequences, starting at first and third "d"
			assertEquals(2, found.size());
			assertSequenceOn(literals2, 6, 2, found.get(0));
			assertSequenceOn(literals2, 8, 2, found.get(1));
		}
	}

	private static spoon.pattern.Pattern patternOfStringLiterals(Factory f, String... strs) {
		return PatternBuilder.create(Arrays.asList(strs).stream().map(s -> f.createLiteral(s)).collect(Collectors.toList())
		).build();
	}

	private void assertSequenceOn(List<? extends CtElement> source, int expectedOffset, int expectedSize, List<CtElement> matches) {
		//check the number of matches
		assertEquals(expectedSize, matches.size());
		//check that each match fits to source collection on the expected offset
		for (int i = 0; i < expectedSize; i++) {
			assertSame(source.get(expectedOffset + i), matches.get(i));
		}
	}

	private <T extends CtElement> T getFirstStmt(CtType type, String methodName, Class<T> stmtType) {
		return (T) type.filterChildren((CtMethod m) -> m.getSimpleName().equals(methodName)).first(CtMethod.class).getBody().getStatement(0);
	}

	private int indexOf(List list, Object o) {
		for(int i=0; i<list.size(); i++) {
			if (list.get(i)==o) {
				return i;
			}
		}
		return -1;
	}

	@Test
	public void testExtensionDecoupledSubstitutionVisitor() {
		//contract: one can add type members with Generator
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/template/testclasses/logger/Logger.java");
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/LoggerModel.java"));

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		final CtClass<?> aTargetType = launcher.getFactory().Class().get(Logger.class);
		final CtMethod<?> toBeLoggedMethod = aTargetType.getMethodsByName("enter").get(0);


		Map<String, Object> params = new HashMap<>();
		params.put("_classname_", factory.Code().createLiteral(aTargetType.getSimpleName()));
		params.put("_methodName_", factory.Code().createLiteral(toBeLoggedMethod.getSimpleName()));
		params.put("_block_", toBeLoggedMethod.getBody());
		//create a patter from the LoggerModel#block
		CtType<?> type = factory.Type().get(LoggerModel.class);


		// creating a pattern from method "block"
		spoon.pattern.Pattern pattern = PatternBuilder.create(type.getMethodsByName("block").get(0))
				//all the variable references which are declared out of type member "block" are automatically considered
				//as pattern parameters
				.configurePatternParameters()
				.build();
		final List<CtMethod> aMethods = pattern.generator().addToType(CtMethod.class, params, aTargetType);
		assertEquals(1, aMethods.size());
		final CtMethod<?> aMethod = aMethods.get(0);
		assertTrue(aMethod.getBody().getStatement(0) instanceof CtTry);
		final CtTry aTry = (CtTry) aMethod.getBody().getStatement(0);
		assertTrue(aTry.getFinalizer().getStatement(0) instanceof CtInvocation);
		assertEquals("spoon.test.template.testclasses.logger.Logger.exit(\"enter\")", aTry.getFinalizer().getStatement(0).toString());
		assertTrue(aTry.getBody().getStatement(0) instanceof CtInvocation);
		assertEquals("spoon.test.template.testclasses.logger.Logger.enter(\"Logger\", \"enter\")", aTry.getBody().getStatement(0).toString());
		assertTrue(aTry.getBody().getStatements().size() > 1);
	}

	@Test
	public void testMatchType() {
		//contract: one can match a type
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/template/testclasses/logger/Logger.java");

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		//create a template class
		final CtClass<?> aTemplateType = launcher.getFactory().Class().create("a.template.Clazz");
		//create a pattern which should match that class
		Pattern pattern = PatternBuilder.create(aTemplateType)
				.configurePatternParameters(pb -> {
					pb.parameter("members").byRole(CtRole.TYPE_MEMBER, e -> e == aTemplateType);
					pb.parameter("modifiers").byRole(CtRole.MODIFIER, e -> e == aTemplateType);
				}).build();
		
		final CtClass<?> aTargetType = launcher.getFactory().Class().get(Logger.class);
		List<Match> matches = pattern.getMatches(aTargetType);
		assertEquals(1, matches.size());
		Match match = matches.get(0);
		assertSame(aTargetType, match.getMatchingElement());
		List<CtTypeMember> expectedTypeMembers = aTargetType.getTypeMembers();
		List<CtTypeMember> typeMembers = (List<CtTypeMember>) match.getParameters().getValue("members");
		assertEquals(expectedTypeMembers.size(), typeMembers.size());
		for (int i = 0; i < expectedTypeMembers.size(); i++) {
			assertSame(expectedTypeMembers.get(i), typeMembers.get(i));
		}
	}

	@Test
	public void testSubstituteExactElements() {
		//contract: one can substitute exactly defined element
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/template/testclasses/logger/Logger.java");

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		final CtClass<?> aTargetType = launcher.getFactory().Class().get(Logger.class);
		CtMethod tobeSubstititedMethod = aTargetType.getMethodsByName("enter").get(0);
		Pattern pattern = PatternBuilder.create(aTargetType)
			.configurePatternParameters(pb -> {
				//substitute NAME of method
				pb.parameter("methodName").byRole(CtRole.NAME, tobeSubstititedMethod);
				//substitute Body of method
				pb.parameter("methodBody").byElement(tobeSubstititedMethod.getBody());
			}).build();
		
		List<Match> matches = pattern.getMatches(aTargetType);
		assertEquals(1, matches.size());
		Match match = matches.get(0);
		assertSame(aTargetType, match.getMatchingElement());
		assertEquals("enter", match.getParameters().getValue("methodName"));
		assertSame(tobeSubstititedMethod.getBody(), match.getParameters().getValue("methodBody"));
	}

	private Map<String, Object> getMap(Match match, String name) {
		Object v = match.getParametersMap().get(name);
		assertNotNull(v);
		return ((ImmutableMap) v).asMap();
	}
}
