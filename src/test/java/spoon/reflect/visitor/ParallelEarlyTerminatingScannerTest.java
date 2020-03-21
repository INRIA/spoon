/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import spoon.FluentLauncher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtMethod;

public class ParallelEarlyTerminatingScannerTest {
	private static final String INPUT_FILES = "src/test/java/spoon/reflect/visitor";
	@Rule
	public TemporaryFolder folderFactory = new TemporaryFolder();

	private AtomicReferenceArray<Integer> createCounter() {
		Integer[] counter = new Integer[] { 0, 0, 0, 0 };
		AtomicReferenceArray<Integer> atomicCounter = new AtomicReferenceArray<Integer>(counter);
		return atomicCounter;
	}

	private EarlyTerminatingScanner<CtMethod<?>> createScanner(AtomicReferenceArray<Integer> atomicCounter, int digit) {
		EarlyTerminatingScanner<CtMethod<?>> scanner = new EarlyTerminatingScanner<CtMethod<?>>() {
			@Override
			public <T> void visitCtMethod(CtMethod<T> m) {
				atomicCounter.getAndUpdate(digit, i -> i + 1);
			}
		};
		return scanner;
	}

	@Test
	public void compareWithSingleThreaded1() throws IOException, InterruptedException {
		// contract: running with 4 scanners parallel must produce the same result as
		// single threaded scanner.
		// for testing this a simple scanner counting visited nodes is used.

		// create a countingArray for the concurrent scanners.
		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		// create scanners
		EarlyTerminatingScanner<CtMethod<?>> p1 = createScanner(atomicCounter, 0);
		EarlyTerminatingScanner<CtMethod<?>> p2 = createScanner(atomicCounter, 1);
		EarlyTerminatingScanner<CtMethod<?>> p3 = createScanner(atomicCounter, 2);
		EarlyTerminatingScanner<CtMethod<?>> p4 = createScanner(atomicCounter, 3);

		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		EarlyTerminatingScanner<CtMethod<?>> singleThread = createScanner(singleThreadCounter);
		createModelAndAcceptScanner(singleThread);

		ParallelEarlyTerminatingScanner<CtMethod<?>> scanner = new ParallelEarlyTerminatingScanner<CtMethod<?>>(
				Arrays.asList(p1, p2, p3, p4));
		createModelAndAcceptScanner(scanner);
		scanner.awaitTermination();
		// after processing both |singleThreadCounter| == sum(|atomicCounter|) must be
		// true.
		// for checking this subtract each array value from the
		// singleThreadCounter and check for == 0
		for (int j = 0; j < atomicCounter.length(); j++) {
			singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(j));
		}
		assertTrue(singleThreadCounter.get() == 0);
	}

	private EarlyTerminatingScanner<CtMethod<?>> createScanner(AtomicInteger singleThreadCounter) {
		return new EarlyTerminatingScanner<CtMethod<?>>() {
			@Override
			public <T> void visitCtMethod(CtMethod<T> m) {
				singleThreadCounter.getAndIncrement();
			}
		};
	}

	private void createModelAndAcceptScanner(EarlyTerminatingScanner<CtMethod<?>> scanner) throws IOException {
		new FluentLauncher().inputResource(INPUT_FILES)
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel()
				.getAllTypes()
				.forEach(scanner::scan);

	}

	@Test
	public void compareWithSingleThreaded2() throws IOException, InterruptedException {
		// contract: a parallelScanner with one thread must produce the same result as
		// a normal scanner.

		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		EarlyTerminatingScanner<CtMethod<?>> p1 = createScanner(atomicCounter, 0);

		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		createModelAndAcceptScanner(createScanner(singleThreadCounter));

		ParallelEarlyTerminatingScanner<CtMethod<?>> scanner = new ParallelEarlyTerminatingScanner<>(Arrays.asList(p1));
		createModelAndAcceptScanner(scanner);
		scanner.awaitTermination();
		singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(0));
		assertTrue(singleThreadCounter.get() == 0);
	}

	@Test
	public void compareWithSingleThreaded3() throws IOException, InterruptedException {
		// contract: using an iterable with more elements than used should only use the
		// given number. Result must be correct too.
		// Here the iterable<scanner> has size 4 and only 3 are used.
		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		EarlyTerminatingScanner<CtMethod<?>> p1 = createScanner(atomicCounter, 0);
		EarlyTerminatingScanner<CtMethod<?>> p2 = createScanner(atomicCounter, 1);
		EarlyTerminatingScanner<CtMethod<?>> p3 = createScanner(atomicCounter, 2);
		EarlyTerminatingScanner<CtMethod<?>> p4 = createScanner(atomicCounter, 3);

		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		createModelAndAcceptScanner(createScanner(singleThreadCounter));

		ParallelEarlyTerminatingScanner<CtMethod<?>> scanner = new ParallelEarlyTerminatingScanner<CtMethod<?>>(
				Arrays.asList(p1, p2, p3, p4), 3);
		createModelAndAcceptScanner(scanner);
		scanner.awaitTermination();
		for (int j = 0; j < atomicCounter.length(); j++) {
			singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(j));
		}
		assertTrue(singleThreadCounter.get() == 0);
		// because only 3 are used
		assertTrue(atomicCounter.get(3) == 0);

	}

	@Test
	public void testSize() throws IOException {
		// contract: a thread pool with size zero must not created.
		assertThrows(IllegalArgumentException.class, () -> createModelAndAcceptScanner(
				new ParallelEarlyTerminatingScanner<CtMethod<?>>(Collections.emptyList(), 0)));
	}

	@Test
	public void testSize2() throws IOException { // contract: negative scanner numbers must throw an exception.
		assertThrows(IllegalArgumentException.class, () -> createModelAndAcceptScanner(
				new ParallelEarlyTerminatingScanner<CtMethod<?>>(Collections.emptyList(), -2)));
	}

	@Test
	public void testSize4() throws IOException {
		// contract: trying to consume more scanner than provided must throw an
		// exception.
		assertThrows(SpoonException.class, () -> createModelAndAcceptScanner(
				new ParallelEarlyTerminatingScanner<CtMethod<?>>(Collections.emptyList(), 2)));
	}

	@Test
	public void testSize5() throws IOException {
		// contract: a thread pool with size zero must not created.
		assertThrows(IllegalArgumentException.class, () -> createModelAndAcceptScanner(
				new ParallelEarlyTerminatingScanner<CtMethod<?>>(Collections.emptyList(), 0)));
	}

	@Test
	public void tryTerminate() throws IOException, InterruptedException {
		AtomicReferenceArray<Integer> atomicCounter = createCounter();

		EarlyTerminatingScanner<CtMethod<?>> p1 = createScanner(atomicCounter, 0);
		EarlyTerminatingScanner<CtMethod<?>> p2 = createScanner(atomicCounter, 1);
		EarlyTerminatingScanner<CtMethod<?>> p3 = createScanner(atomicCounter, 2);
		EarlyTerminatingScanner<CtMethod<?>> p4 = new EarlyTerminatingScanner<CtMethod<?>>() {
			@Override
			public <T> void visitCtMethod(CtMethod<T> m) {
				this.setResult(m);
				this.terminate();
			}
		};
		ParallelEarlyTerminatingScanner<CtMethod<?>> parallelScanner = new ParallelEarlyTerminatingScanner<CtMethod<?>>(
				Arrays.asList(p1, p2, p3, p4));
		createModelAndAcceptScanner(parallelScanner);
		parallelScanner.awaitTermination();
		assertNotNull(parallelScanner.getResult());
		assertTrue(Arrays.asList(p1, p2, p3, p4).stream().allMatch(EarlyTerminatingScanner::isTerminated));
	}
}
