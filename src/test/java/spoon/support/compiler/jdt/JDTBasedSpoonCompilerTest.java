package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.junit.Test;
import spoon.Launcher;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class JDTBasedSpoonCompilerTest {

	@Test
	public void testOrderCompilationUnits() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/main/java");
		JDTBasedSpoonCompiler spoonCompiler = (JDTBasedSpoonCompiler) launcher.getModelBuilder();

		CompilationUnitDeclaration[] compilationUnitDeclarations = spoonCompiler.buildUnits(null, spoonCompiler.sources, spoonCompiler.getSourceClasspath(), "");

		List<CompilationUnitDeclaration> compilationUnitDeclarations1 = spoonCompiler.sortCompilationUnits(compilationUnitDeclarations);

		if (System.getenv("SPOON_SEED_CU_COMPARATOR") == null || "0".equals(System.getenv("SPOON_SEED_CU_COMPARATOR"))) {
			for (int i = 1; i < compilationUnitDeclarations1.size(); i++) {
				CompilationUnitDeclaration cu0 = compilationUnitDeclarations1.get(i - 1);
				CompilationUnitDeclaration cu1 = compilationUnitDeclarations1.get(i);

				String filenameCu0 = new String(cu0.getFileName());
				String filenameCu1 = new String(cu1.getFileName());

				assertTrue("There is a sort error: " + filenameCu0 + " should be before " + filenameCu1, filenameCu0.compareTo(filenameCu1) < 0);
			}
		}
	}
}
