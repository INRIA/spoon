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

import spoon.diff.AddAction;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

public class SniperCtElement extends AbstractSniperListener<CtElement> {

	public SniperCtElement(SniperWriter writer, CtElement element) {
		super(writer, element);
	}

	@Override
	public void onAdd(AddAction action) {
		CtElement newElement = (CtElement) action.getNewElement();
		if (newElement instanceof CtAnnotation) {
			CtAnnotation annotation = (CtAnnotation) newElement;
			CtElement parent = annotation.getParent();
			SourcePosition parentPosition = parent.getPosition();
			int sourceStart = parentPosition.getSourceStart();
			if (!parent.getComments().isEmpty()) {
				sourceStart = parent.getComments().get(parent.getComments().size() -1).getPosition().getSourceEnd();
			}

			getWriter().write(annotation, sourceStart - 1,true);
		} else if (newElement instanceof CtComment) {
			int position = newElement.getParent().getPosition().getSourceStart();
			getWriter().write(newElement, position - 1, true);
		} else {
			throw new SniperNotHandledAction(action);
		}
	}
}
