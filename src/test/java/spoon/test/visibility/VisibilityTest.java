package spoon.test.visibility;

import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static spoon.test.TestUtils.build;
import static spoon.test.TestUtils.canBeBuild;

public class VisibilityTest {
    @Test
    public void testMethodeWithNonAccessibleTypeArgument() throws Exception {
        Factory f = build(spoon.test.visibility.MethodeWithNonAccessibleTypeArgument.class,
                spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf.class,
                Class.forName("spoon.test.visibility.packageprotected.NonAccessibleInterf")
                );
        CtClass<?> type = f.Class().get(spoon.test.visibility.MethodeWithNonAccessibleTypeArgument.class);
        assertEquals("MethodeWithNonAccessibleTypeArgument", type.getSimpleName());
        CtMethod<?> m = type.getMethodsByName("method").get(0);
        assertEquals(
				"new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf().method(new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf())",
				m.getBody().getStatement(0).toString()
		);
    }

	@Test
	public void testVisibilityOfClassesNamedByClassesInJavaLangPackage() throws Exception {
		final File sourceOutputDir = new File("target/spooned");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setDefaultFileGenerator(launcher.createOutputWriter(sourceOutputDir, launcher.getEnvironment()));
		final Factory factory = launcher.getFactory();
		final SpoonCompiler compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/visibility/testclasses/"));
		compiler.setOutputDirectory(sourceOutputDir);
		compiler.build();
		compiler.generateProcessedSourceFiles(OutputType.CLASSES);

		// Class must be imported.
		final CtClass<?> aDouble = (CtClass<?>) factory.Type().get(spoon.test.visibility.testclasses.internal.Double.class);
		assertNotNull(aDouble);
		assertEquals(spoon.test.visibility.testclasses.internal.Double.class, aDouble.getActualClass());

		// Class mustn't be imported.
		final CtClass<?> aFloat = (CtClass<?>) factory.Type().get(spoon.test.visibility.testclasses.Float.class);
		assertNotNull(aFloat);
		assertEquals(spoon.test.visibility.testclasses.Float.class, aFloat.getActualClass());

		canBeBuild(new File("./target/spooned/spoon/test/visibility/testclasses/"), 7);
	}
}
