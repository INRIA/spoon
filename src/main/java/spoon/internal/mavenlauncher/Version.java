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
		int buildIndex = version.indexOf("-");
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
