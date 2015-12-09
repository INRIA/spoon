/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtVisitor;

public class CtContinueImpl extends CtStatementImpl implements CtContinue {
	private static final long serialVersionUID = 1L;

	CtStatement labelledStatement;

	String targetLabel;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtContinue(this);
	}

	@Override
	public CtStatement getLabelledStatement() {
		return labelledStatement;
	}

	@Override
	public <T extends CtContinue> T setLabelledStatement(CtStatement labelledStatement) {
		if (labelledStatement != null) {
			labelledStatement.setParent(this);
		}
		this.labelledStatement = labelledStatement;
		return (T) this;
	}

	@Override
	public String getTargetLabel() {
		return targetLabel;
	}

	@Override
	public <T extends CtContinue> T setTargetLabel(String targetLabel) {
		this.targetLabel = targetLabel;
		return (T) this;
	}

}
