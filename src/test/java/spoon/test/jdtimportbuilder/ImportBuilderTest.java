package spoon.test.jdtimportbuilder;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.declaration.CtImportKind;
import spoon.test.imports.testclasses.A;
import spoon.test.imports.testclasses.ClassWithInvocation;
import spoon.test.jdtimportbuilder.testclasses.StarredImport;
import spoon.test.jdtimportbuilder.testclasses.StaticImport;
import spoon.test.jdtimportbuilder.testclasses.StaticImportWithInheritance;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 09/08/2017.
 */
public class ImportBuilderTest {

	private static final String nl = System.getProperty("line.separator");

	@Test
	public void testWithNoImport() {
		// contract: when the source code has no import, none is created when building model
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/A.java");
		spoon.getEnvironment().setAutoImports(true);
		spoon.buildModel();

		CtClass classA = spoon.getFactory().Class().get(A.class);
		CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		assertTrue(unitA.getImports().isEmpty());
	}

	@Test
	public void testWithSimpleImport() {
		// contract: when the source has one import, the same import is created as a reference in auto-import mode
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClassWithInvocation.java");
		spoon.getEnvironment().setAutoImports(true);
		spoon.buildModel();

		CtClass classA = spoon.getFactory().Class().get(ClassWithInvocation.class);
		CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitA.getImports();

		assertEquals(1, imports.size());

		CtImport ref = imports.iterator().next();
		assertEquals("import spoon.test.annotation.testclasses.GlobalAnnotation;" + nl, ref.toString());
		assertTrue(ref.getReference() instanceof CtTypeReference);

		CtTypeReference refType = (CtTypeReference) ref.getReference();
		assertEquals("spoon.test.annotation.testclasses.GlobalAnnotation", refType.getQualifiedName());
	}

	@Test
	public void testWithSimpleImportNoAutoimport() {
		// contract: when the source has one import, nothing is imported when not in autoimport
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClassWithInvocation.java");
		spoon.getEnvironment().setAutoImports(false);
		spoon.buildModel();

		CtClass classA = spoon.getFactory().Class().get(ClassWithInvocation.class);
		CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		assertTrue(unitA.getImports().isEmpty());
	}

	@Test
	public void testInternalImportWhenNoClasspath() {
		// contract: in no-classpath anything which is not loaded cannot be imported, even if original source code has imports
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/noclasspath/Attachment.java");
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();

		CtClass classA = spoon.getFactory().Class().get("it.feio.android.omninotes.models.Attachment");
		CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		assertTrue(unitA.getImports().isEmpty());
	}

	@Test
	public void testSimpleStaticImport() {
		// contract: simple static import are imported correctly
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/StaticImport.java");
		spoon.getEnvironment().setAutoImports(true);
		spoon.buildModel();

		CtClass classA = spoon.getFactory().Class().get(StaticImport.class);
		CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitA.getImports();

		assertEquals(1, imports.size());

		CtImport ref = imports.iterator().next();

		assertTrue(ref.getReference() instanceof CtFieldReference);
		assertEquals("spoon.test.jdtimportbuilder.testclasses.staticimport.Dependency#ANY", ((CtFieldReference) ref.getReference()).getQualifiedName());
	}

	@Test
	public void testWithStaticStarredImportFromInterface() {
		// contract: when a starred import is used with a target package, all classes of the package should be imported
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/StarredImport.java");
		spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/fullpack/");
		spoon.getEnvironment().setAutoImports(true);
		spoon.buildModel();

		CtClass classA = spoon.getFactory().Class().get(StarredImport.class);
		CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitA.getImports();

		assertEquals(1, imports.size());

		Iterator<CtImport> iterator = imports.iterator();
		CtImport ctImport = iterator.next();

		assertEquals(CtImportKind.ALL_TYPES, ctImport.getImportKind());

		assertTrue(ctImport.getReference() instanceof CtPackageReference);

		CtPackageReference ref = (CtPackageReference) ctImport.getReference();
		assertEquals("spoon.test.jdtimportbuilder.testclasses.fullpack", ref.getQualifiedName());
	}

	@Test
	public void testWithStaticInheritedImport() {
		// contract: When using starred static import of a type, it imports a starred type
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/StaticImportWithInheritance.java");
		spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/staticimport");
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setShouldCompile(true);
		spoon.setSourceOutputDirectory("./target/spoon-jdtimport-inheritedstatic");
		spoon.run();

		CtClass classStatic = spoon.getFactory().Class().get(StaticImportWithInheritance.class);
		CompilationUnit unitStatic = spoon.getFactory().CompilationUnit().getMap().get(classStatic.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitStatic.getImports();

		assertEquals(1, imports.size());
		CtImport ctImport = imports.iterator().next();
		assertEquals(CtImportKind.ALL_STATIC_MEMBERS, ctImport.getImportKind());
		assertEquals("import static spoon.test.jdtimportbuilder.testclasses.staticimport.DependencySubClass.*;" + nl, ctImport.toString());
	}

	@Test
	public void testWithImportFromItf() {
		// contract: When using starred static import of an interface, it imports a starred type
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/jdtimportbuilder/");
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setShouldCompile(true);
		spoon.setSourceOutputDirectory("./target/spoon-jdtimport-itfimport");
		spoon.run();

		CtClass classStatic = spoon.getFactory().Class().get("jdtimportbuilder.ItfImport");
		CompilationUnit unitStatic = spoon.getFactory().CompilationUnit().getMap().get(classStatic.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitStatic.getImports();

		assertEquals(1, imports.size());
		CtImport ctImport = imports.iterator().next();

		assertEquals(CtImportKind.ALL_STATIC_MEMBERS, ctImport.getImportKind());
		assertEquals("import static jdtimportbuilder.itf.DumbItf.*;" + nl, ctImport.toString());
	}
}
