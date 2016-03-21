/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.generating.replace;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtScanner;
import spoon.template.AbstractTemplate;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.Substitution;

public class ReplaceBlockTemplate extends AbstractTemplate<CtBlock<?>> {
	@Parameter
	CtConstructorCall<CtScanner> _call_;

	@Local
	public ReplaceBlockTemplate(CtConstructorCall<CtScanner> _call_) {
		this._call_ = _call_;
	}

	public void replace(CtElement original, CtElement replace) {
		try {
			_call_.S().scan(original.getParent());
		} catch (SpoonException ignore) {
		}
	}

	@Local
	@Override
	public CtBlock<?> apply(CtType<?> targetType) {
		return Substitution.substitute(targetType, this, getBlock(targetType.getFactory().Class().get(this.getClass())));
	}

	@Local
	public static CtBlock<?> getBlock(CtClass<Object> p) {
		return p.getMethodsByName("replace").get(0).getBody();
	}
}
