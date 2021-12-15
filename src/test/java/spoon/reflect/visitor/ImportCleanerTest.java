package spoon.reflect.visitor;

import org.junit.jupiter.api.Test;
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
	void testDoesNotRemoveImportOfSubType() {
		testImportCleanerDoesNotAlterImports("src/test/resources/importCleaner/DoNotRemoveSubType.java", "importCleaner.DoNotRemoveSubType");
	}

	@Test
	void testDoesNotImportTypeWhoseParentTypeIsAlreadyImported() {
		testImportCleanerDoesNotAlterImports("src/test/resources/importCleaner/TypeImportButUseSubType.java", "importCleaner.TypeImportButUseSubType");
	}

	@Test
	void testDoesNotRemoveImportForStaticFieldOfStaticClass() {
		// contract: The import cleaner should not remove import of the static field
		testImportCleanerDoesNotAlterImports("src/test/resources/fieldImport", "fieldImport.StaticFieldImport");
	}

	@Test
	void testDoesNotImportClassesIfAlreadyImportedViaWildCard() {
		// contract: The import cleaner should not import classes if they are encompassed in wildcard import.
		testImportCleanerDoesNotAlterImports("src/test/resources/importCleaner/WildCardImport.java", "WildCardImport");
	}

	@Test
	void testDoesNotDuplicateUnresolvedImports() {
	    // contract: The import cleaner should not duplicate unresolved imports
		testImportCleanerDoesNotAlterImports("./src/test/resources/unresolved/UnresolvedImport.java", "UnresolvedImport");
	}

	@Test
	void testDoesNotImportInheritedStaticMethod() {
		// contract: The import cleaner should not import static attributes that are inherited
		testImportCleanerDoesNotAlterImports("./src/test/resources/inherit-static-method", "Derived");
	}

	/**
	 * Test that processing the target class' compilation unit with the import cleaner does not
	 * alter the imports.
	 */
	private static void testImportCleanerDoesNotAlterImports(String source, String targetClassQualname) {
		// arrange
		Launcher launcher = new Launcher();
		launcher.addInputResource(source);
		CtModel model = launcher.buildModel();
		CtType<?> type = model.getUnnamedModule().getFactory().Type().get(targetClassQualname);
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
