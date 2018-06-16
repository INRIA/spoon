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
package spoon.support.compiler;

/**
 * is the interface to follow the progress of the creation of the Spoon model.
 */
public interface SpoonProgress {

	enum Process {
		COMPILE,
		COMMENT,
		MODEL,
		IMPORT,
		PROCESS,
		PRINT
	}

	/**
	 * is called when a new process is started
	 * @param process the started process
	 */
	void start(Process process);

	/**
	 * is called when a step in the precess is started
	 * @param process the current process
	 * @param task the task that has been processed
	 * @param taskId the task id
	 * @param nbTask the number of task in the process
	 */
	void step(Process process, String task, int taskId, int nbTask);

	/**
	 * is called when a step in the precess is started
	 * @param process the current process
	 * @param task the task that has been processed
	 */
	void step(Process process, String task);

	/**
	 * is called when a new process is started
	 * @param process the finished process
	 */
	void end(Process process);

}
