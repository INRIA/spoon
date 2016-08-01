package spoon.test.prettyprinter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.prettyprinter.testclasses.QualifiedThisRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QualifiedThisRefTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/prettyprinter/testclasses/QualifiedThisRef.java"))
				.build();
		factory.getEnvironment().setAutoImports(true);
	}

	@Test
	public void testQualifiedThisRef() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(factory.getEnvironment());
		CtType<?> ctClass = factory.Type().get(QualifiedThisRef.class);
		Collection<CtTypeReference<?>> imports = printer.computeImports(ctClass);
		final List<CtType<?>> ctTypes = new ArrayList<>();
		ctTypes.add(ctClass);
		printer.getElementPrinterHelper().writeHeader(ctTypes, imports);
		printer.scan(ctClass);
		Assert.assertTrue(printer.getResult().contains("Object o = QualifiedThisRef.Sub.this"));
	}
}
