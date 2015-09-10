package spoon.test.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.Launcher;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LogTest {

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{Level.ALL, true, true, true, true },
				{Level.DEBUG, true, true, true, true },
				{Level.INFO, true, true, true, false },
				{Level.WARN, false, true, true, false },
				{Level.ERROR, false, false, true, false },
				{Level.OFF, false, false, false, false }
		});
	}

	@Parameterized.Parameter(0)
	public Level level;

	@Parameterized.Parameter(1)
	public boolean isInfo;

	@Parameterized.Parameter(2)
	public boolean isWarn;

	@Parameterized.Parameter(3)
	public boolean isError;

	@Parameterized.Parameter(4)
	public boolean isDebug;

	@Test
	public void testAllLevelsForLogs() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "./src/test/java/spoon/test/logging",
				"--level", level.toString()
		});
		assertEquals(level, launcher.getFactory().getEnvironment().getLevel());
		Launcher.LOGGER.info("Log info");
		Launcher.LOGGER.warn("Log warn");
		Launcher.LOGGER.error("Log error");
		Launcher.LOGGER.debug("Log debug");

		assertEquals(isInfo, Launcher.LOGGER.isEnabledFor(Priority.INFO));
		assertEquals(isWarn, Launcher.LOGGER.isEnabledFor(Priority.WARN));
		assertEquals(isError, Launcher.LOGGER.isEnabledFor(Priority.ERROR));
		assertEquals(isDebug, Launcher.LOGGER.isEnabledFor(Priority.DEBUG));
	}
}
