package spoon.support.compiler;

public interface SpoonProgress {

	enum STEP {
		COMPILE,
		COMMENT,
		MODEL,
		IMPORT,
		PROCESS,
		PRINT
	}

	void start(STEP step);

	void step(STEP step, String element, int taskId, int nbTask);

	void end(STEP step);

}
