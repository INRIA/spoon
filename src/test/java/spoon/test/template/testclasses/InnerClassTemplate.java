package spoon.test.template.testclasses;

import spoon.template.ExtensionTemplate;

public class InnerClassTemplate extends ExtensionTemplate {
	public class Inner {
		int innerField;
		Inner() {
			innerField = 0;
		}
	}
}