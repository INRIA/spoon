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

import spoon.experimental.modelobs.action.AddAction;
import spoon.experimental.modelobs.action.DeleteAction;
import spoon.experimental.modelobs.action.DeleteAllAction;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

public class SniperCtExecutable extends AbstractSniperListener<CtExecutable> {
	public SniperCtExecutable(SniperWriter writer, CtExecutable element) {
		super(writer, element);
	}

	@Override
	public void onAdd(AddAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.PARAMETER) {
			onParameterAdd(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.THROWN) {
			onThrownAdd(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDelete(DeleteAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.PARAMETER) {
			onParameterDelete(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.THROWN) {
			onThrownDelete(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.PARAMETER) {
			onParameterDeleteAll(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.THROWN) {
			onThrownDeleteAll(action);
			return;
		}
		notHandled(action);
	}

	private void onParameterAdd(AddAction action) {
		notHandled(action);
	}

	private void onParameterDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onParameterDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onThrownAdd(AddAction action) {
		notHandled(action);
	}

	private void onThrownDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onThrownDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}
}

