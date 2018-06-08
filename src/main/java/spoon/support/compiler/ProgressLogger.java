package spoon.support.compiler;

import spoon.support.compiler.jdt.JDTTreeBuilder;

import java.util.GregorianCalendar;

public class ProgressLogger implements SpoonProgress {
	private long stepTimer;
	private long timer;

	@Override
	public void start(PROCESS process) {
		System.out.println("Start " + process);
		timer = getCurrentTimeInMillis();
		stepTimer = timer;
	}

	@Override
	public void step(PROCESS process, String task, int taskId, int nbTask) {
		JDTTreeBuilder.getLogger().trace("Step " + process + " " + taskId + "/" + nbTask + " " + task + " in " + (getCurrentTimeInMillis() - timer) + " ms");
		timer = getCurrentTimeInMillis();
	}

	@Override
	public void end(PROCESS process) {
		JDTTreeBuilder.getLogger().trace("End " + process + " in " + (getCurrentTimeInMillis() - stepTimer) + " ms");
	}

	private long getCurrentTimeInMillis() {
		return new GregorianCalendar().getTimeInMillis();
	}
}
