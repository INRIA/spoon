/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.generating.clone;

import spoon.reflect.declaration.CtElement;
import spoon.template.TemplateParameter;

public class GetterTemplateMatcher {
	private TemplateParameter<CtElement> _element_;

	public CtElement getElement() {
		return _element_.S();
	}
}
