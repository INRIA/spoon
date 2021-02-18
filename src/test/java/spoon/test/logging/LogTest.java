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

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.support.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class LogTest {

	@RunWith(Parameterized.class)
	public static class ParameterizedTest {
		@Parameterized.Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] {
					{Level.DEBUG, 6 },
					{Level.INFO, 2 },
					{Level.WARN, 0 },
					{Level.ERROR, 0 }
			});
		}

		@Parameterized.Parameter(0)
		public Level level;

		@Parameterized.Parameter(1)
		public int nbLogMessagesMinimum;

		@Test
		public void testAllLevelsForLogs() {
			final TestLogger logger = TestLoggerFactory.getTestLogger(Launcher.class);
			final Launcher launcher = new Launcher();
			logger.clear();
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
			assertTrue(logger.getLoggingEvents().size() >= nbLogMessagesMinimum);
		}
	}

	public static class NonParameterizedTest {

		@Test
		public void testMavenLauncherLogs() {
			// contract: MavenLauncher should output different logs depending on whether the classpath is inferred or manually set
			final TestLogger logger = TestLoggerFactory.getTestLogger(MavenLauncher.class);
			MavenLauncher mavenLauncher = new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
			assertEquals("Running in FULLCLASSPATH mode. Source folders and dependencies are inferred from the pom.xml file (doc: http://spoon.gforge.inria.fr/launcher.html).",logger.getLoggingEvents().get(0).getMessage());
			logger.clear();
			mavenLauncher = new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE, new String[]{"./"});
			assertEquals("Running in FULLCLASSPATH mode. Classpath is manually set (doc: http://spoon.gforge.inria.fr/launcher.html).",logger.getLoggingEvents().get(0).getMessage());
		}
	}
}