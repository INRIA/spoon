package spoon.test.sourcePosition;

import org.jspecify.annotations.Nullable;

import java.nio.file.Files;

class ModifierSourcePositions {
	public@Deprecated /**/static void method(final@Nullable AutoCloseable o) {
		final@Nullable String s;
		fin\u0061l@Nullable String s;
		try(final var var = Files.newBufferedReader(null);final var v2 = o) {

		} catch (final Exception e) {

		}
	}
}
