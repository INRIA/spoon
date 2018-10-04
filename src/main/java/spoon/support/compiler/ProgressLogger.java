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
