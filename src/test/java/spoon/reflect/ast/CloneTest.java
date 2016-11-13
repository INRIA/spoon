package spoon.reflect.ast;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtConditional;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CloneTest {
	@Test
	public void testCloneMethodsDeclaredInAST() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.run();

		new CtScanner() {
			@Override
			public <T> void visitCtClass(CtClass<T> ctClass) {
				if (!ctClass.getSimpleName().startsWith("Ct")) {
					return;
				}
				final CtMethod<Object> clone = ctClass.getMethod("clone");
				assertNotNull(ctClass.getQualifiedName() + " hasn't clone method.", clone);
				assertTrue(ctClass.getQualifiedName() + " hasn't Override annotation on clone method.", clone.getAnnotations().stream().map(ctAnnotation -> ctAnnotation.getActualAnnotation().annotationType()).collect(Collectors.toList()).contains(Override.class));
			}

			@Override
			public <T> void visitCtInterface(CtInterface<T> intrface) {
				if (!intrface.getSimpleName().startsWith("Ct")) {
					return;
				}
				final CtMethod<Object> clone = intrface.getMethod("clone");
				if (hasConcreteImpl(intrface)) {
					assertNotNull(intrface.getQualifiedName() + " hasn't clone method.", clone);
					if (!isRootDeclaration(intrface)) {
						assertTrue(intrface.getQualifiedName() + " hasn't Override annotation on clone method.",
								clone.getAnnotations().stream().map(ctAnnotation -> ctAnnotation.getActualAnnotation().annotationType()).collect(Collectors.toList()).contains(Override.class));
					}
				}
			}

			private <T> boolean hasConcreteImpl(CtInterface<T> intrface) {
				return Query.getElements(intrface.getFactory(), new TypeFilter<CtClass<?>>(CtClass.class) {
					@Override
					public boolean matches(CtClass<?> element) {
						return super.matches(element) && element.getSuperInterfaces().contains(intrface.getReference());
					}
				}).size() > 0;
			}

			private <T> boolean isRootDeclaration(CtInterface<T> intrface) {
				return "CtElement".equals(intrface.getSimpleName());
			}
		}.scan(launcher.getModel().getRootPackage());
	}

	@Test
	public void testCloneCastConditional() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);

		launcher.addInputResource("./src/test/resources/spoon/test/visitor/ConditionalRes.java");

		launcher.addProcessor(new AbstractProcessor<CtConditional<?>>() {
			@Override
			public void process(CtConditional<?> conditional) {
				CtConditional clone = conditional.clone();
				Assert.assertEquals(0, conditional.getTypeCasts().size());
				Assert.assertEquals(0, clone.getTypeCasts().size());
				Assert.assertEquals(conditional, clone);
				conditional.addTypeCast(getFactory().Type().bytePrimitiveType());
				Assert.assertEquals(1, conditional.getTypeCasts().size());
				Assert.assertNotEquals(conditional, clone);
				clone = conditional.clone();
				Assert.assertEquals(conditional, clone);
				Assert.assertEquals(1, clone.getTypeCasts().size());
			}
		});
		launcher.run();
	}
}
