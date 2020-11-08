/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture.report;

import java.lang.reflect.Method;

/**
 * This defines the api for a report printer. A report printer is called at start of all checks, before a method, after a method and after all checks.
 * It is guaranteed that {@link #finishPrinting()} is called even if a test case fails.
 */
public interface IReportPrinter {

	default void startPrinting() {

	}

	default void beforeMethod(Method method) {

	}

	default void afterMethod(Method method) {

	}

	default void finishPrinting() {

	}
}
