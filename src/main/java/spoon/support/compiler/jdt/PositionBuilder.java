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

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import spoon.SpoonException;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.CoreFactory;
import spoon.support.reflect.CtExtendedModifier;

import java.util.Iterator;
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
		final int[] lineSeparatorPositions = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.compilationResult.lineSeparatorPositions;
		return this.jdtTreeBuilder.getFactory().Core().createSourcePosition(cu, sourceStart, sourceEnd, lineSeparatorPositions);
	}

	SourcePosition buildPositionCtElement(CtElement e, ASTNode node) {
		CoreFactory cf = this.jdtTreeBuilder.getFactory().Core();
		CompilationUnit cu = this.jdtTreeBuilder.getFactory().CompilationUnit().getOrCreate(new String(this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.getFileName()));
		CompilationResult cr = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.compilationResult;
		int[] lineSeparatorPositions = cr.lineSeparatorPositions;
		char[] contents = cr.compilationUnit.getContents();


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

			if (modifiersSourceStart <= 0) {
				modifiersSourceStart = declarationSourceStart;
			}
			int modifiersSourceEnd;
			if (variableDeclaration.type != null) {
				modifiersSourceEnd = variableDeclaration.type.sourceStart() - 2;
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

			if (modifiersSourceStart <= 0) {
				modifiersSourceStart = declarationSourceStart;
			}
			//look for start of first keyword before the type keyword e.g. "class". `sourceStart` points at first char of type name
			int modifiersSourceEnd = findPrevNonWhitespace(contents, modifiersSourceStart - 1,
										findPrevWhitespace(contents, modifiersSourceStart - 1,
											findPrevNonWhitespace(contents, modifiersSourceStart - 1, sourceStart - 1)));
			if (e instanceof CtModifiable) {
				setModifiersPosition((CtModifiable) e, modifiersSourceStart, bodyStart);
			}
			if (modifiersSourceEnd < modifiersSourceStart) {
				//there is no modifier
				modifiersSourceEnd = modifiersSourceStart - 1;
			}
			if (typeDeclaration.name.length == 0) {
				//it is anonymous type, there is no name start/end
				sourceEnd = sourceStart - 1;
				if (contents[sourceStart] == '{') {
					//adjust bodyEnd of annonymous type in definition of enum value
					bodyEnd++;
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
				if (bodyStart < bodyEnd) {
					//include brackets if they are there
					if (contents[bodyStart - 1] == '{') {
						bodyStart--;
						if (contents[bodyEnd + 1] == '}') {
							bodyEnd++;
						} else {
							throw new SpoonException("Missing body end in\n" + new String(contents, sourceStart, sourceEnd - sourceStart));
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
			Iterator<ASTPair> iterator = this.jdtTreeBuilder.getContextBuilder().stack.iterator();
			ASTPair catchNode = iterator.next();
			while (!(catchNode.node instanceof Argument)) {
				catchNode = iterator.next();
			}
			DeclarationSourcePosition argumentPosition = (DeclarationSourcePosition) buildPositionCtElement(e, catchNode.node);

			int variableNameStart = findNextNonWhitespace(contents, argumentPosition.getSourceEnd(), sourceEnd + 1);
			int variableNameEnd = argumentPosition.getSourceEnd();

			int modifierStart = sourceStart;
			int modifierEnd = sourceStart - 1;
			if (!getModifiers(((Argument) catchNode.node).modifiers, false, false).isEmpty()) {
				modifierStart = argumentPosition.getModifierSourceStart();
				modifierEnd = argumentPosition.getModifierSourceEnd();

				sourceStart = modifierStart;
			}
			sourceEnd = argumentPosition.getSourceEnd();
			return cf.createDeclarationSourcePosition(cu,
					variableNameStart, variableNameEnd,
					modifierStart, modifierEnd,
					sourceStart, sourceEnd,
					lineSeparatorPositions);
		} else if (node instanceof TypeReference) {
			sourceEnd = getSourceEndOfTypeReference(contents, (TypeReference) node, sourceEnd);
		}

		if (e instanceof CtModifiable) {
			setModifiersPosition((CtModifiable) e, sourceStart, sourceEnd);
		}
		return cf.createSourcePosition(cu, sourceStart, sourceEnd, lineSeparatorPositions);
	}


	private void setModifiersPosition(CtModifiable e, int start, int end) {
		CoreFactory cf = this.jdtTreeBuilder.getFactory().Core();
		CompilationUnit cu = this.jdtTreeBuilder.getFactory().CompilationUnit().getOrCreate(new String(this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.getFileName()));
		CompilationResult cr = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.compilationResult;
		char[] contents = cr.compilationUnit.getContents();

		Set<CtExtendedModifier> modifiers = e.getExtendedModifiers();
		String modifierContent = String.valueOf(contents, start, end - start + 1);
		for (CtExtendedModifier modifier: modifiers) {
			if (modifier.isImplicit()) {
				modifier.setPosition(cf.createPartialSourcePosition(cu));
				continue;
			}
			int index = modifierContent.indexOf(modifier.getKind().toString());
			if (index == -1) {
				throw new SpoonException("Explicit modifier not found");
			}
			int indexStart = index + start;
			int indexEnd = indexStart + modifier.getKind().toString().length() - 1;

			modifier.setPosition(cf.createSourcePosition(cu, indexStart, indexEnd, cr.lineSeparatorPositions));
		}
	}

	private int getSourceEndOfTypeReference(char[] contents, TypeReference node, int sourceEnd) {
		//e.g. SomeType<String,T>
		TypeReference[][] typeArgs = ((TypeReference) node).getTypeArguments();
		if (typeArgs != null && typeArgs.length > 0) {
			TypeReference[] trs = typeArgs[typeArgs.length - 1];
			if (trs != null && trs.length > 0) {
				TypeReference tr = trs[trs.length - 1];
				if (sourceEnd < tr.sourceEnd) {
					//the sourceEnd of reference is smaller then source of type argument of this reference
					//move sourceEnd so that type argument is included in sources
					//TODO handle comments correctly here. E.g. List<T /*ccc*/ >
					sourceEnd = findNextNonWhitespace(contents, contents.length, tr.sourceEnd + 1);
				}
			}
		}
		return sourceEnd;
	}

	/**
	 * @param maxOff maximum acceptable return value
	 * @return index of first non whitespace char, searching forward.
	 * Can return 'off' if it is non whitespace.
	 * Note: all kinds of java comments are understood as whitespace too.
	 * The search must start out of comment or on the first character of the comment
	 */
	private int findNextNonWhitespace(char[] content, int maxOff, int off) {
		maxOff = Math.min(maxOff, content.length - 1);
		while (off >= 0 && off <= maxOff) {
			char c = content[off];
			if (Character.isWhitespace(c) == false) {
				//non whitespace found
				int endOfCommentOff = getEndOfComment(content, maxOff, off);
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
	private int findNextWhitespace(char[] content, int maxOff, int off) {
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
	int findPrevNonWhitespace(char[] content, int minOff, int off) {
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

	/**
	 * @param minOff the minimal acceptable return value
	 * @return index of first whitespace char, searching backward. Can return off if it is whitespace.
	 * Note: all kinds of java comments are understood as whitespace too. Then it returns offset of the last comment character.
	 * in case of line comment it returns last character of EOL which ends the comment
	 */
	private int findPrevWhitespace(char[] content, int minOff, int off) {
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
	private int getEndOfComment(char[] content, int maxOff, int off) {
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
						if (content[off] == '\n') {
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
	private int getStartOfComment(char[] content, int minOff, int off) {
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
}
