/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.ParentFunction;

import java.util.List;

import static spoon.reflect.path.CtRole.TARGET_LABEL;

public class CtBreakImpl extends CtStatementImpl implements CtBreak {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = TARGET_LABEL)
	String targetLabel;


	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtBreak(this);
	}

	@Override
	public String getTargetLabel() {
		return targetLabel;
	}

	@Override
	public <T extends CtLabelledFlowBreak> T setTargetLabel(String targetLabel) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TARGET_LABEL, targetLabel, this.targetLabel);
		this.targetLabel = targetLabel;
		return (T) this;
	}

	@Override
	public CtStatement getLabelledStatement() {
		List<CtStatement> listParents = this.map(new ParentFunction().includingSelf(true)).list();

		for (CtElement parent : listParents) {
			if (parent instanceof CtStatement) {
				CtStatement statement = (CtStatement) parent;

				if (statement.getLabel() != null && statement.getLabel().equals(this.getTargetLabel())) {
					return statement;
				}
			}
		}
		return null;
	}


	@Override
	public CtBreak clone() {
		return (CtBreak) super.clone();
	}
}
