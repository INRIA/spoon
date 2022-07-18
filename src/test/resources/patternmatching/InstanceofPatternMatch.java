class X {
	String typePattern(Object obj) {
		if (obj instanceof String s) {
			return s;
		}
		return "";
	}
}
