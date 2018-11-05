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
package spoon.test.imports;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.visitor.CtImportVisitor;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.ImportScanner;
import spoon.reflect.visitor.ImportScannerImpl;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.SuperInheritanceHierarchyFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.util.SortedList;
import spoon.test.imports.testclasses.A;
import spoon.test.imports.testclasses.ClassWithInvocation;
import spoon.test.imports.testclasses.ClientClass;
import spoon.test.imports.testclasses.Mole;
import spoon.test.imports.testclasses.NotImportExecutableType;
import spoon.test.imports.testclasses.Pozole;
import spoon.test.imports.testclasses.Reflection;
import spoon.test.imports.testclasses.StaticNoOrdered;
import spoon.test.imports.testclasses.SubClass;
import spoon.test.imports.testclasses.Tacos;
import spoon.test.imports.testclasses.internal.ChildClass;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class ImportTest {

	@Test
	public void testImportOfAnInnerClassInASuperClassPackageAutoImport() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setShouldCompile(true);
		spoon.getEnvironment().setAutoImports(true);
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/SuperClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/ChildClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/PublicInterface2.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClientClass.java");
		spoon.setBinaryOutputDirectory("./target/spoon/super_imports/bin");
		spoon.setSourceOutputDirectory("./target/spoon/super_imports/src");
		spoon.run();

		final List<CtClass> classes = Query.getElements(spoon.getFactory(), new NamedElementFilter<>(CtClass.class,"ClientClass"));

		final CtType<?> innerClass = classes.get(0).getNestedType("InnerClass");
		String expected = "spoon.test.imports.testclasses.ClientClass.InnerClass";
		assertEquals(expected, innerClass.getReference().toString());

		//test that acces path depends on the context
		//this checks the access path in context of innerClass. The context is defined by CtTypeReference.getParent(CtType.class). 
		assertEquals("spoon.test.imports.testclasses.internal.ChildClass.InnerClassProtected", innerClass.getSuperclass().toString());
		//this checks the access path in context of SuperClass. The context is defined by CtTypeReference.getParent(CtType.class) 
		assertEquals("spoon.test.imports.testclasses.internal.SuperClass.InnerClassProtected", innerClass.getSuperclass().getTypeDeclaration().getReference().toString());
		assertEquals("InnerClassProtected", innerClass.getSuperclass().getSimpleName());


		assertEquals("SuperClass", innerClass.getSuperclass().getDeclaringType().getSimpleName());
		assertEquals(spoon.getFactory().Class().get("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected"), innerClass.getSuperclass().getDeclaration());
	}

	@Test
	public void testImportOfAnInnerClassInASuperClassPackageFullQualified() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setShouldCompile(true);
		spoon.getEnvironment().setAutoImports(false);
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/SuperClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/ChildClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClientClass.java");
		spoon.setBinaryOutputDirectory("./target/spoon/super_imports/bin");
		spoon.setSourceOutputDirectory("./target/spoon/super_imports/src");
		spoon.run();

		final List<CtClass> classes = Query.getElements(spoon.getFactory(), new NamedElementFilter<>(CtClass.class,"ClientClass"));

		final CtType<?> innerClass = classes.get(0).getNestedType("InnerClass");
		
		assertEquals("spoon.test.imports.testclasses.ClientClass$InnerClass", innerClass.getQualifiedName());
		
		String expected = "spoon.test.imports.testclasses.ClientClass.InnerClass";
		assertEquals(expected, innerClass.getReference().toString());

		assertEquals("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected", innerClass.getSuperclass().getQualifiedName());

		expected = "spoon.test.imports.testclasses.internal.ChildClass.InnerClassProtected";
		assertEquals(expected, innerClass.getSuperclass().toString());

		assertEquals("SuperClass", innerClass.getSuperclass().getDeclaringType().getSimpleName());
		assertEquals(spoon.getFactory().Class().get("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected"), innerClass.getSuperclass().getDeclaration());
	}

	@Test
	public void testImportOfAnInnerClassInASuperClassAvailableInLibrary() throws Exception {
		SpoonModelBuilder comp = new Launcher().createCompiler();
		List<SpoonResource> fileToBeSpooned = SpoonResourceHelper.resources("./src/test/resources/visibility/YamlRepresenter.java");
		assertEquals(1, fileToBeSpooned.size());
		comp.addInputSources(fileToBeSpooned);
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/visibility/snakeyaml-1.9.jar");
		assertEquals(1, classpath.size());
		comp.setSourceClasspath(classpath.get(0).getPath());
		comp.build();
		Factory factory = comp.getFactory();
		CtType<?> theClass = factory.Type().get("visibility.YamlRepresenter");

		final CtClass<?> innerClass = theClass.getNestedType("RepresentConfigurationSection");
		String expected = "visibility.YamlRepresenter.RepresentConfigurationSection";
		assertEquals(expected, innerClass.getReference().toString());

		expected = "org.yaml.snakeyaml.representer.Representer.RepresentMap";
		assertEquals(expected, innerClass.getSuperclass().toString());
	}

	@Test
	public void testImportOfAnInnerClassInAClassPackage() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		Factory factory = spoon.createFactory();

		SpoonModelBuilder compiler = spoon.createCompiler(factory, SpoonResourceHelper
				.resources("./src/test/java/spoon/test/imports/testclasses/internal/PublicSuperClass.java", "./src/test/java/spoon/test/imports/testclasses/DefaultClientClass.java"));

		compiler.build();

		final CtClass<?> client = (CtClass<?>) factory.Type().get("spoon.test.imports.testclasses.DefaultClientClass");
		final CtMethod<?> methodVisit = client.getMethodsByName("visit").get(0);

		final CtType<Object> innerClass = factory.Type().get("spoon.test.imports.testclasses.DefaultClientClass$InnerClass");
		assertEquals("Type of the method must to be InnerClass accessed via DefaultClientClass.", innerClass, methodVisit.getType().getDeclaration());
	}

	@Test
	public void testNewInnerClassDefinesInItsClassAndSuperClass() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		Factory factory = spoon.createFactory();

		SpoonModelBuilder compiler = spoon.createCompiler(factory,
				SpoonResourceHelper.resources("./src/test/java/spoon/test/imports/testclasses/SuperClass.java", "./src/test/java/spoon/test/imports/testclasses/SubClass.java"));

		compiler.build();
		final CtClass<?> subClass = (CtClass<?>) factory.Type().get(SubClass.class);
		final CtConstructorCall<?> ctConstructorCall = subClass.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class)).get(0);

		assertEquals("new spoon.test.imports.testclasses.SubClass.Item(\"\")", ctConstructorCall.toString());

		// here the buggy behavior with type members was encoded
		// so we fix it
		final String expected = "public class SubClass extends spoon.test.imports.testclasses.SuperClass {" +
		System.lineSeparator() + "    public void aMethod() {" + System.lineSeparator()
				+ "        new spoon.test.imports.testclasses.SubClass.Item(\"\");"+   System.lineSeparator() + "    }"
				+   System.lineSeparator() + System.lineSeparator() +   "    public static class Item extends spoon.test.imports.testclasses.SuperClass.Item {" + System.lineSeparator() + "        public Item(java.lang.String s) {" + System
				.lineSeparator() + "            super(1, s);" + System.lineSeparator() + "        }" + System.lineSeparator() + "    }" + System.lineSeparator() + "}";
		assertEquals(expected, subClass.toString());
	}

	@Test
	public void testMissingImport() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setLevel("OFF");

		SpoonModelBuilder compiler = spoon.createCompiler(factory, SpoonResourceHelper.resources("./src/test/resources/import-resources/fr/inria/MissingImport.java"));

		compiler.build();
		CtTypeReference<?> type = factory.Class().getAll().get(0).getFields().get(0).getType();
		assertEquals("Abcd", type.getSimpleName());
		assertEquals("fr.inria.internal", type.getPackage().getSimpleName());
	}

	@Test
	public void testAnotherMissingImport() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setLevel("OFF");

		SpoonModelBuilder compiler = spoon.createCompiler(factory, SpoonResourceHelper.resources("./src/test/resources/import-resources/fr/inria/AnotherMissingImport.java"));

		compiler.build();
		List<CtMethod> methods = factory.getModel().getElements(new NamedElementFilter<>(CtMethod.class,"doSomething"));

		List<CtParameter> parameters = methods.get(0).getParameters();
		CtTypeReference<?> type = parameters.get(0).getType();
		assertEquals("SomeType", type.getSimpleName());
		assertEquals("externallib", type.getPackage().getSimpleName());

		CtMethod<?> mainMethod = factory.Class().getAll().get(0).getMethodsByName("main").get(0);
		List<CtStatement> statements = mainMethod.getBody().getStatements();

		CtStatement invocationStatement = statements.get(1);

		assertTrue(invocationStatement instanceof CtInvocation);

		CtInvocation invocation = (CtInvocation) invocationStatement;
		CtExecutableReference executableReference = invocation.getExecutable();

		assertEquals("doSomething(externallib.SomeType)", executableReference.getSignature());
		assertSame(methods.get(0), executableReference.getDeclaration());
	}

	@Test
	public void testSpoonWithImports() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses", "--output-type", "nooutput", "--with-imports"
		});
		final CtClass<ImportTest> aClass = launcher.getFactory().Class().get(ChildClass.class);
		final CtClass<ImportTest> anotherClass = launcher.getFactory().Class().get(ClientClass.class);
		final CtClass<ImportTest> classWithInvocation = launcher.getFactory().Class().get(ClassWithInvocation.class);

		ImportScanner importScanner = new ImportScannerImpl();
		importScanner.computeImports(aClass);
		assertEquals(2, importScanner.getAllImports().size());

		importScanner = new ImportScannerImpl();
		importScanner.computeImports(anotherClass);
		//ClientClass needs 2 imports: ChildClass, PublicInterface2
		Collection<CtImport> allImports = importScanner.getAllImports();
		assertEquals(2, allImports.size());

		//check that printer did not used the package protected class like "SuperClass.InnerClassProtected"
		assertTrue(anotherClass.toString().indexOf("InnerClass extends ChildClass.InnerClassProtected")>0);
		importScanner = new ImportScannerImpl();
		importScanner.computeImports(classWithInvocation);
		// java.lang imports are also computed
		allImports = importScanner.getAllImports();
		assertEquals("Spoon ignores the arguments of CtInvocations", 1, allImports.size());
	}

	@Test
	public void testStaticImportForInvocationInNoClasspath() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/resources/import-static", "--output-type", "nooutput", "--noclasspath"
		});

		final List<CtInvocation<?>> elements = new SortedList(new CtLineElementComparator());
		elements.addAll(Query.getElements(launcher.getFactory(), new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return !element.getExecutable().isConstructor() && super.matches(element);
			}
		}));

		// Invocation for a static method with the declaring class specified.
		assertCorrectInvocation(new Expected().name("staticMethod").target("A").declaringType("A").typeIsNull(true), elements.get(0));

		// Invocation for a static method without the declaring class specified.
		assertCorrectInvocation(new Expected().name("staticMethod").target("pack1.A").declaringType("A").typeIsNull(true), elements.get(1));

		// Invocation for a static method with the declaring class specified and a return type.
		assertCorrectInvocation(new Expected().name("staticMethod").target("A").declaringType("A").typeIsNull(false), elements.get(2));

		// Invocation for a static method without the declaring class specified and a return type.
		assertCorrectInvocation(new Expected().name("staticMethod").target("pack1.A").declaringType("A").typeIsNull(false), elements.get(3));

		// Invocation for a static method in an inner class with the declaring class specified.
		assertCorrectInvocation(new Expected().name("makeBurritos").target("Tacos.Burritos").declaringType("Burritos").typeIsNull(false), elements.get(4));

		// Invocation for a static method in an inner class without the declaring class specified.
		assertCorrectInvocation(new Expected().name("makeBurritos").target("Tacos.Burritos").declaringType("Burritos").typeIsNull(true), elements.get(5));

		// Invocation for a static method in an inner class with the declaring class specified and a return type.
		assertCorrectInvocation(new Expected().name("makeBurritos").target("Tacos.Burritos").declaringType("Burritos").typeIsNull(false), elements.get(6));

		// Invocation for a static method in an innser class without the declaring class specified and a return type.
		assertCorrectInvocation(new Expected().name("makeBurritos").target("Tacos.Burritos").declaringType("Burritos").typeIsNull(false), elements.get(7));

		// Invocation for a static method in an inner class with the declaring class specified.
		assertCorrectInvocation(new Expected().name("staticD").target("C.D").declaringType("D").typeIsNull(true), elements.get(8));

		// Invocation for a static method in an inner class without the declaring class specified.
		assertCorrectInvocation(new Expected().name("staticD").target("pack2.C.D").declaringType("D").typeIsNull(true), elements.get(9));

		// Invocation for a static method in an inner class with the declaring class specified and a return type.
		assertCorrectInvocation(new Expected().name("staticD").target("C.D").declaringType("D").typeIsNull(false), elements.get(10));

		// Invocation for a static method in an inner class without the declaring class specified and a return type.
		assertCorrectInvocation(new Expected().name("staticD").target("pack2.C.D").declaringType("D").typeIsNull(false), elements.get(11));

		// Invocation for a static method with the declaring class specified and an import *.
		assertCorrectInvocation(new Expected().name("staticE").target("pack3.E.E").declaringType("E").typeIsNull(true), elements.get(12));

		// Invocation for a static method without the declaring class specified and an import *.
		assertCorrectInvocationWithLimit(new Expected().name("staticE").typeIsNull(true), elements.get(13));

		// Invocation for a static method with the declaring class specified, a return type and an import *.
		assertCorrectInvocation(new Expected().name("staticE").target("pack3.E.E").declaringType("E").typeIsNull(false), elements.get(14));

		// Invocation for a static method without the declaring class specified, a return type and an import *.
		assertCorrectInvocationWithLimit(new Expected().name("staticE").typeIsNull(false), elements.get(15));
	}

	@Test
	public void testImportOfInvocationOfPrivateClass() {
		final Factory factory = getFactory(
				"./src/test/java/spoon/test/imports/testclasses/internal2/Chimichanga.java",
				"./src/test/java/spoon/test/imports/testclasses/Mole.java");

		ImportScanner importContext = new ImportScannerImpl();
		importContext.computeImports(factory.Class().get(Mole.class));

		Collection<CtImport> imports = importContext.getAllImports();

		assertEquals(1, imports.size());
		assertEquals("import spoon.test.imports.testclasses.internal2.Chimichanga;", imports.toArray()[0].toString().trim());
	}

	@Test
	public void testNotImportExecutableType() {
		final Factory factory = getFactory(
				"./src/test/java/spoon/test/imports/testclasses/internal3/Foo.java",
				"./src/test/java/spoon/test/imports/testclasses/internal3/Bar.java",
				"./src/test/java/spoon/test/imports/testclasses/NotImportExecutableType.java");

		ImportScanner importContext = new ImportScannerImpl();
		importContext.computeImports(factory.Class().get(NotImportExecutableType.class));

		Collection<CtImport> imports = importContext.getAllImports();

		assertEquals(2, imports.size());
		Set<String> expectedImports = new HashSet<>(
				Arrays.asList("spoon.test.imports.testclasses.internal3.Foo", "java.io.File"));
		Set<String> actualImports = imports.stream().map(CtImport::getReference).map(CtReference::toString).collect(Collectors.toSet());
		assertEquals(expectedImports, actualImports);
	}

	@Test
	public void testImportOfInvocationOfStaticMethod() {
		final Factory factory = getFactory(
				"./src/test/java/spoon/test/imports/testclasses/internal2/Menudo.java",
				"./src/test/java/spoon/test/imports/testclasses/Pozole.java");

		ImportScanner importContext = new ImportScannerImpl();
		importContext.computeImports(factory.Class().get(Pozole.class));

		Collection<CtImport> imports = importContext.getAllImports();

		assertEquals(1, imports.size());
		assertEquals("import spoon.test.imports.testclasses.internal2.Menudo;", imports.toArray()[0].toString().trim());
	}

	@Test
	public void testImportStaticAndFieldAccess() {
		// contract: Qualified field access and an import static should rewrite in fully qualified mode.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal4/");
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/Tacos.java");
		launcher.buildModel();

		final CtType<Object> aTacos = launcher.getFactory().Type().get(Tacos.class);
		final CtStatement assignment = aTacos.getMethod("m").getBody().getStatement(0);
		assertTrue(assignment instanceof CtLocalVariable);
		assertEquals("spoon.test.imports.testclasses.internal4.Constants.CONSTANT.foo", ((CtLocalVariable) assignment).getAssignment().toString());
	}

	@Test
	public void testImportStaticAndFieldAccessWithImport() {
		// contract: Qualified field access and an import static with import should import the type first, and not use static import
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput", "--with-imports" });
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal4/");
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/Tacos.java");
		launcher.buildModel();

		final CtType<Object> aTacos = launcher.getFactory().Type().get(Tacos.class);
		final CtStatement assignment = aTacos.getMethod("m").getBody().getStatement(0);
		assertTrue(assignment instanceof CtLocalVariable);
		assertEquals("Constants.CONSTANT.foo", ((CtLocalVariable) assignment).getAssignment().toString());
	}

	@Test
	public void testFullQualifiedNameImport() throws Exception {
		String newLine = System.getProperty("line.separator");

		Factory factory = ModelUtils.build(A.class);
		factory.getEnvironment().setAutoImports(true);

		CtClass<Object> aClass = factory.Class().get(A.class);
		assertEquals("public class A {" + newLine
				+ "    public class ArrayList extends java.util.ArrayList {}" + newLine
				+ "}", aClass.toString());
	}

	@Test
	public void testAccessToNestedClass() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses", "--with-imports"
		});
		launcher.buildModel();
		final CtClass<ImportTest> aClass = launcher.getFactory().Class().get(ClientClass.class.getName()+"$InnerClass");
		assertEquals(ClientClass.class.getName()+"$InnerClass", aClass.getQualifiedName()); 
		final CtTypeReference<?> parentClass = aClass.getSuperclass();
		//comment next line and parentClass.getActualClass(); will fail anyway
		assertEquals("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected", parentClass.getQualifiedName()); 
		Class<?> actualClass = parentClass.getActualClass();
		assertEquals("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected", actualClass.getName()); 
	}

	@Test
	public void testAccessType() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses", "--with-imports"
		});
		launcher.buildModel();
		final CtClass<ImportTest> aInnerClass = launcher.getFactory().Class().get(ClientClass.class.getName()+"$InnerClass");
		final CtClass<ImportTest> aSuperClass = launcher.getFactory().Class().get("spoon.test.imports.testclasses.internal.SuperClass");
		assertEquals(ClientClass.class.getName()+"$InnerClass", aInnerClass.getQualifiedName());
		
		//Check that access type of ClientClass$InnerClass in package protected class is still ClientClass
		assertEquals(ClientClass.class.getName(), aInnerClass.getReference().getAccessType().getQualifiedName());
		
		final CtTypeReference<?> innerClassProtectedByGetSuperClass = aInnerClass.getSuperclass();
		final CtTypeReference<?> innerClassProtectedByQualifiedName = launcher.getFactory().Class().get("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected").getReference();

		assertEquals("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected", innerClassProtectedByGetSuperClass.getQualifiedName()); 
		assertEquals("spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected", innerClassProtectedByQualifiedName.getQualifiedName()); 
		assertEquals("spoon.test.imports.testclasses.internal.ChildClass", innerClassProtectedByGetSuperClass.getAccessType().getQualifiedName());
		assertEquals("spoon.test.imports.testclasses.internal.SuperClass", innerClassProtectedByQualifiedName.getAccessType().getQualifiedName());
		assertEquals("spoon.test.imports.testclasses.internal.ChildClass.InnerClassProtected", innerClassProtectedByGetSuperClass.toString());
		assertEquals("spoon.test.imports.testclasses.internal.SuperClass.InnerClassProtected", innerClassProtectedByQualifiedName.toString());
	}

	@Test
	public void testCanAccess() {
		
		class Checker {
			final Launcher launcher;
			final CtTypeReference<?> aClientClass;
			final CtTypeReference<?> anotherClass;
			Checker() {
				launcher = new Launcher();
				launcher.setArgs(new String[] {
						"-i", "./src/test/java/spoon/test/imports/testclasses", "--with-imports"
				});
				launcher.buildModel();
				aClientClass = launcher.getFactory().Class().get(ClientClass.class).getReference();
				anotherClass = launcher.getFactory().Class().get(Tacos.class).getReference();
			}
			void checkCanAccess(String aClassName, boolean isInterface, boolean canAccessClientClass, boolean canAccessAnotherClass, String clientAccessType, String anotherAccessType) {
				CtTypeReference<?> target;
				if(isInterface) {
					target = launcher.getFactory().Interface().create(aClassName).getReference();
				} else {
					target = launcher.getFactory().Class().get(aClassName).getReference();
				}
				boolean isNested = target.getDeclaringType()!=null;
				CtTypeReference<?> accessType;
				
				target.setParent(aClientClass.getTypeDeclaration());
				if(canAccessClientClass) {
					assertTrue("ClientClass should have access to "+aClassName+" but it has not", aClientClass.canAccess(target));
				} else {
					assertFalse("ClientClass should have NO access to "+aClassName+" but it has", aClientClass.canAccess(target));
				}
				if(isNested) {
					accessType = target.getAccessType();
					if(clientAccessType!=null) {
						assertEquals(clientAccessType, accessType.getQualifiedName());
					} else if(accessType!=null){
						fail("ClientClass should have NO accessType to "+aClassName+" but it has "+accessType.getQualifiedName());
					}
				}

				target.setParent(anotherClass.getTypeDeclaration());
				if(canAccessAnotherClass) {
					assertTrue("Tacos class should have access to "+aClassName+" but it has not", anotherClass.canAccess(target));
				} else {
					assertFalse("Tacos class should have NO access to "+aClassName+" but it has", anotherClass.canAccess(target));
				}
				if(isNested) {
					if(anotherAccessType!=null) {
						accessType = target.getAccessType();
						assertEquals(anotherAccessType, accessType.getQualifiedName());
					} else {
						try {
							accessType = target.getAccessType();
						} catch (SpoonException e) {
							if(!e.getMessage().contains("Cannot compute access path to type: ")) {
								throw e;
							}//else OK, it should throw exception
							accessType = null;
						}
						if(accessType!=null){
							fail("Tacos class should have NO accessType to "+aClassName+" but it has "+accessType.getQualifiedName());
						}
					}
				}
			}
		}
		Checker c = new Checker();

		c.checkCanAccess("spoon.test.imports.testclasses.ClientClass", false, true, true, null, null);
		c.checkCanAccess("spoon.test.imports.testclasses.ClientClass$InnerClass", false, true, false, "spoon.test.imports.testclasses.ClientClass", "spoon.test.imports.testclasses.ClientClass");
		c.checkCanAccess("spoon.test.imports.testclasses.internal.ChildClass", false, true, true, null, null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.PublicInterface2", true, true, true, null, null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.PublicInterface2$NestedInterface", true, true, true, "spoon.test.imports.testclasses.internal.PublicInterface2", "spoon.test.imports.testclasses.internal.PublicInterface2");
		c.checkCanAccess("spoon.test.imports.testclasses.internal.PublicInterface2$NestedClass", true, true, true, "spoon.test.imports.testclasses.internal.PublicInterface2", "spoon.test.imports.testclasses.internal.PublicInterface2");
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$PublicInterface", true, true, true, "spoon.test.imports.testclasses.internal.ChildClass", null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$PackageProtectedInterface", true, false, false, "spoon.test.imports.testclasses.internal.ChildClass", null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$ProtectedInterface", true, true, false, "spoon.test.imports.testclasses.internal.ChildClass", null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$ProtectedInterface$NestedOfProtectedInterface", true, true, true/*canAccess, but has no access to accessType*/, "spoon.test.imports.testclasses.internal.SuperClass$ProtectedInterface", null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$ProtectedInterface$NestedPublicInterface", true, true, true/*canAccess, but has no access to accessType*/, "spoon.test.imports.testclasses.internal.SuperClass$ProtectedInterface", null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$PublicInterface", true, true, true/*canAccess, but has no access to accessType*/, "spoon.test.imports.testclasses.internal.ChildClass", null);
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$PublicInterface$NestedOfPublicInterface", true, true, true/*canAccess, has access to first accessType, but not to full accesspath*/, "spoon.test.imports.testclasses.internal.SuperClass$PublicInterface", "spoon.test.imports.testclasses.internal.SuperClass$PublicInterface");
		c.checkCanAccess("spoon.test.imports.testclasses.internal.SuperClass$PublicInterface$NestedPublicInterface", true, true, true/*canAccess, has access to first accessType, but not to full accesspath*/, "spoon.test.imports.testclasses.internal.SuperClass$PublicInterface", "spoon.test.imports.testclasses.internal.SuperClass$PublicInterface");
	}

	@Test
	public void testNestedAccessPathWithTypedParameter() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/resources/spoon/test/imports/testclasses2/AbstractMapBasedMultimap.java"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap");
		CtClass<?> mmwli = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap$WrappedList$WrappedListIterator");
		assertEquals("private class WrappedListIterator extends spoon.test.imports.testclasses2.AbstractMapBasedMultimap<K, V>.WrappedCollection.WrappedIterator {}",mmwli.toString());
		assertTrue(mm.toString().contains("AbstractMapBasedMultimap<K, V>.WrappedCollection.WrappedIterator"));

		CtClass<?> mmwliother = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap$OtherWrappedList$WrappedListIterator");
		assertEquals("private class WrappedListIterator extends spoon.test.imports.testclasses2.AbstractMapBasedMultimap<K, V>.OtherWrappedList.WrappedIterator {}",mmwliother.toString());
		 								  
	}

	@Test
	public void testNestedAccessPathWithTypedParameterWithImports() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/resources/spoon/test/imports/testclasses2/AbstractMapBasedMultimap.java", "--with-imports"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}

		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap");
		CtClass<?> mmwli = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap$WrappedList$WrappedListIterator");
		assertEquals("private class WrappedListIterator extends AbstractMapBasedMultimap<K, V>.WrappedCollection.WrappedIterator {}",mmwli.toString());
		assertTrue(mm.toString().contains("AbstractMapBasedMultimap<K, V>.WrappedCollection.WrappedIterator"));

		CtClass<?> mmwliother = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap$OtherWrappedList$WrappedListIterator");
		assertEquals("private class WrappedListIterator extends AbstractMapBasedMultimap<K, V>.OtherWrappedList.WrappedIterator {}",mmwliother.toString());
	}

	@Test
	public void testNestedStaticPathWithTypedParameter() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/resources/spoon/test/imports/testclasses2/Interners.java"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.Interners");
		assertTrue(mm.toString().contains("java.util.List<spoon.test.imports.testclasses2.Interners.WeakInterner.Dummy> list;"));
	}

	@Test
	public void testNestedStaticPathWithTypedParameterWithImports() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/resources/spoon/test/imports/testclasses2/Interners.java", "--with-imports"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.Interners");
		assertTrue(mm.toString().contains("List<Interners.WeakInterner.Dummy> list;"));
	}

	@Test
	public void testDeepNestedStaticPathWithTypedParameter() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/resources/spoon/test/imports/testclasses2/StaticWithNested.java"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.StaticWithNested");
		assertTrue("new spoon.test.imports.testclasses2.StaticWithNested.StaticNested.StaticNested2<K>();", mm.toString().contains("new spoon.test.imports.testclasses2.StaticWithNested.StaticNested.StaticNested2<K>();"));
	}

	@Test
	public void testDeepNestedStaticPathWithTypedParameterWithImports() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/resources/spoon/test/imports/testclasses2/StaticWithNested.java", "--with-imports"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.StaticWithNested");
		assertTrue("new StaticWithNested.StaticNested.StaticNested2<K>();", mm.toString().contains("new StaticWithNested.StaticNested.StaticNested2<K>();"));
	}

	private Factory getFactory(String...inputs) {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		for (String input : inputs) {
			launcher.addInputResource(input);
		}
		launcher.run();
		return launcher.getFactory();
	}

	private void assertCorrectInvocation(Expected expected, CtInvocation<?> ctInvocation) {
		assertEquals(1, ctInvocation.getArguments().size());
		assertNotNull(ctInvocation.getTarget());
		assertTrue(ctInvocation.getTarget() instanceof CtTypeAccess);
		assertEquals(expected.target, ctInvocation.getTarget().toString());
		assertNotNull(ctInvocation.getExecutable());
		assertEquals(expected.name, ctInvocation.getExecutable().getSimpleName());
		assertNotNull(ctInvocation.getExecutable().getDeclaringType());
		assertEquals(expected.declaringType, ctInvocation.getExecutable().getDeclaringType().getSimpleName());
		assertEquals(expected.isNull, ctInvocation.getExecutable().getType() == null);
		assertEquals(1, ctInvocation.getExecutable().getParameters().size());
	}

	private void assertCorrectInvocationWithLimit(Expected expected, CtInvocation<?> ctInvocation) {
		assertEquals(1, ctInvocation.getArguments().size());
		assertTrue(ctInvocation.getTarget() instanceof CtThisAccess);
		assertNotNull(ctInvocation.getExecutable());
		assertEquals(expected.name, ctInvocation.getExecutable().getSimpleName());
		assertNull(ctInvocation.getExecutable().getDeclaringType());
		assertEquals(expected.isNull, ctInvocation.getExecutable().getType() == null);
		assertEquals(1, ctInvocation.getExecutable().getParameters().size());
	}

	private class Expected {
		String name;
		String target;
		String declaringType;
		boolean isNull;

		public Expected name(String name) {
			this.name = name;
			return this;
		}

		public Expected target(String target) {
			this.target = target;
			return this;
		}

		public Expected declaringType(String declaringType) {
			this.declaringType = declaringType;
			return this;
		}

		public Expected typeIsNull(boolean isNull) {
			this.isNull = isNull;
			return this;
		}
	}

	@Test
	public void testWithInnerEnumDoesNotImportStaticInnerMethods() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned-innerenum";
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/StaticImportsFromEnum.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();
		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		CtType element = launcher.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		assertTrue("The file should not contain a static import to the inner enum method values",!output.contains("import static spoon.test.imports.testclasses.StaticImportsFromEnum$DataElement.values;"));
		assertTrue("The file should not contain a static import to the inner enum method values of a distinct interface",!output.contains("import static spoon.test.imports.testclasses.ItfWithEnum$Bar.values;"));
		assertTrue("The file should not contain a static import to the inner enum value",!output.contains("import static spoon.test.imports.testclasses.ItfWithEnum$Bar.Lip;"));
		canBeBuilt(outputDir, 7);
	}

	@Test
	public void testStaticImportOfEnumField() {
		//contract: static import of enum field doesn't cause import of enum
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned-enumField";
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/Kun.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();
		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		CtType element = launcher.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		assertTrue("The file should not contain the import of enum",!output.contains("import spoon.reflect.path.CtRole;"));
		assertTrue("The file should contain the static import of enum field",!output.contains("import spoon.reflect.path.CtRole.NAME;"));
		canBeBuilt(outputDir, 7);
	}

	@Test
	public void testShouldNotCreateAutoreference() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		String outputDir = "./target/spooned-autoref";
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/ShouldNotAutoreference.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();
		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		CtType element = launcher.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		assertTrue("The file should not contain a static import for NOFOLLOW_LINKS",!output.contains("import static java.nio.file.LinkOption.NOFOLLOW_LINKS;"));
		canBeBuilt(outputDir, 7);
	}

	@Test
	public void testAccessPath() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/TransportIndicesShardStoresAction.java");
		String outputDir = "./target/spooned-accessPath";
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();
		CtType element = launcher.getFactory().Class().getAll().get(0);

		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		canBeBuilt(outputDir, 7);
	}

	@Test
	public void testSuperInheritanceHierarchyFunction() throws Exception {
		CtType<?> clientClass = (CtClass<?>) ModelUtils.buildClass(ClientClass.class);
		CtTypeReference<?> childClass = clientClass.getSuperclass();
		CtTypeReference<?> superClass = childClass.getSuperclass();
		
		List<String> result = clientClass.map(new SuperInheritanceHierarchyFunction().includingSelf(true)).map(e->{
			assertTrue(e instanceof CtType);
			return ((CtType)e).getQualifiedName();
		}).list();
		//contract: includingSelf(true) should return input type too
		assertTrue(result.contains(clientClass.getQualifiedName()));
		assertTrue(result.contains(childClass.getQualifiedName()));
		assertTrue(result.contains(superClass.getQualifiedName()));
		assertTrue(result.contains(Object.class.getName()));

		result = clientClass.map(new SuperInheritanceHierarchyFunction().includingSelf(false)).map(e->{
			assertTrue(e instanceof CtType);
			return ((CtType)e).getQualifiedName();
		}).list();
		//contract: includingSelf(false) should return input type too
		assertFalse(result.contains(clientClass.getQualifiedName()));
		assertTrue(result.contains(childClass.getQualifiedName()));
		assertTrue(result.contains(superClass.getQualifiedName()));
		assertTrue(result.contains(Object.class.getName()));

		//contract: returnTypeReferences(true) returns CtTypeReferences
		result = clientClass.map(new SuperInheritanceHierarchyFunction().includingSelf(true).returnTypeReferences(true)).map(e->{
			assertTrue(e instanceof CtTypeReference);
			return ((CtTypeReference)e).getQualifiedName();
		}).list();
		//contract: includingSelf(false) should return input type too
		assertTrue(result.contains(clientClass.getQualifiedName()));
		assertTrue(result.contains(childClass.getQualifiedName()));
		assertTrue(result.contains(superClass.getQualifiedName()));
		assertTrue(result.contains(Object.class.getName()));

		//contract: the mapping can be started on type reference too
		result = clientClass.getReference().map(new SuperInheritanceHierarchyFunction().includingSelf(true).returnTypeReferences(true)).map(e->{
			assertTrue(e instanceof CtTypeReference);
			return ((CtTypeReference)e).getQualifiedName();
		}).list();
		//contract: includingSelf(false) should return input type too
		assertTrue(result.contains(clientClass.getQualifiedName()));
		assertTrue(result.contains(childClass.getQualifiedName()));
		assertTrue(result.contains(superClass.getQualifiedName()));
		assertTrue(result.contains(Object.class.getName()));

		//contract: super type of Object is nothing
		List<CtTypeReference<?>> typeResult = clientClass.getFactory().Type().OBJECT.map(new SuperInheritanceHierarchyFunction().includingSelf(false).returnTypeReferences(true)).list();
		assertEquals(0, typeResult.size());
		typeResult = clientClass.getFactory().Type().OBJECT.map(new SuperInheritanceHierarchyFunction().includingSelf(true).returnTypeReferences(true)).list();
		assertEquals(1, typeResult.size());
		assertEquals(clientClass.getFactory().Type().OBJECT, typeResult.get(0));
	}

	@Test
	public void testSuperInheritanceHierarchyFunctionListener() throws Exception {
		CtType<?> clientClass = (CtClass<?>) ModelUtils.buildClass(ClientClass.class);
		CtTypeReference<?> childClass = clientClass.getSuperclass();
		CtTypeReference<?> superClass = childClass.getSuperclass();
		
		//contract: the enter and exit are always called with CtTypeReference instance
		List<String> result = clientClass.map(new SuperInheritanceHierarchyFunction().includingSelf(true).setListener(new CtScannerListener() {
			@Override
			public ScanningMode enter(CtElement element) {
				assertTrue(element instanceof CtTypeReference);
				return ScanningMode.NORMAL;
			}
			@Override
			public void exit(CtElement element) {
				assertTrue(element instanceof CtTypeReference);
			}
		})).map(e->{
			assertTrue(e instanceof CtType);
			return ((CtType)e).getQualifiedName();
		}).list();
		assertTrue(result.contains(clientClass.getQualifiedName()));
		assertTrue(result.contains(childClass.getQualifiedName()));
		assertTrue(result.contains(superClass.getQualifiedName()));
		assertTrue(result.contains(Object.class.getName()));

		//contract: if listener skips ALL, then skipped element and all super classes are not returned
		result = clientClass.map(new SuperInheritanceHierarchyFunction().includingSelf(true).setListener(new CtScannerListener() {
			@Override
			public ScanningMode enter(CtElement element) {
				assertTrue(element instanceof CtTypeReference);
				if(superClass.getQualifiedName().equals(((CtTypeReference<?>)element).getQualifiedName())) {
					return ScanningMode.SKIP_ALL;
				}
				return ScanningMode.NORMAL;
			}
			@Override
			public void exit(CtElement element) {
				assertTrue(element instanceof CtTypeReference);
			}
		})).map(e->{
			assertTrue(e instanceof CtType);
			return ((CtType)e).getQualifiedName();
		}).list();
		assertTrue(result.contains(clientClass.getQualifiedName()));
		assertTrue(result.contains(childClass.getQualifiedName()));
		assertFalse(result.contains(superClass.getQualifiedName()));
		assertFalse(result.contains(Object.class.getName()));

		//contract: if listener skips CHIDLREN, then skipped element is returned but all super classes are not returned
		result = clientClass.map(new SuperInheritanceHierarchyFunction().includingSelf(true).setListener(new CtScannerListener() {
			@Override
			public ScanningMode enter(CtElement element) {
				assertTrue(element instanceof CtTypeReference);
				if(superClass.getQualifiedName().equals(((CtTypeReference<?>)element).getQualifiedName())) {
					return ScanningMode.SKIP_CHILDREN;
				}
				return ScanningMode.NORMAL;
			}
			@Override
			public void exit(CtElement element) {
				assertTrue(element instanceof CtTypeReference);
			}
		})).map(e->{
			assertTrue(e instanceof CtType);
			return ((CtType)e).getQualifiedName();
		}).list();
		assertTrue(result.contains(clientClass.getQualifiedName()));
		assertTrue(result.contains(childClass.getQualifiedName()));
		assertTrue(result.contains(superClass.getQualifiedName()));
		assertFalse(result.contains(Object.class.getName()));
	}

	@Test
	public void testSuperInheritanceHierarchyFunctionNoClasspath() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/noclasspath/superclass/UnknownSuperClass.java");
		launcher.buildModel();
		final CtModel model = launcher.getModel();

		CtClass<?> classUSC = launcher.getFactory().Class().get("UnknownSuperClass");

		//contract: super inheritance scanner returns only Types on class path including final Object
		List<CtType> types = classUSC.map(new SuperInheritanceHierarchyFunction().includingSelf(true)).list();
		assertEquals(2, types.size());
		assertEquals("UnknownSuperClass", types.get(0).getQualifiedName());
		assertEquals("java.lang.Object", types.get(1).getQualifiedName());

		//contract: super inheritance scanner in reference mode returns type references including these which are not on class path and including final Object
		List<CtTypeReference> typeRefs = classUSC.map(new SuperInheritanceHierarchyFunction().includingSelf(true).returnTypeReferences(true)).list();
		assertEquals(3, typeRefs.size());
		assertEquals("UnknownSuperClass", typeRefs.get(0).getQualifiedName());
		assertEquals("NotInClasspath", typeRefs.get(1).getQualifiedName());
		assertEquals("java.lang.Object", typeRefs.get(2).getQualifiedName());

		//contract: super inheritance scanner in reference mode, which starts on class which is not available in model returns no Object, because it does not know if type is class or interface 
		typeRefs = classUSC.getSuperclass().map(new SuperInheritanceHierarchyFunction().includingSelf(true).returnTypeReferences(true)).list();
		assertEquals(1, typeRefs.size());
		assertEquals("NotInClasspath", typeRefs.get(0).getQualifiedName());

		//contract: super inheritance scanner in type mode, which starts on class which is not available in model returns nothing 
		types = classUSC.getSuperclass().map(new SuperInheritanceHierarchyFunction().includingSelf(true)).list();
		assertEquals(0, types.size());
	}

	@Test
	public void testJavaLangIsConsideredAsImported() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		String outputDir = "./target/spooned-javalang";
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/JavaLangConflict.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();

		canBeBuilt(outputDir, 7);
	}

	@Test
	public void testJavaLangIsConsideredAsImportedButNotForSubPackages() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned-javalang-sub";
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/Reflection.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();

		canBeBuilt(outputDir, 7);
	}

	@Test
	public void testmportInCu() throws  Exception{
		// contract: auto-import works for compilation units with multiple classes
		String[] options = {"--output-type", "compilationunits",
				"--output", "target/testmportInCu", "--with-imports"};

		String path = "spoon/test/prettyprinter/testclasses/A.java";

		final Launcher launcher = new Launcher();
		launcher.setArgs(options);
		launcher.addInputResource("./src/test/java/"+path);
		launcher.run();

		File output = new File("target/testmportInCu/"+path);
		String code = IOUtils.toString(new FileReader(output));

		// the ArrayList is imported and used in short mode
		assertTrue(code.contains("import java.util.ArrayList"));

		// no fully qualified usage
		assertFalse(code.contains("new java.util.ArrayList"));

		// sanity check: the actual code
		assertTrue(code.contains("ArrayList<String> list = new ArrayList<>()"));

		// cleaning
		output.delete();
	}

	@Test
	public void testMultipleCU() throws IOException {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned-multiplecu";
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/multiplecu/");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();

		canBeBuilt(outputDir, 7);

		String pathA = "spoon/test/imports/testclasses/multiplecu/A.java";
		String pathB = "spoon/test/imports/testclasses/multiplecu/B.java";

		File outputA = new File(outputDir+"/"+pathA);
		String codeA = IOUtils.toString(new FileReader(outputA));

		assertThat(codeA, containsString("import java.util.List;"));

		File outputB = new File(outputDir+"/"+pathB);
		String codeB = IOUtils.toString(new FileReader(outputB));

		assertThat(codeB, containsString("import java.awt.List;"));
	}

	@Test
	public void testStaticMethodWithDifferentClassSameNameJava7NoCollision() {
		// contract: when there is a collision between class names when using static method, we should create a static import for the method
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned-staticmethod";
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/staticmethod/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/enums/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/enum2/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/LangTestSuite.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.getEnvironment().setComplianceLevel(7);
		launcher.run();
		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		CtType element = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.apachetestsuite.staticmethod.AllLangTestSuiteStaticMethod");
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		assertTrue("The file should contain a static import ", output.contains("import static spoon.test.imports.testclasses2.apachetestsuite.enums.EnumTestSuite.suite;"));
		assertTrue("The call to the last EnumTestSuite should be in FQN", output.contains("suite.addTest(suite());"));


		canBeBuilt(outputDir, 7);
	}

	@Test
	public void testStaticMethodWithDifferentClassSameNameJava3NoCollision() {
		// contract: when there is a collision between class names when using static method, we could not create a static import
		// as it is not compliant with java < 1.5, so we should use fully qualified name of the class
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned-staticjava3";
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/staticjava3/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/enums/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/enum2/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/LangTestSuite.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.getEnvironment().setComplianceLevel(3);
		launcher.run();
		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		CtType element = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.apachetestsuite.staticjava3.AllLangTestJava3");
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		assertFalse("The file should not contain a static import ", output.contains("import static"));
		assertTrue("The call to the last EnumTestSuite should be in FQN", output.contains("suite.addTest(spoon.test.imports.testclasses2.apachetestsuite.enums.EnumTestSuite.suite());"));


		canBeBuilt(outputDir, 3);
	}

	@Test
	public void testStaticMethodWithDifferentClassSameNameCollision() {
		// contract: when using static method, if there is a collision between class name AND between method names,
		// we can only use the fully qualified name of the class to call the static method
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned-apache";
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/staticcollision/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/enums/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/enum2/");
		launcher.addInputResource("./src/test/resources/spoon/test/imports/testclasses2/apachetestsuite/LangTestSuite.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.getEnvironment().setComplianceLevel(3);
		launcher.run();
		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		CtType element = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.apachetestsuite.staticcollision.AllLangTestSuite");
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		assertTrue("The file should not contain a static import ",!output.contains("import static spoon.test.imports.testclasses2.apachetestsuite.enum2.EnumTestSuite.suite;"));
		assertTrue("The call to the last EnumTestSuite should be in FQN", output.contains("suite.addTest(spoon.test.imports.testclasses2.apachetestsuite.enum2.EnumTestSuite.suite());"));

		canBeBuilt(outputDir, 3);
	}

	@Test
	public void testSortingOfImports() {
		// contract: imports are sorted alphabetically
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		String outputDir = "./target/spooned";
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/DefaultJavaPrettyPrinter.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();
		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		CtType element = launcher.getFactory().Class().get(DefaultJavaPrettyPrinter.class);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		StringTokenizer st = new StringTokenizer(output, System.getProperty("line.separator"));
		String lastImport = null;
		int countOfImports = 0;
		while(st.hasMoreTokens()) {
			String line = st.nextToken();
			if(line.startsWith("import")) {
				line = line.substring(0, line.length() - 2); // we remove the last ';' to be able to compare x.y and x.y.z
				countOfImports++;
				if(lastImport!=null) {
					//check that next import is alphabetically higher then last import
					assertTrue(lastImport+" should be before "+line, lastImport.compareTo(line) < 0);
				}
				lastImport = line;
			} else {
				if(lastImport!=null) {
					//there are no more imports. Finish
					break;
				}
				//no import found yet. Continue with next line
			}
		}
		assertTrue(countOfImports>10);
	}

	@Test
	public void testSortImportPutStaticImportAfterTypeImport() {
		//contract: static import should be after import
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setShouldCompile(true);
		String outputDir = "./target/spoon-sort-import";
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/StaticNoOrdered.java");
		launcher.setSourceOutputDirectory(outputDir);
		launcher.run();

		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();
		CtType element = launcher.getFactory().Class().get(StaticNoOrdered.class);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		StringTokenizer st = new StringTokenizer(output, System.getProperty("line.separator"));

		int countImports = 0;

		int nbStaticImports = 2;
		int nbStandardImports = 3;

		boolean startStatic = false;

		while (st.hasMoreTokens()) {
			String line = st.nextToken();

			if (line.startsWith("import static")) {
				if (!startStatic) {
					assertEquals("Static import should start after exactly "+nbStandardImports+" standard imports", nbStandardImports, countImports);
				} else {
					assertTrue("It will normally have only "+nbStaticImports+" static imports", countImports <= nbStandardImports+nbStaticImports);
				}
				startStatic = true;
				assertTrue("Static import should be after normal import", countImports >= nbStandardImports);
			}

			if (line.startsWith("import")) {
				countImports++;
			}
		}

		int totalImports = nbStandardImports + nbStaticImports;
		assertEquals("Exactly "+totalImports+" should have been counted.", (nbStandardImports+nbStaticImports), countImports);
	}

	@Test
	public void testImportStarredPackageWithNonVisibleClass() throws IOException {
		// contract: when importing starred import, it should import the starred import

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setShouldCompile(true);

		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/");
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/DumbClassUsingInternal.java");
		launcher.run();

		File f = new File("./src/test/java/spoon/test/imports/testclasses/DumbClassUsingInternal.java");
		CompilationUnit cu = launcher.getFactory().CompilationUnit().getMap().get(f.getCanonicalPath());

		assertNotNull(cu);

		assertEquals(1, cu.getImports().size());
		assertEquals(CtImportKind.ALL_TYPES, cu.getImports().iterator().next().getImportKind());
	}

	@Test
	public void testImportWithGenerics() {
		// contract: in noclasspath autoimport, we should be able to use generic type
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/import-with-generics/TestWithGenerics.java");
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setShouldCompile(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.setSourceOutputDirectory("./target/import-with-generics");
		launcher.run();

		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();
		CtType element = launcher.getFactory().Class().get("spoon.test.imports.testclasses.TestWithGenerics");
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String output = prettyPrinter.getResult();

		assertTrue(output.contains("import spoon.test.imports.testclasses.withgenerics.Target;"));
	}

	@Test
	public void testEqualsImports() {
		// contract: two imports of same kind with same reference should be equals
		final Launcher launcher = new Launcher();

		CtType typeA = launcher.getFactory().Type().get(A.class);

		CtImport importsA1 = launcher.getFactory().createImport(typeA.getReference());
		CtImport importsA2 = launcher.getFactory().createImport(typeA.getReference());

		assertEquals(importsA1, importsA2);
		assertEquals(importsA1.hashCode(), importsA2.hashCode());

		CtType typeB = launcher.getFactory().Type().get(Pozole.class);
		CtImport importsB = launcher.getFactory().createImport(typeB.getReference());
		assertNotEquals(importsA1, importsB);
		assertNotEquals(importsA1.hashCode(), importsB.hashCode());
	}

	@Test
	public void testGetImportKindReturnRightValue() {
		// contract: the importKind is computed based on the reference class type and the boolean isImportAllStaticTypeMembers
		final Launcher spoon = new Launcher();

		CtType aType = spoon.getFactory().Type().get(Reflection.class);

		CtImport ctImport = spoon.getFactory().createImport(aType.getReference());
		assertEquals(CtImportKind.TYPE, ctImport.getImportKind());

		ctImport = spoon.getFactory().createImport(spoon.getFactory().Type().createTypeMemberWildcardImportReference(aType.getReference()));
		assertEquals(CtImportKind.ALL_STATIC_MEMBERS, ctImport.getImportKind());

		ctImport = spoon.getFactory().createImport(((CtMethod)aType.getAllMethods().iterator().next()).getReference());
		assertEquals(CtImportKind.METHOD, ctImport.getImportKind());

		ctImport = spoon.getFactory().createImport(((CtField)aType.getFields().get(0)).getReference());
		assertEquals(CtImportKind.FIELD, ctImport.getImportKind());

		ctImport = spoon.getFactory().createImport(aType.getPackage().getReference());
		assertEquals(CtImportKind.ALL_TYPES, ctImport.getImportKind());
	}
	
	@Test
	public void testVisitImportByKind() {
		// contract: the CtImportVisitor is called based on the reference class type and the boolean isImportAllStaticTypeMembers
		final Launcher spoon = new Launcher();

		CtType aType = spoon.getFactory().Type().get(Reflection.class);

		CtTypeReference<?> typeRef;
		
		CtImport ctImport = spoon.getFactory().createImport(aType.getReference());
		assertImportVisitor(ctImport);

		ctImport = spoon.getFactory().createImport(spoon.getFactory().Type().createTypeMemberWildcardImportReference(aType.getReference()));
		assertImportVisitor(ctImport);

		ctImport = spoon.getFactory().createImport(((CtMethod)aType.getAllMethods().iterator().next()).getReference());
		assertImportVisitor(ctImport);

		ctImport = spoon.getFactory().createImport(((CtField)aType.getFields().get(0)).getReference());
		assertImportVisitor(ctImport);

		ctImport = spoon.getFactory().createImport(aType.getPackage().getReference());
		assertImportVisitor(ctImport);
	}

	private void assertImportVisitor(CtImport imprt) {
		class ImportInfo {
			CtImportKind kind;
			void setKind(CtImportKind kind) {
				if (this.kind != null) {
					fail();
				}
				this.kind = kind;
			}
		}
		ImportInfo info = new ImportInfo();
		imprt.accept(new CtImportVisitor() {
			
			@Override
			public <T> void visitTypeImport(CtTypeReference<T> typeReference) {
				info.setKind(CtImportKind.TYPE);
				assertSame(imprt.getReference(), typeReference);
			}
			
			@Override
			public <T> void visitMethodImport(CtExecutableReference<T> executableReference) {
				info.setKind(CtImportKind.METHOD);
				assertSame(imprt.getReference(), executableReference);
			}
			
			@Override
			public <T> void visitFieldImport(CtFieldReference<T> fieldReference) {
				info.setKind(CtImportKind.FIELD);
				assertSame(imprt.getReference(), fieldReference);
			}
			
			@Override
			public void visitAllTypesImport(CtPackageReference packageReference) {
				info.setKind(CtImportKind.ALL_TYPES);
				assertSame(imprt.getReference(), packageReference);
			}
			
			@Override
			public <T> void visitAllStaticMembersImport(CtTypeMemberWildcardImportReference typeReference) {
				info.setKind(CtImportKind.ALL_STATIC_MEMBERS);
				assertSame(imprt.getReference(), typeReference);
			}
		});
		assertSame(imprt.getImportKind(), info.kind);
	}

	@Test
	public void testNullable() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/resources/noclasspath/TestNullable.java"));
		comp.build();
		// should build
		assertNotNull(launcher.getFactory().Type().get("TestNullable"));
	}

	@Test
	public void testBug2369_fqn() {
		// see https://github.com/INRIA/spoon/issues/2369
		final Launcher launcher = new Launcher();
launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/JavaLongUse.java");
		launcher.buildModel();
		final String nl = System.lineSeparator();
		assertEquals("public class JavaLongUse {" + nl +
				"    public class Long {}" + nl +
				nl +
				"    public static long method() {" + nl +
				"        return java.lang.Long.parseLong(\"10000\");" + nl +
				"    }" + nl +
				nl +
				"    public static void main(java.lang.String[] args) {" + nl +
				"        java.lang.System.out.println(spoon.test.imports.testclasses.JavaLongUse.method());" + nl +
				"    }" + nl +
				"}", launcher.getFactory().Type().get("spoon.test.imports.testclasses.JavaLongUse").toString());
	}

	@Test
	public void testBug2369_autoimports() {
		// https://github.com/INRIA/spoon/issues/2369
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/imports/testclasses/JavaLongUse.java");
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		final String nl = System.lineSeparator();
		assertEquals("public class JavaLongUse {" + nl +
				"    public class Long {}" + nl +
				"" + nl +
				"    public static long method() {" + nl +
				"        return java.lang.Long.parseLong(\"10000\");" + nl +
				"    }" + nl +
				"" + nl +
				"    public static void main(String[] args) {" + nl +
				"        System.out.println(JavaLongUse.method());" + nl +
				"    }" + nl +
				"}", launcher.getFactory().Type().get("spoon.test.imports.testclasses.JavaLongUse").toString());
	}

}
