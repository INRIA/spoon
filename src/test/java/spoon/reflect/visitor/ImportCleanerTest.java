package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtType;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ImportCleanerTest {

	@Test
	public void testDoesNotDuplicateUnresolvedImports() {
	    // contract: The import cleaner should not duplicate unresolved imports

		// arrange
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/unresolved/UnresolvedImport.java");
		CtModel model = launcher.buildModel();
		CtType<?> type = model.getAllTypes().stream().findFirst().get();
		CtCompilationUnit cu = type.getFactory().CompilationUnit().getOrCreate(type);
		List<String> importsBefore = getTextualImports(cu);

		// act
		new ImportCleaner().process(cu);

		// assert
		List<String> importsAfter = getTextualImports(cu);
		assertThat(importsAfter, equalTo(importsBefore));
	}

	private static List<String> getTextualImports(CtCompilationUnit cu) {
		return cu.getImports().stream()
				.map(CtImport::toString)
				.collect(Collectors.toList());
	}
}
