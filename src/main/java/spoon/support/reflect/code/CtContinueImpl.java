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
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.ParentFunction;

import java.util.List;

import static spoon.reflect.path.CtRole.TARGET_LABEL;

public class CtContinueImpl extends CtStatementImpl implements CtContinue {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.TARGET_LABEL)
	String targetLabel;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtContinue(this);
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
	public CtContinue clone() {
		return (CtContinue) super.clone();
	}
}
