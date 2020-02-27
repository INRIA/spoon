package spoon;

import org.junit.Test;

import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

/**
 * Issue3275
 */
public class Issue3275 {
	// the following code reproduces the behavior from the issue
	@Test
	public void test() {
		CtModel model = new FluentLauncher().inputResource("src/test/resources/issue3275/Foo.java")
				.noClasspath(true)
				.processor(new AbstractProcessor<CtTypeReference<?>>() {
					public void process(CtTypeReference<?> element) {
						System.out.println(element.isGenerics());
					}
				})
				.buildModel();
	}
}