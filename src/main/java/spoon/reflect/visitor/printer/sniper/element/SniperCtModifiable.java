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

import spoon.SpoonException;
import spoon.experimental.modelobs.action.Action;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

public class SniperCtModifiable extends AbstractSniperListener<CtModifiable> {
	public SniperCtModifiable(SniperWriter writer, CtModifiable element) {
		super(writer, element);
	}

	@Override
	public void onAction(Action action) {
		if ((action.getContext().getChangedProperty()) == CtRole.MODIFIER) {
			onModifierChange(action);
			return;
		}
		notHandled(action);
	}

	private void onModifierChange(Action action) {
		CtElement element = action.getContext().getElementWhereChangeHappens();
		if (element.getPosition() instanceof BodyHolderSourcePosition) {
			BodyHolderSourcePosition position = (BodyHolderSourcePosition) element
					.getPosition();
			getWriter().replaceModifiers(position.getModifierSourceStart(),
					position.getModifierSourceEnd(), (CtModifiable) element);
		} else if (!(element.getPosition() instanceof NoSourcePosition)) {
			throw new SpoonException(
					"Position is not correct" + element.getPosition());
		}
	}
}

