package spoon.support.compiler;

/**
 * is the interface to follow the progress of the creation of the Spoon model.
 */
public interface SpoonProgress {

	enum PROCESS {
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
	void start(PROCESS process);

	/**
	 * is called when a step in the precess is started
	 * @param process the current process
	 * @param task the task that has been processed
	 * @param taskId the task if
	 * @param nbTask the number of task in the process
	 */
	void step(PROCESS process, String task, int taskId, int nbTask);

	/**
	 * is called when a new process is started
	 * @param process the finished process
	 */
	void end(PROCESS process);

}
