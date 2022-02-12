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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class CombinationsGeneratorTest {
	@Test
	public void test() {

		// contract: CombinationsGenerator should iteratively build the cartesian product of sets

		List<String> S1 = Arrays.asList("A", "B", "C");
		List<String> S2 = Arrays.asList("x", "y");
		List<String> S3 = Arrays.asList("1");

		CombinationsGenerator<String> combo = new CombinationsGenerator<>();

		combo.addWheel(S1);
		combo.addWheel(S2);
		combo.addWheel(S3);

		StringBuilder sb = new StringBuilder();

		while (combo.next()) {
			for (String s : combo.current()) {
				sb.append(s);
			}

			sb.append("\n");
		}

		assertEquals("Ax1\n" +
					 "Bx1\n" +
					 "Cx1\n" +
					 "Ay1\n" +
					 "By1\n" +
					 "Cy1\n", sb.toString());
	}
}
