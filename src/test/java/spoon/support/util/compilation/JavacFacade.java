package spoon.support.util.compilation;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavacFacade {

	/**
	 * Compiles a list of files and returns a classloader containing them.
	 *
	 * @param files the java files (unique name -> content) to compile
	 * @param parameters the parameters to pass to javac
	 * @return a classloader with the compiled classes loaded
	 * @throws AssertionError if anything goes wrong during compilation
	 * @throws IllegalArgumentException if the parameters are invalid
	 */
	public static ClassLoader compileFiles(Map<String, String> files, List<String> parameters) {
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		StringWriter output = new StringWriter();

		List<InMemoryInputObject> compilationUnits = files.entrySet()
			.stream()
			.map(entry -> new InMemoryInputObject(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());

		ClassFileManager manager = new ClassFileManager(
			javaCompiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)
		);

		Map<String, List<String>> diagnostics = new HashMap<>();

		Boolean successful = javaCompiler.getTask(
				output,
				manager,
				diagnostic -> {
					// Skip general information not related to the classes
					// Like "Note: Some messages have been simplified;"
					if (diagnostic.getSource() == null) {
						return;
					}
					List<String> diagnosticMessages = diagnostics
						.getOrDefault(diagnostic.getSource().getName(), new ArrayList<>());

					diagnosticMessages.add(diagnostic.toString());

					diagnostics.put(diagnostic.getSource().getName(), diagnosticMessages);
				},
				parameters,
				null,
				compilationUnits
			)
			.call();

		assertTrue(successful, "Compilation failed: " + diagnostics);

		Map<String, byte[]> compiledFiles = manager.getAll().entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				it -> it.getValue().getContent()
			));

		return new InMemoryClassLoader(compiledFiles);
	}
}
