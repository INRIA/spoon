/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture.runner;
/**
 * This interfaces defines an architecture checker. This has methods for starting the checking with serval different parameters.
 * Running checks consists of multiple steps
 * <ul>
 * <li> building models
 * <li> selection of architecture tests
 * <li> invocation of architecture tests
 * <li> handling of errors/printing a report
 * </ul>
 */
public interface ISpoonArchitecturalChecker {
/**
 * Starts the architecture check. The default testPath is used for architecture test lookup.
 */
	void runChecks();

	/**
	 * Starts the architecture check. The testPath is used for architecture test lookup.
	 * @param testPath  lowercase non null identifier for test model.
	 */
	void runChecks(String testPath);

}
