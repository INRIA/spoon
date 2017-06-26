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
package spoon.reflect.visitor.printer.sniper.element;

import spoon.experimental.modelobs.action.UpdateAction;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

public class SniperCtAnnotationMethod
		extends AbstractSniperListener<CtAnnotationMethod> {
	public SniperCtAnnotationMethod(SniperWriter writer,
			CtAnnotationMethod element) {
		super(writer, element);
	}

	@Override
	public void onUpdate(UpdateAction action) {
		if ((action.getContext().getChangedProperty())
				== CtRole.DEFAULT_EXPRESSION) {
			onDefaultExpressionUpdate(action);
			return;
		}
		notHandled(action);
	}

	private void onDefaultExpressionUpdate(UpdateAction action) {
		notHandled(action);
	}
}

