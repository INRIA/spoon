package spoon;

import org.junit.Test;

import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtTypeReference;

/**
 * Issue3275
 */
public class Issue3275Test {
	// the following code reproduces the behavior from the issue
	@Test
	public void test() {
		CtModel model = new FluentLauncher().inputResource("src/test/resources/issue3275/BOMCostPrice.java")
				.noClasspath(true)
				.processor(new AbstractProcessor<CtTypeReference<?>>() {
					public void process(CtTypeReference<?> element) {
						System.out.println(element.isGenerics());
					}
				})
				.buildModel();
	}
}
