package spoon.smpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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
