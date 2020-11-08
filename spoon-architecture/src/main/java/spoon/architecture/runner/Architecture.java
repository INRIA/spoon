/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture.runner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker annotation for a marking method as architecture test case.
 * The method must only have CtModels as arguments and the defining class needs an argument less constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Architecture {
	/**
	 * An array of Strings defining the identifier of meta model parameters. The order is important and identifier should be lower case only.
	 * An empty array could lead to errors and shouldn't be done.
	*/
	String[] modelNames() default {"srcModel", "testModel"};
	// marker annotation
}
