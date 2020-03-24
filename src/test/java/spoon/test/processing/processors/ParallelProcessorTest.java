/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.processing.processors;

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
import spoon.processing.AbstractParallelProcessor;
import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;

public class ParallelProcessorTest {
	private static final String INPUT_FILES = "src/test/resources/deprecated/input";
	@Rule
	public TemporaryFolder folderFactory = new TemporaryFolder();

	private AtomicReferenceArray<Integer> createCounter() {
		Integer[] counter = new Integer[] { 0, 0, 0, 0 };
		AtomicReferenceArray<Integer> atomicCounter = new AtomicReferenceArray<Integer>(counter);
		return atomicCounter;
	}

	private Processor<CtElement> createProcessor(AtomicReferenceArray<Integer> atomicCounter, int digit) {
		Processor<CtElement> processor = new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				atomicCounter.getAndUpdate(digit, i -> i + 1);
			}
		};
		return processor;
	}

	@Test
	public void compareWithSingleThreaded1() throws IOException {
		// contract: running with 4 processors parallel must produce the same result as
		// single threaded processor.
		// for testing this a simple processor counting visited nodes is used.

		// create a countingArray for the concurrent processors.
		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		// create processors
		Processor<CtElement> p1 = createProcessor(atomicCounter, 0);
		Processor<CtElement> p2 = createProcessor(atomicCounter, 1);
		Processor<CtElement> p3 = createProcessor(atomicCounter, 2);
		Processor<CtElement> p4 = createProcessor(atomicCounter, 3);

		new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1, p2, p3, p4)) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();

		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		new FluentLauncher().inputResource(INPUT_FILES).processor(new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				singleThreadCounter.incrementAndGet();
			}
		}).noClasspath(true).outputDirectory(folderFactory.newFolder()).buildModel();

		// after processing both |singleThreadCounter| == sum(|atomicCounter|) must be
		// true.
		// for checking this subtract each array value from the
		// singleThreadCounter and check for == 0
		for (int j = 0; j < atomicCounter.length(); j++) {
			singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(j));
		}
		assertTrue(singleThreadCounter.get() == 0);
	}

	@Test
	public void compareWithSingleThreaded2() throws IOException {
		// contract: a parallelProcessor with one thread must produce the same result as
		// a normal processor.

		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		Processor<CtElement> p1 = createProcessor(atomicCounter, 0);

		new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1)) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		new FluentLauncher().inputResource(INPUT_FILES).processor(new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				singleThreadCounter.incrementAndGet();
			}
		}).noClasspath(true).outputDirectory(folderFactory.newFolder()).buildModel();
		singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(0));
		assertTrue(singleThreadCounter.get() == 0);
	}

	@Test
	public void consumerConstructorTest() throws IOException {
		// contract: creating with consumer constructor must produces correct results.
		// See other tests for explanation how the testing works.
		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>((e) -> atomicCounter.getAndUpdate(0, i -> i + 1), 4) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		new FluentLauncher().inputResource(INPUT_FILES).processor(new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				singleThreadCounter.incrementAndGet();
			}
		}).noClasspath(true).outputDirectory(folderFactory.newFolder()).buildModel();
		for (int j = 0; j < atomicCounter.length(); j++) {
			singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(j));
		}
		assertTrue(singleThreadCounter.get() == 0);
	}

	@Test
	public void compareWithSingleThreaded3() throws IOException {
		// contract: using an iterable with more elements than used should only use the
		// given number. Result must be correct too.
		// Here the iterable<Processor> has size 4 and only 3 are used.
		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		Processor<CtElement> p1 = createProcessor(atomicCounter, 0);
		Processor<CtElement> p2 = createProcessor(atomicCounter, 1);
		Processor<CtElement> p3 = createProcessor(atomicCounter, 2);
		Processor<CtElement> p4 = createProcessor(atomicCounter, 3);

		new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1, p2, p3, p4), 3) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		new FluentLauncher().inputResource(INPUT_FILES).processor(new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				singleThreadCounter.incrementAndGet();
			}
		}).noClasspath(true).outputDirectory(folderFactory.newFolder()).buildModel();
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
		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		Processor<CtElement> p1 = createProcessor(atomicCounter, 0);

		assertThrows(IllegalArgumentException.class, () -> new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1), 0) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel());
	}

	@Test
	public void testSize2() throws IOException {
		// contract: negative processor numbers must throw an exception.
		AtomicReferenceArray<Integer> atomicCounter = createCounter();
		Processor<CtElement> p1 = createProcessor(atomicCounter, 0);
		assertThrows(IllegalArgumentException.class, () -> new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1), -5) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel());
	}

	@Test
	public void testSize3() throws IOException {
		// contract: trying to consume more processor than provided must throw an
		// exception.
		assertThrows(SpoonException.class, () -> new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>(Collections.emptyList(), 1) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel());
	}

	@Test
	public void testSize4() throws IOException {
		// contract: trying to consume more processor than provided must throw an
		// exception.
		assertThrows(IllegalArgumentException.class, () -> new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>(Collections.emptyList()) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel());
	}

	@Test
	public void testSize5() throws IOException {
		// contract: a thread pool with size zero must not created.

		assertThrows(IllegalArgumentException.class, () -> new FluentLauncher().inputResource(INPUT_FILES)
				.processor(new AbstractParallelProcessor<CtElement>((v) -> v.toString(), 0) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel());
	}
}
