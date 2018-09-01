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

public class Version implements Comparable<Version> {
	String version;
	int major = -1;
	int minor = -1;
	int incremental = -1;
	int build = -1;
	String qualifier;

	Version(String version) {
		this.version = version;
		int buildIndex = version.indexOf('-');
		if (buildIndex != -1) {
			String build = version.substring(buildIndex + 1);
			try {
				this.build = Integer.parseInt(build);
			} catch (NumberFormatException e) {
				this.qualifier = build;
			}
			version = version.substring(0, buildIndex);
		}
		String[] splitVersion = version.split("\\.");
		try {
			this.major = Integer.parseInt(splitVersion[0]);
		} catch (NumberFormatException ignore) {
		}
		if (splitVersion.length > 1) {
			try {
				this.minor = Integer.parseInt(splitVersion[1]);
			} catch (NumberFormatException ignore) {
			}
		}
		if (splitVersion.length > 2) {
			try {
				this.incremental = Integer.parseInt(splitVersion[2]);
			} catch (NumberFormatException ignore) {
			}
		}
	}

	@Override
	public int compareTo(Version v) {
		if (major < v.major) {
			return -1;
		}
		if (major > v.major) {
			return 1;
		}
		if (minor < v.minor) {
			return -1;
		}
		if (minor > v.minor) {
			return 1;
		}
		if (incremental < v.incremental) {
			return -1;
		}
		if (incremental > v.incremental) {
			return 1;
		}
		if (build < v.build) {
			return -1;
		}
		if (build > v.build) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return version;
	}
}
