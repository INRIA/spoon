package spoon.test.methodreference;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;
import spoon.test.methodreference.testclasses.Foo;

import java.io.File;
import java.util.Comparator;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MethodReferenceTest {
	private static final String TEST_CLASS = "spoon.test.methodreference.testclasses.Foo.";
	private CtClass<?> foo;

	@Before
	public void setUp() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		final File sourceOutputDir = new File("./target/spooned/");
		factory.getEnvironment().setDefaultFileGenerator(launcher.createOutputWriter(sourceOutputDir, factory.getEnvironment()));
		final SpoonCompiler compiler = launcher.createCompiler(factory);

		compiler.setSourceOutputDirectory(sourceOutputDir);
		compiler.addInputSource(new File("./src/test/java/spoon/test/methodreference/testclasses/"));
		compiler.build();
		compiler.generateProcessedSourceFiles(OutputType.CLASSES);

		foo = (CtClass<?>) factory.Type().get(Foo.class);
	}

	@Test
	public void testReferenceToAStaticMethod() throws Exception {
		final String methodReference = TEST_CLASS + "Person::compareByAge";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy(TEST_CLASS + "Person", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertExecutableNamedBy("compareByAge", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAnInstanceMethodOfAParticularObject() throws Exception {
		final String methodReference = "myComparisonProvider::compareByName";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy("myComparisonProvider", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtVariableRead);
		assertExecutableNamedBy("compareByName", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAnInstanceMethodOfMultiParticularObject() throws Exception {
		final String methodReference = "tarzan.phone::compareByNumbers";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy("tarzan.phone", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtVariableRead);
		assertExecutableNamedBy("compareByNumbers", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAnInstanceMethodOfAnArbitraryObjectOfAParticularType() throws Exception {
		final String methodReference = "java.lang.String::compareToIgnoreCase";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy("java.lang.String", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertExecutableNamedBy("compareToIgnoreCase", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAConstructor() throws Exception {
		final String methodReference = TEST_CLASS + "Person::new";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Supplier.class, reference.getType());
		assertTargetedBy(TEST_CLASS + "Person", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertIsConstructorReference(reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAClassParametrizedConstructor() throws Exception {
		final String methodReference = TEST_CLASS + "Type<java.lang.String>::new";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Supplier.class, reference.getType());
		assertTargetedBy(TEST_CLASS + "Type<java.lang.String>", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertIsConstructorReference(reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAJavaUtilClassConstructor() throws Exception {
		final String methodReference = "java.util.HashSet<" + TEST_CLASS + "Person>::new";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Supplier.class, reference.getType());
		assertTargetedBy("java.util.HashSet<" + TEST_CLASS + "Person>", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertIsConstructorReference(reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testCompileMethodReferenceGeneratedBySpoon() throws Exception {
		TestUtils.canBeBuilt(new File("./target/spooned/spoon/test/methodreference/testclasses/"), 8);
	}

	@Test
	public void testNoClasspathExecutableReferenceExpression() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/resources/executable-reference-expression/Bar.java", "-o", "./target/spooned", "--noclasspath"
		});
		final CtExecutableReferenceExpression<?, ?> element = Query
				.getElements(launcher.getFactory(), new TypeFilter<CtExecutableReferenceExpression<?, ?>>(CtExecutableReferenceExpression.class)).get(0);

		assertEquals("isInstance", element.getExecutable().getSimpleName());
		assertNotNull(element.getExecutable().getDeclaringType());
		assertEquals("Tacos", element.getExecutable().getDeclaringType().getSimpleName());
		assertEquals("elemType::isInstance", element.toString());
	}

	private void assertTypedBy(Class<?> expected, CtTypeReference<?> type) {
		assertEquals("Method reference must be typed.", expected, type.getActualClass());
	}

	private void assertTargetedBy(String expected, CtExpression<?> target) {
		assertNotNull("Method reference must have a target expression.", target);
		assertEquals("Target reference correspond to the enclosing class.", expected, target.toString());
	}

	private void assertIsConstructorReference(CtExecutableReference<?> executable) {
		assertExecutableNamedBy("<init>", executable);
	}

	private void assertExecutableNamedBy(String expected, CtExecutableReference<?> executable) {
		assertNotNull("Method reference must reference an executable.", executable);
		assertEquals("Method reference must reference the right executable.", expected, executable.getSimpleName());
	}

	private void assertIsWellPrinted(String methodReference, CtExecutableReferenceExpression<?,?> reference) {
		assertEquals("Method reference must be well printed", methodReference, reference.toString());
	}

	private CtExecutableReferenceExpression<?,?> getCtExecutableReferenceExpression(final String methodReference) {
		return foo.getElements(new AbstractFilter<CtExecutableReferenceExpression<?,?>>(CtExecutableReferenceExpression.class) {
			@Override
			public boolean matches(CtExecutableReferenceExpression<?,?> element) {
				return (methodReference).equals(element.toString());
			}
		}).get(0);
	}
}
