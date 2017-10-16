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
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

public class SniperCtElement extends AbstractSniperListener<CtElement> {
	public SniperCtElement(SniperWriter writer, CtElement element) {
		super(writer, element);
	}

	@Override
	public void onAdd(AddAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.ANNOTATION) {
			onAnnotationAdd(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.COMMENT) {
			onCommentAdd(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDelete(DeleteAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.ANNOTATION) {
			onAnnotationDelete(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.COMMENT) {
			onCommentDelete(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.ANNOTATION) {
			onAnnotationDeleteAll(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.COMMENT) {
			onCommentDeleteAll(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onUpdate(UpdateAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.IS_IMPLICIT) {
			onIsImplicitUpdate(action);
			return;
		}
		if ((action.getContext().getChangedProperty()) == CtRole.POSITION) {
			onPositionUpdate(action);
			return;
		}
		notHandled(action);
	}

	private void onAnnotationAdd(AddAction action) {
		CtAnnotation annotation = (CtAnnotation) action.getNewValue();
		CtElement parent = annotation.getParent();
		SourcePosition parentPosition = parent.getPosition();
		int sourceStart = parentPosition.getSourceStart();
		if (!parent.getComments().isEmpty()) {
			sourceStart = parent.getComments()
					.get(parent.getComments().size() - 1).getPosition()
					.getSourceEnd();
		}
		getWriter().write(annotation, sourceStart - 1, true);
	}

	private void onAnnotationDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onAnnotationDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onCommentAdd(AddAction action) {
		CtElement newElement = (CtElement) action.getNewValue();
		int position = newElement.getParent().getPosition().getSourceStart();
		getWriter().write(newElement, position - 1, true);
	}

	private void onCommentDelete(DeleteAction action) {
		notHandled(action);
	}

	private void onCommentDeleteAll(DeleteAllAction action) {
		notHandled(action);
	}

	private void onIsImplicitUpdate(UpdateAction action) {
		notHandled(action);
	}

	private void onPositionUpdate(UpdateAction action) {
		notHandled(action);
	}
}

