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
		if (splitRange[0].length() == 0) {
			start = new Version("0.0.0.0");
		} else {
			start = new Version(splitRange[0]);
		}
		if (splitRange.length == 1 || splitRange[1].length() == 0) {
			end = new Version("99999999.9999999.999999.99999");
		} else {
			end = new Version(splitRange[1]);
		}
	}


	boolean include(Version v) {
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
