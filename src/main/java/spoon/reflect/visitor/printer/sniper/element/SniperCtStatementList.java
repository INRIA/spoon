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
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.sniper.AbstractSniperListener;
import spoon.reflect.visitor.printer.sniper.SniperWriter;

import java.util.List;

public class SniperCtStatementList
		extends AbstractSniperListener<CtStatementList> {
	public SniperCtStatementList(SniperWriter writer, CtStatementList element) {
		super(writer, element);
	}

	@Override
	public void onAdd(AddAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.STATEMENT) {
			onStatementAdd(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDelete(DeleteAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.STATEMENT) {
			onStatementDelete(action);
			return;
		}
		notHandled(action);
	}

	@Override
	public void onDeleteAll(DeleteAllAction action) {
		if ((action.getContext().getChangedProperty()) == CtRole.STATEMENT) {
			onStatementDeleteAll(action);
			return;
		}
		notHandled(action);
	}

	private void onStatementAdd(AddAction action) {
		CtStatementList block = getElement();

		CtElement element = (CtElement) action.getNewValue();
		int position;
		int index = block.getStatements().indexOf(element);
		// add at the beginning
		if (index == 0) {
			position = block.getPosition().getSourceStart();
		} else {
			position = block.getStatements().get(index - 1).getPosition()
					.getSourceEnd();
		}

		getWriter().write(element, position, true);
	}

	private void onStatementDelete(DeleteAction action) {
		CtElement statement = (CtElement) action.getRemovedValue();
		getWriter().remove(statement);
	}

	private void onStatementDeleteAll(DeleteAllAction action) {
		List<CtStatement> statements = (List<CtStatement>) action
				.getRemovedValue();
		if (!statements.isEmpty()) {
			getWriter().remove(statements.get(0).getPosition().getSourceStart(),
					statements.get(statements.size() - 1).getPosition()
							.getSourceEnd());
		}
	}
}

