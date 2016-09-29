/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

import spoon.reflect.code.CtStatementList;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.CoreFactory;

/**
 * Created by bdanglot on 07/07/16.
 */
public class PositionBuilder {

	private final JDTTreeBuilder jdtTreeBuilder;

	public PositionBuilder(JDTTreeBuilder jdtTreeBuilder) {
		this.jdtTreeBuilder = jdtTreeBuilder;
	}

	SourcePosition buildPosition(int sourceStart, int sourceEnd) {
		CompilationUnit cu = this.jdtTreeBuilder.getContextBuilder().compilationUnitSpoon;
		final int[] lineSeparatorPositions = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.compilationResult.lineSeparatorPositions;
		return this.jdtTreeBuilder.getFactory().Core().createSourcePosition(cu, sourceStart, sourceStart, sourceEnd, lineSeparatorPositions);
	}

	SourcePosition buildPositionCtElement(CtElement e, ASTNode node) {
		CoreFactory cf = this.jdtTreeBuilder.getFactory().Core();
		int sourceStartDeclaration = node.sourceStart;
		int sourceStartSource = node.sourceStart;
		// default value
		if (!(e instanceof CtNamedElement)) {
			sourceStartSource = sourceStartDeclaration;
		}
		int sourceEnd = node.sourceEnd;
		if ((node instanceof Expression)) {
			if (((Expression) node).statementEnd > 0) {
				sourceEnd = ((Expression) node).statementEnd;
			}
		}

		if (node instanceof AbstractVariableDeclaration) {
			sourceStartDeclaration = ((AbstractVariableDeclaration) node).declarationSourceStart;
			sourceEnd = ((AbstractVariableDeclaration) node).declarationSourceEnd;
		} else if (node instanceof TypeDeclaration) {
			sourceStartDeclaration = ((TypeDeclaration) node).declarationSourceStart;
			sourceEnd = ((TypeDeclaration) node).declarationSourceEnd;
		} else if ((e instanceof CtStatementList) && (node instanceof AbstractMethodDeclaration)) {
			sourceStartDeclaration = ((AbstractMethodDeclaration) node).bodyStart - 1;
			sourceEnd = ((AbstractMethodDeclaration) node).bodyEnd + 1;
		} else if ((node instanceof AbstractMethodDeclaration)) {
			if (((AbstractMethodDeclaration) node).bodyStart == 0) {
				sourceStartDeclaration = -1;
				sourceStartSource = -1;
				sourceEnd = -1;
			} else {
				sourceStartDeclaration = ((AbstractMethodDeclaration) node).declarationSourceStart;
				sourceEnd = ((AbstractMethodDeclaration) node).declarationSourceEnd;
			}
		}
		CompilationUnit cu = this.jdtTreeBuilder.getContextBuilder().compilationUnitSpoon;
		return cf.createSourcePosition(cu, sourceStartDeclaration, sourceStartSource, sourceEnd, this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.compilationResult.lineSeparatorPositions);
	}
}
