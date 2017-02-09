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

import java.util.Collection;

import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;

/**
 * This Query expects a {@link CtVariableReference}, which represents reference to an variable, as input
 * and returns all {@link CtElement} instances, which might be a declaration of that variable reference
 * <br>
 * In other words, it returns all elements,
 * which might be declaration of input variable reference.
 * <br>
 * It returns {@link CtParameter} instances from methods, lambdas and catch blocks.
 * It returns {@link CtField} instances from wrapping classes and their super classes too.
 * <br>
 * The elements are visited in defined order. First are elements from nearest parent blocks,
 * then fields of wrapping classes, then fields of super classes, etc.
 * <br>
 * It can be used to search for variable declarations of
 * variable references and for detection of variable name conflicts
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtVariableReference varRef = ...;
 * varRef.map(new PotentialVariableDeclarationFunction()).forEach(...process result...);
 * }
 * </pre>
 */
public class PotentialVariableDeclarationFunction implements CtConsumableFunction<CtElement> {

	private boolean includingFields = true;

	public PotentialVariableDeclarationFunction() {
	}

	@Override
	public void apply(CtElement input, CtConsumer<Object> outputConsumer) {
		//Search previous siblings for element which may represents the declaration of this local variable
		CtQuery siblingsQuery = input.getFactory().createQuery().map(new SiblingsFunction().mode(SiblingsFunction.Mode.PREVIOUS));

		CtElement scopeElement = input;
		//Search input and then all parents until first CtPackage for element which may represents the declaration of this local variable
		while (scopeElement != null && !(scopeElement instanceof CtPackage)) {
			CtElement parent = scopeElement.getParent();
			if (parent instanceof CtType<?>) {
				if (includingFields) {
					//TODO replace getAllFields() followed by getFieldDeclaration, by direct visiting of fields of types in super classes.
					Collection<CtFieldReference<?>> allFields = ((CtType<?>) parent).getAllFields();
					for (CtFieldReference<?> fieldReference : allFields) {
						outputConsumer.accept(fieldReference.getFieldDeclaration());
					}
				}
			} else if (parent instanceof CtBodyHolder || parent instanceof CtStatementList) {
				//visit all previous siblings of scopeElement element in parent BodyHolder or Statement list
				siblingsQuery.setInput(scopeElement).forEach(outputConsumer);
				//visit parameters of CtCatch and CtExecutable (method, lambda)
				if (parent instanceof CtCatch) {
					CtCatch ctCatch = (CtCatch) parent;
					outputConsumer.accept(ctCatch.getParameter());
				} else if (parent instanceof CtExecutable) {
					CtExecutable<?> exec = (CtExecutable<?>) parent;
					for (CtParameter<?> param : exec.getParameters()) {
						outputConsumer.accept(param);
					}
				}
			}
			scopeElement = parent;
		}
	}

	public boolean isIncludingFields() {
		return includingFields;
	}

	/**
	 * @param includingFields if true then CtFields of wrapping class and all super classes are returned too
	 */
	public PotentialVariableDeclarationFunction includingFields(boolean includingFields) {
		this.includingFields = includingFields;
		return this;
	}
}
