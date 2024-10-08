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
package spoon.test.ctType;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.test.ctType.testclasses.X;
import spoon.testing.utils.ModelTest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtTypeTest {
	@Test
	public void testHasMethodInDirectMethod() {
		CtClass<?> clazz = createFactory().Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		assertTrue(clazz.hasMethod(clazz.getMethods().iterator().next()));
	}

	@Test
	public void testHasMethodNotHasMethod() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		CtClass<?> clazz2 = factory.Code().createCodeSnippetStatement(
			"class Y { public void foo2() {} }").compile();
		assertFalse(clazz.hasMethod(clazz2.getMethods().iterator().next()));
	}

	@Test
	public void testHasMethodOnNull() {
		CtClass<?> clazz = createFactory().Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		assertFalse(clazz.hasMethod(null));
	}

	@Test
	public void testHasMethodInSuperClass() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/X.java");
		launcher.run();

		final CtClass<?> xClass = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.X");
		final CtClass<?> yClass = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.Y");
		final CtMethod<?> superMethod = xClass.getMethods().iterator().next();

		assertTrue(yClass.hasMethod(superMethod));
	}

	@ModelTest(value = "./src/test/java/spoon/test/ctType/testclasses/X.java", complianceLevel = 8)
	public void testHasMethodInDefaultMethod(Launcher launcher, Factory factory) {
		final CtClass<?> x = factory.Class().get("spoon.test.ctType.testclasses.W");
		final CtInterface<?> z = factory.Interface().get("spoon.test.ctType.testclasses.Z");
		final CtMethod<?> superMethod = z.getMethods().iterator().next();

		assertTrue(x.hasMethod(superMethod));
	}

	@Test
	public void testIsSubTypeOf() throws Exception {
		CtType<X> xCtType = buildClass(X.class);
		CtType<?> yCtType = xCtType.getFactory().Type().get("spoon.test.ctType.testclasses.Y");

		assertFalse(xCtType.isSubtypeOf(yCtType.getReference()));
		assertTrue(yCtType.isSubtypeOf(xCtType.getReference()));
		//contract: x isSubtypeOf x
		//using CtTypeReference implementation
		assertTrue(xCtType.getReference().isSubtypeOf(xCtType.getReference()));
		//using CtType implementation
		assertTrue(xCtType.isSubtypeOf(xCtType.getReference()));
	}

	@Test
	public void testIsSubTypeOfonTypeParameters() throws Exception {
		CtType<X> xCtType = buildClass(X.class);
		Factory factory = xCtType.getFactory();

		CtType<?> oCtType = factory.Type().get("spoon.test.ctType.testclasses.O");
		CtType<?> pCtType = factory.Type().get("spoon.test.ctType.testclasses.P");
		CtTypeReference<?> objectCtTypeRef = factory.Type().objectType();

		List<CtTypeParameter> oTypeParameters = oCtType.getFormalCtTypeParameters();
		assertTrue(oTypeParameters.size() == 1);
		List<CtTypeParameter> pTypeParameters = pCtType.getFormalCtTypeParameters();
		assertTrue(pTypeParameters.size() == 2);

		CtType<?> O_A_CtType = oTypeParameters.get(0);
		CtType<?> P_D_CtType = pTypeParameters.get(0);
		CtType<?> P_F_CtType = pTypeParameters.get(1);

		CtMethod<?> O_FooMethod = oCtType.filterChildren(new NamedElementFilter<>(CtMethod.class, "foo")).first();
		CtMethod<?> P_FooMethod = pCtType.filterChildren(new NamedElementFilter<>(CtMethod.class, "foo")).first();

		CtType<?> O_B_CtType = O_FooMethod.getType().getDeclaration();
		CtType<?> P_E_CtType = P_FooMethod.getType().getDeclaration();

		assertTrue(O_B_CtType.isSubtypeOf(xCtType.getReference()));
		assertTrue(O_B_CtType.isSubtypeOf(O_A_CtType.getReference()));

		assertTrue(P_E_CtType.isSubtypeOf(xCtType.getReference()));
		assertTrue(P_E_CtType.isSubtypeOf(P_D_CtType.getReference()));
		assertTrue(P_E_CtType.isSubtypeOf(O_A_CtType.getReference()));

		assertTrue(P_D_CtType.isSubtypeOf(O_A_CtType.getReference()));
		assertTrue(P_E_CtType.isSubtypeOf(O_B_CtType.getReference()));

		assertTrue(P_E_CtType.isSubtypeOf(objectCtTypeRef));
		assertTrue(P_F_CtType.isSubtypeOf(objectCtTypeRef));
	}

	@Test
	public void testIsSubTypeOfonTypeReferences() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{"-c"});
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/SubtypeModel.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();

		CtType<?> oCtType = factory.Class().get("spoon.test.ctType.testclasses.SubtypeModel");
		CtMethod<?> O_FooMethod = oCtType.filterChildren(new NamedElementFilter<>(CtMethod.class, "foo")).first();

		Map<String, CtTypeReference<?>> nameToTypeRef = new HashMap<>();
		O_FooMethod.filterChildren(new TypeFilter<>(CtLocalVariable.class)).forEach((CtLocalVariable var) -> {
			nameToTypeRef.put(var.getSimpleName(), var.getType());
		});

		int[] count = new int[1];

		O_FooMethod.filterChildren(new TypeFilter<>(CtAssignment.class)).forEach((CtAssignment ass) -> {
			for (CtComment comment : ass.getComments()) {
				checkIsNotSubtype(comment, nameToTypeRef);
				count[0]++;
			}
			count[0]++;
			checkIsSubtype(((CtVariableAccess) ass.getAssigned()).getVariable().getType(), ((CtVariableAccess) ass.getAssignment()).getVariable().getType(), nameToTypeRef);
		});

		assertTrue(count[0] > (9 * 8));

		// contract: isSubTypeOf does not throw any exception
		// #2288 cannot be reproduced, probably fixed by #2406
		CtTypeReference<Object> typeReferenceWithNoDeclaration = launcher.getFactory().createTypeReference();
		typeReferenceWithNoDeclaration.setSimpleName("DoesNotExist");
		assertFalse(typeReferenceWithNoDeclaration.isSubtypeOf(oCtType.getReference()));
		assertFalse(oCtType.isSubtypeOf(typeReferenceWithNoDeclaration));
	}

	private void checkIsSubtype(CtTypeReference superType, CtTypeReference subType, Map<String, CtTypeReference<?>> nameToTypeRef) {
		String msg = getTypeName(subType) + " isSubTypeOf " + getTypeName(superType);
		assertTrue(subType.isSubtypeOf(superType), msg);
	}

	private static final Pattern assignment = Pattern.compile("\\s*(\\w+)\\s*=\\s*(\\w+);");

	private void checkIsNotSubtype(CtComment comment, Map<String, CtTypeReference<?>> nameToTypeRef) {
		Matcher m = assignment.matcher(comment.getContent());
		assertTrue(m.matches());
		CtTypeReference<?> superType = nameToTypeRef.get(m.group(1));
		CtTypeReference<?> subType = nameToTypeRef.get(m.group(2));
		String msg = getTypeName(subType) + " is NOT SubTypeOf " + getTypeName(superType);
		assertFalse(subType.isSubtypeOf(superType), msg);
	}

	private String getTypeName(CtTypeReference<?> ref) {
		String name;
		CtReference r = ref.getParent(CtReference.class);
		if (r != null) {
			name = r.getSimpleName();
		} else {
			name = ref.getParent(CtNamedElement.class).getSimpleName();
		}
		return ref.toString() + " " + name;
	}

	@Test
	public void testRetainsInterfaceOrder() {
		final Launcher launcher = new Launcher();
		List<String> expectedInterfaceOrder = Arrays.asList(
				"java.util.function.Supplier<java.lang.Integer>",
				"java.util.function.Consumer<java.lang.Integer>",
				"java.lang.Comparable<java.lang.Integer>"
		);
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/MultiInterfaceImplementation.java");

		CtModel model = launcher.buildModel();
		CtType<?> type = model.getAllTypes().iterator().next();
		List<String> interfaces = type.getSuperInterfaces()
				.stream().map(CtElement::toString).collect(Collectors.toList());

		assertEquals(expectedInterfaceOrder, interfaces);
	}
	@Test
	public void getUsedTypesWithWildcard() {
		//contract: Wildcard types like "?" shouldn't create a NPE. For more context see issue#3514
		String input = "src/test/resources/layornos/AllocationStorage.java";
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(input);
		CtModel model = launcher.buildModel();
		model.getAllTypes().forEach(type -> {
			type.getUsedTypes(false);
			});
	}

	@Test
	public void testTypeDeclarationToReferenceRoundTripInNamedModule() {
		// contract: It's possible to go from a type declaration, to a reference, and back to the declaration
		// when the declaration is contained within a named module

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.addInputResource("./src/test/resources/spoon/test/module/simple_module_with_code");
		launcher.buildModel();

		CtType<?> typeDecl = launcher.getFactory().Type().get("fr.simplemodule.pack.SimpleClass");
		CtTypeReference<?> typeRef = typeDecl.getReference();
		CtType<?> reFetchedTypeDecl = typeRef.getTypeDeclaration();

		assertSame(reFetchedTypeDecl, typeDecl);
	}
  
	@Test
	public void testSneakyThrowsInSubClasses() {
		// contract: Sneaky throws doesn't crash spoons method return type resolution.
		// see e.g https://projectlombok.org/features/SneakyThrows for explanation
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/npe");
		CtModel model = launcher.buildModel();
		assertDoesNotThrow(() -> model.getAllTypes().stream().forEach(CtType::getAllExecutables));
  }
  
  @Test
	public void testGetAllExecutablesOnTypeImplementingNestedInterface() {
		// contract: implicit static nested interfaces are correct handled in getAllExecutables.
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/extendsStaticInnerType");
		CtModel model = launcher.buildModel();
		CtType<?> type = model.getAllTypes().stream().filter(v -> v.getSimpleName().contains("BarBaz")).findAny().get();
		int expectedNumExecutablesInJDK8 = 13;
		int expectedNumExecutablesPostJDK8 = 14;
		int numExecutables = type.getAllExecutables().size();
		assertThat(numExecutables, anyOf(
				equalTo(expectedNumExecutablesInJDK8),
				equalTo(expectedNumExecutablesPostJDK8))
		);	
	}

	/**
	 * This test captures keyword constraint in CtReferenceImpl based on the compliance level, since the keyword
	 * "enum" was only introduced in Java 5
	 */
	@Test
	@Disabled("Compliance level 4 is not supported anymore")
	public void testEnumPackage() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/keywordCompliance/enum/Foo.java");
		launcher.getEnvironment().setComplianceLevel(4);
		launcher.run();

		Collection<CtType<?>> types = launcher.getModel().getAllTypes();
		assertThat(types.size(), is(1));
		assertThat(types.stream().findFirst().get(), notNullValue());
		assertThat(types.stream().findFirst().get().getQualifiedName(), is("keywordCompliance.enum.Foo"));
	}

	@Test
	void testRecordInnerClassesHaveDefinition() {
		// contract: Record inner classes should have a definition
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("class Foo {\n" +
			"  class Inner {\n" +
			"    record Inner2(String name) {\n" +
			"    }\n" +
			"  }\n" +
			"}"));
		launcher.getEnvironment().setComplianceLevel(17);
		CtType<?> foo = launcher.buildModel().getAllTypes().iterator().next();
		assertEquals(foo, foo.getReference().getTypeDeclaration());
		assertEquals(1, foo.getNestedTypes().size());

		for (CtType<?> nestedType : foo.getNestedTypes()) {
			assertEquals(nestedType, nestedType.getReference().getTypeDeclaration());
			assertEquals(1, nestedType.getNestedTypes().size());
			for (CtType<?> type : nestedType.getNestedTypes()) {
				assertEquals(type, type.getReference().getTypeDeclaration());
			}
		}
	}
}
