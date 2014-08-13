package spoon.test.subclass.prettyprinter;

import spoon.test.prettyprinter.DefaultPrettyPrinterDependency;

public class DefaultPrettyPrinterExample extends DefaultPrettyPrinterDependency {

	public DefaultPrettyPrinterExample(String message) {
		super();
		int a = Math.abs(message.length());
		this.message = message;
	}

	private String message;
}