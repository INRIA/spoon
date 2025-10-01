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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import spoon.Launcher;
import spoon.refactoring.Refactoring;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.test.refactoring.processors.ThisTransformationProcessor;
import spoon.test.refactoring.testclasses.AClass;
import spoon.test.refactoring.testclasses.GenericRenaming;
import spoon.test.refactoring.testclasses.MethodGenericRenaming;
import spoon.testing.utils.ModelUtils;
import spoon.test.refactoring.testclasses.AnnotationMethodRenaming;
import spoon.test.refactoring.testclasses.ExampleAnnotation;
import spoon.test.refactoring.testclasses.InterfaceRenaming;
import spoon.test.refactoring.testclasses.MethodRenaming;

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

		// contract: the new compilation unit has been written
		assertTrue(new File("target/spooned/refactoring/spoon/test/refactoring/testclasses/AClassX.java").exists());

		// contract: the source position has been set correctly
		assertEquals("AClassX.java", aClassX.getPosition().getFile().getName());

		// contract: instanceof parameter have been renamed
		final CtBinaryOperator<?> instanceofInvocation = aClassX.getElements(new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class)).get(0);
		assertEquals(BinaryOperatorKind.INSTANCEOF, instanceofInvocation.getKind());
		assertEquals("o", instanceofInvocation.getLeftHandOperand().toString());
		assertEquals("spoon.test.refactoring.testclasses.AClassX", instanceofInvocation.getRightHandOperand().toString());
	}
	@Test
	@DisabledForJreRange(max = JRE.JAVA_8)
	public void testRemoveDeprecatedMethods() {
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

	@Test
	void renameClassInVirtualFile() {
		// contract: Renaming classes defined in virtual files should work
		Launcher spoon = new Launcher();
		spoon.addInputResource(new VirtualFile(
			"public class Test {\n" +
			"  void foo() {\n" +
			"  }\n" +
			"}\n"));
		CtModel model = spoon.buildModel();
		CtClass<?> clazz = model.getElements(new TypeFilter<>(CtClass.class)).get(0);

		Refactoring.changeTypeName(clazz, "Test2");

		assertTrue(
			clazz.prettyprint().contains("class Test2 "),
			"Class was not renamed: '" + clazz.prettyprint() + "'"
		);
		assertFalse(
			clazz.prettyprint().contains("class Test "),
			"Class was not renamed: '" + clazz.prettyprint() + "'"
		);
	}

	@Test
	void testRenameType() throws Exception {
		Factory factory = ModelUtils.build(GenericRenaming.class);
		CtClass<?> clazz = factory.Class().get(GenericRenaming.class);
		var nestedClass = clazz.getNestedType("SomeNestedType");
		Refactoring.changeTypeName(nestedClass, "RenamedNestedType");

		assertEquals("RenamedNestedType", nestedClass.getSimpleName());

		var refs = clazz.getElements(new TypeFilter<>(CtTypeReference.class)).stream().filter(t -> t.getSimpleName().equals("RenamedNestedType")).toList();
		assertEquals(2, refs.size());
		for (var ref : refs) {
			assertEquals(nestedClass, ref.getDeclaration());
		}
	}

	@Test
	void testRenameGenerics() throws Exception {
		Factory factory = ModelUtils.build(GenericRenaming.class);
		CtClass<?> clazz = factory.Class().get(GenericRenaming.class);
		List<CtType<?>> types = clazz.getElements(new TypeFilter<>(CtType.class));

		renameAndCheckClassGeneric(clazz, types, "SomeIdentifier", "TNew1", 15);
		renameAndCheckClassGeneric(clazz, types, "SomeOther", "TNew2", 17);
		renameAndCheckMethodGeneric(clazz, "doTheThing", types, "SomeMethodGeneric", "TNew3", 2);
	}

	@Test
	void testRenameGenericsShouldRespectScope() throws Exception {
		Factory factory = ModelUtils.build(MethodGenericRenaming.class);
		CtClass<?> clazz = factory.Class().get(MethodGenericRenaming.class);
		List<CtType<?>> types = clazz.getElements(new TypeFilter<>(CtType.class));
		renameAndCheckMethodGeneric(clazz, "sort", types, "T", "TNew", 3);
	}

	private static void renameAndCheckMethodGeneric(CtClass<?> clazz, String method, List<CtType<?>> types, String oldName, String newName, int expectedRefs) {
		var generic = clazz.getMethodsByName(method).get(0).getElements(new TypeFilter<>(CtType.class))
				.stream().filter(t -> t.getSimpleName().equals(oldName)).findFirst().orElseThrow();
		Refactoring.changeTypeName(generic, newName);
		assertEquals(newName, generic.getSimpleName());

		var typeRefs = clazz.getElements(new TypeFilter<>(CtTypeReference.class))
				.stream().filter(typeRef -> typeRef.getSimpleName().equals(newName)).toList();
		assertEquals(expectedRefs, typeRefs.size());
		for (var typeRef : typeRefs) {
			assertEquals(generic.getParent(CtMethod.class), typeRef.getParent(CtMethod.class));
		}
	}

	private static void renameAndCheckClassGeneric(CtClass<?> clazz, List<CtType<?>> types, String oldName, String newName, int expectedRefs) {
		var classGeneric = types.stream().filter((CtType<?> t) -> t.getSimpleName().equals(oldName)).findFirst().orElseThrow();
		Refactoring.changeTypeName(classGeneric, newName);
		assertEquals(newName, classGeneric.getSimpleName());

		var typeRefs = clazz.getElements(new TypeFilter<>(CtTypeReference.class))
				.stream().filter(typeRef -> typeRef.getSimpleName().equals(newName)).toList();

		assertEquals(expectedRefs, typeRefs.size());
		for (var typeRef : typeRefs) {
			assertEquals(classGeneric, typeRef.getDeclaration());
		}
	}

	@Test
	void testChangeMethodName() throws Exception {
		String newName = "methodRenamed";
		String newNameStaticMethod = "nestedStaticMethodRenamed";
		String newNameNestedMethod = "nestedMethodRenamed";

		Factory factory = ModelUtils.build(MethodRenaming.class);
		CtClass<?> clazz = factory.Class().get(MethodRenaming.class);
		var methods = clazz.getElements(new TypeFilter<>(CtMethod.class));
		Refactoring.changeMethodName(methods.get(1), newName);
		Refactoring.changeMethodName(methods.get(2), newNameStaticMethod);
		Refactoring.changeMethodName(methods.get(3), newNameNestedMethod);

		var refs = clazz.getElements(new TypeFilter<>(CtExecutableReference.class))
				.stream().filter(e -> !e.getSimpleName().equals("<init>")).toList();

		assertEquals(newName, methods.get(1).getSimpleName());
		assertEquals(newNameStaticMethod, methods.get(2).getSimpleName());
		assertEquals(newNameNestedMethod, methods.get(3).getSimpleName());

		assertEquals(newName, refs.get(1).getSimpleName());
		assertEquals(newNameStaticMethod, refs.get(2).getSimpleName());
		assertEquals(newNameNestedMethod, refs.get(3).getSimpleName());
	}

	@Test
	void testChangeDefaultInterfaceMethodName() throws Exception {
		String newName = "defaultInterfaceMethodRenamed";
		Factory factory = ModelUtils.build(MethodRenaming.class, InterfaceRenaming.class);
		CtType<?> ctInterface = factory.Interface().get(InterfaceRenaming.class);
		var methods = ctInterface.getElements(new TypeFilter<>(CtMethod.class));
		Refactoring.changeMethodName(methods.get(0), newName);

		assertEquals(newName, methods.get(0).getSimpleName(), "Default interface method name was not changed");
		CtClass<?> clazz = factory.Class().get(MethodRenaming.class);
		var executableRefs = clazz.getElements(new TypeFilter<>(CtExecutableReference.class));
		assertEquals(newName, executableRefs.get(2).getSimpleName(), "Default interface method reference was not changed");
	}

	@Test
	void testChangeAnnotationMethodName() throws Exception {
		String newName = "annotationMethodRenamed";
		Factory factory = ModelUtils.build(AnnotationMethodRenaming.class, ExampleAnnotation.class);
		CtType<?> ctInterface = factory.Annotation().get(ExampleAnnotation.class);
		var methods = ctInterface.getElements(new TypeFilter<>(CtAnnotationMethod.class));
		Refactoring.changeMethodName(methods.get(0), newName);

		assertEquals(newName, methods.get(0).getSimpleName(), "Annotation method name was not changed");
		CtClass<?> clazz = factory.Class().get(AnnotationMethodRenaming.class);
		var annotations = clazz.getElements(new TypeFilter<>(CtAnnotation.class));
		assertEquals(5, annotations.size());
		assertEquals((Integer) 0, annotations.get(0).getValue(newName).getValueByRole(CtRole.VALUE));
		assertEquals((Integer) 1, annotations.get(1).getValue(newName).getValueByRole(CtRole.VALUE));
		assertEquals((Integer) 1, annotations.get(2).getValue(newName).getValueByRole(CtRole.VALUE));
		assertEquals((Integer) 2, annotations.get(3).getValue(newName).getValueByRole(CtRole.VALUE));
		assertEquals((Integer) 3, annotations.get(4).getValue(newName).getValueByRole(CtRole.VALUE));

		var refs = clazz.getMethodsByName("processingTheAnnotation").get(0).getElements(new TypeFilter<>(CtExecutableReference.class));
		assertEquals(newName, refs.get(3).getSimpleName());
	}
}
