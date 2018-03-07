package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import spoon.pattern.Pattern;
import spoon.pattern.parameter.ParameterInfo;
import spoon.reflect.factory.Factory;
import spoon.test.template.testclasses.replace.OldPattern;
import spoon.testing.utils.ModelUtils;

public class PatternTest {

	@Test
	public void testPatternParameters() {
		//contract: all the parameters of Pattern are available
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPattern(f);
		Map<String, ParameterInfo> parameterInfos = p.getParameterInfos();
		
		assertEquals(15, parameterInfos.size());
		assertEquals(new HashSet<>(Arrays.asList("next","item","startPrefixSpace","printer","start",
				"statements","nextPrefixSpace","startSuffixSpace","elementPrinterHelper",
				"endPrefixSpace","startKeyword","useStartKeyword","end","nextSuffixSpace","getIterable"
				)), parameterInfos.keySet());
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
		Pattern p = OldPattern.createPattern(f);
		String strOfPattern = p.toString();
		
		Map<String, ParameterInfo> parameterInfos = p.getParameterInfos();
		assertEquals(15, parameterInfos.size());
		for (Map.Entry<String, ParameterInfo> e : parameterInfos.entrySet()) {
			assertTrue("The parameter " + e.getKey() + " is missing", strOfPattern.indexOf("<= ${"+e.getKey()+"}")>=0);
		}
	}
}
