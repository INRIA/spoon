package spoon.test.parameters;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.filter.NameFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParameterTest {
	@Test
	public void testParameterInNoClasspath() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/parameter");
		launcher.setSourceOutputDirectory("./target/parameter");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.eclipse.draw2d.text.FlowUtilities");
		final CtParameter<?> parameter = aClass.getElements(new NameFilter<CtParameter<?>>("font")).get(0);

		assertEquals("font", parameter.getSimpleName());
		assertNotNull(parameter.getType());
		assertEquals("org.eclipse.swt.graphics.Font", parameter.getType().toString());
		assertEquals("org.eclipse.swt.graphics.Font font", parameter.toString());
	}
}
