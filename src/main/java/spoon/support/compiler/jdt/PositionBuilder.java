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
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.CoreFactory;

import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.getModifiers;

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
		return this.jdtTreeBuilder.getFactory().Core().createSourcePosition(cu, sourceStart, sourceEnd, lineSeparatorPositions);
	}

	SourcePosition buildPositionCtElement(CtElement e, ASTNode node) {
		CoreFactory cf = this.jdtTreeBuilder.getFactory().Core();
		CompilationUnit cu = this.jdtTreeBuilder.getFactory().CompilationUnit().create(new String(this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.getFileName()));
		int[] lineSeparatorPositions = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.compilationResult.lineSeparatorPositions;

		int sourceStart = node.sourceStart;
		int sourceEnd = node.sourceEnd;
		if ((node instanceof Expression)) {
			if (((Expression) node).statementEnd > 0) {
				sourceEnd = ((Expression) node).statementEnd;
			}
		}

		if (node instanceof AbstractVariableDeclaration) {
			AbstractVariableDeclaration variableDeclaration = (AbstractVariableDeclaration) node;
			int modifiersSourceStart = variableDeclaration.modifiersSourceStart;
			int declarationSourceStart = variableDeclaration.declarationSourceStart;
			int declarationSourceEnd = variableDeclaration.declarationSourceEnd;
			int declarationEnd = variableDeclaration.declarationEnd;

			Annotation[] annotations = variableDeclaration.annotations;
			if (annotations != null && annotations.length > 0) {
				if (annotations[0].sourceStart() == sourceStart) {
					modifiersSourceStart = annotations[annotations.length - 1].sourceEnd() + 2;
				}
			}
			if (modifiersSourceStart == 0) {
				modifiersSourceStart = declarationSourceStart;
			}
			int modifiersSourceEnd;
			if (variableDeclaration.type != null) {
				modifiersSourceEnd = variableDeclaration.type.sourceStart() - 2;
			} else {
				// variable that has no type such as TypeParameter
				modifiersSourceEnd = declarationSourceStart - 1;
			}

			// when no modifier
			if (modifiersSourceStart > modifiersSourceEnd) {
				modifiersSourceEnd = modifiersSourceStart - 1;
			}

			return cf.createDeclarationSourcePosition(cu,
					sourceStart, sourceEnd,
					modifiersSourceStart, modifiersSourceEnd,
					declarationSourceStart, declarationSourceEnd,
					lineSeparatorPositions);
		} else if (node instanceof TypeDeclaration) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) node;

			int declarationSourceStart = typeDeclaration.declarationSourceStart;
			int declarationSourceEnd = typeDeclaration.declarationSourceEnd;
			int modifiersSourceStart = typeDeclaration.modifiersSourceStart;
			int bodyStart = typeDeclaration.bodyStart;
			int bodyEnd = typeDeclaration.bodyEnd;

			Annotation[] annotations = typeDeclaration.annotations;
			if (annotations != null && annotations.length > 0) {
				if (annotations[0].sourceStart() == declarationSourceStart) {
					modifiersSourceStart = annotations[annotations.length - 1].sourceEnd() + 2;
				}
			}
			if (modifiersSourceStart == 0) {
				modifiersSourceStart = declarationSourceStart;
			}
			// the position the name minus the size of "class" minus at least 2 spaces
			int modifiersSourceEnd = sourceStart - 8;

			return cf.createBodyHolderSourcePosition(cu, sourceStart, sourceEnd,
					modifiersSourceStart, modifiersSourceEnd,
					declarationSourceStart, declarationSourceEnd,
					bodyStart - 1, bodyEnd,
					lineSeparatorPositions);
		} else if (node instanceof AbstractMethodDeclaration) {
			AbstractMethodDeclaration methodDeclaration = (AbstractMethodDeclaration) node;
			int bodyStart = methodDeclaration.bodyStart;
			int bodyEnd = methodDeclaration.bodyEnd;
			int declarationSourceStart = methodDeclaration.declarationSourceStart;
			int declarationSourceEnd = methodDeclaration.declarationSourceEnd;
			int modifiersSourceStart = methodDeclaration.modifiersSourceStart;

			if (modifiersSourceStart == 0) {
				modifiersSourceStart = declarationSourceStart;
			}
			Javadoc javadoc = methodDeclaration.javadoc;
			if (javadoc != null && javadoc.sourceEnd() > declarationSourceStart) {
				modifiersSourceStart = javadoc.sourceEnd() + 1;
			}
			Annotation[] annotations = methodDeclaration.annotations;
			if (annotations != null && annotations.length > 0) {
				if (annotations[0].sourceStart() == declarationSourceStart) {
					modifiersSourceStart = annotations[annotations.length - 1].sourceEnd() + 2;
				}
			}

			int modifiersSourceEnd = sourceStart - 1;

			if (methodDeclaration instanceof MethodDeclaration && ((MethodDeclaration) methodDeclaration).returnType != null) {
				modifiersSourceEnd = ((MethodDeclaration) methodDeclaration).returnType.sourceStart() - 2;
			}

			TypeParameter[] typeParameters = methodDeclaration.typeParameters();
			if (typeParameters != null && typeParameters.length > 0) {
				modifiersSourceEnd = typeParameters[0].declarationSourceStart - 3;
			}

			if (getModifiers(methodDeclaration.modifiers).isEmpty()) {
				modifiersSourceStart = modifiersSourceEnd + 1;
			}


			sourceEnd = sourceStart + methodDeclaration.selector.length - 1;

			if (e instanceof CtStatementList) {
				return cf.createSourcePosition(cu, bodyStart - 1, bodyEnd + 1, lineSeparatorPositions);
			} else {
				if (bodyStart == 0) {
					return SourcePosition.NOPOSITION;
				} else {
					return cf.createBodyHolderSourcePosition(cu,
							sourceStart, sourceEnd,
							modifiersSourceStart, modifiersSourceEnd,
							declarationSourceStart, declarationSourceEnd,
							bodyStart - 1, bodyEnd + 1,
							lineSeparatorPositions);
				}
			}
		}
		if ((node instanceof Expression)) {
			Expression expression = (Expression) node;
			int statementEnd = expression.statementEnd;

			if (statementEnd > 0) {
				sourceEnd = statementEnd;
			}
		}

		return cf.createSourcePosition(cu, sourceStart, sourceEnd, lineSeparatorPositions);
	}
}
