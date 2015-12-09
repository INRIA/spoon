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

import spoon.reflect.code.CtBreak;
import spoon.reflect.visitor.CtVisitor;

public class CtBreakImpl extends CtStatementImpl implements CtBreak {
	private static final long serialVersionUID = 1L;

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
	public <T extends CtBreak> T setTargetLabel(String targetLabel) {
		this.targetLabel = targetLabel;
		return (T) this;
	}
}
