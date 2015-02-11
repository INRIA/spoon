package spoon.test.imports.testclasses;

import spoon.test.imports.testclasses.internal.PublicSuperClass;

class DefaultClientClass extends PublicSuperClass<DefaultClientClass.InnerClass> {
	static final class InnerClass {
	}

	@Override
	public DefaultClientClass.InnerClass visit() {
		return null;
	}
}
