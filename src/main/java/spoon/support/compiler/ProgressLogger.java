package spoon.support.compiler;

import spoon.support.compiler.jdt.JDTTreeBuilder;

import java.util.GregorianCalendar;

public class ProgressLogger implements SpoonProgress {
	private long stepTimer;
	private long timer;

	@Override
	public void start(STEP step) {
		System.out.println("Start " + step);
		timer = getCurrentTimeInMillis();
		stepTimer = timer;
	}

	@Override
	public void step(STEP step, String element, int taskId, int nbTask) {
		JDTTreeBuilder.getLogger().trace("Step " + step + " " + taskId + "/" + nbTask + " " + element + " in " + (getCurrentTimeInMillis() - timer) + " ms");
		timer = getCurrentTimeInMillis();
	}

	@Override
	public void end(STEP step) {
		JDTTreeBuilder.getLogger().trace("End " + step + " in " + (getCurrentTimeInMillis() - stepTimer) + " ms");
	}

	private long getCurrentTimeInMillis() {
		return new GregorianCalendar().getTimeInMillis();
	}
}
