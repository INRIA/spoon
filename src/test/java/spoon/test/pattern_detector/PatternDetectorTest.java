package spoon.test.pattern_detector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import spoon.pattern.Match;
import spoon.pattern_detector.FoundPattern;
import spoon.pattern_detector.PatternDetector;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.test.pattern_detector.testclasses.Kopernik;
import spoon.test.pattern_detector.testclasses.TargetType_1;
import spoon.test.pattern_detector.testclasses.TargetType_2;
import spoon.testing.utils.ModelUtils;

public class PatternDetectorTest {

	@Test
	public void testDetectPatternFromOneElement() throws Exception {
		//contract: one element produces pattern with no parameters
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		CtMethod<?> method = type.getNestedType("A").getMethodsByName("mars").get(0);

		PatternDetector pd = new PatternDetector();
		pd.matchCode(method);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(1, detectedPattern.getCountOfMatches());
		//there is no pattern parameter
		assertTrue(detectedPattern.getPattern().getParameterInfos().isEmpty());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method));
	}

	@Test
	public void testDetectPatternFromTwoMethodsWithDifferentNames() throws Exception {
		//contract: two similar methods with different method and parameter names produces pattern with 2 parameters
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("A").getMethodsByName("mars").get(0);
		CtMethod<?> method2 = type.getNestedType("B").getMethodsByName("saturn").get(0);
		
		PatternDetector pd = new PatternDetector();
		pd.matchCode(method1);
		pd.matchCode(method2);
		List<FoundPattern> detectedPatterns = pd.getPatterns();

		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(2, detectedPattern.getCountOfMatches());
		assertEquals(3, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
	}
	
	@Test
	public void testDetectPatternFromThreeMethodsWithDifferentNames() throws Exception {
		//contract: three similar methods with different method and parameter names produces pattern with 2 parameters
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("A").getMethodsByName("mars").get(0);
		CtMethod<?> method2 = type.getNestedType("B").getMethodsByName("saturn").get(0);
		CtMethod<?> method3 = type.getNestedType("C").getMethodsByName("merkur").get(0);
		
		PatternDetector pd = new PatternDetector();
		pd.matchCode(method1);
		pd.matchCode(method2);
		pd.matchCode(method3);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(3, detectedPattern.getCountOfMatches());
		assertEquals(3, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method3));
	}

	@Test
	public void testDetectPatternFromThreeMethodsWithDifferentNamesLastMethodBringsMoreChanges() throws Exception {
		//contract: three similar methods with different method and parameter names produces pattern with parameters
		//last method needs different parameters then second method
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("A").getMethodsByName("mars").get(0);
		CtMethod<?> method2 = type.getNestedType("B").getMethodsByName("saturn").get(0);
		CtMethod<?> method3 = type.getNestedType("D").getMethodsByName("mars").get(0);
		
		PatternDetector pd = new PatternDetector();
		pd.matchCode(method1);
		pd.matchCode(method2);
		pd.matchCode(method3);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(3, detectedPattern.getCountOfMatches());
		assertEquals(4, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method3));
	}

	@Test
	public void testDetectPatternFromDifferentExpressions() throws Exception {
		//contract: pattern detected from `return x` and  `return x + 0` - different size of AST
		//last method needs different parameters then second method
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("A").getMethodsByName("mars").get(0);
		CtMethod<?> method2 = type.getNestedType("E").getMethodsByName("mars").get(0);
		
		PatternDetector pd = new PatternDetector();
		pd.matchCode(method1);
		pd.matchCode(method2);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(2, detectedPattern.getCountOfMatches());
		assertEquals(1, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
	}

	@Test
	public void testEachLiteralHasOwnParameter() throws Exception {
		//contract: nodes with same value may share parameter as long as all values are same
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		
		PatternDetector pd = new PatternDetector();
		
		CtMethod<?> method1 = type.getNestedType("Literals").getMethodsByName("m").get(0);
		pd.matchCode(method1);
		CtMethod<?> method2 = type.getNestedType("Literals_Same").getMethodsByName("m").get(0);
		pd.matchCode(method2);

		{	//pattern shares parameters because all code fragments has same values
			List<FoundPattern> detectedPatterns = pd.getPatterns();
			assertEquals(1, detectedPatterns.size());
			FoundPattern detectedPattern = detectedPatterns.get(0);
			assertEquals(2, detectedPattern.getCountOfMatches());
			assertEquals(1, detectedPattern.getPattern().getParameterInfos().size());
			checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
			checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
		}

		CtMethod<?> method3 = type.getNestedType("Literals_Different").getMethodsByName("m").get(0);
		pd.matchCode(method3);
		{
			List<FoundPattern> detectedPatterns = pd.getPatterns();
			assertEquals(1, detectedPatterns.size());
			FoundPattern detectedPattern = detectedPatterns.get(0);
			assertEquals(3, detectedPattern.getCountOfMatches());
			assertEquals(3, detectedPattern.getPattern().getParameterInfos().size());
			checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
			checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
			checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method3));
		}
		
	}
	
	@Test
	public void testReturnFieldOfDifferentType() throws Exception {
		//contract: literals have own parameter. Never use shared variable for them
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("ReturnField_1").getMethodsByName("getA").get(0);
		CtMethod<?> method2 = type.getNestedType("ReturnField_2").getMethodsByName("getB").get(0);
		
		PatternDetector pd = new PatternDetector();
		pd.matchCode(method1);
		pd.matchCode(method2);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(2, detectedPattern.getCountOfMatches());
		assertEquals(4, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
	}

	@Test
	public void testTargetType() throws Exception {
		//contract: The targetType parameter doesn't breaks detected pattern
		Factory factory = ModelUtils.build(TargetType_1.class, TargetType_2.class);
		CtMethod<?> method1 = factory.Type().get(TargetType_1.class).getMethodsByName("call").get(0);
		CtMethod<?> method2 = factory.Type().get(TargetType_2.class).getMethodsByName("call").get(0);
		
		PatternDetector pd = new PatternDetector();
		//method1 uses targetType for all type references to TargetType_1 
		pd.matchCode(method1);
		//method2 reference to TargetType_1 can use parameter targetType which is TargetType_2  
		pd.matchCode(method2);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(2, detectedPattern.getCountOfMatches());
		assertEquals(1, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
	}

	@Test
	public void testIgnoreThisAccess() throws Exception {
		//contract: The `this.field` and `field` is understood as one access to the field
		CtType<?> type = ModelUtils.buildClass(Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("ThisAccess_1").getMethodsByName("thisAccess").get(0);
		CtMethod<?> method2 = type.getNestedType("ThisAccess_2").getMethodsByName("thisAccess").get(0);
		
		PatternDetector pd = new PatternDetector();
		pd.matchCode(method1);
		pd.matchCode(method2);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(2, detectedPattern.getCountOfMatches());
		assertEquals(1, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
	}

	@Test
	public void testIgnoreComments() throws Exception {
		//contract: The comments can be ignored during pattern detection
		CtType<?> type = ModelUtils.buildClass(launcher -> {
			launcher.getEnvironment().setCommentEnabled(true);
		}, Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("Comments_1").getMethodsByName("comment").get(0);
		CtMethod<?> method2 = type.getNestedType("Comments_2").getMethodsByName("comment").get(0);
		
		PatternDetector pd = new PatternDetector().setIgnoreComments(true);
		pd.matchCode(method1);
		pd.matchCode(method2);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		assertEquals(2, detectedPattern.getCountOfMatches());
		assertEquals(0, detectedPattern.getPattern().getParameterInfos().size());
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		/*
		 	the method with comments doesn't matches the pattern without comments - OK for now.
			We do not enforce this behavior as test, because it is not wanted, but understandable
		
		checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
		*/
	}

	@Test
	public void testSeeComments() throws Exception {
		//contract: The comments can be used in pattern detection too
		CtType<?> type = ModelUtils.buildClass(launcher -> {
			launcher.getEnvironment().setCommentEnabled(true);
		}, Kopernik.class);
		CtMethod<?> method1 = type.getNestedType("Comments_1").getMethodsByName("comment").get(0);
		CtMethod<?> method2 = type.getNestedType("Comments_2").getMethodsByName("comment").get(0);
		
		PatternDetector pd = new PatternDetector();
		pd.matchCode(method1);
		pd.matchCode(method2);
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		
		assertEquals(2, detectedPatterns.size());
	}

	public static void checkPatternMatchesCodeAndGeneratesSameCode(FoundPattern pattern, List<? extends CtElement> code) {
		//1) match code using pattern
		List<Match> matches = new ArrayList<>();
		pattern.getPattern().forEachMatch(code, match -> matches.add(match));
		//2) there is exactly one match
		assertEquals(1, matches.size());
		Match match = matches.get(0);
		//2) generate node code using pattern and match parameters
		List<CtElement> generatedFromPattern = pattern.getPattern().generator().generate(CtElement.class, match.getParameters());
		//sanity check that code is really generated
		assertNotSame(code.get(0), generatedFromPattern.get(0));
		//check that code and generated code are same
		assertEquals(code, generatedFromPattern);
	}
}
