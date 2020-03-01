/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.processing.processors;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;
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

/**
 * ParallelProcessorTest
 */
public class ParallelProcessorTest {
	@Rule
	public TemporaryFolder folderFactory = new TemporaryFolder();

	@Test
	public void compareWithSingleThreaded1() throws IOException {
		Integer[] counter = new Integer[] { 0, 0, 0, 0 };
		AtomicReferenceArray<Integer> atomicCounter = new AtomicReferenceArray<Integer>(counter);
		Processor<CtElement> p1 = new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				atomicCounter.getAndUpdate(0, i -> i + 1);
			}
		};
		Processor<CtElement> p2 = new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				atomicCounter.getAndUpdate(1, i -> i + 1);
			}
		};
		Processor<CtElement> p3 = new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				atomicCounter.getAndUpdate(2, i -> i + 1);
			}
		};
		Processor<CtElement> p4 = new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				atomicCounter.getAndUpdate(3, i -> i + 1);
			}
		};
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1, p2, p3, p4)) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.processor(new AbstractProcessor<CtElement>() {
					@Override
					public void process(CtElement element) {
						singleThreadCounter.incrementAndGet();
					}
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		for (int j = 0; j < atomicCounter.length(); j++) {
			singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(j));
		}
		assertTrue(singleThreadCounter.get() == 0);
	}

	@Test
	public void compareWithSingleThreaded2() throws IOException {
		Integer[] counter = new Integer[] { 0, 0, 0, 0 };
		AtomicReferenceArray<Integer> atomicCounter = new AtomicReferenceArray<Integer>(counter);
		Processor<CtElement> p1 = new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				atomicCounter.getAndUpdate(0, i -> i + 1);
			}
		};
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1)) {
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.processor(new AbstractProcessor<CtElement>() {
					@Override
					public void process(CtElement element) {
						singleThreadCounter.incrementAndGet();
					}
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		for (int j = 0; j < atomicCounter.length(); j++) {
			singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(j));
		}
		assertTrue(singleThreadCounter.get() == 0);
	}

	@Test
	public void consumerConstructorTest() throws IOException {
		Integer[] counter = new Integer[] { 0, 0, 0, 0 };
		AtomicReferenceArray<Integer> atomicCounter = new AtomicReferenceArray<Integer>(counter);
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.processor(
						new AbstractParallelProcessor<CtElement>((e) -> atomicCounter.getAndUpdate(0, i -> i + 1), 4) {
						})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		AtomicInteger singleThreadCounter = new AtomicInteger(0);
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.processor(new AbstractProcessor<CtElement>() {
					@Override
					public void process(CtElement element) {
						singleThreadCounter.incrementAndGet();
					}
				})
				.noClasspath(true)
				.outputDirectory(folderFactory.newFolder())
				.buildModel();
		for (int j = 0; j < atomicCounter.length(); j++) {
			singleThreadCounter.set(singleThreadCounter.get() - atomicCounter.get(j));
		}
		assertTrue(singleThreadCounter.get() == 0);
	}

	@Test
	public void testDistinctCheck() throws IOException {
		Integer[] counter = new Integer[] { 0, 0, 0, 0 };
		AtomicReferenceArray<Integer> atomicCounter = new AtomicReferenceArray<Integer>(counter);
		Processor<CtElement> p1 = new AbstractProcessor<CtElement>() {
			@Override
			public void process(CtElement element) {
				atomicCounter.getAndUpdate(0, i -> i + 1);
			}
		};
		assertThrows(SpoonException.class,
				() -> new FluentLauncher().inputResource("src/test/resources/deprecated/input")
						.processor(new AbstractParallelProcessor<CtElement>(Arrays.asList(p1, p1)) {
						})
						.noClasspath(true)
						.outputDirectory(folderFactory.newFolder())
						.buildModel());
	}
}
