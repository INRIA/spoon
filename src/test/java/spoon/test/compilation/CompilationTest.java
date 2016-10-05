package spoon.test.compilation;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class CompilationTest {

	@Test
	public void compileTest() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Simple.java");
		File outputBinDirectory = new File("./target/class-simple");
		if (!outputBinDirectory.exists()) {
			outputBinDirectory.mkdirs();
		}
		launcher.setBinaryOutputDirectory(outputBinDirectory);
		launcher.getEnvironment().setShouldCompile(true);
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CoreFactory core = factory.Core();
		CodeFactory code = factory.Code();

		CtClass simple = factory.Class().get("Simple");

		CtMethod method = core.createMethod();
		method.addModifier(ModifierKind.PUBLIC);
		method.setType(factory.Type().integerPrimitiveType());
		method.setSimpleName("m");

		CtBlock block = core.createBlock();
		CtReturn aReturn = core.createReturn();

		CtBinaryOperator binaryOperator = code.createBinaryOperator(
						code.createLiteral(10),
						code.createLiteral(32),
						BinaryOperatorKind.PLUS);
		aReturn.setReturnedExpression(binaryOperator);

		// return 10 + 32;
		block.addStatement(aReturn);
		method.setBody(block);

		simple.addMethod(method);

		launcher.getModelBuilder().compile();

		final URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { outputBinDirectory.toURL() });

		Class<?> aClass = urlClassLoader.loadClass("Simple");
		Method m = aClass.getMethod("m");
		Assert.assertEquals(42, m.invoke(aClass.newInstance()));
	}
}
