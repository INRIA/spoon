/**
 * Copyright (C) 2006-2016 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.declaration;

import java.util.List;

/**
 * This abstract element defines a declaration that accepts formal type
 * parameters (aka generics), such as a CtType (<code>class A&lt;E&gt;</code>), CtMethod or CtConstructor.
 */
public interface CtFormalTypeDeclarer extends CtElement {

	/**
	 * Returns the formal type parameters of this generic element.
	 */
	List<CtTypeParameter> getFormalCtTypeParameters();

	/**
	 * Sets the type parameters of this generic element.
	 */
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	/**
	 * Add a type parameter to this generic element.
	 */
	<T extends CtFormalTypeDeclarer> T addFormalCtTypeParameter(CtTypeParameter formalTypeParameter);

	/**
	 * Removes a type parameters from this generic element.
	 */
	boolean removeFormalCtTypeParameter(CtTypeParameter formalTypeParameter);
}
