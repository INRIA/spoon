package spoon.processing;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import spoon.Launcher;
import spoon.refactoring.Refactoring;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class DeprecatedDeleteTest {
	@Test
	public void issue3195() {
		// clean dir if exists
		clean();
		// create Spoon
		String input = "src\\test\\resources\\deprecated\\input";
		String resultPath = "src\\test\\resources\\deprecated\\result";
		String correctResultPath = "src\\test\\resources\\deprecated\\correctResult";
		Launcher spoon = new Launcher();

		spoon.addInputResource(correctResultPath);
		List<CtMethod<?>> correctResult = spoon.buildModel().getElements(new TypeFilter<>(CtMethod.class));
		// save Methods before cleaning

		// now refactor code
		Refactoring.removeDeprecatedMethods(input, resultPath);

		// verify result
		spoon = new Launcher();
		spoon.addInputResource(resultPath);
		List<CtMethod<?>> calculation = spoon.buildModel().getElements(new TypeFilter<>(CtMethod.class));
		assertTrue(correctResult.containsAll(calculation));
		assertTrue(calculation.containsAll(calculation));
	}

	public void clean() {
		try {
			Files.walk(Paths.get("src\\test\\resources\\deprecated\\result")).sorted(Comparator.reverseOrder())
					.map(Path::toFile).forEach(File::delete);
		} catch (Exception e) {
			// error is kinda okay
		}

	}
}
