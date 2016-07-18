/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.visitor.printer.sniper;

import spoon.diff.UpdateAction;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;

public class SniperCtVariableAccess extends
		AbstractSniperListener<CtVariableAccess> {

	public SniperCtVariableAccess(SniperWriter writer, CtVariableAccess element) {
		super(writer, element);
	}

	@Override
	public void onUpdate(UpdateAction action) {
		CtElement element = action.getContext().getElement();
		// rename the variable
		if (element instanceof CtReference) {
			element = element.getParent();
		}
		SourcePosition position = element.getPosition();
		getWriter().replace(position.getSourceStart(), position.getSourceEnd(), action.getNewElement() + "");
	}
}
