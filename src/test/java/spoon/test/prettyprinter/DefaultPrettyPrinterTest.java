package spoon.test.prettyprinter;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtImplicitTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.prettyprinter.testclasses.AClass;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultPrettyPrinterTest {

	@Test
	public void printerCanPrintInvocationWithoutException() throws Exception {
		String packageName = "spoon.test.subclass.prettyprinter";
		String className = "DefaultPrettyPrinterExample";
		String qualifiedName = packageName + "." + className;
		SpoonCompiler comp = new Launcher().createCompiler();
		List<SpoonResource> fileToBeSpooned = SpoonResourceHelper.resources("./src/test/resources/printer-test/" + qualifiedName.replace('.', '/') + ".java");
		assertEquals(1, fileToBeSpooned.size());
		comp.addInputSources(fileToBeSpooned);
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/printer-test/DefaultPrettyPrinterDependency.jar");
		assertEquals(1, classpath.size());
		comp.setSourceClasspath(classpath.get(0).getPath());
		comp.build();
		Factory factory = comp.getFactory();
		CtType<?> theClass = factory.Type().get(qualifiedName);
		List<CtInvocation<?>> elements = Query.getElements(theClass, new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		assertEquals(3, elements.size());
		CtInvocation<?> mathAbsInvocation = elements.get(1);
		assertEquals("java.lang.Math.abs(message.length())", mathAbsInvocation.toString());
	}
	
	@Test
	public void superInvocationWithEnclosingInstance() throws Exception {
		
		/**
		 * To extend a nested class an enclosing instance must be provided
		 * to call the super constructor.
		 */
		
		String sourcePath = "./src/test/resources/spoon/test/prettyprinter/NestedSuperCall.java";
		List<SpoonResource> files = SpoonResourceHelper.resources(sourcePath);
		assertEquals(1, files.size());
		
		SpoonCompiler comp = new Launcher().createCompiler();
		comp.addInputSources(files);
		comp.build();
		
		Factory factory = comp.getFactory();
		CtType<?> theClass = factory.Type().get("spoon.test.prettyprinter.NestedSuperCall");
		
		assertTrue(theClass.toString().contains("nc.super(\"a\")"));
	}

	@Test
	public void testPrintAClassWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonCompiler compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/"));
		compiler.build();

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		final String expected = "public class AClass {" + System.lineSeparator()
				+ "    public List<?> aMethod() {" + System.lineSeparator()
				+ "        return new ArrayList<>();" + System.lineSeparator()
				+ "    }" + System.lineSeparator()
				+ "}";
		assertEquals(expected, aClass.toString());
		final CtConstructorCall constructorCall =
				aClass.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class))
					  .get(0);
		final CtTypeReference<?> ctTypeReference = constructorCall.getType()
																  .getActualTypeArguments()
																  .get(0);
		assertTrue(ctTypeReference instanceof CtImplicitTypeReference);
		assertEquals("Object", ctTypeReference.getSimpleName());
	}

	@Test
	public void testPrintAMethodWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonCompiler compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/"));
		compiler.build();

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		final String expected = "public List<?> aMethod() {" + System.lineSeparator()
				+ "    return new ArrayList<>();" + System.lineSeparator()
				+ "}";
		assertEquals(expected, aClass.getMethodsByName("aMethod").get(0).toString());

		final CtConstructorCall constructorCall =
				aClass.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class))
					  .get(0);
		final CtTypeReference<?> ctTypeReference = constructorCall.getType()
																  .getActualTypeArguments()
																  .get(0);
		assertTrue(ctTypeReference instanceof CtImplicitTypeReference);
		assertEquals("Object", ctTypeReference.getSimpleName());
	}
}
