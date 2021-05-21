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

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.support.JavaOutputProcessor;
import spoon.support.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogTest {

	@ParameterizedTest
	@MethodSource("getLogLevelsAndExpectedCounts")
	public void testAllLevelsForLogs(Pair<Level, Integer> levelAndExpectedCount) {
		final Level level = levelAndExpectedCount.getLeft();
		final int expectedCount = levelAndExpectedCount.getRight();
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
		assertTrue(logger.getLoggingEvents().size() >= expectedCount);

	}

	/**
	 * @return log level and expected amount of logs for that level for the
	 * {@link LogTest::testAllLevelsForLogs} test.
	 */
	private static Stream<Pair<Level, Integer>> getLogLevelsAndExpectedCounts() {
		return Stream.of(
				Pair.of(Level.DEBUG, 6),
				Pair.of(Level.INFO, 2),
				Pair.of(Level.WARN, 0),
				Pair.of(Level.ERROR, 0),
				Pair.of(Level.OFF, 0)
		);
	}


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

	@Test
	public void testLoggingOff() {
		// contract: When logging is off, no message should me logged independent of logging level.
		final TestLogger logger = TestLoggerFactory.getTestLogger(Launcher.class);
		final Launcher launcher = new Launcher();
		logger.clear();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/logging",
				"--level", Level.OFF.toString()
		});

		// test messages with all logging levels
		for (Level level : Level.values()) {
			launcher.getEnvironment().report(new JavaOutputProcessor(), level,
					"This is a message with level " + level.toString());
		}
		assertEquals(0, logger.getLoggingEvents().size());
	}
}