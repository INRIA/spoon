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
import spoon.reflect.code.CtNewArray;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

public class SniperCtNewArray extends AbstractSniperListener<CtNewArray> {
	public SniperCtNewArray(SniperWriter writer, CtNewArray element) {
		super(writer, element);
	}

	@Override
	public void onAdd(AddAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.DIMENSION) {
			onDimensionAdd(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.EXPRESSION) {
			onExpressionAdd(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDelete(DeleteAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.DIMENSION) {
			onDimensionDelete(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.EXPRESSION) {
			onExpressionDelete(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.DIMENSION) {
			onDimensionDeleteAll(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.EXPRESSION) {
			onExpressionDeleteAll(action);
			return;
		}
		notHandled(action);
	}

	private void onDimensionAdd(AddAction action) {
		notHandled(action);
	}

	private void onDimensionDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onDimensionDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onExpressionAdd(AddAction action) {
		notHandled(action);
	}

	private void onExpressionDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onExpressionDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}
}

