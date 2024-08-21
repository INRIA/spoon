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
package spoon.test.parent;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import spoon.reflect.CtModelImpl;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnnamedPattern;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.ModuleFactory;
import spoon.reflect.reference.CtReference;
import spoon.test.SpoonTestHelpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.testing.utils.ModelUtils.createFactory;

// contract: setParent does not modifiy the state of the parent
public class SetParentTest{

	private static Factory factory = createFactory();

	@TestFactory
	public Collection<DynamicTest> createReceiverList() {
		List<DynamicTest> values = new ArrayList<>();
		for (CtType<?> t : SpoonTestHelpers.getAllInstantiableMetamodelInterfaces()) {
			if (!(CtReference.class.isAssignableFrom(t.getActualClass()))) {
				values.add(DynamicTest.dynamicTest(t.getSimpleName(), () -> 
					testSetParentDoesNotAlterState(t)));
			}
		}
		return values;
	}
	
	private void testSetParentDoesNotAlterState(CtType<?> toTest) throws Throwable {
		// contract: setParent does not modifiy the state of the parent

		Object o = factory.Core().create((Class<? extends CtElement>) toTest.getActualClass());
		CtMethod<?> setter = factory.Type().get(CtElement.class).getMethodsByName("setParent").get(0);

		CtElement argument = createCompatibleParent(toTest);

		// we create a fresh object
		CtElement receiver = ((CtElement) o).clone();

		if ("CtClass".equals(toTest.getSimpleName())
				|| "CtInterface".equals(toTest.getSimpleName())
				|| "CtEnum".equals(toTest.getSimpleName())
				|| "CtAnnotationType".equals(toTest.getSimpleName())
				|| "CtPackage".equals(toTest.getSimpleName())
				) {
			// contract: root package is the parent for those classes
			assertTrue(receiver.getParent() instanceof CtModelImpl.CtRootPackage);
		} else if ("CtModule".equals(toTest.getSimpleName())) {
			// contract: module parent is necessarily the unnamedmodule
			assertTrue(receiver.getParent() instanceof ModuleFactory.CtUnnamedModule);
		} else if ("CtCompilationUnit".equals(toTest.getSimpleName())) {
			// contract: CtCompilationUnit parent is null
			assertNull(receiver.getParent());
		} else {
			// contract: there is no parent before
			try {
				receiver.getParent().hashCode();
				fail(receiver.getParent().getClass().getSimpleName());
			} catch (ParentNotInitializedException normal) {
			}
		}

		Method actualMethod = setter.getReference().getActualMethod();
		CtElement argumentClone = argument.clone();
		actualMethod.invoke(receiver, new Object[]{argument});

		// contract: the parent has not been changed by a call to setParent on an elemnt
		assertTrue(argument.equals(argumentClone));
		assertNotSame(argument, argumentClone);

	}

	private static CtElement createCompatibleParent(CtType<?> e) {
		if (CtUnnamedPattern.class.getSimpleName().equals(e.getSimpleName())) {
			return factory.Core().createRecordPattern();
		} else if (CtTypePattern.class.getSimpleName().equals(e.getSimpleName())) {
			return createInstanceOfBinaryOperator();
		}
		return e.getFactory().createAssignment();
	}

	private static CtBinaryOperator<?> createInstanceOfBinaryOperator() {
		CtBinaryOperator<?> op = factory.createBinaryOperator();
		op.setKind(BinaryOperatorKind.INSTANCEOF);
		return op;
	}
}
