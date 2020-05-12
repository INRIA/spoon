/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import spoon.SpoonException;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtTry;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.jdt.ContextBuilder.CastInfo;
import spoon.support.reflect.CtExtendedModifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		final int[] lineSeparatorPositions = this.jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions();
		return this.jdtTreeBuilder.getFactory().Core().createSourcePosition(cu, sourceStart, sourceEnd, lineSeparatorPositions);
	}

	/** creates a position for a given element with the information of ASTNode */
	SourcePosition buildPositionCtElement(CtElement e, ASTNode node) {
		if (e instanceof CtCatch) {
			//we cannot compute position of CtCatch, because we do not know position of its body yet
			//it is computed later by #buildPosition(CtCatch)
			return SourcePosition.NOPOSITION;
		}
		CoreFactory cf = this.jdtTreeBuilder.getFactory().Core();
		CompilationUnit cu = this.jdtTreeBuilder.getContextBuilder().compilationUnitSpoon;
		int[] lineSeparatorPositions = jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions();
		char[] contents = jdtTreeBuilder.getContextBuilder().getCompilationUnitContents();

		int sourceStart = node.sourceStart;
		int sourceEnd = node.sourceEnd;
		if ((node instanceof Annotation)) {
			Annotation ann = (Annotation) node;
			int declEnd = ann.declarationSourceEnd;

			if (declEnd > 0) {
				sourceEnd = declEnd;
			}
		} else if ((node instanceof Expression)) {
			Expression expression = (Expression) node;
			int statementEnd = expression.statementEnd;

			if (statementEnd > 0) {
				sourceEnd = statementEnd;
			}

			if (this.jdtTreeBuilder.getContextBuilder().isBuildTypeCast && e instanceof CtTypeReference) {
				//the type cast reference must be enclosed with brackets
				int declarationSourceStart = sourceStart;
				int declarationSourceEnd = sourceEnd;
				declarationSourceStart = findPrevNonWhitespace(contents, getParentsSourceStart(), declarationSourceStart - 1);
				if (contents[declarationSourceStart] != '(') {
					return handlePositionProblem("Unexpected character \'" + contents[declarationSourceStart] + "\' at start of cast expression on offset: " + declarationSourceStart);
				}
				declarationSourceEnd = findNextNonWhitespace(contents, contents.length - 1, declarationSourceEnd + 1);
				if (contents[declarationSourceEnd] != ')') {
					return handlePositionProblem("Unexpected character \'" + contents[declarationSourceStart] + "\' at end of cast expression on offset: " + declarationSourceEnd);
				}
				return cf.createCompoundSourcePosition(cu,
						sourceStart, sourceEnd,
						declarationSourceStart, declarationSourceEnd,
						lineSeparatorPositions);
			}

			List<CastInfo> casts = this.jdtTreeBuilder.getContextBuilder().casts;

			if (!casts.isEmpty() && e instanceof CtExpression) {
				int declarationSourceStart = sourceStart;
				int declarationSourceEnd = sourceEnd;
				SourcePosition pos = casts.get(0).typeRef.getPosition();
				if (pos.isValidPosition()) {
					declarationSourceStart = pos.getSourceStart();
					int nrOfBrackets = getNrOfFirstCastExpressionBrackets();
					while (nrOfBrackets > 0) {
						declarationSourceStart = findPrevNonWhitespace(contents, getParentsSourceStart(), declarationSourceStart - 1);
						if (declarationSourceStart < 0) {
							return handlePositionProblem("Cannot found beginning of cast expression until offset: " + getParentsSourceStart());
						}
						if (contents[declarationSourceStart] != '(') {
							return handlePositionProblem("Unexpected character \'" + contents[declarationSourceStart] + "\' at start of expression on offset: " + declarationSourceStart);
						}
						nrOfBrackets--;
					}
					nrOfBrackets = getNrOfCastExpressionBrackets();
					while (nrOfBrackets > 0) {
						declarationSourceEnd = findNextNonWhitespace(contents, contents.length - 1, declarationSourceEnd + 1);
						if (contents[declarationSourceEnd] != ')') {
							return handlePositionProblem("Unexpected character \'" + contents[declarationSourceStart] + "\' at end of expression on offset: " + declarationSourceEnd);
						}
						nrOfBrackets--;
					}
				}
				return cf.createCompoundSourcePosition(cu,
						sourceStart, sourceEnd,
						declarationSourceStart, declarationSourceEnd,
						lineSeparatorPositions);
			}
		}

		if (node instanceof TypeParameter) {
			TypeParameter typeParameter = (TypeParameter) node;
			sourceStart = typeParameter.declarationSourceStart;
			sourceEnd = typeParameter.declarationSourceEnd;
			if (typeParameter.type != null) {
				sourceEnd = getSourceEndOfTypeReference(contents, typeParameter.type, sourceEnd);
			}
		} else if (node instanceof AbstractVariableDeclaration) {
			AbstractVariableDeclaration variableDeclaration = (AbstractVariableDeclaration) node;
			int modifiersSourceStart = variableDeclaration.modifiersSourceStart;
			int declarationSourceStart = variableDeclaration.declarationSourceStart;
			int declarationSourceEnd = variableDeclaration.declarationSourceEnd;
			if (declarationSourceStart == 0 && declarationSourceEnd == 0) {
				return SourcePosition.NOPOSITION;
			}
			if (e instanceof CtCatchVariable) {
				/* compiler delivers wrong declarationSourceStart in case like: */
				//... catch/*2*/ ( /*3*/ final @Deprecated /*4*/ ClassCastException /*5*/ e /*6*/) /*7*/ {
				/*
				 * the declarationSourceStart should be after the '(', but sometime it is before
				 * So we have to compute correct offset here
				 */
				CtTry tryStatement = this.jdtTreeBuilder.getContextBuilder().getParentElementOfType(CtTry.class);
				int endOfTry = tryStatement.getPosition().getSourceEnd();
				//offset of the bracket before catch
				int lastBracket = getEndOfLastTryBlock(tryStatement, 0);
				int catchStart = findNextNonWhitespace(contents, endOfTry, lastBracket + 1);
				if (CATCH.equals(new String(contents, catchStart, CATCH.length())) == false) {
					return handlePositionProblem("Unexpected beginning of catch statement on offset: " + catchStart);
				}
				int bracketStart = findNextNonWhitespace(contents, endOfTry, catchStart + CATCH.length());
				if (bracketStart < 0) {
					return handlePositionProblem("Unexpected end of file instead of \'(\' after catch statement on offset: " + catchStart);
				}
				if (contents[bracketStart] != '(') {
					return handlePositionProblem("Unexpected character " + contents[bracketStart] + " instead of \'(\' after catch statement on offset: " + bracketStart);
				}
				declarationSourceStart = bracketStart + 1;
			}
			CtElement parent = this.jdtTreeBuilder.getContextBuilder().getContextElementOnLevel(1);
			if (parent instanceof CtForEach) {
				CtForEach forEach = (CtForEach) parent;
				//compiler deliver wrong local variable position when for(...:...) starts with line comment
				int parentStart = parent.getPosition().getSourceStart();
				if (contents[parentStart] != 'f' || contents[parentStart + 1] != 'o' || contents[parentStart + 2] != 'r') {
					return handlePositionProblem("Expected keyword for at offset: " + parentStart);
				}
				int bracketOff = findNextNonWhitespace(contents, forEach.getPosition().getSourceEnd(), parentStart + 3);
				if (bracketOff < 0 || contents[bracketOff] != '(') {
					return handlePositionProblem("Expected character after \'for\' instead of \'(\' at offset: " + (parentStart + 3));
				}
				declarationSourceStart = bracketOff + 1;
				declarationSourceEnd = sourceEnd;
			}

			if (variableDeclaration instanceof Argument && variableDeclaration.type instanceof ArrayTypeReference) {
				//handle type declarations like `String[] arg` `String arg[]` and `String []arg[]`
				ArrayTypeReference arrTypeRef = (ArrayTypeReference) variableDeclaration.type;
				int dimensions = arrTypeRef.dimensions();
				if (dimensions > 0) {
					//count number of brackets between type and variable name
					int foundDimensions = getNrOfDimensions(contents, declarationSourceStart, declarationSourceEnd);
					while (dimensions > foundDimensions) {
						//some brackets are after the variable name
						declarationSourceEnd = findNextChar(contents, contents.length, declarationSourceEnd + 1, ']');
						if (declarationSourceEnd < 0) {
							return handlePositionProblem("Unexpected array type declaration on offset: " + declarationSourceStart);
						}
						foundDimensions++;
					}
				}
			}

			// Handle lambda parameters without explicit type
			if (variableDeclaration instanceof Argument && variableDeclaration.type == null) {
				declarationSourceStart = findPrevNonWhitespace(contents, 0, declarationSourceStart);
				declarationSourceEnd = findNextNonWhitespace(contents, contents.length - 1, declarationSourceEnd);
			}

			if (modifiersSourceStart <= 0) {
				modifiersSourceStart = findNextNonWhitespace(contents, contents.length - 1, declarationSourceStart);
			}
			int modifiersSourceEnd;
			if (variableDeclaration.type != null) {
				modifiersSourceEnd = findPrevNonWhitespace(contents, declarationSourceStart, variableDeclaration.type.sourceStart() - 1);
			} else if (variableDeclaration instanceof Initializer) {
				modifiersSourceEnd = ((Initializer) variableDeclaration).block.sourceStart - 1;
			} else {
				// variable that has no type such as TypeParameter
				modifiersSourceEnd = declarationSourceStart - 1;
			}

			// when no modifier
			if (modifiersSourceStart > modifiersSourceEnd) {
				modifiersSourceEnd = modifiersSourceStart - 1;
			}  else if (e instanceof CtModifiable) {
				setModifiersPosition((CtModifiable) e, modifiersSourceStart, modifiersSourceEnd);
			}

			return cf.createDeclarationSourcePosition(cu,
					sourceStart, sourceEnd,
					modifiersSourceStart, modifiersSourceEnd,
					declarationSourceStart, declarationSourceEnd,
					lineSeparatorPositions);
		} else if (node instanceof TypeDeclaration && e instanceof CtPackage) {
			// the position returned by JTD is equals to 0
			return cf.createSourcePosition(cu, 0, contents.length - 1, lineSeparatorPositions);
		} else if (node instanceof TypeDeclaration) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) node;

			int declarationSourceStart = typeDeclaration.declarationSourceStart;
			int declarationSourceEnd = typeDeclaration.declarationSourceEnd;
			int modifiersSourceStart = typeDeclaration.modifiersSourceStart;
			int bodyStart = typeDeclaration.bodyStart;
			int bodyEnd = typeDeclaration.bodyEnd;

			int modifiersSourceEnd;
			if (typeDeclaration.name.length == 0) {
				//it is anonymous type
				if (contents[bodyStart] != '{') {
					//adjust bodyStart of annonymous type in definition of enum value
					if (bodyStart < 1 || contents[bodyStart - 1] != '{') {
						throw new SpoonException("Cannot found body start at offset " + bodyStart + " of annonymous class with sources:\n" + new String(contents));
					}
					bodyStart--;
				}
				declarationSourceStart = modifiersSourceStart = sourceStart = bodyStart;
				if (contents[bodyEnd] != '}') {
					//adjust bodyEnd of annonymous type in definition of enum value
					if (contents[bodyEnd + 1] != '}') {
						throw new SpoonException("Cannot found body end at offset " + bodyEnd + " of annonymous class with sources:\n" + new String(contents));
					}
					bodyEnd++;
				}
				declarationSourceEnd = bodyEnd;
				//there is no name of annonymous class
				sourceEnd = sourceStart - 1;
				//there are no modifiers of annonymous class
				modifiersSourceEnd = modifiersSourceStart - 1;
				bodyStart++;
			} else {
				if (modifiersSourceStart <= 0) {
					modifiersSourceStart = declarationSourceStart;
				}
				//look for start of first keyword before the type keyword e.g. "class". `sourceStart` points at first char of type name
				modifiersSourceEnd = findPrevNonWhitespace(contents, modifiersSourceStart - 1,
											findPrevWhitespace(contents, modifiersSourceStart - 1,
												findPrevNonWhitespace(contents, modifiersSourceStart - 1, sourceStart - 1)));
				if (e instanceof CtModifiable) {
					setModifiersPosition((CtModifiable) e, modifiersSourceStart, modifiersSourceEnd);
				}
				if (modifiersSourceEnd < modifiersSourceStart) {
					//there is no modifier
					modifiersSourceEnd = modifiersSourceStart - 1;
				}
			}

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

			if (modifiersSourceStart <= 0) {
				modifiersSourceStart = declarationSourceStart;
			}

			if (node instanceof AnnotationMethodDeclaration && bodyStart == bodyEnd) {
				//The ";" at the end of annotation method declaration is not part of body
				//let it behave same like in abstract MethodDeclaration
				bodyEnd--;
			}

			Javadoc javadoc = methodDeclaration.javadoc;
			if (javadoc != null && javadoc.sourceEnd() > declarationSourceStart) {
				modifiersSourceStart = javadoc.sourceEnd() + 1;
			}

			int modifiersSourceEnd = sourceStart - 1;

			if (e instanceof CtModifiable) {
				setModifiersPosition((CtModifiable) e, modifiersSourceStart, declarationSourceEnd);
			}

			if (methodDeclaration instanceof MethodDeclaration && ((MethodDeclaration) methodDeclaration).returnType != null) {
				modifiersSourceEnd = ((MethodDeclaration) methodDeclaration).returnType.sourceStart() - 2;
			}

			TypeParameter[] typeParameters = methodDeclaration.typeParameters();
			if (typeParameters != null && typeParameters.length > 0) {
				modifiersSourceEnd = typeParameters[0].declarationSourceStart - 3;
			}

			if (getModifiers(methodDeclaration.modifiers, false, true).isEmpty()) {
				modifiersSourceEnd = modifiersSourceStart - 1;
			}


			sourceEnd = sourceStart + methodDeclaration.selector.length - 1;
			if (bodyStart == 0) {
				return cf.createPartialSourcePosition(cu);
			}
			if (e instanceof CtStatementList) {
				return cf.createSourcePosition(cu, bodyStart - 1, bodyEnd + 1, lineSeparatorPositions);
			} else {
				//include brackets if they are there
				if (contents[bodyStart - 1] == '{') {
					bodyStart--;
					if (contents[bodyEnd + 1] == '}') {
						bodyEnd++;
					} else {
						if (bodyStart < bodyEnd) {
							return handlePositionProblem("Missing body end in\n" + new String(contents, sourceStart, sourceEnd - sourceStart));
						}
					}
				}
				return cf.createBodyHolderSourcePosition(cu,
						sourceStart, sourceEnd,
						modifiersSourceStart, modifiersSourceEnd,
						declarationSourceStart, declarationSourceEnd,
						bodyStart, bodyEnd,
						lineSeparatorPositions);
			}
		} else if (e instanceof CtCatchVariable) {
			ASTPair pair = this.jdtTreeBuilder.getContextBuilder().getParentContextOfType(CtCatch.class);
			if (pair == null) {
				return handlePositionProblem("There is no CtCatch parent for CtCatchVariable");
			}
			//build position with appropriate context
			return buildPositionCtElement(e, (Argument) pair.node);
		} else if (node instanceof TypeReference) {
			TypeReference typeReference = (TypeReference) node;
			if (typeReference.resolvedType.getTypeAnnotations() != null) {
				for (int a = 0; a < typeReference.resolvedType.getTypeAnnotations().length; a++) {
					sourceStart = findPrevAnnotations(contents, 0, sourceStart);
				}
			}
			sourceEnd = getSourceEndOfTypeReference(contents, (TypeReference) node, sourceEnd);
		} else if (node instanceof AllocationExpression) {
			AllocationExpression allocationExpression = (AllocationExpression) node;
			if (allocationExpression.enumConstant != null) {
				FieldDeclaration fieldDeclaration = allocationExpression.enumConstant;
				//1) skip comments
				sourceStart = findNextNonWhitespace(contents, sourceEnd, sourceStart);
				//2) move to beginning of enum construction
				sourceStart += fieldDeclaration.name.length;
			}
		} else if (node instanceof CaseStatement) {
			sourceEnd = findNextNonWhitespace(contents, contents.length - 1, sourceEnd + 1);
			if (sourceEnd < 0) {
				return handlePositionProblem("Unexpected end of file in CtCase on: " + sourceStart);
			}
			if (contents[sourceEnd] != ':') {
				if (contents[sourceEnd] == '-' && contents.length > sourceEnd + 1 && contents[sourceEnd + 1] == '>') {
					sourceEnd++;
				} else {
					return handlePositionProblem("Unexpected character " + contents[sourceEnd] + " instead of \':\' or \'->\' in CtCase on: " + sourceEnd);
				}
			}
		} else if ((node instanceof AssertStatement)) {
			AssertStatement assert_ = (AssertStatement) node;
			sourceEnd = findNextChar(contents, contents.length, sourceEnd, ';');
		}

		if (e instanceof CtModifiable) {
			setModifiersPosition((CtModifiable) e, sourceStart, sourceEnd);
		}
		if (sourceStart == 0 && sourceEnd == 0) {
			return SourcePosition.NOPOSITION;
		}
		return cf.createSourcePosition(cu, sourceStart, sourceEnd, lineSeparatorPositions);
	}

	private int getParentsSourceStart() {
		Iterator<ASTPair> iter = this.jdtTreeBuilder.getContextBuilder().stack.iterator();
		if (iter.hasNext()) {
			iter.next();
			if (iter.hasNext()) {
				ASTPair pair = iter.next();
				SourcePosition pos = pair.element.getPosition();
				if (pos.isValidPosition()) {
					return pos.getSourceStart();
				}
			}
		}
		return 0;
	}

	private int getNrOfDimensions(char[] contents, int start, int end) {
		int nrDims = 0;
		while ((start = findNextNonWhitespace(contents, end, start)) >= 0) {
			if (contents[start] == ']') {
				nrDims++;
			}
			if (contents[start] == '.' && start + 2 <= end && contents[start + 1] == '.' && contents[start + 2] == '.') {
				//String...arg is same like String[] arg, so it is counted as dimension too
				start = start + 2;
				nrDims++;
			}
			start++;
		}
		return nrDims;
	}

	private static final String CATCH = "catch";

	SourcePosition buildPosition(CtCatch catcher) {
		int[] lineSeparatorPositions = jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions();

		CtTry tryElement = catcher.getParent(CtTry.class);
		//offset after last bracket before catch
		int declarationStart = getEndOfLastTryBlock(tryElement, 1) + 1;
		DeclarationSourcePosition paramPos = (DeclarationSourcePosition) catcher.getParameter().getPosition();
		int bodyStart = catcher.getBody().getPosition().getSourceStart();
		int bodyEnd = catcher.getBody().getPosition().getSourceEnd();
		return catcher.getFactory().Core().createBodyHolderSourcePosition(
				tryElement.getPosition().getCompilationUnit(),
				//on the place of name there is catch variable
				paramPos.getSourceStart(), paramPos.getSourceEnd(),
				//catch has no modifiers, They are in catch variable
				declarationStart, declarationStart - 1,
				declarationStart, bodyEnd,
				bodyStart, bodyEnd,
				lineSeparatorPositions);
	}

	SourcePosition buildPosition(CtCase<?> child) {
		List<CtStatement> statements = child.getStatements();
		SourcePosition oldPosition = child.getPosition();
		if (statements.isEmpty()) {
			//There are no statements. Keep origin position
			return oldPosition;
		}
		int[] lineSeparatorPositions = this.jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions();

		int bodyStart = child.getPosition().getSourceEnd() + 1;
		int bodyEnd = statements.get(statements.size() - 1).getPosition().getSourceEnd();
		return child.getFactory().Core().createBodyHolderSourcePosition(
				oldPosition.getCompilationUnit(),
				oldPosition.getSourceStart(), oldPosition.getSourceEnd(),
				oldPosition.getSourceStart(), oldPosition.getSourceStart() - 1,
				oldPosition.getSourceStart(), bodyEnd,
				bodyStart, bodyEnd,
				lineSeparatorPositions);
	}

	/**
	 * @param tryElement
	 * @param negIdx 0 - last block, 1 - one before last block, ...
	 * @return
	 */
	private int getEndOfLastTryBlock(CtTry tryElement, int negIdx) {
		//offset where we can start to search for catch
		int endOfLastBlock = tryElement.getBody().getPosition().getSourceEnd();
		if (tryElement.getCatchers().size() > negIdx) {
			CtCatch prevCatcher = tryElement.getCatchers().get(tryElement.getCatchers().size() - 1 - negIdx);
			endOfLastBlock = prevCatcher.getPosition().getSourceEnd();
		}
		return endOfLastBlock;
	}

	private int getNrOfFirstCastExpressionBrackets() {
		return this.jdtTreeBuilder.getContextBuilder().casts.get(0).nrOfBrackets;
	}

	private int getNrOfCastExpressionBrackets() {
		int nr = 0;
		for (CastInfo castInfo : this.jdtTreeBuilder.getContextBuilder().casts) {
			nr += castInfo.nrOfBrackets;
		}
		return nr;
	}

	private void setModifiersPosition(CtModifiable e, int start, int end) {
		CoreFactory cf = this.jdtTreeBuilder.getFactory().Core();
		CompilationUnit cu = this.jdtTreeBuilder.getContextBuilder().compilationUnitSpoon;
		char[] contents = jdtTreeBuilder.getContextBuilder().getCompilationUnitContents();

		Set<CtExtendedModifier> modifiers = e.getExtendedModifiers();
		Map<String, CtExtendedModifier> explicitModifiersByName = new HashMap<>();
		for (CtExtendedModifier modifier: modifiers) {
			if (modifier.isImplicit()) {
				modifier.setPosition(cf.createPartialSourcePosition(cu));
				continue;
			}
			if (explicitModifiersByName.put(modifier.getKind().toString(), modifier) != null) {
				throw new SpoonException("The modifier " + modifier.getKind().toString() + " found twice");
			}
		}

		//move end after the last char
		end++;
		while (start < end && explicitModifiersByName.size() > 0) {
			int o1 = findNextNonWhitespace(contents, end - 1, start);
			if (o1 == -1) {
				break;
			}
			int o2 = findNextWhitespace(contents, end - 1, o1);
			if (o2 == -1) {
				o2 = end;
			}
			String modifierName = String.valueOf(contents, o1, o2 - o1);
			CtExtendedModifier modifier = explicitModifiersByName.remove(modifierName);
			if (modifier != null) {
				modifier.setPosition(cf.createSourcePosition(cu, o1, o2 - 1, jdtTreeBuilder.getContextBuilder().getCompilationUnitLineSeparatorPositions()));
			}
			start = o2;
		}
		if (explicitModifiersByName.size() > 0) {
			throw new SpoonException("Position of CtExtendedModifiers: [" + String.join(", ", explicitModifiersByName.keySet()) + "] not found in " + String.valueOf(contents, start, end - start));
		}
	}

	private int getSourceEndOfTypeReference(char[] contents, TypeReference node, int sourceEnd) {
		//e.g. SomeType<String,T>
		TypeReference[][] typeArgs = node.getTypeArguments();
		if (typeArgs != null && typeArgs.length > 0) {
			TypeReference[] trs = typeArgs[typeArgs.length - 1];
			if (trs != null && trs.length > 0) {
				TypeReference tr = trs[trs.length - 1];
				if (sourceEnd < tr.sourceEnd) {
					//the sourceEnd of reference is smaller then source of type argument of this reference
					//move sourceEnd so that type argument is included in sources
					//TODO handle comments correctly here. E.g. List<T /*ccc*/ >
					sourceEnd = findNextNonWhitespace(contents, contents.length - 1, getSourceEndOfTypeReference(contents, tr, tr.sourceEnd) + 1);
				}
			} else {
				//SomeType<>
				int startIdx = findNextNonWhitespace(contents, contents.length - 1, sourceEnd + 1);
				if (startIdx != -1 && contents[startIdx] == '<') {
					int endIdx = findNextNonWhitespace(contents, contents.length - 1, startIdx + 1);
					if (endIdx != -1 && contents[endIdx] == '>') {
						sourceEnd = endIdx;
					}
				}
			}
		}
		if (node instanceof Wildcard) {
			Wildcard wildcard = (Wildcard) node;
			if (wildcard.bound != null) {
				sourceEnd = getSourceEndOfTypeReference(contents, wildcard.bound, sourceEnd);
			}
		}
		return sourceEnd;
	}

	/**
	 * @return index of first character `expectedChar`, searching forward..
	 * Can return 'off' if it is `expectedChar`. returns -1 if not found
	 * Note: all kinds of java comments are understood as whitespace.
	 * The search must start out of comment or on the first character of the comment
	 */
	static int findNextChar(char[] contents, int maxOff, int off, char expectedChar) {
		while ((off = findNextNonWhitespace(contents, maxOff, off)) >= 0) {
			if (contents[off] == expectedChar) {
				return off;
			}
			off++;
		}
		return -1;
	}

	/**
	 * @param maxOff maximum acceptable return value
	 * @return index of first non whitespace char, searching forward.
	 * Can return 'off' if it is non whitespace.
	 * Note: all kinds of java comments are understood as whitespace too.
	 * The search must start out of comment or on the first character of the comment
	 */
	static int findNextNonWhitespace(char[] content, int maxOff, int off) {
		return findNextNonWhitespace(true, content, maxOff, off);
	}
	static int findNextNonWhitespace(boolean commentIsWhiteSpace, char[] content, int maxOff, int off) {
		maxOff = Math.min(maxOff, content.length - 1);
		while (off >= 0 && off <= maxOff) {
			char c = content[off];
			if (Character.isWhitespace(c) == false) {
				//non whitespace found
				int endOfCommentOff = commentIsWhiteSpace ? getEndOfComment(content, maxOff, off) : -1;
				if (endOfCommentOff == -1) {
					//it is not a comment. Finish
					return off;
				}
				//it is a comment move to the end of comment and continue
				off = endOfCommentOff;
			}
			off++;
		}
		return -1;
	}

	/**
	 * @param maxOff maximum acceptable return value
	 * @return index of first whitespace char, searching forward. Return -1 if there is no white space.
	 * Can return `off` if it is already a non whitespace.
	 * Note: all kinds of java comments are understood as whitespace too. Then it returns offset of the first character of the comment
	 */
	static int findNextWhitespace(char[] content, int maxOff, int off) {
		maxOff = Math.min(maxOff, content.length - 1);
		while (off >= 0 && off <= maxOff) {
			char c = content[off];
			if (Character.isWhitespace(c) || getEndOfComment(content, maxOff, off) >= 0) {
				//it is whitespace or comment starts there
				return off;
			}
			off++;
		}
		return -1;
	}
	/**
	 * @param minOff the minimal acceptable return value
	 * @return index of first non whitespace char, searching backward. Can return `off` if it is already a non whitespace.
	 * Note: all kinds of java comments are understood as whitespace too. Then it returns offset of the first non whitespace character before the comment
	 */
	static int findPrevNonWhitespace(char[] content, int minOff, int off) {
		minOff = Math.max(0, minOff);
		while (off >= minOff) {
			char c = content[off];
			//first check a comment and then whitesapce
			//because line comment "// ...  \n" ends with EOL, which would be eat by isWhitespace and the comment detection would fail then
			int startOfCommentOff = getStartOfComment(content, minOff, off);
			if (startOfCommentOff >= 0) {
				off = startOfCommentOff;
			} else if (Character.isWhitespace(c) == false) {
				//non whitespace found.
				return off;
			}
			off--;
		}
		return -1;
	}

	static int findPrevAnnotations(char[] content, int minOff, int off) {
		minOff = Math.max(0, minOff);
		while (off >= minOff) {
			char c = content[off];
			//first check a comment and then whitesapce
			//because line comment "// ...  \n" ends with EOL, which would be eat by isWhitespace and the comment detection would fail then
			int startOfCommentOff = getStartOfComment(content, minOff, off);
			if (startOfCommentOff >= 0) {
				off = startOfCommentOff;
			} else if (c == '@') {
				//non whitespace found.
				return off;
			}
			off--;
		}
		return -1;
	}

	/**
	 * @param minOff the minimal acceptable return value
	 * @return index of first whitespace char, searching backward. Can return off if it is whitespace.
	 * Note: all kinds of java comments are understood as whitespace too. Then it returns offset of the last comment character.
	 * in case of line comment it returns last character of EOL which ends the comment
	 */
	static int findPrevWhitespace(char[] content, int minOff, int off) {
		minOff = Math.max(0, minOff);
		while (off >= minOff) {
			char c = content[off];
			if (Character.isWhitespace(c) || getStartOfComment(content, minOff, off) >= 0) {
				return off;
			}
			off--;
		}
		return -1;
	}
	/**
	 * @param maxOff maximum acceptable return value
	 * @return if the off points at start of comment then it returns offset which points on last character of the comment
	 * if the off does not point at start of comment then it returns -1
	 */
	static int getEndOfComment(char[] content, int maxOff, int off) {
		maxOff = Math.min(maxOff, content.length - 1);
		if (off + 1 <= maxOff) {
			if (content[off] == '/' && content[off + 1] == '*') {
				// +3, because we are searching for first possible '/' and not for '*'
				//this is shortest comment: /**/
				off = off + 3;
				while (off <= maxOff) {
					if (content[off] == '/' && content[off - 1] == '*') {
						//we have found end of this comment
						return off;
					}
					off++;
				}
				//the content ended. Comment ends with end of file too
				return off;
			} else if (content[off] == '/' && content[off + 1] == '/') {
				while (off <= maxOff) {
					/*
					 * Handle all 3 kinds of EOLs
					 * \r\n
					 * \r
					 * \n
					 */
					if (content[off] == '\n') {
						return off;
					}
					if (content[off] == '\r') {
						//we have found end of this comment
						//skip windows \n too if any
						if (off < maxOff && content[off + 1] == '\n') {
							off++;
						}
						return off;
					}
					off++;
				}
			}
		}
		return -1;
	}

	/**
	 * @param minOff minimum offset where it should search for start of comment
	 * @return if the off points at end of comment then it returns offset which points on first character of the comment
	 * if the off does not point at the end of comment then it returns -1
	 */
	static int getStartOfComment(char[] content, int minOff, int off) {
		if (off < 2) {
			//there cannot start comment
			return -1;
		}
		if ((content[off] == '/' && content[off - 1] == '*')
				|| content[off] == '\n'
				|| content[off] == '\r') {
			//it is probably end of some comment. Not that it is not enough to search for /* recursivelly
			//because there may be something like: comment starts here: /* /* /* and not here: /*  */
			//or something like this
//			/*// */ this code is not in comment EOL
			//so search for comment from beginning of `minOff`
			int maxOff = off;
			off = minOff;
			while (off <= maxOff) {
				int endOfComment = getEndOfComment(content, maxOff, off);
				if (endOfComment >= 0) {
					//it detected a comment
					if (endOfComment == maxOff) {
						//off points to start of comment which ends on maxOff. We found it
						return off;
					}
					//else we have found some previous comment
					//jump over it and continue searching
					off = endOfComment;
				}
				off++;
			}
		}
		return -1;
	}

	private SourcePosition handlePositionProblem(String errorMessage) {
		if (jdtTreeBuilder.getFactory().getEnvironment().checksAreSkipped()) {
			jdtTreeBuilder.getFactory().getEnvironment().debugMessage("Source position detection failed: " + errorMessage);
			return SourcePosition.NOPOSITION;
		}
		throw new SpoonException("Source position detection failed: " + errorMessage);
	}

}
