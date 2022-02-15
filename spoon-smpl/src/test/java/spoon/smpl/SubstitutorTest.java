/**
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package spoon.smpl;

import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtElement;
import static spoon.smpl.TestUtils.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
