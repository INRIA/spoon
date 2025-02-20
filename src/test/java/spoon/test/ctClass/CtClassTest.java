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
package spoon.test.ctClass;


import java.io.File;
import java.util.Set;

import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.StandardEnvironment;
import spoon.test.SpoonTestHelpers;
import spoon.test.ctClass.testclasses.AnonymousClass;
import spoon.test.ctClass.testclasses.Foo;
import spoon.test.ctClass.testclasses.Pozole;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.test.SpoonTestHelpers.contentEquals;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class CtClassTest {

	@Test
	public void getConstructor() throws Exception {
		final Factory build = build(Foo.class);
		final CtClass<?> foo = (CtClass<?>) build.Type().get(Foo.class);

		assertEquals(3, foo.getConstructors().size());

		CtTypeReference<Object> typeString = build.Code().createCtTypeReference(String.class);
		CtConstructor<?> constructor = foo.getConstructor(typeString);
		assertEquals(typeString, constructor.getParameters().get(0).getType());

		CtArrayTypeReference<Object> typeStringArray = build.Core().createArrayTypeReference();
		typeStringArray.setComponentType(typeString);
		constructor = foo.getConstructor(typeStringArray);
		assertEquals(typeStringArray, constructor.getParameters().get(0).getType());

		CtArrayTypeReference<Object> typeStringArrayArray = build.Core().createArrayTypeReference();
		typeStringArrayArray.setComponentType(typeStringArray);
		constructor = foo.getConstructor(typeStringArrayArray);
		assertEquals(typeStringArrayArray, constructor.getParameters().get(0).getType());
		// contract: one could add a type member that already exists (equals but not same) and modify it afterwards
		// this adds some flexibility for client code
		// see https://github.com/INRIA/spoon/issues/1862
		CtConstructor cons = foo.getConstructors().toArray(new CtConstructor[0])[0].clone();
		foo.addConstructor(cons);
		int position = foo.getTypeMembers().indexOf(cons);
		// as long as we have not changed the signature, getConstructors, which is based on signatures,
		// thinks there is one single constructor (and that's OK)
		assertEquals(3, foo.getConstructors().size());
		cons.addParameter(cons.getFactory().createParameter().setType(cons.getFactory().Type().objectType()));
		// now that we have changed the signature we can call getConstructors safely
		assertEquals(4, foo.getConstructors().size());
		// we cloned the first constructor, so it has the same position, and comes before the 2nd and 3rd constructor
		assertSame(cons, foo.getTypeMembers().get(position));
		// the parent is set (the core problem described in the issue has been fixed)
		assertSame(foo, cons.getParent());

		// now we clone and reset the position
		CtConstructor cons2 = foo.getConstructors().toArray(new CtConstructor[0])[0].clone();
		cons2.setPosition(null);
		// adding the constructor, this time, without a position
		foo.addConstructor(cons2);
		// without position, it has been addded at the end
		assertSame(cons2, foo.getTypeMembers().get(4));

		// contract: setPositions sets the position of all children
		NoSourcePosition position1 = new NoSourcePosition();
		cons2.setPositions(position1);
		for(CtElement el: cons2.getDirectChildren()) {
			assertSame(position1, el.getPosition());
		}
	}

	@Test
	public void testParentOfTheEnclosingClassOfStaticClass() {
		// contract: When we have a static class which extends a superclass in the classpath,
		// the enclosing class don't have a superclass. This is probably a bug in JDT but good
		// luck to report a bug about noclasspath in their bugtracker. :)

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/InvariantChecker.java");
		launcher.addInputResource("./src/test/resources/noclasspath/FileIO.java");
		launcher.addInputResource("./src/test/resources/noclasspath/Daikon.java");
		launcher.setSourceOutputDirectory("./target/class");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("daikon.tools.InvariantChecker");

		final CtType<?> staticClass = aClass.getNestedType("InvariantCheckProcessor");
		assertNotNull(staticClass);
		assertEquals("InvariantCheckProcessor", staticClass.getSimpleName());
		assertNotNull(staticClass.getSuperclass());
		assertEquals("daikon.FileIO$Processor", staticClass.getSuperclass().getQualifiedName());
		assertNull(aClass.getSuperclass());

		canBeBuilt("./target/class", 8, true);
	}

	@Test
	public void testNoClasspathWithSuperClassOfAClassInAnInterface() {
		// contract: When we specify a superclass which is declared in an interface and
		// where the visibility is okay, we must use it.

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/draw2d");
		launcher.setSourceOutputDirectory("./target/draw2d");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.eclipse.draw2d.parts.ScrollableThumbnail");
		final CtType<?> innerClass = aClass.getNestedType("ClickScrollerAndDragTransferrer");
		assertEquals("org.eclipse.draw2d.MouseMotionListener$Stub", innerClass.getSuperclass().getQualifiedName());

		canBeBuilt("./target/draw2d", 8, true);
	}

	@Test
	public void testAllTypeReferencesToALocalTypeShouldNotStartWithNumber() throws Exception {
		// contract: When we have a local type, we should never start with its number. But this
		// number isn't rewrite only in the class declaration but in all type references and in
		// the constructor.
		final CtType<Pozole> aPozole = buildClass(Pozole.class);
		final CtClass<?> cook = aPozole.getNestedType("1Cook");

		assertEquals("1Cook", cook.getSimpleName());
		assertEquals("spoon.test.ctClass.testclasses.Pozole$1Cook", cook.getQualifiedName());
		final Set<? extends CtConstructor<?>> constructors = cook.getConstructors();
		final String expectedConstructor = "public Cook() {" + System.lineSeparator() + "}";
		assertEquals(expectedConstructor, constructors.toArray(new CtConstructor[constructors.size()])[0].toString());
		CtLocalVariable m = cook.getMethod("m").getBody().getStatement(0);
		assertEquals("final java.lang.Class<Cook> cookClass = Cook.class", m.toString());
		CtFieldAccess ac = (CtFieldAccess) m.getAssignment();
		assertEquals("class", ac.getVariable().getSimpleName());
		assertEquals(true, ac.getTarget() instanceof CtTypeAccess);

		// contract: one can call getModifiers on ".class" of "Aclass.class", it does not crash and it returns 0
		assertEquals(0, ac.getVariable().getModifiers().size());

		Factory factory = aPozole.getFactory();

		aPozole.removeModifier(ModifierKind.PUBLIC);
		factory.Code().createCodeSnippetStatement(aPozole.toString()).compile();

		CtClass internalClass = factory.Core().createClass();
		internalClass.setSimpleName("Foo");
		cook.getParent(CtBlock.class).addStatement(internalClass);
		assertEquals("Foo", internalClass.getSimpleName());
		assertEquals("spoon.test.ctClass.testclasses.Pozole$Foo", internalClass.getQualifiedName());

		internalClass.addConstructor(factory.Core().createConstructor());
		CtConstructor cons = (CtConstructor) internalClass.getConstructors().toArray(new CtConstructor[0])[0];
		cons.setBody(factory.Core().createBlock());

		CtConstructorCall call = cook.getFactory().Core().createConstructorCall();
		call.setExecutable(cons.getReference());
		assertEquals(internalClass, internalClass.getReference().getDeclaration());
		assertEquals("new Foo()", call.toString());
		internalClass.insertAfter(call);

		factory.getEnvironment().setAutoImports(true);
		factory.Code().createCodeSnippetStatement(aPozole.toString()).compile();

		factory.getEnvironment().setAutoImports(false);
		factory.Code().createCodeSnippetStatement(aPozole.toString()).compile();
	}

	@Test
	public void testSpoonShouldInferImplicitPackageInNoClasspath() {
		// contract: in noClasspath, when a type is used and no import is specified, then Spoon
		// should infer that this type is in the same package as the current class.
		final Launcher launcher2 = new Launcher();
		launcher2.addInputResource("./src/test/resources/noclasspath/issue1293/com/cristal/ircica/applicationcolis/userinterface/fragments/TransporteurFragment.java");
		launcher2.getEnvironment().setNoClasspath(true);
		launcher2.buildModel();

		final CtClass<Object> aClass2 = launcher2.getFactory().Class().get("com.cristal.ircica.applicationcolis.userinterface.fragments.TransporteurFragment");
		final String type2 = aClass2.getSuperclass().getQualifiedName();

		CtField field = aClass2.getField("transporteurRadioGroup");
		assertThat(field.getType().getQualifiedName(), is("android.widget.RadioGroup"));

		assertThat(type2, is("com.cristal.ircica.applicationcolis.userinterface.fragments.CompletableFragment"));
	}

	@Test
	public void toStringWithImports() {
		String newLine = System.getProperty("line.separator");

		final Launcher launcher2 = new Launcher();
		launcher2.addInputResource("./src/test/java/spoon/test/ctClass/");
		launcher2.getEnvironment().setNoClasspath(true);
		launcher2.buildModel();
		final CtClass<Object> aClass2 = launcher2.getFactory().Class().get(AnonymousClass.class);
		DefaultJavaPrettyPrinter djpp = new DefaultJavaPrettyPrinter(launcher2.getEnvironment());
		aClass2.accept(djpp);

		// contract: a class can be printed with full context
		assertEquals("package spoon.test.ctClass.testclasses;" + newLine +
				"/**" + newLine +
				" * Created by urli on 11/10/2017." + newLine +
				" */" + newLine +
				"public class AnonymousClass {" + newLine +
				"    final int machin = new java.util.Comparator<java.lang.Integer>() {" + newLine +
				"        @java.lang.Override" + newLine +
				"        public int compare(java.lang.Integer o1, java.lang.Integer o2) {" + newLine +
				"            return 0;" + newLine +
				"        }" + newLine +
				"    }.compare(1, 2);" + newLine +
				"}" + newLine, aClass2.toStringWithImports());

		// contract: a class can be printed with full context in autoimports
		aClass2.getFactory().getEnvironment().setAutoImports(true);
		assertEquals("package spoon.test.ctClass.testclasses;" + newLine +
				"import java.util.Comparator;" + newLine +
				"/**" + newLine +
				" * Created by urli on 11/10/2017." + newLine +
				" */" + newLine +
				"public class AnonymousClass {" + newLine +
				"    final int machin = new Comparator<Integer>() {" + newLine +
				"        @Override" + newLine +
				"        public int compare(Integer o1, Integer o2) {" + newLine +
				"            return 0;" + newLine +
				"        }" + newLine +
				"    }.compare(1, 2);" + newLine +
				"}" + newLine, aClass2.toStringWithImports());

		// contract: toStringWithImports works with a new class with no position
		assertEquals("package foo;" + newLine +
				"import java.io.File;" + newLine +
				"class Bar extends File {}" + newLine, launcher2.getFactory().createClass("foo.Bar").setSuperclass(launcher2.getFactory().Type().get(File.class).getReference()).toStringWithImports());
	}

	@Test
	public void testDefaultConstructorAreOk() {
		// contract: When we specify a superclass which is declared in an interface and
		// where the visibility is okay, we must use it.

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctClass/testclasses/issue1306");
		launcher.setSourceOutputDirectory("./target/issue1306");
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setShouldCompile(true);
		launcher.getEnvironment().setAutoImports(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("spoon.test.ctClass.testclasses.issue1306.internal.BooleanArraysBaseTest");
		assertThat(aClass, notNullValue());

		canBeBuilt("./target/issue1306", 8, true);
	}

	@Test
	public void testCloneAnonymousClassInvocation() {
		// contract: after cloning an anonymous class invocation, we still should be able to print it, when not using autoimport

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctClass/testclasses/AnonymousClass.java");
		launcher.getEnvironment().setAutoImports(false);
		launcher.buildModel();

		CtModel model = launcher.getModel();
		CtNewClass newClassInvocation = launcher.getModel().getElements(new TypeFilter<>(CtNewClass.class)).get(0);
		CtNewClass newClassInvocationCloned = newClassInvocation.clone();

		CtClass anonymousClass = newClassInvocation.getAnonymousClass();
		CtClass anonymousClassCloned = newClassInvocationCloned.getAnonymousClass();

		// The test stops failing if we set the parent below
		//newClassInvocationCloned.setParent(launcher.getFactory().Class().get(AnonymousClass.class));

		assertEquals(0, anonymousClass.getAllFields().size());
		assertEquals(0, anonymousClassCloned.getAllFields().size());

		assertFalse(newClassInvocation.toString().isEmpty());
		assertFalse(newClassInvocationCloned.toString().isEmpty());

		assertEquals(newClassInvocation.toString(), newClassInvocationCloned.toString());
	}

	@Test
	public void testCloneAnonymousClassInvocationWithAutoimports() {
		// contract: after cloning an anonymous class invocation, we still should be able to print it, when using autoimport

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctClass/testclasses/AnonymousClass.java");
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();

		CtModel model = launcher.getModel();
		CtNewClass newClassInvocation = launcher.getModel().getElements(new TypeFilter<>(CtNewClass.class)).get(0);
		CtNewClass newClassInvocationCloned = newClassInvocation.clone();

		CtClass anonymousClass = newClassInvocation.getAnonymousClass();
		CtClass anonymousClassCloned = newClassInvocationCloned.getAnonymousClass();

		// The test stops failing if we set the parent below
		//newClassInvocationCloned.setParent(launcher.getFactory().Class().get(AnonymousClass.class));

		assertEquals(0, anonymousClass.getAllFields().size());
		assertEquals(0, anonymousClassCloned.getAllFields().size());

		assertFalse(newClassInvocation.toString().isEmpty());
		assertFalse(newClassInvocationCloned.toString().isEmpty());

		assertEquals(newClassInvocation.toString(), newClassInvocationCloned.toString());
	}

	@Test
	@Timeout(unit = TimeUnit.MILLISECONDS, value = 5000L)
	public void test_buildParameterizedClass_withTypeParameterUsedInQualifiedName() {
		// contract: It should be possible to build a generic class when one of the type parameters
		// is used in the qualified name of another type.
        //
		// See https://github.com/INRIA/spoon/issues/3903

		CtClass<?> cls = Launcher.parseClass(
				"public class Main<T extends java.util.Map<String, String>> { "
				// Here we add a field with type `T.Entry`, i.e. T is used in a qualified type name
				+ "T.Entry<String, String> entry; }");

		CtField<?> field = cls.getField("entry");
		assertThat(field.getType().getQualifiedName(), equalTo("T$Entry"));
		assertThat(field.getType().isSimplyQualified(), is(false));
	}

	@Test
	public void testRemoveAnnotation() {
		// contract: removeAnnotation returns true after removing an annotation of a class containing a single
		// annotation, and returns false when a non existing annotation is tried to be removed

		// arrange
		CtClass<?> annotatedClass = Launcher.parseClass("@SuppressWarnings(\"unchecked\") class Annotated { }");
		assertEquals(1, annotatedClass.getAnnotations().size());
		CtAnnotation<?> annotationToBeRemoved = annotatedClass.getAnnotations().get(0);

		// act
		boolean firstRemovalSuccessful = annotatedClass.removeAnnotation(annotationToBeRemoved);
		boolean secondRemovalSuccessful = annotatedClass.removeAnnotation(annotationToBeRemoved);

		// assert
		assertEquals(0, annotatedClass.getAnnotations().size());
		assertTrue(firstRemovalSuccessful);
		assertFalse(secondRemovalSuccessful);
	}

	@Test
	void testLocalClassExists() {
		// contract: local classes and their members are part of the model
		String code = SpoonTestHelpers.wrapLocal(
				"		class MyClass {\n" +
						"			private int field = 2;\n" +
						"			public void doNothing() { }\n" +
						"		}\n"
		);
		CtModel model = SpoonTestHelpers.createModelFromString(code, StandardEnvironment.DEFAULT_CODE_COMPLIANCE_LEVEL);
		CtBlock<?> block = SpoonTestHelpers.getBlock(model);

		MatcherAssert.assertThat("The local class does not exist in the model", block.getStatements().size(), CoreMatchers.is(1));

		CtStatement statement = block.getStatement(0);
		Assertions.assertTrue(statement instanceof CtClass<?>);
		CtClass<?> clazz = (CtClass<?>) statement;

		MatcherAssert.assertThat(clazz.isLocalType(), CoreMatchers.is(true));
		MatcherAssert.assertThat(clazz.getSimpleName(), CoreMatchers.is("1MyClass"));
		MatcherAssert.assertThat(clazz.getFields().size(), CoreMatchers.is(1));
		MatcherAssert.assertThat(clazz.getMethods().size(), CoreMatchers.is(1));
		MatcherAssert.assertThat(clazz.getExtendedModifiers(), contentEquals());
	}
}
