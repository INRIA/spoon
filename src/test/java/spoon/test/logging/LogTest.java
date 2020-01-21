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
package spoon.test.logging;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import spoon.Launcher;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class LogTest {

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{Level.ALL, 5 },
				{Level.DEBUG, 5 },
				{Level.INFO, 1 },
				{Level.WARN, 0 },
				{Level.ERROR, 0 },
				{Level.OFF, 0 }
		});
	}

	@Parameterized.Parameter(0)
	public Level level;

	@Parameterized.Parameter(1)
	public int nbLogMessagesMinimum;

	@Test
	public void testAllLevelsForLogs() throws Exception {
		final Launcher launcher = new Launcher();

		Logger logger = mock(Logger.class);
		FieldUtils.writeField(launcher.getEnvironment(), "logger", logger, true);

		ArgumentCaptor<String> valueCaptureMessage = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Level> valueCaptureLevel = ArgumentCaptor.forClass(Level.class);

		doNothing().when(logger).log(valueCaptureLevel.capture(), valueCaptureMessage.capture());

		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/logging",
				"--level", level.toString()
		});

		// launcher provides two logging methods
		launcher.getEnvironment().debugMessage("debugMessage");
		launcher.getEnvironment().reportProgressMessage("reportProgressMessage");

		// contract: the --level arguments sets the level
		assertEquals(level, launcher.getFactory().getEnvironment().getLevel());

		// contract: the number of messages increases with the log level
		// System.out.println(level+ " " + valueCaptureMessage.getAllValues().size());
		assertTrue(valueCaptureMessage.getAllValues().size() >= nbLogMessagesMinimum);



	}
}
