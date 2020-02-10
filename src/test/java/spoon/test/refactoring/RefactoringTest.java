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
package spoon.test.refactoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.refactoring.Refactoring;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.refactoring.processors.ThisTransformationProcessor;
import spoon.test.refactoring.testclasses.AClass;

public class RefactoringTest {
	@Test
	public void testRefactoringClassChangeAllCtTypeReferenceAssociatedWithClassConcerned() {
		Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring"
		});
		launcher.run();

		final CtClass<?> aClass = launcher.getFactory().Class().get(AClass.class);
		assertNotNull(aClass);

		launcher = new Launcher();
		launcher.setArgs(new String[]{
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring",
				"-p", ThisTransformationProcessor.class.getName()
		});
		launcher.run();

		final CtClass<?> classNotAccessible = launcher.getFactory().Class().get(AClass.class);
		assertNull(launcher.getFactory().Class().get("spoon.test.refactoring.testclasses.AClass"));
		assertNotNull(classNotAccessible);

		final CtClass<?> aClassX = launcher.getFactory().Class().get("spoon.test.refactoring.testclasses.AClassX");
		assertNotNull(aClassX);

		final List<CtTypeReference<?>> references = Query.getElements(aClassX.getFactory(), new AbstractReferenceFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				return aClassX.getQualifiedName().equals(reference.getQualifiedName());
			}
		});
		assertNotEquals(0, references.size());
		for (CtTypeReference<?> reference : references) {
			assertEquals("AClassX", reference.getSimpleName());
		}
	}

	@Test
	public void testThisInConstructor() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring"
		});
		launcher.run();
		final CtClass<?> aClass = (CtClass<?>) launcher.getFactory().Type().get(AClass.class);

		final CtInvocation<?> thisInvocation = aClass.getElements(new AbstractFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return element.getExecutable().isConstructor();
			}
		}).get(0);
		assertEquals("this(\"\")", thisInvocation.toString());
	}

	@Test
	public void testThisInConstructorAfterATransformation() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring",
				"-p", ThisTransformationProcessor.class.getName()
		});
		launcher.run();
		final CtClass<?> aClassX = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.refactoring.testclasses.AClassX");
		final CtInvocation<?> thisInvocation = aClassX.getElements(new AbstractFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return element.getExecutable().isConstructor();
			}
		}).get(0);
		assertEquals("this(\"\")", thisInvocation.toString());
	}

	@Test
	public void testTransformedInstanceofAfterATransformation() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring",
				"-p", ThisTransformationProcessor.class.getName()
		});
		launcher.run();
		final CtClass<?> aClassX = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.refactoring.testclasses.AClassX");

		final CtBinaryOperator<?> instanceofInvocation = aClassX.getElements(new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class)).get(0);
		assertEquals(BinaryOperatorKind.INSTANCEOF, instanceofInvocation.getKind());
		assertEquals("o", instanceofInvocation.getLeftHandOperand().toString());
		assertEquals("spoon.test.refactoring.testclasses.AClassX", instanceofInvocation.getRightHandOperand().toString());
	}
	@Test
	public void testRemoveDeprecatedMethods() {
		if (checkJavaVersion()) {
			return;
		}
		// clean dir if exists
		try {
			Files.walk(Paths.get("target/deprecated-refactoring")).sorted(Comparator.reverseOrder())
					.map(Path::toFile).forEach(File::delete);
		} catch (Exception e) {
			// error is kinda okay
		}
		// create Spoon
		String input = "src/test/resources/deprecated/input";
		String resultPath = "target/deprecated-refactoring";
		String correctResultPath = "src/test/resources/deprecated/correctResult";
		Launcher spoon = new Launcher();

		spoon.addInputResource(correctResultPath);
		List<CtMethod<?>> correctResult = spoon.buildModel().getElements(new TypeFilter<>(CtMethod.class));
		// save Methods before cleaning
		// now refactor code
		Refactoring.removeDeprecatedMethods(input, resultPath);
		// verify result
		spoon = new Launcher();
		spoon.addInputResource(resultPath);
		List<CtMethod<?>> calculation = spoon.buildModel().getElements(new TypeFilter<>(CtMethod.class));
		assertTrue(calculation.stream().allMatch(correctResult::contains));
		assertTrue(correctResult.stream().allMatch(calculation::contains));
		//clean again
		try {
			Files.walk(Paths.get("target/deprecated-refactoring")).sorted(Comparator.reverseOrder())
					.map(Path::toFile).forEach(File::delete);
		} catch (Exception e) {
			// error is kinda okay
		}
	}
	/**
	 * checks if current java version is >=9.
	 * True if <=8 else false;
	 */
	private boolean checkJavaVersion() {
		String property = System.getProperty("java.version");
		if (property != null && !property.isEmpty()) {
			// java 8 and less are versionning "1.X" where 9 and more are directly versioned "X"
		return property.startsWith("1.");
		}
		return false;
	}
}
