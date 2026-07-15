class GuardedColonSwitch {
	int classify(Object value) {
		switch (value) {
			case String _ when !((String) value).isEmpty():
			case Integer _, Byte _ when ((Number) value).intValue() > 0:
				value.toString();
				return 1;
			default:
				return -1;
		}
	}

	int castGuard(Object value) {
		return switch (value) {
			case Boolean flag when (boolean) flag -> 1;
			default -> 0;
		};
	}
}
