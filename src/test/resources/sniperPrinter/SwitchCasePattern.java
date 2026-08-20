package sniperPrinter;

class SwitchCasePattern {

	String describe(Object value) {
		return switch (value) {
			case Integer i -> "int:" + i;
			case String s -> "str:" + s;
			default -> "other";
		};
	}
}
