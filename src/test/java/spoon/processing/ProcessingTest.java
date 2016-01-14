/*
 * Copyright (C) 2006-2015 INRIA and contributors
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

package spoon.processing;

import org.junit.Test;
import spoon.Launcher;
import spoon.processing.processors.MyProcessor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ProcessingTest {
	@Test
	public void testInterruptAProcessor() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/tets/java/spoon/processing/");
		launcher.setSourceOutputDirectory("./target/trash");
		final MyProcessor processor = new MyProcessor();
		launcher.addProcessor(processor);
		try {
			launcher.run();
		} catch (ProcessInterruption e) {
			fail("ProcessInterrupt exception must be catch in the ProcessingManager.");
		}
		assertFalse(processor.isShouldStayAtFalse());
	}
}
