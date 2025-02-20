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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.slf4j.LoggerFactory;
import spoon.FluentLauncher;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.JavaOutputProcessor;
import spoon.support.Level;
import spoon.testing.utils.GitHubIssue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(LogTest.LogCaptureExtension.class)
public class LogTest {

	@ParameterizedTest
	@MethodSource("getLogLevelsAndExpectedCounts")
	public void testAllLevelsForLogs(Pair<Level, Integer> levelAndExpectedCount, LogCapture logCapture) {
		final Level level = levelAndExpectedCount.getLeft();
		final int expectedCount = levelAndExpectedCount.getRight();
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/logging",
				"--level", level.toString()
		});

		// launcher provides two logging methods
		launcher.getEnvironment().debugMessage("debugMessage");
		launcher.getEnvironment().reportProgressMessage("reportProgressMessage");

		// contract: the --level arguments sets the level
		assertThat(launcher.getFactory().getEnvironment().getLevel()).isEqualTo(level);

		// contract: the number of messages increases with the log level
		assertThat(logCapture.loggingEvents()).hasSizeGreaterThanOrEqualTo(expectedCount);
		if (expectedCount == 0) {
			assertThat(logCapture.loggingEvents()).isEmpty();
		}
	}

	/**
	 * @return log level and expected number of logs for that level for the
	 * {@link LogTest::testAllLevelsForLogs} test.
	 */
	private static Stream<Pair<Level, Integer>> getLogLevelsAndExpectedCounts() {
		return Stream.of(
				Pair.of(Level.DEBUG, 6),
				Pair.of(Level.INFO, 1),
				Pair.of(Level.WARN, 0),
				Pair.of(Level.ERROR, 0),
				Pair.of(Level.OFF, 0)
		);
	}


	@Test
	public void testMavenLauncherLogs(LogCapture logCapture) {
		// contract: MavenLauncher should output different logs depending on whether the classpath is inferred or manually set
		new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
		assertThat(logCapture.loggingEvents(Level.INFO).get(0).getMessage()).isEqualTo(
			"Running in FULLCLASSPATH mode. Source folders and dependencies are inferred from the pom.xml file (doc: http://spoon.gforge.inria.fr/launcher.html)."
		);

		logCapture.clear();

		new MavenLauncher("./pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE, new String[]{"./"});
		assertThat(logCapture.loggingEvents(Level.INFO).get(0).getMessage()).isEqualTo(
			"Running in FULLCLASSPATH mode. Classpath is manually set (doc: http://spoon.gforge.inria.fr/launcher.html)."
		);
	}

	@Test
	public void testLoggingOff(LogCapture logCapture) {
		// contract: When logging is off, no message should be logged independent of logging level.
		Launcher launcher = new Launcher();
		logCapture.clear();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/logging",
				"--level", Level.OFF.toString()
		});

		// test messages with all logging levels
		for (Level level : Level.values()) {
			launcher.getEnvironment().report(
				new JavaOutputProcessor(),
				level,
				"This is a message with level " + level.toString()
			);
		}
		assertThat(logCapture.loggingEvents()).isEmpty();
	}

	@Test
	@GitHubIssue(issueNumber = 4997, fixed = true)
	void innerTypesCrashesLogging() {
		// contract: when a class has inner types, the logging should not crash with a NPE
		String codePath = "src/test/resources/logging/TestCase2.java";
		var processor = new AbstractProcessor<CtConstructor<?>>() {
			@Override
			public void process(CtConstructor<?> element) {
				// do nothing
			}
			public boolean isToBeProcessed(CtConstructor<?> candidate) {
        			List<CtInvocation<?>> invocations = Query.getElements(candidate, new TypeFilter<>(CtInvocation.class));
        			invocations.forEach(i -> getEnvironment().report(this, Level.INFO, i, "Message"));
       				return false;
	  		}};
		assertDoesNotThrow(() -> new FluentLauncher().inputResource(codePath).processor(processor).buildModel());
	}

	public record LogCapture(ListAppender<ILoggingEvent> listAppender) {
		public List<ILoggingEvent> loggingEvents() {
			return Collections.unmodifiableList(listAppender.list);
		}

		public List<ILoggingEvent> loggingEvents(Level minLevel) {
			return listAppender.list.stream()
				.filter(event -> minLevel.compareTo(Level.valueOf(event.getLevel().toString())) >= 0)
				.toList();
		}

		public void start() {
			listAppender.start();
		}

		public void stop() {
			listAppender.stop();
		}

		public void clear() {
			listAppender.list.clear();
		}
	}

	public static class LogCaptureExtension implements ParameterResolver, AfterTestExecutionCallback {

		private final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

		private LogCapture logCapture;

		@Override
		public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
			return parameterContext.getParameter().getType() == LogCapture.class;
		}

		@Override
		public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
			logCapture = new LogCapture(new ListAppender<>());

			setup();

			return logCapture;
		}

		@Override
		public void afterTestExecution(ExtensionContext context) {
			teardown();
		}

		private void setup() {
			logger.addAppender(logCapture.listAppender());
			logCapture.start();
		}

		private void teardown() {
			if (logCapture == null || logger == null) {
				return;
			}

			logger.detachAndStopAllAppenders();
			logCapture.stop();
		}
	}
}
