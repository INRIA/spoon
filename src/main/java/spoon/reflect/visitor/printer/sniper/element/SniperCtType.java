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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

public class SniperCtType extends AbstractSniperListener<CtType> {
	public SniperCtType(SniperWriter writer, CtType element) {
		super(writer, element);
	}

	@Override
	public void onAdd(AddAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.EXECUTABLE) {
			onExecutableAdd(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.INTERFACE) {
			onInterfaceAdd(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.NESTED_TYPE) {
			onNestedTypeAdd(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.FIELD) {
			onFieldAdd(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDelete(DeleteAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.EXECUTABLE) {
			onExecutableDelete(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.INTERFACE) {
			onInterfaceDelete(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.NESTED_TYPE) {
			onNestedTypeDelete(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.FIELD) {
			onFieldDelete(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.EXECUTABLE) {
			onExecutableDeleteAll(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.INTERFACE) {
			onInterfaceDeleteAll(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.NESTED_TYPE) {
			onNestedTypeDeleteAll(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.FIELD) {
			onFieldDeleteAll(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onUpdate(UpdateAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.SUPER_TYPE) {
			onSuperTypeUpdate(action);
			return;
		}
		notHandled(action);
	}

	private void onExecutableAdd(AddAction action) {
		CtType<?> type = getElement();
		int position = 0;
		for (CtMethod<?> method : type.getMethods()) {
			if (method.getPosition() != null) {
				position = method.getPosition().getSourceEnd() + 2;
				break;
			}
		}
		if (position == 0) {
			position = type.getPosition().getSourceEnd() - 2;
		}
		getWriter().write((CtElement) action.getNewValue(), position);
	}

	private void onExecutableDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onExecutableDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onFieldAdd(AddAction action) {
		CtType<?> type = getElement();
		int position = 0;
		for (int i = getElement().getFields().size() - 1; i >= 0; i--) {
			CtField<?> ctField = type.getFields().get(i);
			if (ctField.getPosition() != null) {
				position = ctField.getPosition().getSourceEnd() + 2;
				break;
			}
		}
		if (position == 0) {
			position = type.getPosition().getSourceEnd() - 2;
		}
		getWriter().write((CtElement) action.getNewValue(), position);
	}

	private void onFieldDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onFieldDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onInterfaceAdd(AddAction action) {
		notHandled(action);
	}

	private void onInterfaceDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onInterfaceDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onNestedTypeAdd(AddAction action) {
		notHandled(action);
	}

	private void onNestedTypeDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onNestedTypeDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onSuperTypeUpdate(UpdateAction action) {
		notHandled(action);
	}
}

