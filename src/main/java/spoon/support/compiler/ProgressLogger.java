/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import spoon.support.StandardEnvironment;

import java.util.GregorianCalendar;

public class ProgressLogger implements SpoonProgress {
	private long stepTimer;
	private long timer;
	private StandardEnvironment environment;

	public ProgressLogger(StandardEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public void start(Process process) {
		environment.debugMessage("Start " + process);
		timer = getCurrentTimeInMillis();
		stepTimer = timer;
	}

	@Override
	public void step(Process process, String task, int taskId, int nbTask) {
		environment.debugMessage("Step " + process + " " + taskId + "/" + nbTask + " " + task + " in " + (getCurrentTimeInMillis() - timer) + " ms");
		timer = getCurrentTimeInMillis();
	}

	@Override
	public void step(Process process, String task) {
		environment.debugMessage("Step " + process + " " + task + " in " + (getCurrentTimeInMillis() - timer) + " ms");
		timer = getCurrentTimeInMillis();
	}

	@Override
	public void end(Process process) {
		environment.debugMessage("End " + process + " in " + (getCurrentTimeInMillis() - stepTimer) + " ms");
	}

	private long getCurrentTimeInMillis() {
		return new GregorianCalendar().getTimeInMillis();
	}
}
