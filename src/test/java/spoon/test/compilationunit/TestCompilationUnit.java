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
package spoon.test.compilationunit;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonFile;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;
import spoon.test.api.testclasses.Bar;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Created by urli on 18/08/2017.
 */
public class TestCompilationUnit {

	@Test
	public void testIsoEncodingIsSupported() throws Exception {

		File resource = new File("./src/test/resources/noclasspath/IsoEncoding.java");
		String content = new String(Files.readAllBytes(resource.toPath()), "ISO-8859-1");

		Launcher launcher = new Launcher();
		launcher.addInputResource(resource.getPath());
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setEncoding(Charset.forName("ISO-8859-1"));
		launcher.buildModel();

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(resource.getPath());
		assertEquals(content, cu.getOriginalSourceCode());
	}

	@Test
	public void testGetUnitTypeWorksWithDeclaredType() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");
		launcher.buildModel();

		CtType type = launcher.getFactory().Type().get(Bar.class);
		CompilationUnit compilationUnit = type.getPosition().getCompilationUnit();

		assertEquals(CompilationUnit.UNIT_TYPE.TYPE_DECLARATION, compilationUnit.getUnitType());
	}

	@Test
	public void testGetUnitTypeWorksWithDeclaredPackage() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/pkg/package-info.java");
		launcher.buildModel();

		CtPackage ctPackage = launcher.getFactory().Package().get("spoon.test.pkg");
		CompilationUnit compilationUnit = ctPackage.getPosition().getCompilationUnit();
		assertEquals(CompilationUnit.UNIT_TYPE.PACKAGE_DECLARATION, compilationUnit.getUnitType());
	}

	@Test
	public void testGetUnitTypeWorksWithCreatedObjects() {
		final Launcher launcher = new Launcher();
		CtPackage myFooPackage = launcher.getFactory().Package().getOrCreate("my.foo");
		CompilationUnit cu = launcher.getFactory().createCompilationUnit();
		assertEquals(CompilationUnit.UNIT_TYPE.UNKNOWN, cu.getUnitType());
		
		cu.setDeclaredPackage(myFooPackage);
		assertEquals(CompilationUnit.UNIT_TYPE.PACKAGE_DECLARATION, cu.getUnitType());

		cu.setDeclaredTypes(Collections.singletonList(launcher.getFactory().createClass()));
		assertEquals(CompilationUnit.UNIT_TYPE.TYPE_DECLARATION, cu.getUnitType());
	}

	@Test
	public void testCompilationUnitDeclaredTypes() throws IOException {
		// contract: the list of declared types should be unmodifiable
		File resource = new File("./src/test/java/spoon/test/model/Foo.java");
		final Launcher launcher = new Launcher();
		launcher.addInputResource(resource.getPath());
		launcher.buildModel();

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(resource.getCanonicalPath());
		assertEquals(3, cu.getDeclaredTypes().size());

		List<CtType<?>> typeList = cu.getDeclaredTypes();
		try {
			typeList.remove(0);
			fail();
		} catch (UnsupportedOperationException e) {
			// do nothing
		}
	}

	@Test
	public void testCompilationUnitSourcePosition() throws IOException {
		// contract: the CompilationUnit has root source position
		File resource = new File("./src/test/java/spoon/test/model/Foo.java");
		final Launcher launcher = new Launcher();
		launcher.addInputResource(resource.getPath());
		launcher.buildModel();

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(resource.getCanonicalPath());
		SourcePosition sp = cu.getPosition();
		assertNotNull(sp);
		assertEquals(0, sp.getSourceStart());
		assertEquals(cu.getOriginalSourceCode().length(), sp.getSourceEnd() + 1);
		assertSame(cu, sp.getCompilationUnit());
	}

	@Test
	public void testAddDeclaredTypeInCU() throws IOException {
		// contract: when a type is added to a CU, it should also be pretty printed in cu mode
		File resource = new File("./src/test/java/spoon/test/model/Foo.java");
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{"--output-type", "compilationunits"});
		launcher.addInputResource(resource.getPath());
		launcher.setSourceOutputDirectory("./target/cu-onemoretype");
		launcher.buildModel();

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(resource.getCanonicalPath());
		assertEquals(3, cu.getDeclaredTypes().size());

		CtType typeBla = launcher.getFactory().Class().create("spoon.test.model.Bla");
		cu.addDeclaredType(typeBla);

		assertEquals(4, cu.getDeclaredTypes().size());

		launcher.prettyprint();

		File output = new File("./target/cu-onemoretype/spoon/test/model/Foo.java");
		List<String> lines = Files.readAllLines(output.toPath());

		String fullContent = StringUtils.join(lines, "\n");
		assertTrue(fullContent.contains("public class Foo"));
		assertTrue(fullContent.contains("class Bar"));
		assertTrue(fullContent.contains("class Baz"));
		assertTrue(fullContent.contains("class Bla"));
	}

	@Test
	public void testNewlyCreatedCUWouldGetAPartialPosition() throws IOException {
		// contract: when a type is created, a CU can be created and added as partial position
		final Launcher launcher = new Launcher();
		assertTrue(launcher.getFactory().CompilationUnit().getMap().isEmpty());

		CtClass myFooClass = launcher.getFactory().createClass("my.foo.MyClass");
		assertEquals(SourcePosition.NOPOSITION, myFooClass.getPosition());

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(myFooClass);

		assertNotNull(cu);
		assertSame(cu, launcher.getFactory().CompilationUnit().getOrCreate(myFooClass));
		SourcePosition sourcePosition = myFooClass.getPosition();
		assertTrue(sourcePosition instanceof PartialSourcePositionImpl);
		assertSame(cu, sourcePosition.getCompilationUnit());

		File f = new File(Launcher.OUTPUTDIR, "my/foo/MyClass.java");
		assertEquals(f.getCanonicalFile(), cu.getFile());
	}

	@Test
	public void testCompilationUnitModelContracts() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");
		launcher.buildModel();

		CtType type = launcher.getFactory().Type().get(Bar.class);
		//contract: type built from model has BodyHolderSourcePosition
		assertTrue(type.getPosition() instanceof BodyHolderSourcePosition);
		//contract: type has compilation unit in position
		CompilationUnit compilationUnit = type.getPosition().getCompilationUnit();

		//contract: parent of compilationUnit is always null
		//compilation unit is not part of factory.getModel()
		assertNull(compilationUnit.getParent());

		//contract: parent of CtImport is CompilationUnit
		CtImport anImport = compilationUnit.getImports().iterator().next();
		assertSame(compilationUnit, anImport.getParent());
		
		//contract: parent of type declared in Compilation unit is a package (never CompilationUnit)
		assertTrue(compilationUnit.getMainType().getParent() instanceof CtPackage);
		
		//contract: compilation unit which contains types has null declared module
		assertNull(compilationUnit.getDeclaredModule());
		//contract: compilation unit knows declared package
		assertSame(type.getPackage(), compilationUnit.getDeclaredPackage());
		
		//the package declaration exists and points to correct package
		assertEquals(type.getPackage().getReference(), compilationUnit.getPackageDeclaration().getReference());
		
		assertSame(compilationUnit, compilationUnit.getPackageDeclaration().getParent());
		
		//contract: types and imports are scanned exactly once when scanning starts from compilation unit
		//note: therefore compilationUnit.getDeclaredPackage() must return null
		List<CtType<?>> types = new ArrayList<>();
		List<CtTypeReference<?>> typeRefs = new ArrayList<>();
		List<CtImport> imports = new ArrayList<>();
		new CtScanner() {
			public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
				typeRefs.add(reference);
			}
			public <T> void visitCtClass(CtClass<T> ctClass) {
				types.add(ctClass);
			}
			public <T> void visitCtInterface(CtInterface<T> intrface) {
				types.add(intrface);
			}
			public void visitCtImport(CtImport ctImport) {
				imports.add(ctImport);
				assertSame(compilationUnit, ctImport.getParent());
			}
		}.scan(compilationUnit);
		
		assertEquals(0, types.size());
		assertEquals(compilationUnit.getDeclaredTypeReferences(), typeRefs);
		assertEquals(compilationUnit.getImports(), imports);
		
		//contract: compilation unit is not visited by scanner when scanning started from model
		new CtScanner() {
			@Override
			public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
				fail("CtCompilation unit must not be scanned when started from model unnamed module");
			}
		}.scan(type.getFactory().getModel().getUnnamedModule());

		new CtScanner() {
			@Override
			public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
				fail("CtCompilation unit must not be scanned when started from model root package");
			}
		}.scan(type.getFactory().getModel().getRootPackage());
	}

	private Charset detectEncodingDummy(SpoonFile unused, byte[] fileBytes) {
		if (fileBytes.length == 76) {
			return Charset.forName("Cp1251");
		} else if (fileBytes.length == 86) {
			return Charset.forName("UTF-8");
		}
		throw new SpoonException("unexpected length");
	}

	@Test
	public void testDifferentEncodings() throws Exception {
		//contract: both utf-8 and cp1251 files in the same project should be handled properly
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/encodings/Cp1251.java");
		launcher.addInputResource("./src/test/resources/encodings/Utf8.java");
		launcher.getEnvironment().setEncodingProvider(this::detectEncodingDummy);
		CtModel model = launcher.buildModel();

		CtType<?> utf8Type = model.getAllTypes()
				.stream()
				.filter(t -> "Utf8".equals(t.getSimpleName()))
				.findFirst()
				.get();

		CtType<?> cp1251Type = model.getAllTypes()
				.stream()
				.filter(t -> "Cp1251".equals(t.getSimpleName()))
				.findFirst()
				.get();

		assertEquals("\"Привет мир\"", utf8Type.getField("s1").getAssignment().toString());
		assertEquals("\"Привет мир\"", cp1251Type.getField("s1").getAssignment().toString());
		assertEquals(utf8Type.getField("s1"), cp1251Type.getField("s1"));
		assertNotEquals(utf8Type.getField("s2"), cp1251Type.getField("s2"));
	}

	@Test
	public void testPrintImport() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/simple-import/TestClass.java");
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();

		CtType t = launcher.getModel().getRootPackage().getPackage("matchers").getType("TestClass");

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(t);
		List<CtImport> imports = cu.getImports();

		//contract: imports are accessible from CompilationUnit
		assertEquals(2, imports.size());

		//contract: CompilationUnitImpl#toString() does not throw a NPE
		assertNotNull(cu.toString());

		//contract: CompilationUnitImpl#toString() returns the file's name.
		assertEquals("TestClass.java", cu.toString());
	}
}
