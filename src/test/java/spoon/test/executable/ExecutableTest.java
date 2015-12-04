package spoon.test.executable;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExecutableTest {
	@Test
	public void testInfoInsideAnonymousExecutable() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/executable/testclasses/AnonymousExecutableSample.java");
		launcher.setSourceOutputDirectory("./target/trash");
		launcher.run();

		final List<CtAnonymousExecutable> anonymousExecutables = Query.getElements(launcher.getFactory(), new TypeFilter<CtAnonymousExecutable>(CtAnonymousExecutable.class));

		assertEquals(2, anonymousExecutables.size());

		for (CtAnonymousExecutable anonymousExecutable : anonymousExecutables) {
			assertEquals("", anonymousExecutable.getSimpleName());
			assertEquals(launcher.getFactory().Type().VOID_PRIMITIVE, anonymousExecutable.getType());
			assertEquals(0, anonymousExecutable.getParameters().size());
			assertEquals(0, anonymousExecutable.getThrownTypes().size());
		}
	}
}
