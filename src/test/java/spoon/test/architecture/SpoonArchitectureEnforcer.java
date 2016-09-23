package spoon.test.architecture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.TreeSet;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.filter.AbstractFilter;

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

}
