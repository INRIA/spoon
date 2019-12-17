package spoon.processing;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Test;

import spoon.Launcher;
import spoon.refactoring.Refactoring;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class DeprecatedDeleteTest {
	@Test
	public void issue3195() {
		String path = "src\\test\\resources\\deprecated";
		String output = "src\\test\\resources\\deprecated\\deprecated";
		Launcher spoon = new Launcher();
		spoon.addInputResource(path);
		List<String> before = spoon.buildModel().getElements(new TypeFilter<>(CtMethod.class)).stream()
				.map(v -> v.toStringDebug()).collect(Collectors.toList());
		Refactoring.removeDeprecatedMethods(path);
		// verify result
		spoon = new Launcher();
		spoon.addInputResource(output);
		List<String> result = spoon.buildModel().getElements(new TypeFilter<>(CtMethod.class)).stream()
				.map(v -> v.toStringDebug()).collect(Collectors.toList());
		assertTrue(before.containsAll(result));
		assertFalse(result.containsAll(before));
		List<String> cut = before.stream().filter(v -> !result.contains(v)).collect(Collectors.toList());
		assertTrue(cut.size() == 1);
	}

	@After
	public void clean() throws IOException {
		Files.walk(Paths.get("src\\test\\resources\\deprecated\\deprecated")).sorted(Comparator.reverseOrder())
				.map(Path::toFile).forEach(File::delete);
	}
}
