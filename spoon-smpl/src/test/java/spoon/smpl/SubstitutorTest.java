package spoon.smpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import spoon.reflect.declaration.CtElement;

import java.util.HashMap;

import static spoon.smpl.TestUtils.*;

/**
 * This suite is intentionally left very sparse as the current idea is that Substitutor
 * will be thoroughly tested by the end-to-end SmPL patch application tests.
 * <p>
 * Tests for bugs specific to the Substitutor should go in this suite.
 */
public class SubstitutorTest {
	@Test
	public void testEmptyMetavariableBindings() {

		// contract: given an empty set of metavariable bindings the substitutor should make no changes to input

		CtElement element = parseStatement("int x = 1;");
		int pre = element.hashCode();

		Substitutor.apply(element, new HashMap<>());

		assertEquals(pre, element.hashCode());
	}
}
