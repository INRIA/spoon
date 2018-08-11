package spoon.test.compilationunit;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;
import spoon.test.api.testclasses.Bar;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
		CtPackage myPackage = launcher.getFactory().Package().getOrCreate("my.package");
		CompilationUnit cu = launcher.getFactory().createCompilationUnit();
		assertEquals(CompilationUnit.UNIT_TYPE.UNKNOWN, cu.getUnitType());

		cu.setDeclaredPackage(myPackage);
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

		CtClass myNewClass = launcher.getFactory().createClass("my.new.MyClass");
		assertEquals(SourcePosition.NOPOSITION, myNewClass.getPosition());

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(myNewClass);

		assertNotNull(cu);
		assertSame(cu, launcher.getFactory().CompilationUnit().getOrCreate(myNewClass));
		SourcePosition sourcePosition = myNewClass.getPosition();
		assertTrue(sourcePosition instanceof PartialSourcePositionImpl);
		assertSame(cu, sourcePosition.getCompilationUnit());

		File f = new File(Launcher.OUTPUTDIR, "my/new/MyClass.java");
		assertEquals(f.getCanonicalFile(), cu.getFile());
	}
}
