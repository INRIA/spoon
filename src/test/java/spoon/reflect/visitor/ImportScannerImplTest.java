package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtType;
import spoon.support.reflect.cu.CompilationUnitImpl;

import java.util.List;

import static org.junit.Assert.*;

public class ImportScannerImplTest {

	@Test
	public void testPrintImport() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/simple-import/TestClass.java");
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();

		CtType t = launcher.getModel().getRootPackage().getPackage("matchers").getType("TestClass");

		CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(t);
		List<CtImport> imports = cu.getImports();

		//contract: import are accessible from CompilationUnit
		assertEquals(imports.size(), 2);

		//contract: CompilationUnitImpl#toString() does not throw a NPE
		assertNotNull(cu.toString());

		//contract: CompilationUnitImpl#toString() returns the file's name.
		assertEquals(cu.toString(), "TestClass.java");
	}

}