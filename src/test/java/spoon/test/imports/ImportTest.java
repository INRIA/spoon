package spoon.test.imports;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.ImportScanner;
import spoon.reflect.visitor.ImportScannerImpl;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.util.SortedList;
import spoon.test.imports.testclasses.A;
import spoon.test.imports.testclasses.ClassWithInvocation;
import spoon.test.imports.testclasses.ClientClass;
import spoon.test.imports.testclasses.Mole;
import spoon.test.imports.testclasses.NotImportExecutableType;
import spoon.test.imports.testclasses.Pozole;
import spoon.test.imports.testclasses.SubClass;
import spoon.test.imports.testclasses.Tacos;
import spoon.test.imports.testclasses.internal.ChildClass;
import spoon.testing.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class ImportTest {

	@Test
	public void testImportOfAnInnerClassInASuperClassPackageAutoImport() throws Exception {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setShouldCompile(true);
		spoon.getEnvironment().setAutoImports(true);
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/SuperClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/ChildClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClientClass.java");
		spoon.setBinaryOutputDirectory("./target/spoon/super_imports/bin");
		spoon.setSourceOutputDirectory("./target/spoon/super_imports/src");
		spoon.run();

		final List<CtClass<?>> classes = Query.getElements(spoon.getFactory(), new NameFilter<CtClass<?>>("ClientClass"));

		final CtClass<?> innerClass = classes.get(0).getNestedType("InnerClass");
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
	public void testImportOfAnInnerClassInASuperClassPackageFullQualified() throws Exception {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setShouldCompile(true);
		spoon.getEnvironment().setAutoImports(false);
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/SuperClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/internal/ChildClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClientClass.java");
		spoon.setBinaryOutputDirectory("./target/spoon/super_imports/bin");
		spoon.setSourceOutputDirectory("./target/spoon/super_imports/src");
		spoon.run();

		final List<CtClass<?>> classes = Query.getElements(spoon.getFactory(), new NameFilter<CtClass<?>>("ClientClass"));

		final CtClass<?> innerClass = classes.get(0).getNestedType("InnerClass");
		
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
		final String expected = "public class SubClass extends spoon.test.imports.testclasses.SuperClass {" + System.lineSeparator() + "    public void aMethod() {" + System.lineSeparator()
				+ "        new spoon.test.imports.testclasses.SubClass.Item(\"\");" + System.lineSeparator() + "    }" + System.lineSeparator() + System.lineSeparator()
				+ "    public static class Item extends spoon.test.imports.testclasses.SuperClass.Item {" + System.lineSeparator() + "        public Item(java.lang.String s) {" + System
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
	public void testSpoonWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses", "--output-type", "nooutput", "--with-imports"
		});
		final CtClass<ImportTest> aClass = launcher.getFactory().Class().get(ChildClass.class);
		final CtClass<ImportTest> anotherClass = launcher.getFactory().Class().get(ClientClass.class);
		final CtClass<ImportTest> classWithInvocation = launcher.getFactory().Class().get(ClassWithInvocation.class);

		final ImportScanner importScanner = new ImportScannerImpl();
		final Collection<CtTypeReference<?>> imports = importScanner.computeImports(aClass);
		assertEquals(2, imports.size());
		final Collection<CtTypeReference<?>> imports1 = importScanner.computeImports(anotherClass);
		//ClientClass needs 2 imports: ChildClass, PublicInterface2
		assertEquals(2, imports1.size());
		//check that printer did not used the package protected class like "SuperClass.InnerClassProtected"
		assertTrue(anotherClass.toString().indexOf("InnerClass extends ChildClass.InnerClassProtected")>0);
		final Collection<CtTypeReference<?>> imports2 = importScanner.computeImports(classWithInvocation);
		assertEquals("Spoon ignores the arguments of CtInvocations", 1, imports2.size());
	}

	@Test
	public void testStaticImportForInvocationInNoClasspath() throws Exception {
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
	public void testImportOfInvocationOfPrivateClass() throws Exception {
		final Factory factory = getFactory(
				"./src/test/java/spoon/test/imports/testclasses/internal2/Chimichanga.java",
				"./src/test/java/spoon/test/imports/testclasses/Mole.java");

		ImportScanner importContext = new ImportScannerImpl();
		Collection<CtTypeReference<?>> imports = importContext.computeImports(factory.Class().get(Mole.class));

		assertEquals(1, imports.size());
		assertEquals("spoon.test.imports.testclasses.internal2.Chimichanga", imports.toArray()[0].toString());
	}

	@Test
	public void testNotImportExecutableType() throws Exception {
		final Factory factory = getFactory(
				"./src/test/java/spoon/test/imports/testclasses/internal3/Foo.java",
				"./src/test/java/spoon/test/imports/testclasses/internal3/Bar.java",
				"./src/test/java/spoon/test/imports/testclasses/NotImportExecutableType.java");

		ImportScanner importContext = new ImportScannerImpl();
		Collection<CtTypeReference<?>> imports = importContext.computeImports(factory.Class().get(NotImportExecutableType.class));

		assertEquals(2, imports.size());
		Set<String> expectedImports = new HashSet<>(
				Arrays.asList("spoon.test.imports.testclasses.internal3.Foo", "java.io.File"));
		Set<String> actualImports = imports.stream().map(CtTypeReference::toString).collect(Collectors.toSet());
		assertEquals(expectedImports, actualImports);
	}

	@Test
	public void testImportOfInvocationOfStaticMethod() throws Exception {
		final Factory factory = getFactory(
				"./src/test/java/spoon/test/imports/testclasses/internal2/Menudo.java",
				"./src/test/java/spoon/test/imports/testclasses/Pozole.java");

		ImportScanner importContext = new ImportScannerImpl();
		Collection<CtTypeReference<?>> imports = importContext.computeImports(factory.Class().get(Pozole.class));

		assertEquals(1, imports.size());
		assertEquals("spoon.test.imports.testclasses.internal2.Menudo", imports.toArray()[0].toString());
	}

	@Test
	public void testImportStaticAndFieldAccess() throws Exception {
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
	public void testFullQualifiedNameImport() throws Exception {
		String newLine = System.getProperty("line.separator");

		Factory factory = ModelUtils.build(A.class);
		factory.getEnvironment().setAutoImports(true);

		CtClass<Object> aClass = factory.Class().get(A.class);
		assertEquals("public class A {" + newLine
				+ "    public class ArrayList extends java.util.ArrayList {    }" + newLine
				+ "}", aClass.toString());
	}
	
	@Test
	public void testAccessToNestedClass() throws Exception {
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
	public void testAccessType() throws Exception {
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
	public void testCanAccess() throws Exception {
		
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
							if(e.getMessage().indexOf("Cannot compute access path to type: ")==-1) {
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
	public void testNestedAccessPathWithTypedParameter() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses2/AbstractMapBasedMultimap.java"
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
		assertTrue(mm.toString().indexOf("AbstractMapBasedMultimap<K, V>.WrappedCollection.WrappedIterator")>=0);

		CtClass<?> mmwliother = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap$OtherWrappedList$WrappedListIterator");
		assertEquals("private class WrappedListIterator extends spoon.test.imports.testclasses2.AbstractMapBasedMultimap<K, V>.OtherWrappedList.WrappedIterator {}",mmwliother.toString());
		 								  
	}

	@Test
	public void testNestedAccessPathWithTypedParameterWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses2/AbstractMapBasedMultimap.java", "--with-imports"
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
		assertTrue(mm.toString().indexOf("AbstractMapBasedMultimap<K, V>.WrappedCollection.WrappedIterator")>=0);

		CtClass<?> mmwliother = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.AbstractMapBasedMultimap$OtherWrappedList$WrappedListIterator");
		assertEquals("private class WrappedListIterator extends AbstractMapBasedMultimap<K, V>.OtherWrappedList.WrappedIterator {}",mmwliother.toString());

	}

	@Test
	public void testNestedStaticPathWithTypedParameter() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses2/Interners.java"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.Interners");
		assertTrue(mm.toString().indexOf("java.util.List<spoon.test.imports.testclasses2.Interners.WeakInterner.Dummy> list;")>=0);
		 								  
	}

	@Test
	public void testNestedStaticPathWithTypedParameterWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses2/Interners.java", "--with-imports"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.Interners");
		assertTrue(mm.toString().indexOf("List<Interners.WeakInterner.Dummy> list;")>=0);
		 								  
	}

	@Test
	public void testDeepNestedStaticPathWithTypedParameter() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses2/StaticWithNested.java"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.StaticWithNested");
		assertTrue("new spoon.test.imports.testclasses2.StaticWithNested.StaticNested.StaticNested2<K>();", mm.toString().indexOf("new spoon.test.imports.testclasses2.StaticWithNested.StaticNested.StaticNested2<K>();")>=0);
		 								  
	}
	@Test
	public void testDeepNestedStaticPathWithTypedParameterWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/imports/testclasses2/StaticWithNested.java", "--with-imports"
		});
		launcher.buildModel();
		launcher.prettyprint();
		try {
			launcher.getModelBuilder().compile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CtClass<?> mm = launcher.getFactory().Class().get("spoon.test.imports.testclasses2.StaticWithNested");
		assertTrue("new StaticWithNested.StaticNested.StaticNested2<K>();", mm.toString().indexOf("new StaticWithNested.StaticNested.StaticNested2<K>();")>=0);
		 								  
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
	
}
