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
package spoon.internal.mavenlauncher;

public class RangeVersion {
	String range;
	boolean includeStart;
	boolean includeEnd;
	Version start;
	Version end;

	RangeVersion(String range) {
		this.range = range;
		includeStart = range.startsWith("[");
		includeEnd = range.endsWith("]");
		String[] splitRange = range.substring(1, range.length() - 1).split(",");
		if (splitRange[0].isEmpty()) {
			start = new Version("0.0.0.0");
		} else {
			start = new Version(splitRange[0]);
		}
		if (splitRange.length == 1 || splitRange[1].isEmpty()) {
			end = new Version("99999999.9999999.999999.99999");
		} else {
			end = new Version(splitRange[1]);
		}
	}

	boolean isIncluded(Version v) {
		int compareToStart = v.compareTo(start);
		int compareToEnd = v.compareTo(end);
		if (compareToStart < 0 || compareToEnd > 0) {
			return false;
		}
		if ((compareToStart == 0 && includeStart) || (compareToEnd == 0 && includeEnd)) {
			return true;
		}
		return compareToStart > 0 && compareToEnd < 0;
	}

	@Override
	public String toString() {
		return range;
	}
}
