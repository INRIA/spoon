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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities.BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;

public class CtStatementListImpl<R> extends CtCodeElementImpl implements CtStatementList {
	private static final long serialVersionUID = 1L;

	List<CtStatement> statements = emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtStatementList(this);
	}

	@Override
	public List<CtStatement> getStatements() {
		return statements;
	}

	@Override
	public <T extends CtStatementList> T setStatements(List<CtStatement> stmts) {
		this.statements.clear();
		for (CtStatement stmt : stmts) {
			addStatement(stmt);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T addStatement(CtStatement statement) {
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			this.statements = new ArrayList<CtStatement>(BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
		statement.setParent(this);
		this.statements.add(statement);
		return (T) this;
	}

	@Override
	public void removeStatement(CtStatement statement) {
		if (this.statements != CtElementImpl.<CtStatement>emptyList()) {
			this.statements.remove(statement);
		}
	}

	@Override
	public <E extends CtElement> E setPosition(SourcePosition position) {
		for (CtStatement s : statements) {
			s.setPosition(position);
		}
		return (E) this;
	}

	@Override
	public Iterator<CtStatement> iterator() {
		return statements.iterator();
	}

	public CtStatementList getSubstitution(CtType<?> targetType) {
		return getFactory().Core().clone(this);
	}
}
