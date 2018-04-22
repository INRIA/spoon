package spoon.test.template;

import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.pattern.ConflictResolutionMode;
import spoon.pattern.ParametersBuilder;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.pattern.matcher.Match;
import spoon.pattern.matcher.Quantifier;
import spoon.pattern.parameter.ParameterInfo;
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
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.util.ParameterValueProvider;
import spoon.support.util.UnmodifiableParameterValueProvider;
import spoon.test.template.testclasses.LoggerModel;
import spoon.test.template.testclasses.ToBeMatched;
import spoon.test.template.testclasses.logger.Logger;
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
import spoon.test.template.testclasses.replace.NewPattern;
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
					.configureParameters(pb -> {
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
				.configureParameters(pb -> {
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
				.configureParameters(pb -> {
					pb.parameter("option").byVariable("option");
					pb.parameter("value").byFilter(new TypeFilter(CtLiteral.class));
				})
				//we have to configure inline statements after all expressions
				//of combined if statement are marked as pattern parameters
				.configureInlineStatements(lsb -> lsb.byVariableName("option"))
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
	public void testGenerateMultiValues() throws Exception {
		// contract: the pattern parameter (in this case 'statements')
		//can have type List and can be replaced by list of elements
		//(in this case by list of statements)

		// here, in particular, we test method "substituteList"


		// setup of the test
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Factory factory = ctClass.getFactory();

		Pattern pattern = MatchMultiple.createPattern(null, null, null);
		ParameterValueProvider params = new UnmodifiableParameterValueProvider();

		// created in "MatchMultiple.createPattern",matching a literal "something"
		// so "something" si replaced by "does it work?"
		params = params.putValue("printedValue", "does it work?");
		List<CtStatement> statementsToBeAdded = null;

		//statementsToBeAdded = ctClass.getMethodsByName("testMatch1").get(0).getBody().getStatements().subList(0, 3); // we don't use this in order not to mix the matching and the transformation
		statementsToBeAdded = Arrays.asList(new CtStatement[] {factory.createCodeSnippetStatement("int foo = 0"), factory.createCodeSnippetStatement("foo++")});

		// created in "MatchMultiple.createPattern",matching a method "statements"
		params = params.putValue("statements", statementsToBeAdded);

		List<CtStatement> generated = pattern.substitute(factory, CtStatement.class, params);
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
		// This is done with method parameterBuilder.setMaxOccurence(maxCount)

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
		//contract: possessive matching eats everything and never returns anything
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Pattern pattern = MatchMultiple.createPattern(Quantifier.POSSESSIVE, null, null);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
		//the last template has nothing to match -> no match
		assertEquals(0, matches.size());
	}
	@Test
	public void testMatchPossesiveMultiValueMaxCount4() throws Exception {
		//contract: multivalue parameter can match multiple nodes into list of parameter values.
		//contract: possessive matching eats everything and never returns back
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Pattern pattern = MatchMultiple.createPattern(Quantifier.POSSESSIVE, null, 4);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

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
		//contract: support for possessive matching with min count limit mixed with GREEDY back off
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple3.class);
		for (int i = 0; i < 7; i++) {
			final int count = i;
			CtType<?> type = ctClass.getFactory().Type().get(MatchMultiple3.class);
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
					.configureTemplateParameters()
					.configureParameters(pb -> {
						pb.parameter("statements1").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.GREEDY);
						pb.parameter("statements2").setContainerKind(ContainerKind.LIST).setMatchingStrategy(Quantifier.POSSESSIVE).setMinOccurence(count).setMaxOccurence(count);
						pb.parameter("printedValue").byFilter((CtLiteral<?> literal) -> "something".equals(literal.getValue()));
					})
					.build();

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
			if (count < 6) {
				//the last template has nothing to match -> no match
				assertEquals("count="+count, 1, matches.size());
				assertEquals("count="+count, 5-count, getCollectionSize(matches.get(0).getParameters().getValue("statements1")));
				assertEquals("count="+count, count, getCollectionSize(matches.get(0).getParameters().getValue("statements2")));
			} else {
				//the possessive matcher eat too much. There is no target element for last `printedValue` variable
				assertEquals("count="+count, 0, matches.size());
			}
		}
	}

	@Test
	public void testMatchPossesiveMultiValueMinCount2() throws Exception {
		//contract: support for check possessive matching with min count limit and GREEDY back off
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple2.class);
		for (int i = 0; i < 7; i++) {
			final int count = i;
			Pattern pattern = MatchMultiple2.createPattern(ctClass.getFactory(), pb -> {
				pb.parameter("statements1").setMatchingStrategy(Quantifier.GREEDY);
				pb.parameter("statements2").setMatchingStrategy(Quantifier.POSSESSIVE).setMinOccurence(count).setMaxOccurence(count);
				pb.parameter("printedValue").setMatchingStrategy(Quantifier.POSSESSIVE).setContainerKind(ContainerKind.LIST).setMinOccurence(2);
			});

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
			if (count < 5) {
				//the last template has nothing to match -> no match
				assertEquals("count="+count, 1, matches.size());
				assertEquals("count="+count, 4-count, getCollectionSize(matches.get(0).getParameters().getValue("statements1")));
				assertEquals("count="+count, count, getCollectionSize(matches.get(0).getParameters().getValue("statements2")));
				assertEquals("count="+count, 2, getCollectionSize(matches.get(0).getParameters().getValue("printedValue")));
			} else {
				//the possessive matcher eat too much. There is no target element for last `printedValue` variable
				assertEquals("count="+count, 0, matches.size());
			}
		}
	}
	@Test
	public void testMatchGreedyMultiValueMinCount2() throws Exception {
		//contract: check possessive matching with min count limit and GREEDY back off
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple2.class);
		for (int i = 0; i < 7; i++) {
			final int count = i;
			Pattern pattern = MatchMultiple2.createPattern(ctClass.getFactory(), pb -> {
				pb.parameter("statements1").setMatchingStrategy(Quantifier.RELUCTANT);
				pb.parameter("statements2").setMatchingStrategy(Quantifier.GREEDY).setMaxOccurence(count);
				pb.parameter("printedValue").setMatchingStrategy(Quantifier.GREEDY).setContainerKind(ContainerKind.LIST).setMinOccurence(2);
			});

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
			if (count < 7) {
				//the last template has nothing to match -> no match
				assertEquals("count="+count, 1, matches.size());
				assertEquals("count="+count, Math.max(0, 3-count), getCollectionSize(matches.get(0).getParameters().getValue("statements1")));
				assertEquals("count="+count, count - Math.max(0, count-4), getCollectionSize(matches.get(0).getParameters().getValue("statements2")));
				assertEquals("count="+count, Math.max(2, 3 - Math.max(0, count-3)), getCollectionSize(matches.get(0).getParameters().getValue("printedValue")));
			} else {
				//the possessive matcher eat too much. There is no target element for last `printedValue` variable
				assertEquals("count="+count, 0, matches.size());
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
		//contract: if matching on the pattern itself, the matched parameter value is the originak AST node from the pattern
		// see last assertSame of this test
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterType.class);

		// pattern: a call to System.out.println with anything as parameter
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
				.configureParameters(pb -> {
					pb.parameter("value").byVariable("value");
				})
				.build();

		List<Match> matches = pattern.getMatches(ctClass);

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
		//contract: pattern parameters can be restricted to only certain types
		// (here CtLiteral.class)
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterType.class);
		{
			// now we match only the ones with a literal as parameter
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setBodyOfMethod("matcher1").getPatternElements())
					.configureParameters(pb -> {
						pb.parameter("value").byVariable("value");
						pb.setValueType(CtLiteral.class);
					})
					.build();

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1"));

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
					.configureParameters(pb -> {
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
					.configureParameters(pb -> {
						pb.parameter("value").byVariable("value");
						pb.matchCondition(null, (Object value) -> value instanceof CtLiteral);
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
		//contract: match some nodes like template, but with some variable attributes
		// tested methods: ParameterBuilder#byRole and ParameterBuilder#byString
		CtType<?> ctClass = ModelUtils.buildClass(MatchModifiers.class);
		{
			//match all methods with arbitrary name, modifiers, parameters, but with empty body and return type void
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setTypeMember("matcher1").getPatternElements())
					.configureParameters(pb -> {
						pb.parameter("modifiers").byRole(new TypeFilter(CtMethod.class), CtRole.MODIFIER);
						pb.parameter("methodName").byString("matcher1");
						pb.parameter("parameters").byRole(new TypeFilter(CtMethod.class), CtRole.PARAMETER);
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
					.configureParameters(pb -> {
						pb.parameter("modifiers").byRole(new TypeFilter(CtMethod.class), CtRole.MODIFIER);
						pb.parameter("methodName").byString("matcher1");
						pb.parameter("parameters").byRole(new TypeFilter(CtMethod.class), CtRole.PARAMETER);
						pb.parameter("statements").byRole(new TypeFilter(CtBlock.class), CtRole.STATEMENT);
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
		//contract: there is support for matching annotations with different values
		CtType<?> matchMapClass = ModelUtils.buildClass(MatchMap.class);
		{
			CtType<?> type = matchMapClass.getFactory().Type().get(MatchMap.class);
			// create a pattern from method matcher1
			// match all methods with arbitrary name, and annotation @Check, parameters, but with empty body and return type void
//			@Check()
//			void matcher1() {
//			}
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("matcher1").getPatternElements())
					.configureParameters(pb -> {
						//match any value of @Check annotation to parameter `testAnnotations`
						pb.parameter("__pattern_param_annot").byRole(new TypeFilter(CtAnnotation.class), CtRole.VALUE).setContainerKind(ContainerKind.MAP);
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

	private Map<String, Object> getMap(Match match, String name) {
		Object v = match.getParametersMap().get(name);
		assertNotNull(v);
		return ((ParameterValueProvider) v).asMap();
	}

	@Test
	public void testMatchOfMapAttributeAndOtherAnnotations() throws Exception {
		//contract: match a pattern with an "open" annotation (different values can be matched)
		// same test but with one more pattern parameter: allAnnotations
		CtType<?> ctClass = ModelUtils.buildClass(MatchMap.class);
		{
			CtType<?> type = ctClass.getFactory().Type().get(MatchMap.class);
			// create a pattern from method matcher1
			// match all methods with arbitrary name, with any annotation set, Test modifiers, parameters, but with empty body and return type void
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("matcher1").getPatternElements())
					.configureParameters(pb -> {
						//match any value of @Check annotation to parameter `testAnnotations`
						//match any method name
						pb.parameter("methodName").byString("matcher1");
						// match on any annotation
						pb.parameter("allAnnotations")
								.setConflictResolutionMode(ConflictResolutionMode.APPEND)
								.byRole(new TypeFilter<>(CtMethod.class), CtRole.ANNOTATION)
						;
						pb.parameter("CheckAnnotationValues").byRole(new TypeFilter(CtAnnotation.class), CtRole.VALUE).setContainerKind(ContainerKind.MAP);
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
		//contract: one can capture in parameters a key in an annotation key-> value map
		CtType<?> ctClass = ModelUtils.buildClass(MatchMap.class);
		{
			// match all methods with arbitrary name, and Annotation Test modifiers, parameters, but with empty body and return type void
			CtType<?> type = ctClass.getFactory().Type().get(MatchMap.class);
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("m1").getPatternElements())
					.configureParameters(pb -> {
						//match any value of @Check annotation to parameter `testAnnotations`
						pb.parameter("CheckKey").bySubstring("value");
						pb.parameter("CheckValue").byFilter((CtLiteral lit) -> true);
						//match any method name
						pb.parameter("methodName").byString("m1");
						//match on all annotations of method
						pb.parameter("allAnnotations")
								.setConflictResolutionMode(ConflictResolutionMode.APPEND)
								.byRole(new TypeFilter<>(CtMethod.class), CtRole.ANNOTATION);
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
		// contract: the container type "Set" is supported to match set-related AST nodes (eg the throws clause)
		// tested method: setContainerKind(ContainerKind.SET)
		CtType<?> ctClass = ModelUtils.buildClass(MatchThrowables.class);
		Factory f = ctClass.getFactory();

		// we match a method with any "throws" clause
		// and the match "throws" are captured in the parameter
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(ctClass).setTypeMember("matcher1").getPatternElements())
				.configureParameters(pb -> {
					pb.parameter("otherThrowables")
							//add matcher for other arbitrary throwables
							.setConflictResolutionMode(ConflictResolutionMode.APPEND)
							.setContainerKind(ContainerKind.SET)
							.setMinOccurence(0)
							.byRole(new TypeFilter(CtMethod.class), CtRole.THROWN);
				})
				.configureParameters(pb -> {
					//define other parameters too to match all kinds of methods
					pb.parameter("modifiers").byRole(new TypeFilter(CtMethod.class), CtRole.MODIFIER);
					pb.parameter("methodName").byString("matcher1");
					pb.parameter("parameters").byRole(new TypeFilter(CtMethod.class), CtRole.PARAMETER);
					pb.parameter("statements").byRole(new TypeFilter(CtBlock.class), CtRole.STATEMENT);
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
			Match match = matches.get(3);
			assertEquals(1, match.getMatchingElements().size());
			assertEquals("sample4", match.getMatchingElement(CtMethod.class).getSimpleName());
			assertNotNull(match.getParameters().getValue("otherThrowables"));
			assertEquals(2, getCollectionSize(match.getParameters().getValue("otherThrowables")));
		}
	}

	private List<String> toListOfStrings(List<? extends Object> list) {
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
		// the pattern has 15 pattern parameters (all usages of variable "params" and "item"
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
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPatternFromMethodPatternModel(f);
		assertEquals("if (/* CtInvocation\n" +
				"    / <= ${useStartKeyword}\n" +
				" */\n" +
				"useStartKeyword()) {\n" +
				"    /* CtInvocation\n" +
				"        /argument/ <= ${startKeyword}\n" +
				"     */\n" +
				"    /* CtInvocation\n" +
				"        /target/ <= ${printer}\n" +
				"     */\n" +
				"    /* CtInvocation\n" +
				"        / <= ${printer}\n" +
				"     */\n" +
				"    printer().writeSpace().writeKeyword(/* CtInvocation\n" +
				"        / <= ${startKeyword}\n" +
				"     */\n" +
				"    startKeyword()).writeSpace();\n" +
				"}\n" +
				"try (final spoon.reflect.visitor.ListPrinter lp = /* CtInvocation\n" +
				"    /argument/ <= ${end}\n" +
				"    /target/ <= ${elementPrinterHelper}\n" +
				" */\n" +
				"/* CtInvocation\n" +
				"    / <= ${elementPrinterHelper}\n" +
				" */\n" +
				"elementPrinterHelper().createListPrinter(/* CtInvocation\n" +
				"    / <= ${startPrefixSpace}\n" +
				" */\n" +
				"startPrefixSpace(), /* CtInvocation\n" +
				"    / <= ${start}\n" +
				" */\n" +
				"start(), /* CtInvocation\n" +
				"    / <= ${startSuffixSpace}\n" +
				" */\n" +
				"startSuffixSpace(), /* CtInvocation\n" +
				"    / <= ${nextPrefixSpace}\n" +
				" */\n" +
				"nextPrefixSpace(), /* CtInvocation\n" +
				"    / <= ${next}\n" +
				" */\n" +
				"next(), /* CtInvocation\n" +
				"    / <= ${nextSuffixSpace}\n" +
				" */\n" +
				"nextSuffixSpace(), /* CtInvocation\n" +
				"    / <= ${endPrefixSpace}\n" +
				" */\n" +
				"endPrefixSpace(), /* CtInvocation\n" +
				"    / <= ${end}\n" +
				" */\n" +
				"end())) {\n" +
				"    /* CtForEach\n" +
				"        /expression/ <= ${getIterable}\n" +
				"        /foreachVariable/ <= ${item}\n" +
				"     */\n" +
				"    for (/* CtLocalVariable\n" +
				"        / <= ${item}\n" +
				"     */\n" +
				"    java.lang.Object item : /* CtInvocation\n" +
				"        / <= ${getIterable}\n" +
				"     */\n" +
				"    getIterable()) /* CtBlock\n" +
				"        /statement/ <= ${statements}\n" +
				"     */\n" +
				"    {\n" +
				"        lp.printSeparatorIfAppropriate();\n" +
				"        /* CtInvocation\n" +
				"            / <= ${statements}\n" +
				"         */\n" +
				"        statements();\n" +
				"    }\n" +
				"}\n", p.toString());
	}

	@Test
	public void testMatchSample1() throws Exception {
		// contract: a super omplex pattern is well matched
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
				.configureParameters((ParametersBuilder pb) -> pb
						// creating patterns parameters for all references to "params" and "items"
						.createPatternParameterForVariable("params", "item")
						.parameter("statements").setContainerKind(ContainerKind.LIST)
				)
				.createPatternParameters()
				.configureInlineStatements(ls -> ls.byVariableName("useStartKeyword"))
				.build();

		// so let's try to match this complex pattern  on DJPP
		List<Match> matches = p.getMatches(classDJPP);

		// there are two results (the try-with-resource in each method)
		assertEquals(2, matches.size());
		ParameterValueProvider params = matches.get(0).getParameters();
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
		assertEquals(null, params.getValue("startKeyword"));
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
	}

	@Test
	public void testTemplateReplace() throws Exception {
		// contract: ??
		Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setComplianceLevel(8);
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setCommentEnabled(true);
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.addInputSource(new File("./src/main/java/spoon/reflect/visitor"));
		compiler.addInputSource(new File("./src/test/java/spoon/test/template/testclasses/replace"));
		compiler.build();
		CtClass<?> classDJPP = factory.Class().get(DefaultJavaPrettyPrinter.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());
		CtType<?> targetType = (classDJPP instanceof CtType) ? (CtType) classDJPP : classDJPP.getParent(CtType.class);
		Factory f = classDJPP.getFactory();

		// we create two different patterns
		Pattern newPattern = NewPattern.createPatternFromNewPattern(f);
		Pattern oldPattern = OldPattern.createPatternFromMethodPatternModel(f);
		
		oldPattern.forEachMatch(classDJPP, (match) -> {
			CtElement matchingElement = match.getMatchingElement(CtElement.class, false);
			RoleHandler role = RoleHandlerHelper.getRoleHandlerWrtParent(matchingElement);
			List<CtElement> elements = newPattern.applyToType(targetType, (Class) role.getValueClass(), match.getParametersMap());
			match.replaceMatchesBy(elements);
		});

		launcher.setSourceOutputDirectory(new File("./target/spooned-template-replace/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
	}
	
	@Test
	public void testGenerateClassWithSelfReferences() throws Exception {
		//contract: a class with methods and fields can be used as template to generate a clone
		//all the references to the origin class are replace by reference to the new class
		CtType templateModel = ModelUtils.buildClass(AClassWithMethodsAndRefs.class);
		Factory factory = templateModel.getFactory();
		Pattern pattern = PatternBuilder.create(templateModel).build();
		//contract: by default generated by comments are not generated
		assertFalse(pattern.isAddGeneratedBy());
		//contract: generated by comments can be switched ON/OFF later
		pattern.setAddGeneratedBy(true);
		assertTrue(pattern.isAddGeneratedBy());
		final String newQName = "spoon.test.generated.ACloneOfAClassWithMethodsAndRefs";
		CtClass<?> generatedType = pattern.createType(factory, newQName, Collections.emptyMap());
		assertNotNull(generatedType);
		assertEquals(newQName, generatedType.getQualifiedName());
		assertEquals("ACloneOfAClassWithMethodsAndRefs", generatedType.getSimpleName());
		assertEquals(Arrays.asList("<init>","local","sameType","sameTypeStatic","anotherMethod","someMethod","Local","foo"),
				generatedType.getTypeMembers().stream().map(CtTypeMember::getSimpleName).collect(Collectors.toList()));
		//contract: all the type references points to new type
		Set<String> usedTypeRefs = new HashSet<>();
		generatedType.filterChildren(new TypeFilter<>(CtTypeReference.class))
			.forEach((CtTypeReference ref) -> usedTypeRefs.add(ref.getQualifiedName()));
		assertEquals(new HashSet<>(Arrays.asList(
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs","void","boolean",
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$1Bar",
				"java.lang.Object","int","spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$Local")),
				usedTypeRefs);
		//contract: all executable references points to executables in cloned type
		generatedType.filterChildren(new TypeFilter<>(CtExecutableReference.class)).forEach((CtExecutableReference execRef) ->{
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
		//all the references to the origin class are replace by reference to the new class
		CtType templateModel = ModelUtils.buildClass(AClassWithMethodsAndRefs.class);
		Factory factory = templateModel.getFactory();
		Pattern pattern = PatternBuilder.create(
				(CtMethod) templateModel.getMethodsByName("foo").get(0),
				templateModel.getNestedType("Local"))
				//switch ON: generate by comments
				.setAddGeneratedBy(true)
				.build();
		final String newQName = "spoon.test.generated.ACloneOfAClassWithMethodsAndRefs";
		
		CtClass<?> generatedType = factory.createClass(newQName);
		
		assertNotNull(generatedType);
		assertEquals(newQName, generatedType.getQualifiedName());
		assertEquals("ACloneOfAClassWithMethodsAndRefs", generatedType.getSimpleName());

		pattern.applyToType(generatedType, CtMethod.class, Collections.emptyMap());
		//contract: new method and interface were added
		assertEquals(Arrays.asList("Local","foo"),
				generatedType.getTypeMembers().stream().map(CtTypeMember::getSimpleName).collect(Collectors.toList()));
		assertEquals(1, generatedType.getMethodsByName("foo").size());
		assertNotNull(generatedType.getNestedType("Local"));
		//contract: generate by comments are appended
		assertEquals("Generated by spoon.test.template.testclasses.types.AClassWithMethodsAndRefs#foo(AClassWithMethodsAndRefs.java:30)",
				generatedType.getMethodsByName("foo").get(0).getDocComment().trim());
		assertEquals("Generated by spoon.test.template.testclasses.types.AClassWithMethodsAndRefs$Local(AClassWithMethodsAndRefs.java:26)",
				generatedType.getNestedType("Local").getDocComment().trim());
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
		generatedType.filterChildren(new TypeFilter<>(CtExecutableReference.class)).forEach((CtExecutableReference execRef) ->{
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
	public void testInlineStatementsBuilder() throws Exception {
		// TODO: specify what InlineStatementsBuilder does
	}

	@Test
	public void testTemplateMatchOfMultipleElements() throws Exception {
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
	public void testExtensionDecoupledSubstitutionVisitor() throws Exception {
		//contract: substitution can be done on model, which is not based on Template
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
				.createPatternParameters()
				.build();
		final List<CtMethod> aMethods = pattern.applyToType(aTargetType, CtMethod.class, params);
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

}
