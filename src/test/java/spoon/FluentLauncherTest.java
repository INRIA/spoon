/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.AstParentConsistencyChecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FluentLauncherTest
 */
public class FluentLauncherTest {

	@Test
	public void testSimpleUsage(@TempDir Path tempDir) throws IOException {
		CtModel model = new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.noClasspath(true)
				.outputDirectory(tempDir.toString())
				.buildModel();
		assertNotNull(model);
	}

	@Test
	public void testProcessor(@TempDir Path tempDir) throws IOException {
		List<String> output = new ArrayList<>();
		List<String> output2 = new ArrayList<>();
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.outputDirectory(tempDir.toString())
				.processor(Arrays.asList(new AbstractProcessor<CtType<?>>() {
					public void process(CtType<?> element) {
						output.add(element.toString());
					}
				}))
				.processor(new AbstractProcessor<CtType<?>>() {
					public void process(CtType<?> element) {
						output2.add(element.toString());
					}
				})
				.noClasspath(true)
				.buildModel();
		// shouldn't be empty after processor usage.
		assertTrue(!output.isEmpty());
		// both lists should be same because same processor type
		assertEquals(output, output2);
	}

	@Test
	public void testConsistency(@TempDir Path tempDir) throws IOException {
		new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.noClasspath(true)
				.complianceLevel(11)
				.disableConsistencyChecks()
				.outputDirectory(tempDir.toString())
				.buildModel()
				.getUnnamedModule()
				.accept(new AstParentConsistencyChecker());
	}

	@Test
	public void testSettings(@TempDir Path tempDir) throws IOException {
		CtModel model = new FluentLauncher().inputResource("src/test/resources/deprecated/input")
				.noClasspath(true)
				.encoding(Charset.defaultCharset())
				.autoImports(true)
				.outputDirectory(tempDir.toString())
				.buildModel();
		assertNotNull(model);
	}

	/**
	 * shows using the FluentLauncher with different launchers.
	 *
	 * @throws IOException
	 */
	@Test
	public void testMavenLauncher(@TempDir Path tempDir) throws IOException {
		CtModel model = new FluentLauncher(new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.ALL_SOURCE))
				.complianceLevel(17)
				.outputDirectory(tempDir.toString())
				.buildModel();
		assertNotNull(model);
	}
}
