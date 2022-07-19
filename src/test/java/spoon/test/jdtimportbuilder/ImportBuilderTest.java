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
package spoon.test.jdtimportbuilder;

import spoon.reflect.factory.Factory;
import spoon.test.imports.testclasses.A;
import spoon.reflect.reference.CtFieldReference;
import spoon.test.jdtimportbuilder.testclasses.StaticImport;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.test.jdtimportbuilder.testclasses.StaticImportWithInheritance;
import spoon.reflect.declaration.CtImportKind;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.test.jdtimportbuilder.testclasses.StarredImport;
import spoon.test.imports.testclasses.ClassWithInvocation;
import org.junit.jupiter.api.Test;
import spoon.testing.utils.ModelTest;

import java.util.stream.Collectors;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by urli on 09/08/2017.
 */
public class ImportBuilderTest {

	@ModelTest(value = "./src/test/java/spoon/test/imports/testclasses/A.java", autoImport = true)
	public void testWithNoImport(Factory factory) {
		// contract: when the source code has no import, none is created when building model
		CtClass classA = factory.Class().get(A.class);
		CompilationUnit unitA = factory.CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		assertTrue(unitA.getImports().isEmpty());
	}

	@ModelTest(value = "./src/test/java/spoon/test/imports/testclasses/ClassWithInvocation.java", autoImport = true)
	public void testWithSimpleImport(Factory factory) {
		// contract: when the source has one import, the same import is created as a reference in auto-import mode
		CtClass classA = factory.Class().get(ClassWithInvocation.class);
		CompilationUnit unitA = factory.CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitA.getImports();

		assertEquals(1, imports.size());

		CtImport ref = imports.iterator().next();
		assertEquals("import spoon.test.annotation.testclasses.GlobalAnnotation;", ref.toString());
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
		//build and print model. During printing the autoImport==false validators are applied
		spoon.run();

		CtClass classA = spoon.getFactory().Class().get(ClassWithInvocation.class);
		CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		assertTrue(unitA.getImports().isEmpty());
	}

	@ModelTest(value = "./src/test/resources/noclasspath/Attachment.java", autoImport = true)
	public void testInternalImportWhenNoClasspath(Factory factory) {
		// contract: in no-classpath anything which is not loaded becomes CtUnresolvedImport, even if original source code has imports
		CtClass classA = factory.Class().get("it.feio.android.omninotes.models.Attachment");
		CompilationUnit unitA = factory.CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());

		assertTrue(unitA.getImports().stream().filter(i -> !(i instanceof CtUnresolvedImport)).collect(Collectors.toList()).isEmpty());
		assertEquals(3, unitA.getImports().size());

		Set<String> importRefs = unitA.getImports().stream().map(i -> ((CtUnresolvedImport) i).getUnresolvedReference()).collect(Collectors.toSet());
		assertTrue(importRefs.contains("android.net.Uri"));
		assertTrue(importRefs.contains("android.os.Parcel"));
		assertTrue(importRefs.contains("android.os.Parcelable"));
	}

	@ModelTest(value = "./src/test/java/spoon/test/jdtimportbuilder/testclasses/StaticImport.java", autoImport = true)
	public void testSimpleStaticImport(Factory factory) {
		// contract: simple static import are imported correctly
		CtClass classA = factory.Class().get(StaticImport.class);
		CompilationUnit unitA = factory.CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitA.getImports();

		assertEquals(1, imports.size());

		CtImport ref = imports.iterator().next();

		assertTrue(ref.getReference() instanceof CtFieldReference);
		assertEquals("spoon.test.jdtimportbuilder.testclasses.staticimport.Dependency#ANY", ((CtFieldReference) ref.getReference()).getQualifiedName());
	}

	@ModelTest(
		value = {
			"./src/test/java/spoon/test/jdtimportbuilder/testclasses/StarredImport.java",
			"./src/test/java/spoon/test/jdtimportbuilder/testclasses/fullpack/",
		},
		autoImport = true
	)
	public void testWithStaticStarredImportFromInterface(Factory factory) {
		// contract: when a starred import is used with a target package, all classes of the package should be imported
		CtClass classA = factory.Class().get(StarredImport.class);
		CompilationUnit unitA = factory.CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitA.getImports();

		assertEquals(1, imports.size());

		Iterator<CtImport> iterator = imports.iterator();
		CtImport ctImport = iterator.next();

		assertEquals(CtImportKind.ALL_TYPES, ctImport.getImportKind());

		assertTrue(ctImport.getReference() instanceof CtPackageReference);

		CtPackageReference ref = (CtPackageReference) ctImport.getReference();
		assertEquals("spoon.test.jdtimportbuilder.testclasses.fullpack", ref.getQualifiedName());
	}

	@ModelTest(
		value = {
			"./src/test/java/spoon/test/jdtimportbuilder/testclasses/StaticImportWithInheritance.java",
			"./src/test/java/spoon/test/jdtimportbuilder/testclasses/staticimport",
		},
		autoImport = true
	)
	public void testWithStaticInheritedImport(Factory factory) {
		// contract: When using starred static import of a type, it imports a starred type
		CtClass classStatic = factory.Class().get(StaticImportWithInheritance.class);
		CompilationUnit unitStatic = factory.CompilationUnit().getMap().get(classStatic.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitStatic.getImports();

		assertEquals(1, imports.size());
		CtImport ctImport = imports.iterator().next();
		assertEquals(CtImportKind.ALL_STATIC_MEMBERS, ctImport.getImportKind());
		assertEquals("import static spoon.test.jdtimportbuilder.testclasses.staticimport.DependencySubClass.*;", ctImport.toString());
	}

	@ModelTest(value = "./src/test/resources/jdtimportbuilder/", autoImport = true)
	public void testWithImportFromItf(Factory factory) {
		// contract: When using starred static import of an interface, it imports a starred type
		CtClass classStatic = factory.Class().get("jdtimportbuilder.ItfImport");
		CompilationUnit unitStatic = factory.CompilationUnit().getMap().get(classStatic.getPosition().getFile().getPath());
		Collection<CtImport> imports = unitStatic.getImports();

		assertEquals(1, imports.size(), imports.toString());
		CtImport ctImport = imports.iterator().next();

		assertEquals(CtImportKind.ALL_STATIC_MEMBERS, ctImport.getImportKind());
		assertEquals("import static jdtimportbuilder.itf.DumbItf.*;", ctImport.toString());
	}
}
