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
package spoon.reflect.visitor.filter;

import java.util.List;

import spoon.SpoonException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;

/**
 * This Query expects a {@link CtLocalVariable} as input
 * and returns all CtElements,
 * which are in visibility scope of that local variable.
 * In other words, it returns all elements,
 * which might be reference to that local variable.
 * <br>
 * It can be used to search for variable declarations or
 * variable references which might be in name conflict with input local variable.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtLocalVariable var = ...;
 * var.map(new LocalVariableScopeFunction()).forEach(...process result...);
 * }
 * </pre>
 */
public class LocalVariableScopeFunction implements CtConsumableFunction<CtLocalVariable<?>> {

	public LocalVariableScopeFunction() {
	}

	@Override
	public void apply(CtLocalVariable<?> localVariable, CtConsumer<Object> outputConsumer) {
		CtStatementList statements = localVariable.getParent(CtStatementList.class);
		if (statements == null) {
			//cannot search for variable references of variable which has no parent statement list/block
			return;
		}
		//create query which will be evaluated on each statement after local variable declaration
		CtQuery query = localVariable.getFactory().createQuery().filterChildren(null);
		List<CtStatement> stats = statements.getStatements();
		//search for variable declaration in statements of current block
		int idxOfVar = stats.indexOf(localVariable);
		if (idxOfVar < 0) {
			throw new SpoonException("Cannot found index of local variable declaration " + localVariable + " in statement list " + statements);
		}
		//scan only all elements AFTER this variable declaration
		for (int i = idxOfVar + 1; i < stats.size(); i++) {
			query.setInput(stats.get(i)).forEach(outputConsumer);
		}
	}
}
