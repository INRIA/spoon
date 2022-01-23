/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.intercession;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import spoon.Launcher;
import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static spoon.test.parent.ContractOnSettersParametrizedTest.createCompatibleObject;

// contract: one can call all setters with null as parameter (no problem with parent)
public class OneCanCallSetterWithNullParameterizedTest {

	@TestFactory
	public Collection<DynamicTest> createTests() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		// all metamodel interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.buildModel();
		List<DynamicTest> tests = new ArrayList<>();
		new IntercessionScanner(launcher.getFactory()) {
			@Override
			protected boolean isToBeProcessed(CtMethod<?> candidate) {
				return (candidate.getSimpleName().startsWith("set")
						|| candidate.getSimpleName().startsWith("add"))
						&& takeSetterForCtElement(candidate);
			}

			@Override
			protected void process(CtMethod<?> element) {
				tests.add(dynamicTest(element.getReference().getActualMethod().getName(),
						() -> testNullSafe(createCompatibleObject(element.getDeclaringType().getReference()),
								element.getReference().getActualMethod())));
			}
		}.scan(launcher.getModel().getRootPackage());
		return tests;
	}


	private void testNullSafe(Object instance, Method setter) throws Throwable {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),new StandardEnvironment());
		if (instance instanceof FactoryAccessor) {
			((FactoryAccessor) instance).setFactory(factory);
		}
		// we invoke the setter
		setter.invoke(instance, new Object[] { null });
	}
}
