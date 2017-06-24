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

import spoon.experimental.modelobs.ActionBasedChangeListener;
import spoon.experimental.modelobs.action.Action;
import spoon.experimental.modelobs.action.AddAction;
import spoon.experimental.modelobs.action.DeleteAction;
import spoon.experimental.modelobs.action.DeleteAllAction;
import spoon.experimental.modelobs.action.UpdateAction;

public abstract class AbstractSniperListener<T> implements
		ActionBasedChangeListener {
	private SniperWriter writer;
	private T element;

	public AbstractSniperListener(SniperWriter writer, T element) {
		this.writer = writer;
		this.element = element;
	}

	public T getElement() {
		return element;
	}

	public SniperWriter getWriter() {
		return writer;
	}

	protected void notHandled(Action action) throws SniperNotHandledAction {
		throw new SniperNotHandledAction(action);
	}

	@Override
	public void onAdd(AddAction action) {
		notHandled(action);
	}

	@Override
	public void onDelete(DeleteAction action) {
		notHandled(action);
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	@Override
	public void onUpdate(UpdateAction action) {
		notHandled(action);
	}

	@Override
	public void onAction(Action action) {
		notHandled(action);
	}
}
