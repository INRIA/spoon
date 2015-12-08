/**
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
package spoon.reflect;

import java.util.ArrayList;

/**
 * Recommended default capacities for containers (primarily {@link ArrayList})
 * of AST model elements. There are mostly only several linked elements, so
 * using default capacity (10, in case of {@code ArrayList}) is a memory waste.
 */
public final class ModelElementContainerDefaultCapacities {
	/*
	 * Author Roman Leventov
     *
     * Some element types were analyzed through JDK 7 sources,
     * others chosen for my feelings only.
     *
     * For those I have precise averages, I choose to take a ceiling of that
     * average as the default capacity. This choice is biased towards performing
     * less container resizes, rather deadly memory efficiency. Not sure this is
     * the right choice, for most Spoon use-cases, but anyway this is better
     * than ArrayList's default of 10.
     */

	// JDK 7 average is 1.063 (methods), 1.207 (constructors)
	public static final int PARAMETERS_CONTAINER_DEFAULT_CAPACITY = 2;

	// > 1 very rarely
	public static final int CASTS_CONTAINER_DEFAULT_CAPACITY = 1;

	// JDK 7 average is 2.150
	public static final int BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY = 3;

	// JDK 7 average is 1.652
	public static final int CASE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY = 2;

	// > 1 very rarely
	public static final int FOR_INIT_STATEMENTS_CONTAINER_DEFAULT_CAPACITY = 1;

	// > 1 very rarely
	public static final int FOR_UPDATE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY = 1;

	// 1-2
	public static final int CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY = 2;

	// > 1 very rarely
	public static final int NEW_ARRAY_DEFAULT_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY = 1;

	// JDK 7 average is 6.487
	public static final int SWITCH_CASES_CONTAINER_DEFAULT_CAPACITY = 7;

	// 1-2
	public static final int CATCH_CASES_CONTAINER_DEFAULT_CAPACITY = 2;

	// > 1 very rarely
	public static final int RESOURCES_CONTAINER_DEFAULT_CAPACITY = 1;

	// > 1 very rarely
	public static final int COMPILATION_UNIT_DECLARED_TYPES_CONTAINER_DEFAULT_CAPACITY = 1;

	// > 1 very rarely
	public static final int ANONYMOUS_EXECUTABLES_CONTAINER_DEFAULT_CAPACITY = 1;

	// In JDK 7 only 1, if any
	public static final int CONSTRUCTOR_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY = 1;

	// 1-2
	public static final int ANNOTATIONS_CONTAINER_DEFAULT_CAPACITY = 2;

	// 1-2
	public static final int METHOD_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY = 2;

	// 1-2
	public static final int TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY = 2;

	// 1-2
	public static final int CONSTRUCTOR_CALL_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY = 2;

	// JDK 7 average 3.861
	public static final int FIELDS_CONTAINER_DEFAULT_CAPACITY = 4;

	// > 1 very rarely
	public static final int TYPE_BOUNDS_CONTAINER_DEFAULT_CAPACITY = 1;

	private ModelElementContainerDefaultCapacities() {
	}
}
