package spoon.test.architecture;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.processing.AbstractManualProcessor;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SpoonArchitectureEnforcerTest {

	@Test
	public void statelessFactory() throws Exception {
		// the factories must be stateless
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/spoon/reflect/factory");
		spoon.buildModel();

		for (CtType t : spoon.getFactory().Package().getRootPackage().getElements(new AbstractFilter<CtType>() {
			@Override
			public boolean matches(CtType element) {
				return super.matches(element)
						&& element.getSimpleName().contains("Factory");
			};
		})) {
			for (Object o : t.getFields()) {
				CtField f=(CtField)o;
				if (f.getSimpleName().equals("factory")) { continue; }
				if (f.hasModifier(ModifierKind.FINAL) || f.hasModifier(ModifierKind.TRANSIENT) ) { continue; }

				fail("architectural constraint: a factory must be stateless");
			}
		}

	}

	@Test
	public void testFactorySubFactory() throws Exception {
		// contract:: all subfactory methods must also be in the main factory
		// this is very important for usability and discoverability
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/main/java/spoon/reflect/factory");
		class SanityCheck { int val = 0; };
		SanityCheck sanityCheck = new SanityCheck();
		launcher.addProcessor(new AbstractManualProcessor() {
			@Override
			public void process() {
				CtType factoryImpl = getFactory().Interface().get(Factory.class);
				CtPackage factoryPackage = getFactory().Package().getOrCreate("spoon.reflect.factory");
				CtInterface itf = getFactory().Interface().create("MegaFactoryItf");
				CtClass impl = getFactory().Class().create("MegaFactory");
				for (CtType<?> t : factoryPackage.getTypes()) {
					if (t.getSimpleName().startsWith("Mega")) continue; //
					for (CtMethod<?> m : t.getMethods()) {
						// we only consider factory methods
						if (!m.getSimpleName().startsWith("create")) continue;

						// too generic, what should we create??
						if (m.getSimpleName().equals("create")) continue;

						// too generic, is it a fieldref? an execref? etc
						if (m.getSimpleName().equals("createReference"))
							continue;

						if (m.getModifiers().contains(ModifierKind.ABSTRACT)) continue;

						sanityCheck.val++;

						// the core assertion
						assertTrue(factoryImpl.hasMethod(m));
					}
				}
			}
		});
		launcher.run();
		assertTrue(sanityCheck.val > 100);
	}

	@Test
	public void noTreeSetInSpoon() throws Exception {
		// we don't use TreeSet, because they implicitly depend on Comparable (no static check, only dynamic checks)
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();

		List<CtConstructorCall> treeSetWithoutComparators = spoon.getFactory().Package().getRootPackage().filterChildren(new AbstractFilter<CtConstructorCall>() {
			@Override
			public boolean matches(CtConstructorCall element) {
				return element.getType().getActualClass().equals(TreeSet.class) && element.getArguments().size() == 0;
			}
		}).list();

		assertEquals(0, treeSetWithoutComparators.size());
	}

	@Test
	public void metamodelPackageRule() throws Exception {
		// all implementations of the metamodel classes have a corresponding interface in the appropriate package
		SpoonAPI implementations = new Launcher();
		implementations.addInputResource("src/main/java/spoon/support/reflect/declaration");
		implementations.addInputResource("src/main/java/spoon/support/reflect/code");
		implementations.addInputResource("src/main/java/spoon/support/reflect/reference");
		implementations.buildModel();

		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.addInputResource("src/main/java/spoon/support/DefaultCoreFactory.java");
		interfaces.buildModel();

		for (CtType<?> implType : implementations.getModel().getAllTypes()) {
			String impl = implType.getQualifiedName().replace(".support", "").replace("Impl", "");
			CtType interfaceType = interfaces.getFactory().Type().get(impl);
			// the implementation is a subtype of the superinterface
			assertTrue(implType.getReference().isSubtypeOf(interfaceType.getReference()));
		}
	}


	@Test
	public void testGoodTestClassNames() throws Exception {
		// contract: to be run by Maven surefire, all test classes must be called Test* or *Test
		// reference: "By default, the Surefire Plugin will automatically include all test classes with the following wildcard patterns:"
		// "**/Test*.java" and "**/*Test.java"
		// http://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/test/java/");
		spoon.buildModel();

		for (CtMethod<?> meth : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtMethod>(CtMethod.class) {
			@Override
			public boolean matches(CtMethod element) {
				return super.matches(element) && element.getAnnotation(Test.class) != null;
			}
		})) {
			assertTrue("naming contract violated for "+meth.getParent(CtClass.class).getSimpleName(), meth.getParent(CtClass.class).getSimpleName().startsWith("Test") || meth.getParent(CtClass.class).getSimpleName().endsWith("Test"));
		}
	}

	@Test
	public void testStaticClasses() throws Exception {
		// contract: helper classes only have static methods and a private constructor

//		spoon.compiler.SpoonResourceHelper
//		spoon.reflect.visitor.Query
//		spoon.support.compiler.jdt.JDTTreeBuilderQuery
//		spoon.support.compiler.SnippetCompilationHelper
//		spoon.support.util.ByteSerialization
//		spoon.support.util.RtHelper
//		spoon.support.visitor.equals.CloneHelper
//		spoon.template.Substitution
//		spoon.testing.utils.Check
//		spoon.testing.utils.ProcessorUtils
//		spoon.testing.Assert

		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();

		for (CtClass<?> klass : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class) {
			@Override
			public boolean matches(CtClass element) {
				return element.getSuperclass() == null && super.matches(element) && element.getMethods().size()>0
						&& element.getElements(new TypeFilter<>(CtMethod.class)).stream().allMatch( x -> x.hasModifier(ModifierKind.STATIC));
			}
		})) {
			assertTrue(klass.getElements(new TypeFilter<>(CtConstructor.class)).stream().allMatch(x -> x.hasModifier(ModifierKind.PRIVATE)));
		}
	}
}
