package spoon.test.architecture;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.compiler.SnippetCompilationHelper;

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SpoonArchitectureEnforcer {

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
	public void noTreeSetInSpoon() throws Exception {
		// we don't use TreeSet, because they implicitly depend on Comparable (no static check, only dynamic checks)
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();

		assertEquals(0, spoon.getFactory().Package().getRootPackage().getElements(new AbstractFilter<CtConstructorCall>() {
			@Override
			public boolean matches(CtConstructorCall element) {
				return element.getType().getActualClass().equals(TreeSet.class);
			};
		}).size());
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

		for (CtType<?> t : implementations.getModel().getAllTypes()) {
			String impl = t.getQualifiedName().replace(".support", "").replace("Impl", "");
			CtType itf = interfaces.getFactory().Type().get(impl);
			assertTrue(itf.isSubtypeOf(t.getReference()));
		}
	}
}
