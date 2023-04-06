/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

/**
 * is the interface to follow the progress of the creation of the Spoon model.
 * All methods have a default implementation doing nothing to avoid the need checking for null
 */
public interface SpoonProgress {

	enum Process {
		COMPILE,
		COMMENT,
		MODEL,
		IMPORT,
		COMMENT_LINKING,
		PROCESS,
		PRINT
	}

	/**
	 * is called when a new process is started
	 * @param process the started process
	 */
	default void start(Process process) {

	}

	/**
	 * is called when a step in the precess is started
	 * @param process the current process
	 * @param task the task that has been processed
	 * @param taskId the task id
	 * @param nbTask the number of task in the process
	 */
	default void step(Process process, String task, int taskId, int nbTask) {

	}

	/**
	 * is called when a step in the precess is started
	 * @param process the current process
	 * @param task the task that has been processed
	 */
	default void step(Process process, String task) {

	}

	/**
	 * is called when a new process is started
	 * @param process the finished process
	 */
	default void end(Process process) {

	}

}
