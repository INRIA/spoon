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
package spoon.test;

import org.apache.commons.io.FileUtils;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.kohsuke.MetaInfServices;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An execution listener maintaining a temporary directory tests can use. This is intended as an alternative to
 * Junit's {@link org.junit.jupiter.api.io.TempDir} but handles file locking a bit more leniently.
 */
@MetaInfServices(TestExecutionListener.class)
public class TemporaryDirectoryExecutionListener implements TestExecutionListener {

	public static final Path TEMPDIR = Path.of("spooned");

	@Override
	public void executionStarted(TestIdentifier testIdentifier) {
		try {
			if (Files.exists(TEMPDIR)) {
				tryDelete(3);
			}
			Files.createDirectory(TEMPDIR);
		} catch (IOException e) {
			// Not much we can do. Let it bubble up and get logged.
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Tries to delete a folder and retries after a delay on failure.
	 * <p>
	 * This might be needed on Windows, where recently exited processes might still hold file locks that prevent
	 * deletion. See <a href="https://github.com/INRIA/spoon/issues/4877">#4877</a> for more information.
	 *
	 * @param allowedFailures the number of times deletion may fail before giving up
	 * @throws IOException if deletion was not possible {@code allowedFailures} times in row
	 * @throws InterruptedException if the current thread is interrupted while waiting between deletion retries
	 */
	private void tryDelete(int allowedFailures) throws IOException, InterruptedException {
		try {
			FileUtils.deleteDirectory(TEMPDIR.toFile());
		} catch (IOException e) {
			if (allowedFailures > 0) {
				Thread.sleep(1000);
				tryDelete(allowedFailures - 1);
			} else {
				throw e;
			}
		}
	}

}
