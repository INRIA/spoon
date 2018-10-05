/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.properties;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PropertiesTest {

	@Test
	public void testNonExistingDirectory() throws Exception {
		File tempFile = File.createTempFile("SPOON", "SPOON");
		tempFile.delete();

		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/properties/testclasses/Sample.java"
						));
		compiler.build();

		compiler.instantiateAndProcess(Arrays.asList(SimpleProcessor.class.getName()));
		assertEquals(0, factory.getEnvironment().getErrorCount());
	}

}
