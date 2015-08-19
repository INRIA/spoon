package spoon.test.visibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static spoon.test.TestUtils.build;
import static spoon.test.TestUtils.canBeBuild;

import java.io.File;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonAPI;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;

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
		final File sourceOutputDir = new File("target/spooned/visibility");
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

	@Test
	public void testComplexVisibilityWithGenerics() throws Exception {
		final SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/visibility/testclasses/A.java",
				"-o", "./target/spooned/visibility"
		});

		canBeBuild("./target/spooned/spoon/test/visibility/testclasses/", 8);
	}

	@Test
	public void testName() throws Exception {
		final SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/visibility/testclasses/Tacos.java",
				"-o", "./target/spooned/visibility"
		});

		final List<CtFieldReference<?>> references = Query.getReferences(launcher.getFactory(), new AbstractReferenceFilter<CtFieldReference<?>>(CtFieldReference.class) {
			@Override
			public boolean matches(CtFieldReference<?> reference) {
				return "x".equals(reference.getSimpleName());
			}
		});
		assertEquals(1, references.size());
		final CtFieldReference<?> field = references.get(0);
		assertNotNull(field.getDeclaration());
		final CtClass<?> tacos = launcher.getFactory().Class().get("spoon.test.visibility.testclasses.Tacos");
		assertEquals(tacos, field.getDeclaringType().getDeclaration());
		assertEquals(tacos.getFields().get(0), field.getDeclaration());
	}
}
