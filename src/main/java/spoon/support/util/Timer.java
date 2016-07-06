/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A utility class for performance statistics of Spoon.
 */
public class Timer {
	private static List<Timer> timestamps = new ArrayList<>();

	private static Deque<Timer> current = new ArrayDeque<>();

	/**
	 * Starts a timer.
	 *
	 * @param name
	 * 		the timer name
	 */
	public static void start(String name) {
		current.push(new Timer(name));
	}

	/**
	 * Stops a timer.
	 *
	 * @param name
	 * 		the timer name
	 */
	public static void stop(String name) {
		if (!current.peek().getName().equals(name)) {
			throw new RuntimeException("Must stop last timer");
		}
		current.peek().stop();
		timestamps.add(current.pop());
	}

	/**
	 * Displays all the timers.
	 */
	public static void display() {
		for (Timer time : timestamps) {
			System.out.println(time);
		}
	}

	String name;

	long start, stop;

	/**
	 * Constructs a timer.
	 *
	 * @param name
	 * 		the timer name
	 */
	public Timer(String name) {
		super();
		this.name = name;
		start = System.currentTimeMillis();
	}

	/**
	 * Stops the current timer.
	 */
	public void stop() {
		stop = System.currentTimeMillis();
	}

	/**
	 * Gets this timer's name.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the current time of this timer.
	 */
	public long getValue() {
		return stop - start;
	}

	/**
	 * A string representation of this timer.
	 */
	@Override
	public String toString() {
		return getName() + " \t" + getValue() + "ms";
	}

}
