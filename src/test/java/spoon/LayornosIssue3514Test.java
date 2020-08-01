package spoon;

import org.junit.Test;

import spoon.reflect.CtModel;


public class LayornosIssue3514Test {

	private String input = "src/test/resources/layornos/AllocationStorage.java";
	@Test
	public void test() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(input);
		CtModel model = launcher.buildModel();
		model.getAllTypes().forEach(type -> {
			type.getUsedTypes(false);
			});
	}
}

