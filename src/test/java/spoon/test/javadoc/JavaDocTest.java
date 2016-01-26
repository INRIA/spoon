package spoon.test.javadoc;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.test.javadoc.testclasses.Bar;

public class JavaDocTest {
	@Test
	public void testJavaDocReprint() throws Exception {
		SpoonAPI launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setGenerateJavadoc(true);
		launcher.getEnvironment().setCopyResources(false);
		launcher.addInputResource("./src/test/java/spoon/test/javadoc/testclasses/");
		launcher.setSourceOutputDirectory("./target/spooned/");
		launcher.run();
		Factory factory = launcher.getFactory();
		CtClass<?> aClass = factory.Class().get(Bar.class);
		
		Assert.assertEquals("public class Bar {" + System.lineSeparator()
				+ "    /** " + System.lineSeparator()
				+ "     * Creates an annotation type." + System.lineSeparator()
				+ "     * " + System.lineSeparator()
				+ "     *  @param owner" + System.lineSeparator()
				+ "     *  \t\tthe package of the annotation type" + System.lineSeparator()
				+ "     *  @param simpleName" + System.lineSeparator()
				+ "     *  \t\tthe name of annotation" + System.lineSeparator()
				+ "     */" + System.lineSeparator()
				+ "    public <T>CtAnnotationType<?> create(CtPackage owner, String simpleName) {" + System.lineSeparator()
				+ "        return null;" + System.lineSeparator()
				+ "    }" + System.lineSeparator()
				+ "}", aClass.toString());
	}
}
