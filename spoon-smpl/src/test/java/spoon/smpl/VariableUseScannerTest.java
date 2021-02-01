package spoon.smpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static spoon.smpl.TestUtils.*;

public class VariableUseScannerTest {
	@Test
	public void testScanExplicitVariableDeclaration() {

		// contract: VariableUseScanner should find variable names used in variable declarations

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x = 1;\n" +
										 "    int y = 2;\n" +
										 "}\n");

		Map<String, CtElement> result = new VariableUseScanner(method).getResult();

		assertEquals(2, result.keySet().size());
		assertEquals("int x = 1", result.get("x").toString());
		assertEquals("int y = 2", result.get("y").toString());
	}

	@Test
	public void testScanExplicitVariableAccesses() {

		// contract: VariableUseScanner should find variable names used in variable accesses

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x;\n" +
										 "    x = 10;\n" +
										 "    y = 20;\n" +
										 "}\n");

		Map<String, CtElement> result = new VariableUseScanner(method).getResult();

		assertEquals(2, result.keySet().size());
		assertEquals("x", result.get("x").toString());
		assertEquals("y", result.get("y").toString());
	}

	@Test
	public void testScanTypeNameWithoutKnownVariables() {

		// contract: VariableUseScanner should NOT find type names not indicated to be known variable names

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    A x;\n" +
										 "    bar(B);\n" +
										 "}\n");

		Map<String, CtElement> result = new VariableUseScanner(method).getResult();

		assertEquals(2, result.keySet().size());
		assertTrue(result.containsKey("x"));
		assertTrue(result.containsKey("B"));
	}

	@Test
	public void testScanTypeNameUsingKnownVariables() {

		// contract: VariableUseScanner should find type names indicated to be known variable names

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    A x;\n" +
										 "    bar(B);\n" +
										 "}\n");

		List<String> knownVariables = new ArrayList<>();
		knownVariables.add("A");
		knownVariables.add("B");

		Map<String, CtElement> result = new VariableUseScanner(method, knownVariables).getResult();

		assertEquals(3, result.keySet().size());
		assertEquals("A x", result.get("x").toString());
		assertEquals("A x", result.get("A").toString());
		assertEquals("B", result.get("B").toString());
	}
}
