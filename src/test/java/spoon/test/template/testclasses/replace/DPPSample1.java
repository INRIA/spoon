package spoon.test.template.testclasses.replace;

import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.visitor.TokenWriter;

public class DPPSample1 {
	private ElementPrinterHelper elementPrinterHelper;
	private TokenWriter printer;

	public <T extends Enum<?>> void method1(CtEnum<T> ctEnum) {
		printer.writeSpace().writeKeyword("extends").writeSpace();
		try (spoon.reflect.visitor.ListPrinter lp = elementPrinterHelper.createListPrinter(false, null, false, false, ",", true, false, ";")) {
		    for (spoon.reflect.declaration.CtEnumValue<?> enumValue : ctEnum.getEnumValues()) {
		        lp.printSeparatorIfAppropriate();
		        scan(enumValue);
		    }
		}
	}

	public <T extends Enum<?>> void method2(CtEnum<T> ctEnum) {
		try (spoon.reflect.visitor.ListPrinter lp = elementPrinterHelper.createListPrinter(false, null, false, false, ",", true, false, ";")) {
		    for (spoon.reflect.declaration.CtEnumValue<?> enumValue : ctEnum.getEnumValues()) {
		        lp.printSeparatorIfAppropriate();
		        scan(enumValue);
		    }
		}
	}

	private void scan(CtEnumValue<?> enumValue) {
	}

}
