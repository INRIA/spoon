/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support;

/**
 * Enum for representing logging levels.
 */
public enum Level {
	OFF(0), ERROR(100), WARN(200), INFO(300), DEBUG(400), TRACE(500);

	private final int levelValue;

	Level(int levelValue) {
		this.levelValue = levelValue;
	}

	/**
	 * The ordinal representation of this logging level. The higher this value, the more logging
	 * output is provided (i.e. the value increases with decreasing specificity).
	 *
	 * @return The ordinal logging level.
	 */
	public int toInt() {
		return levelValue;
	}
}
