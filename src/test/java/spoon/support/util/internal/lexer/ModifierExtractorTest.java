package spoon.support.util.internal.lexer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.CtExtendedModifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

class ModifierExtractorTest {
	private static final Map<String, ModifierKind> LOOKUP = Arrays.stream(ModifierKind.values())
			.collect(Collectors.toMap(ModifierKind::toString, Function.identity()));
	private static final List<String> DELIMITER = List.of(
			" ",
			"(", ")",
			"[", "]",
			"{", "}",
			";", ",",
			".", "...",
			"@",
			"::",
			"=", ">", "<", "!", "~", "?", ":", "->",
			"==", ">=", "<=", "!=", "&&", "||", "++", "--",
			"+", "-", "*", "/", "&", "|", "^", "%", "<<", ">>", ">>>",
			"+=", "-=", "*=", "/=", "&=", "|=", "^=", "%=", "<<=", ">>=", ">>>="
	);

	@ParameterizedTest
	@MethodSource("modifiers")
	void testSingleModifier(String input) {
		// arrange
		ModifierExtractor extractor = new ModifierExtractor();
		char[] chars = input.toCharArray();
		ModifierKind kind = LOOKUP.get(input);
		CtExtendedModifier extended = CtExtendedModifier.explicit(kind);
		Map<ModifierKind, CtExtendedModifier> modifier = new HashMap<>(Map.of(
				kind, extended
		));

		// act
		extractor.collectModifiers(chars, 0, chars.length, modifier, SimpleSourcePosition::new);

		// assert
		assertThat(modifier, is(anEmptyMap()));
		assertThat(extended.getPosition(), is(instanceOf(SimpleSourcePosition.class)));
		assertThat(extended.getPosition(), is(equalTo(new SimpleSourcePosition(0, input.length() - 1))));
	}

	static Stream<Arguments> modifiers() {
		return Arrays.stream(ModifierKind.values())
				.map(ModifierKind::toString)
				.map(Arguments::of);
	}

	@ParameterizedTest
	@MethodSource("mixedContents")
	void testMixedDelimitedModifiers(String input) {
		// arrange
		ModifierExtractor extractor = new ModifierExtractor();
		char[] chars = input.toCharArray();
		Map<ModifierKind, CtExtendedModifier> allModifiers = Arrays.stream(ModifierKind.values())
				.collect(Collectors.toMap(Function.identity(), CtExtendedModifier::explicit));

		// act
		extractor.collectModifiers(chars, 0, input.length(), allModifiers, SimpleSourcePosition::new);

		// assert
		assertThat(allModifiers, is(anEmptyMap()));
	}

	static Stream<Arguments> mixedContents() {
		Random random = new Random(42);
		return Stream.generate(() -> {
					StringBuilder builder = new StringBuilder();
					includeAllModifiers(builder, random);
					return builder.toString();
				})
				.filter(string -> !(string.contains("//") || string.contains("/*"))) // oops, we created a comment
				.limit(10)
				.map(Arguments::of);

	}

	static void includeAllModifiers(StringBuilder builder, Random random) {
		List<ModifierKind> modifiers = Arrays.asList(ModifierKind.values());
		Collections.shuffle(modifiers, random);
		addDelimiters(builder, random);
		for (ModifierKind modifier : modifiers) {
			builder.append(modifier);
			addDelimiters(builder, random);
		}
	}

	static void addDelimiters(StringBuilder builder, Random random) {
		int count = random.nextInt(2) + 1; // [1, 3)
		for (int i = 0; i < count; i++) {
			builder.append(DELIMITER.get(random.nextInt(DELIMITER.size())));
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"/**/public",
			"public/**/",
			"/**/public/**/",
			"public/*final*/",
			"public//final",
			"//\npublic",
	})
	void testWithComments(String input) {
		// arrange
		ModifierExtractor extractor = new ModifierExtractor();
		char[] chars = input.toCharArray();
		Map<ModifierKind, CtExtendedModifier> allModifiers = Arrays.stream(ModifierKind.values())
				.collect(Collectors.toMap(Function.identity(), CtExtendedModifier::explicit));

		// act
		extractor.collectModifiers(chars, 0, input.length(), allModifiers, SimpleSourcePosition::new);

		// assert
		assertThat(allModifiers.keySet(), not(hasItem(ModifierKind.PUBLIC))); // public should be removed
		assertThat(allModifiers.keySet(), hasItem(ModifierKind.FINAL)); // final shouldn't be removed
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"try",
			"synchronize", // not synchronized
			"class",
			"int",
			"apublic",
			"publica",
			"non",
	})
	void testNonModifiers(String input) {
		// arrange
		ModifierExtractor extractor = new ModifierExtractor();
		char[] chars = input.toCharArray();
		Map<ModifierKind, CtExtendedModifier> allModifiers = Arrays.stream(ModifierKind.values())
				.collect(Collectors.toMap(Function.identity(), CtExtendedModifier::explicit));
		List<SimpleSourcePosition> foundStartPositions = new ArrayList<>();
		BiFunction<Integer, Integer, SourcePosition> createAndAdd = (start, end) -> {
			SimpleSourcePosition position = new SimpleSourcePosition(start, end);
			foundStartPositions.add(position);
			return position;
		};

		// act
		extractor.collectModifiers(chars, 0, input.length(), allModifiers, createAndAdd);

		// assert
		assertThat(foundStartPositions, is(empty()));
	}

	static class SimpleSourcePosition implements SourcePosition {
		private final int start;
		private final int end;

		SimpleSourcePosition(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			SimpleSourcePosition that = (SimpleSourcePosition) o;
			return start == that.start && end == that.end;
		}

		@Override
		public int hashCode() {
			return Objects.hash(start, end);
		}

		// just ignore all these methods

		@Override
		public boolean isValidPosition() {
			return false;
		}

		@Override
		public File getFile() {
			return null;
		}

		@Override
		public CompilationUnit getCompilationUnit() {
			return null;
		}

		@Override
		public int getLine() {
			return 0;
		}

		@Override
		public int getEndLine() {
			return 0;
		}

		@Override
		public int getColumn() {
			return 0;
		}

		@Override
		public int getEndColumn() {
			return 0;
		}

		@Override
		public int getSourceEnd() {
			return 0;
		}

		@Override
		public int getSourceStart() {
			return 0;
		}
	}

}
