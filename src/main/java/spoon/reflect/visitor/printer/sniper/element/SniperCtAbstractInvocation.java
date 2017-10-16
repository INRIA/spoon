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
import spoon.experimental.modelobs.action.UpdateAction;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

public class SniperCtAbstractInvocation
		extends AbstractSniperListener<CtAbstractInvocation> {
	public SniperCtAbstractInvocation(SniperWriter writer,
			CtAbstractInvocation element) {
		super(writer, element);
	}

	@Override
	public void onAdd(AddAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.ARGUMENT) {
			onArgumentAdd(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDelete(DeleteAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.ARGUMENT) {
			onArgumentDelete(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.ARGUMENT) {
			onArgumentDeleteAll(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onUpdate(UpdateAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.EXECUTABLE) {
			onExecutableUpdate(action);
			return;
		}
		notHandled(action);
	}

	private void onArgumentAdd(AddAction action) {
		notHandled(action);
	}

	private void onArgumentDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onArgumentDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onExecutableUpdate(UpdateAction action) {
		notHandled(action);
	}
}

