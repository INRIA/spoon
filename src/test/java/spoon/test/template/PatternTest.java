package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
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
			assertEquals(Arrays.asList("java.lang.System.out.println(value)"), listToListOfStrings(match.getMatchingElements()));
			//FIX IT
//			assertEquals(Arrays.asList(""), listToListOfStrings((List) match.getParameters().getValue("values")));
		}
		{
			Match match = matches.get(1);
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(\"a\")",
					"java.lang.System.out.println(\"Xxxx\")",
					"java.lang.System.out.println(((java.lang.String) (null)))",
					"java.lang.System.out.println(java.lang.Long.class.toString())"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(Arrays.asList(
					"\"a\"",
					"\"Xxxx\"",
					"((java.lang.String) (null))",
					"java.lang.Long.class.toString()"), listToListOfStrings((List) match.getParameters().getValue("values")));
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
			assertEquals(Arrays.asList("int var = 0"), listToListOfStrings(match.getMatchingElements()));
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
					"cc++"), listToListOfStrings(match.getMatchingElements()));

			// correctly matching the outer parameter
			assertEquals("cc", match.getParameters().getValue("varName"));
		}
		{
			Match match = matches.get(2);
			assertEquals(Arrays.asList(
					"int dd = 0",
					"java.lang.System.out.println(java.lang.Long.class.toString())",
					"dd++"), listToListOfStrings(match.getMatchingElements()));

			// correctly matching the outer parameter
			assertEquals("dd", match.getParameters().getValue("varName"));
		}
	}

	@Test
	public void testMatchIfElse() throws Exception {
		//contract: inline switch Pattern can match one of the models
		CtType<?> ctClass = ModelUtils.buildClass(MatchIfElse.class);

		CtType<?> type = ctClass.getFactory().Type().get(MatchIfElse.class);
		Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("matcher1").getPatternElements())
				.configureParameters(pb -> {
					pb.parameter("option").byVariable("option");
					pb.parameter("option2").byVariable("option2");
					pb.parameter("value").byFilter(new TypeFilter(CtLiteral.class));
				})
				//we have to configure inline statements after all expressions
				//of combined if statement are marked as pattern parameters
				.configureInlineStatements(lsb -> lsb.byVariableName("option"))
				.build();

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		assertEquals(7, matches.size());
		{
			Match match = matches.get(0);
			assertEquals(Arrays.asList("java.lang.System.out.println(i)"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(false, match.getParameters().getValue("option"));
			assertEquals(true, match.getParameters().getValue("option2"));
			assertEquals("i", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(1);
			assertEquals(Arrays.asList("java.lang.System.out.println(\"a\")"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(true, match.getParameters().getValue("option"));
			assertEquals(false, match.getParameters().getValue("option2"));
			assertEquals("\"a\"", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(2);
			assertEquals(Arrays.asList("java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(true, match.getParameters().getValue("option"));
			assertEquals(false, match.getParameters().getValue("option2"));
			assertEquals("\"Xxxx\"", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(3);
			assertEquals(Arrays.asList("java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(true, match.getParameters().getValue("option"));
			assertEquals(false, match.getParameters().getValue("option2"));
			assertEquals("((java.lang.String) (null))", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(4);
			assertEquals(Arrays.asList("java.lang.System.out.println(2018)"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(false, match.getParameters().getValue("option"));
			assertEquals(true, match.getParameters().getValue("option2"));
			assertEquals("2018", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(5);
			assertEquals(Arrays.asList("java.lang.System.out.println(java.lang.Long.class.toString())"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(true, match.getParameters().getValue("option"));
			assertEquals(false, match.getParameters().getValue("option2"));
			assertEquals("java.lang.Long.class.toString()", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(6);
			assertEquals(Arrays.asList("java.lang.System.out.println(3.14)"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(false, match.getParameters().getValue("option"));
			assertEquals(false, match.getParameters().getValue("option2"));
			assertEquals("3.14", match.getParameters().getValue("value").toString());
		}
	}
	@Test
	public void testGenerateMultiValues() throws Exception {
		// todo: what's the tested contract?
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Pattern pattern = MatchMultiple.createPattern(null, null, null);
		Map<String, Object> params = new HashMap<>();
		params.put("printedValue", "does it work?");
		params.put("statements", ctClass.getMethodsByName("testMatch1").get(0).getBody().getStatements().subList(0, 3));
		List<CtStatement> generated = pattern.substituteList(ctClass.getFactory(), CtStatement.class, params);
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"does it work?\")"), generated.stream().map(Object::toString).collect(Collectors.toList()));
	}

	@Test
	public void testMatchGreedyMultiValueUnlimited() throws Exception {
		//contract: multivalue parameter can match multiple nodes into list of parameter values.
		//contract: default greedy matching eats everything but can leave some matches if it is needed to match remaining template parameters
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);

		Pattern pattern = MatchMultiple.createPattern(null, null, null);

		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));

		assertEquals(1, matches.size());
		Match match = matches.get(0);
		//check all statements are matched
		// TODO: why all statements are matched? AFAIU, nothing in the pattern definition says "any kind of statement"
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"Xxxx\")",
				"java.lang.System.out.println(((java.lang.String) (null)))",
				"java.lang.System.out.println(\"last one\")"), listToListOfStrings(match.getMatchingElements()));

		//check all statements excluding last are stored as value of "statements" parameter
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"Xxxx\")",
				"java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings((List) match.getParameters().getValue("statements")));

		//last statement is matched by last template, which saves printed value
		assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
		assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
	}

	@Test
	public void testMatchGreedyMultiValueMaxCountLimit() throws Exception {
		//contract: default greedy matching eats everything until max count = 3
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
			), listToListOfStrings(match.getMatchingElements()));

			//check 3 statements are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)"), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//4th statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(1);
			//check remaining next 2 statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))",
					"java.lang.System.out.println(\"last one\")"), listToListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
		}
	}


	@Test
	public void testMatchReluctantMultivalue() throws Exception {
		//contract: multivalue parameter can match multiple nodes into list of parameter values.
		//contract: reluctant matches only minimal amount
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
					"java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)"), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(1);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("((java.lang.String) (null))", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(2);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(\"last one\")"), listToListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
		}
	}
	@Test
	public void testMatchReluctantMultivalueMinCount1() throws Exception {
		//contract: multivalue parameter can match multiple nodes into list of parameter values.
		//contract: reluctant matches only at least 1 node in this case
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
					"java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"int i = 0",
					"i++",
					"java.lang.System.out.println(i)"), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
		{
			Match match = matches.get(1);
			//check all statements are matched
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))",
					"java.lang.System.out.println(\"last one\")"), listToListOfStrings(match.getMatchingElements()));

			//check all statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"last one\"", match.getParameters().getValue("printedValue").toString());
		}
	}
	@Test
	public void testMatchReluctantMultivalueExactly2() throws Exception {
		//contract: multivalue parameter can match multiple nodes into list of parameter values.
		//contract: reluctant matches min 2 and max 2 nodes in this case
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
					"java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings(match.getMatchingElements()));

			//check 2 statements excluding last are stored as value of "statements" parameter
			assertEquals(Arrays.asList(
					"i++",
					"java.lang.System.out.println(i)"), listToListOfStrings((List) match.getParameters().getValue("statements")));
			//last statement is matched by last template, which saves printed value
			assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("printedValue").toString());
		}
	}

	@Test
	public void testMatchPossesiveMultiValueUnlimited() throws Exception {
		//contract: multivalue parameter can match multiple nodes into list of parameter values.
		//contract: possessive matching eats everything and never returns back
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
				"java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings(match.getMatchingElements()));

		//check 4 statements excluding last are stored as value of "statements" parameter
		assertEquals(Arrays.asList(
				"int i = 0",
				"i++",
				"java.lang.System.out.println(i)",
				"java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings((List) match.getParameters().getValue("statements")));
		//last statement is matched by last template, which saves printed value
		assertTrue(match.getParameters().getValue("printedValue") instanceof CtLiteral);
		assertEquals("((java.lang.String) (null))", match.getParameters().getValue("printedValue").toString());
	}
	@Test
	public void testMatchPossesiveMultiValueMinCount() throws Exception {
		//contract: check possessive matching with min count limit and GREEDY back off
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple3.class);
		for (int i = 0; i < 7; i++) {
			final int count = i;
			Pattern pattern = MatchMultiple3.createPattern(ctClass.getFactory(), pb -> {
				pb.parameter("statements1").setMatchingStrategy(Quantifier.GREEDY);
				pb.parameter("statements2").setMatchingStrategy(Quantifier.POSSESSIVE).setMinOccurence(count).setMaxOccurence(count);
			});

			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
			if (count < 6) {
				//the last template has nothing to match -> no match
				assertEquals("count="+count, 1, matches.size());
				assertEquals("count="+count, 5-count, getSize(matches.get(0).getParameters().getValue("statements1")));
				assertEquals("count="+count, count, getSize(matches.get(0).getParameters().getValue("statements2")));
			} else {
				//the possessive matcher eat too much. There is no target element for last `printedValue` variable
				assertEquals("count="+count, 0, matches.size());
			}
		}
	}

	@Test
	public void testMatchPossesiveMultiValueMinCount2() throws Exception {
		//contract: check possessive matching with min count limit and GREEDY back off
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
				assertEquals("count="+count, 4-count, getSize(matches.get(0).getParameters().getValue("statements1")));
				assertEquals("count="+count, count, getSize(matches.get(0).getParameters().getValue("statements2")));
				assertEquals("count="+count, 2, getSize(matches.get(0).getParameters().getValue("printedValue")));
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
				assertEquals("count="+count, Math.max(0, 3-count), getSize(matches.get(0).getParameters().getValue("statements1")));
				assertEquals("count="+count, count - Math.max(0, count-4), getSize(matches.get(0).getParameters().getValue("statements2")));
				assertEquals("count="+count, Math.max(2, 3 - Math.max(0, count-3)), getSize(matches.get(0).getParameters().getValue("printedValue")));
			} else {
				//the possessive matcher eat too much. There is no target element for last `printedValue` variable
				assertEquals("count="+count, 0, matches.size());
			}
		}
	}

	private int getSize(Object o) {
		if (o instanceof List) {
			return ((List) o).size();
		}
		if (o == null) {
			return 0;
		}
		fail("Unexpected object of type " + o.getClass());
		return -1;
	}

	@Test
	public void testMatchParameterValue() throws Exception {
		//contract: by default the parameter value is the reference to real node from the model
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterType.class);

		Pattern pattern = MatchWithParameterType.createPattern(ctClass.getFactory(), null);

		List<Match> matches = pattern.getMatches(ctClass);

		assertEquals(5, matches.size());
		{
			Match match = matches.get(0);
			assertEquals(Arrays.asList("java.lang.System.out.println(value)"), listToListOfStrings(match.getMatchingElements()));
			Object value = match.getParameters().getValue("value");
			assertTrue(value instanceof CtVariableRead);
			assertEquals("value", value.toString());
			//contract: the value is reference to found node (not a clone)
			assertTrue(((CtElement)value).isParentInitialized());
			assertSame(CtRole.ARGUMENT, ((CtElement)value).getRoleInParent());
		}
		{
			Match match = matches.get(1);
			assertEquals(Arrays.asList("java.lang.System.out.println(\"a\")"), listToListOfStrings(match.getMatchingElements()));
			assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
			assertEquals("\"a\"", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(2);
			assertEquals(Arrays.asList("java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings(match.getMatchingElements()));
			assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
			assertEquals("\"Xxxx\"", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(3);
			assertEquals(Arrays.asList("java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings(match.getMatchingElements()));
			assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
			assertEquals("((java.lang.String) (null))", match.getParameters().getValue("value").toString());
		}
		{
			Match match = matches.get(4);
			assertEquals(Arrays.asList("java.lang.System.out.println(java.lang.Long.class.toString())"), listToListOfStrings(match.getMatchingElements()));
			assertTrue(match.getParameters().getValue("value") instanceof CtInvocation);
			assertEquals("java.lang.Long.class.toString()", match.getParameters().getValue("value").toString());
		}
	}

	@Test
	public void testMatchParameterValueType() throws Exception {
		//contract: the parameter value type matches only values of required type
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterType.class);
		{
			Pattern pattern = MatchWithParameterType.createPattern(ctClass.getFactory(), CtLiteral.class);

			List<Match> matches = pattern.getMatches(ctClass);

			assertEquals(3, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"a\")"), listToListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"a\"", match.getParameters().getValue("value").toString());
			}
			{
				Match match = matches.get(1);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"Xxxx\"", match.getParameters().getValue("value").toString());
			}
			{
				Match match = matches.get(2);
				assertEquals(Arrays.asList("java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("((java.lang.String) (null))", match.getParameters().getValue("value").toString());
			}
		}
		{
			Pattern pattern = MatchWithParameterType.createPattern(ctClass.getFactory(), CtInvocation.class);

			List<Match> matches = pattern.getMatches(ctClass);

			assertEquals(1, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(Arrays.asList("java.lang.System.out.println(java.lang.Long.class.toString())"), listToListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtInvocation);
				assertEquals("java.lang.Long.class.toString()", match.getParameters().getValue("value").toString());
			}

		}
	}

	@Test
	public void testMatchParameterCondition() throws Exception {
		//contract: the parameter value matching condition causes that only matching parameter values are accepted
		//if the value isn't matching then node is not matched
		CtType<?> ctClass = ModelUtils.buildClass(MatchWithParameterCondition.class);
		{
			Pattern pattern = MatchWithParameterCondition.createPattern(ctClass.getFactory(), (Object value) -> value instanceof CtLiteral);

			List<Match> matches = pattern.getMatches(ctClass);

			assertEquals(3, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"a\")"), listToListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"a\"", match.getParameters().getValue("value").toString());
			}
			{
				Match match = matches.get(1);
				assertEquals(Arrays.asList("java.lang.System.out.println(\"Xxxx\")"), listToListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("\"Xxxx\"", match.getParameters().getValue("value").toString());
			}
			{
				Match match = matches.get(2);
				assertEquals(Arrays.asList("java.lang.System.out.println(((java.lang.String) (null)))"), listToListOfStrings(match.getMatchingElements()));
				assertTrue(match.getParameters().getValue("value") instanceof CtLiteral);
				assertEquals("((java.lang.String) (null))", match.getParameters().getValue("value").toString());
			}
		}
	}

	@Test
	public void testMatchOfAttribute() throws Exception {
		//contract: match some nodes like template, but with some variable attributes
		CtType<?> ctClass = ModelUtils.buildClass(MatchModifiers.class);
		{
			//match all methods with arbitrary name, modifiers, parameters, but with empty body and return type void
			Pattern pattern = MatchModifiers.createPattern(ctClass.getFactory(), false);
			List<Match> matches = pattern.getMatches(ctClass);
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
			Pattern pattern = MatchModifiers.createPattern(ctClass.getFactory(), true);
			List<Match> matches = pattern.getMatches(ctClass);
			assertEquals(4, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("matcher1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(4, match.getParametersMap().size());
				assertEquals("matcher1", match.getParametersMap().get("methodName"));
				assertEquals(new HashSet<>(Arrays.asList(ModifierKind.PUBLIC)), match.getParametersMap().get("modifiers"));
				assertEquals(Arrays.asList(), match.getParametersMap().get("parameters"));
				assertEquals(Arrays.asList(), match.getParametersMap().get("statements"));
			}
			{
				Match match = matches.get(1);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("publicStaticMethod", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(4, match.getParametersMap().size());
				assertEquals("publicStaticMethod", match.getParametersMap().get("methodName"));
				assertEquals(new HashSet<>(Arrays.asList(ModifierKind.PUBLIC, ModifierKind.STATIC)), match.getParametersMap().get("modifiers"));
				assertEquals(Arrays.asList(), match.getParametersMap().get("parameters"));
				assertEquals(Arrays.asList(), match.getParametersMap().get("statements"));
			}
			{
				Match match = matches.get(2);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("packageProtectedMethodWithParam", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(4, match.getParametersMap().size());
				assertEquals("packageProtectedMethodWithParam", match.getParametersMap().get("methodName"));
				assertEquals(new HashSet<>(), match.getParametersMap().get("modifiers"));
				assertEquals(2, ((List) match.getParametersMap().get("parameters")).size());
				assertEquals(Arrays.asList(), match.getParametersMap().get("statements"));
			}
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
		//contract: match a pattern with an "open" annotation (different values can be matched)
		CtType<?> matchMapClass = ModelUtils.buildClass(MatchMap.class);
		{
			CtType<?> type = matchMapClass.getFactory().Type().get(MatchMap.class);
			// create a pattern from method matcher1
			//match all methods with arbitrary name, and annotation @Check, parameters, but with empty body and return type void
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
			//match all methods with arbitrary name, with any annotation set, Test modifiers, parameters, but with empty body and return type void
			Pattern pattern = PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("matcher1").getPatternElements())
					.configureParameters(pb -> {
						//match any value of @Check annotation to parameter `testAnnotations`
						//match any method name
						pb.parameter("methodName").byString("matcher1");
						//match on all annotations of method
						pb.parameter("allAnnotations")
								.setConflictResolutionMode(ConflictResolutionMode.APPEND)
								.byRole(new TypeFilter<>(CtMethod.class), CtRole.ANNOTATION)
						;
						pb.parameter("CheckAnnotationValues").byRole(new TypeFilter(CtAnnotation.class), CtRole.VALUE).setContainerKind(ContainerKind.MAP);
					})
					.build();
			List<Match> matches = pattern.getMatches(ctClass);

			// we match all methods
			assertEquals(4, matches.size());
			// the new ones is the one with deprecated
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
		//contract: match substring in key of Map Entry - match key of annotation value
		CtType<?> ctClass = ModelUtils.buildClass(MatchMap.class);
		{
			//match all methods with arbitrary name, and Annotation Test modifiers, parameters, but with empty body and return type void
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
		//contract: match elements in container of type Set - e.g method throwables
		CtType<?> ctClass = ModelUtils.buildClass(MatchThrowables.class);
		Factory f = ctClass.getFactory();
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
			assertEquals(new HashSet(Arrays.asList(
					"modifiers","methodName","parameters","statements")), match.getParametersMap().keySet());
		}
		{
			Match match = matches.get(1);
			assertEquals(1, match.getMatchingElements().size());
			assertEquals("sample2", match.getMatchingElement(CtMethod.class).getSimpleName());
			assertEquals(new HashSet(Arrays.asList(
					"otherThrowables", "modifiers","methodName","parameters","statements")), match.getParametersMap().keySet());
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
					"otherThrowables", "modifiers","methodName","parameters","statements")), match.getParametersMap().keySet());
			assertEquals(new HashSet(Arrays.asList(
					"java.lang.IllegalArgumentException")),
					((Set<CtTypeReference<?>>) match.getParameters().getValue("otherThrowables"))
							.stream().map(e->e.toString()).collect(Collectors.toSet()));
		}
		{
			Match match = matches.get(3);
			assertEquals(1, match.getMatchingElements().size());
			assertEquals("sample4", match.getMatchingElement(CtMethod.class).getSimpleName());
			assertEquals(new HashSet(Arrays.asList(
					"modifiers","methodName","parameters","statements")), match.getParametersMap().keySet());
		}
	}

	private List<String> listToListOfStrings(List<? extends Object> list) {
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
		//contract: all the parameters of Pattern are available
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPatternFromOldPattern(f);
		Map<String, ParameterInfo> parameterInfos = p.getParameterInfos();

		// the code in createPatternFromOldPattern creates 15 pattern parameters
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
		Pattern p = OldPattern.createPatternFromOldPattern(f);
		String strOfPattern = p.toString();
		
		Map<String, ParameterInfo> parameterInfos = p.getParameterInfos();
		assertEquals(15, parameterInfos.size());

		// contract: all parameters from getParameterInfos are pretty-printed
		for (Map.Entry<String, ParameterInfo> e : parameterInfos.entrySet()) {
			assertTrue("The parameter " + e.getKey() + " is missing", strOfPattern.indexOf("<= ${"+e.getKey()+"}")>=0);
		}
	}

	@Test
	public void testMatchSample1() throws Exception {
		// contract: a complex pattern is well matched twice
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
		);
		CtClass<?> classDJPP = f.Class().get(DPPSample1.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());

		CtType<Object> type = f.Type().get(OldPattern.class);
		Pattern p = PatternBuilder
				//Create a pattern from all statements of OldPattern_ParamsInNestedType#patternModel
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

		// there are two results (the try-with-resource in each method
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
		Pattern oldPattern = OldPattern.createPatternFromOldPattern(f);
		
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
}
