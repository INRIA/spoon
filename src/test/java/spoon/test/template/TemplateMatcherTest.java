package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import spoon.pattern.Pattern;
import spoon.pattern.matcher.Match;
import spoon.pattern.matcher.Quantifier;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.pattern.parameter.UnmodifiableParameterValueProvider;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.test.template.testclasses.match.MatchForEach;
import spoon.test.template.testclasses.match.MatchForEach2;
import spoon.test.template.testclasses.match.MatchIfElse;
import spoon.test.template.testclasses.match.MatchMap;
import spoon.test.template.testclasses.match.MatchModifiers;
import spoon.test.template.testclasses.match.MatchMultiple;
import spoon.test.template.testclasses.match.MatchMultiple2;
import spoon.test.template.testclasses.match.MatchMultiple3;
import spoon.test.template.testclasses.match.MatchWithParameterCondition;
import spoon.test.template.testclasses.match.MatchWithParameterType;
import spoon.testing.utils.ModelUtils;

public class TemplateMatcherTest {

	@Test
	public void testMatchForeach() throws Exception {
		//contract: live foreach template can match multiple models into list of parameter values
		CtType<?> ctClass = ModelUtils.buildClass(MatchForEach.class);
		
		Pattern pattern = MatchForEach.createPattern(ctClass.getFactory());
		
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
		//contract: live foreach template can match multiple models into list of parameter values including outer parameters
		CtType<?> ctClass = ModelUtils.buildClass(MatchForEach2.class);
		
		Pattern pattern = MatchForEach2.createPattern(ctClass.getFactory());
		
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
			assertEquals(Arrays.asList(
					"\"Xxxx\"",
					"((java.lang.String) (null))"), listToListOfStrings((List) match.getParameters().getValue("values")));
		}
		{
			Match match = matches.get(2);
			assertEquals(Arrays.asList(
					"int dd = 0",
					"java.lang.System.out.println(java.lang.Long.class.toString())",
					"dd++"), listToListOfStrings(match.getMatchingElements()));
			assertEquals(Arrays.asList(
					"java.lang.Long.class.toString()"), listToListOfStrings((List) match.getParameters().getValue("values")));
		}
	}

	@Test
	public void testMatchIfElse() throws Exception {
		//contract: live switch Pattern can match one of the models
		CtType<?> ctClass = ModelUtils.buildClass(MatchIfElse.class);
		
		Pattern pattern = MatchIfElse.createPattern(ctClass.getFactory());
		
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
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), null, null, null);
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
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), null, null, null);
		
		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0));
		
		assertEquals(1, matches.size());
		Match match = matches.get(0);
		//check all statements are matched
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
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), null, null, 3);
		
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
		
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), Quantifier.RELUCTANT, null, null);
		
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
		
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), Quantifier.RELUCTANT, 1, null);
		
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
		
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), Quantifier.RELUCTANT, 2, 2);
		
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
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), Quantifier.POSSESSIVE, null, null);
		
		List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("testMatch1").get(0).getBody());
		//the last template has nothing to match -> no match
		assertEquals(0, matches.size());
	}
	@Test
	public void testMatchPossesiveMultiValueMaxCount4() throws Exception {
		//contract: multivalue parameter can match multiple nodes into list of parameter values.
		//contract: possessive matching eats everything and never returns back
		CtType<?> ctClass = ModelUtils.buildClass(MatchMultiple.class);
		Pattern pattern = MatchMultiple.createPattern(ctClass.getFactory(), Quantifier.POSSESSIVE, null, 4);
		
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
		//contract: match attribute of type Map - annotations
		CtType<?> ctClass = ModelUtils.buildClass(MatchMap.class);
		{
			//match all methods with arbitrary name, and Annotation Test modifiers, parameters, but with empty body and return type void 
			Pattern pattern = MatchMap.createPattern(ctClass.getFactory(), false);
			List<Match> matches = pattern.getMatches(ctClass);
//			List<Match> matches = pattern.getMatches(ctClass.getMethodsByName("matcher1").get(0));
			assertEquals(3, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("matcher1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("matcher1", match.getParametersMap().get("methodName"));
				Map<String, Object> values = getMap(match, "CheckAnnotationValues");
				assertEquals(0, values.size());
			}
			{
				Match match = matches.get(1);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("m1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("m1", match.getParametersMap().get("methodName"));
				Map<String, Object> values = getMap(match, "CheckAnnotationValues");
				assertEquals(1, values.size());
				assertEquals("\"xyz\"", values.get("value").toString());
			}
			{
				Match match = matches.get(2);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("m2", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("m2", match.getParametersMap().get("methodName"));
				Map<String, Object> values = getMap(match, "CheckAnnotationValues");
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
		//contract: match attribute of type Map - annotations
		CtType<?> ctClass = ModelUtils.buildClass(MatchMap.class);
		{
			//match all methods with arbitrary name, and Annotation Test modifiers, parameters, but with empty body and return type void 
			Pattern pattern = MatchMap.createPattern(ctClass.getFactory(), true);
			List<Match> matches = pattern.getMatches(ctClass);
			assertEquals(4, matches.size());
			{
				Match match = matches.get(0);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("matcher1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("matcher1", match.getParametersMap().get("methodName"));
				assertEquals(map(), getMap(match, "CheckAnnotationValues"));
			}
			{
				Match match = matches.get(1);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("m1", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("m1", match.getParametersMap().get("methodName"));
				assertEquals("{value=\"xyz\"}", getMap(match, "CheckAnnotationValues").toString());
			}
			{
				Match match = matches.get(2);
				assertEquals(1, match.getMatchingElements().size());
				assertEquals("m2", match.getMatchingElement(CtMethod.class).getSimpleName());
				assertEquals(2, match.getParametersMap().size());
				assertEquals("m2", match.getParametersMap().get("methodName"));
				assertEquals("{value=\"abc\", timeout=123}", getMap(match, "CheckAnnotationValues").toString());
			}
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
			Pattern pattern = MatchMap.createMatchKeyPattern(ctClass.getFactory());
			List<Match> matches = pattern.getMatches(ctClass);
			String str = pattern.toString();
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
} 
