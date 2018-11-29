package spoon.test.javadoc.testclasses;

import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtPackage;

public class Bar {


	/**
	 * Creates an annotation type.
	 *
	 * @param owner
	 * 		the package of the annotation type
	 * @param simpleName
	 * 		the name of annotation
	 */
	public <T> CtAnnotationType<?> create(CtPackage owner, String simpleName) {
		return null;
	}
}

/** This is a {@link Exception} */
class Foo {}
