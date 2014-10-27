package spoon.test;

import org.junit.Assert;
import org.junit.Test;

import spoon.OutputType;

public class OutputTypeTest {

	@Test
	public void testOutputTypeLoading()
	{
		OutputType outputType = OutputType.fromString("nulltest");
		Assert.assertNull(outputType);

		outputType = OutputType.fromString("nooutput");
		Assert.assertEquals(OutputType.NO_OUTPUT, outputType);

		outputType = OutputType.fromString("classes");
		Assert.assertEquals(OutputType.CLASSES, outputType);

		outputType = OutputType.fromString("compilationunits");
		Assert.assertEquals(OutputType.COMPILATION_UNITS, outputType);
	}
}
