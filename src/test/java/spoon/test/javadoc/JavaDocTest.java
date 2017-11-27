package spoon.test.javadoc;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;
import spoon.test.javadoc.testclasses.Bar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JavaDocTest {
	@Test
	public void testJavaDocReprint() throws Exception {
		SpoonAPI launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setCopyResources(false);
		launcher.addInputResource("./src/test/java/spoon/test/javadoc/testclasses/");
		launcher.setSourceOutputDirectory("./target/spooned/");
		launcher.run();
		Factory factory = launcher.getFactory();
		CtClass<?> aClass = factory.Class().get(Bar.class);

		assertEquals("public class Bar {" + System.lineSeparator()
				+ "    /**" + System.lineSeparator()
				+ "     * Creates an annotation type." + System.lineSeparator()
				+ "     *" + System.lineSeparator()
				+ "     * @param owner" + System.lineSeparator()
				+ "     * \t\tthe package of the annotation type" + System.lineSeparator()
				+ "     * @param simpleName" + System.lineSeparator()
				+ "     * \t\tthe name of annotation" + System.lineSeparator()
				+ "     */" + System.lineSeparator()
				+ "    public <T> CtAnnotationType<?> create(CtPackage owner, String simpleName) {" + System.lineSeparator()
				+ "        return null;" + System.lineSeparator()
				+ "    }" + System.lineSeparator()
				+ "}", aClass.toString());
	}

	@Test
	public void testJavadocNotPresentInAST() throws Exception {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setCommentEnabled(false);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/javadoc/testclasses/");
		launcher.run();

		new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null) {
					assertEquals(0, element.getComments().size());
				}
				super.scan(element);
			}

			@Override
			public void visitCtComment(CtComment comment) {
				fail("Shouldn't have comment in the model.");
				super.visitCtComment(comment);
			}
		}.scan(launcher.getModel().getRootPackage());
	}
}
