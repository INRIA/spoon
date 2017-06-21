package spoon.test.generics.testclasses;

import java.util.EnumSet;

import spoon.reflect.declaration.ModifierKind;

public class EnumSetOf {

	public void m() {
		EnumSet.of(ModifierKind.STATIC);
	}

}
