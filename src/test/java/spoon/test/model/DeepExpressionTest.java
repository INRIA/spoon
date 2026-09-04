/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that Spoon handles legal but deeply nested expressions, such as OpenJDK's
 * {@code test/langtools/tools/javac/DeepStringConcat.java}, which contains about 32,000
 * binary operators in a single expression (see #6804).
 */
public class DeepExpressionTest {

	/** the number of binary operators in the generated expression, as in OpenJDK's DeepStringConcat.java */
	private static final int OPERATOR_COUNT = 32_000;

	private static String deepConcatenation() {
		StringBuilder source = new StringBuilder("class Deep { String value = \"a\"");
		for (int i = 0; i < OPERATOR_COUNT; i++) {
			source.append(" + \"a\"");
		}
		return source.append("; }").toString();
	}

	@Test
	public void testDeepExpressionPipeline(@TempDir Path outputDirectory) throws IOException {
		// contract: the whole pipeline handles a legal deeply nested expression (#6804)
		// buildModel exercises AstParentConsistencyChecker, process exercises ProcessingVisitor
		// and prettyprint exercises DefaultJavaPrettyPrinter, all of which recurse once per operator
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(new VirtualFile(deepConcatenation(), "Deep.java"));
		launcher.setSourceOutputDirectory(outputDirectory.toFile());
		AtomicInteger processedOperators = new AtomicInteger();
		launcher.addProcessor(new AbstractProcessor<CtBinaryOperator<?>>() {
			@Override
			public void process(CtBinaryOperator<?> operator) {
				processedOperators.incrementAndGet();
			}
		});

		launcher.run();

		assertEquals(OPERATOR_COUNT, processedOperators.get());
		Path printed = outputDirectory.resolve("Deep.java");
		assertTrue(Files.exists(printed), "the deep expression was not pretty-printed");
		assertEquals(OPERATOR_COUNT, countOccurrences(Files.readString(printed), '+'));
	}

	@Test
	public void testStackSizeZeroRunsOnTheCallingThread() {
		// contract: a stack size of 0 runs the pipeline on the calling thread, for embedders
		// that manage their own threads (#6804)
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setStackSize(0);
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		launcher.addInputResource(new VirtualFile("class Shallow { int value = 1 + 2; }", "Shallow.java"));
		AtomicReference<Thread> pipelineThread = new AtomicReference<>();
		launcher.addProcessor(new AbstractProcessor<CtClass<?>>() {
			@Override
			public void process(CtClass<?> type) {
				pipelineThread.set(Thread.currentThread());
			}
		});

		launcher.run();

		assertEquals(Thread.currentThread(), pipelineThread.get());
	}

	@Test
	public void testPipelineRunsOnASingleThread() {
		// contract: nested pipeline steps reuse the thread created by the outermost step (#6804)
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		launcher.addInputResource(new VirtualFile("class Shallow { int value = 1 + 2; }", "Shallow.java"));
		AtomicReference<Thread> pipelineThread = new AtomicReference<>();
		launcher.addProcessor(new AbstractProcessor<CtClass<?>>() {
			@Override
			public void process(CtClass<?> type) {
				pipelineThread.set(Thread.currentThread());
			}
		});

		// run() calls buildModel(), process() and prettyprint(), none of which spawns a thread of its own
		launcher.run();

		assertEquals("spoon", pipelineThread.get().getName());
	}

	@Test
	public void testNegativeStackSizeIsRejected() {
		// contract: an invalid stack size is reported instead of being silently ignored (#6804)
		Environment environment = new StandardEnvironment();
		assertEquals(Environment.DEFAULT_STACK_SIZE, environment.getStackSize());
		assertThrows(SpoonException.class, () -> environment.setStackSize(-1));
	}

	private static int countOccurrences(String text, char character) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == character) {
				count++;
			}
		}
		return count;
	}
}
