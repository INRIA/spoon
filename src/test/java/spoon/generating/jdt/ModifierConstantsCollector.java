package spoon.generating.jdt;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A helper class that lets you generate a markdown file with all Acc*
 * fields in the relevant JDT classes.
 */
public class ModifierConstantsCollector {

	private final Class<?>[] classes;

	public static void main(String[] args) throws IOException {
		ModifierConstantsCollector collector = new ModifierConstantsCollector(
				ClassFileConstants.class,
				ExtraCompilerModifiers.class
		);
		Map<Integer, List<Field>> map = collector.processClasses();

		Path outputDir = Paths.get("target/test/jdt/");
		Files.createDirectories(outputDir);
		try (PrintStream printStream = new PrintStream(Files.newOutputStream(
				outputDir.resolve("constants.md"),
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE
		))) {
			print(map, printStream);
		}
	}

	public ModifierConstantsCollector(Class<?>... classes) {
		this.classes = classes;
	}

	private static Predicate<Field> isIntField() {
		return field -> field.getType() == int.class;
	}

	private static Function<Field, Integer> extractDefault() {
		return field -> {
			try {
				return field.getInt(null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		};
	}

	private static Predicate<Field> hasModifier(int modifier) {
		return field -> (field.getModifiers() & modifier) != 0;
	}

	private static Predicate<Field> nameStartsWith(String s) {
		return field -> field.getName().startsWith(s);
	}

	public Map<Integer, List<Field>> processClasses() {
		return Arrays.stream(classes)
				.map(this::collectConstants)
				.flatMap(Collection::stream)
				.sorted(Comparator.comparing(extractDefault()))
				.collect(Collectors.groupingBy(extractDefault(), LinkedHashMap::new, Collectors.toList()));
	}

	public static void print(Map<Integer, List<Field>> map, PrintStream out) {
		map.forEach((k, v) -> {
			out.println("| 0x" + Integer.toHexString(k) + " |");
			out.println("| -------------------------------- |");
			v.forEach(f -> {
				Class<?> decl = f.getDeclaringClass();
				String name = f.getName();
				out.println("| " + decl.getSimpleName() + "#" + name + " |");
			});
			out.println();
		});
	}

	private Set<Field> collectConstants(Class<?> clazz) {
		return Arrays.stream(clazz.getFields())
				.filter(isIntField())
				.filter(hasModifier(Modifier.STATIC))
				.filter(hasModifier(Modifier.PUBLIC))
				.filter(nameStartsWith("Acc"))
				.collect(Collectors.toSet());
	}
}
