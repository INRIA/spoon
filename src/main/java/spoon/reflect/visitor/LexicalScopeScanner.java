/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A {@link CtScanner} which provides current lexical scope
 * of currently scanned AST node.
 */
public class LexicalScopeScanner extends EarlyTerminatingScanner<Object> {
	private final Deque<LexicalScope> scopes = new ArrayDeque<>();
	protected void enter(spoon.reflect.declaration.CtElement e) {
		LexicalScope newFinder = onElement(scopes.peek(), e);
		if (newFinder != null) {
			scopes.push(newFinder);
		}
	}
	protected void exit(spoon.reflect.declaration.CtElement e) {
		LexicalScope topFinder = scopes.peek();
		if (topFinder != null && topFinder.getScopeElement() == e) {
			//we are living scope of this ConflictFinder. Pop it
			scopes.pop();
		}
	}
	private static NameScopeImpl EMPTY = new NameScopeImpl(null, null);
	/**
	 * @return {@link LexicalScope} of actually scanned element. The {@link LexicalScope#forEachElementByName(String, java.util.function.Function)} can be used
	 * to get all {@link CtElement}s which are mapped to that simple name
	 */
	public LexicalScope getCurrentLexicalScope() {
		LexicalScope ns = scopes.peek();
		return ns == null ? EMPTY : ns;
	}

	/**
	 * Call it for each visited CtElement
	 * @param parent the parent ConflictFinder
	 * @param target an element
	 * @return new {@link NameScopeImpl} if `target` element declares new naming scope or null if there is no new scope
	 */
	private NameScopeImpl onElement(LexicalScope parent, CtElement target) {
		class Visitor extends CtAbstractVisitor {
			NameScopeImpl finder = null;
			@Override
			public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
				//compilation unit items are added in TypeNameScope, because they depend on the inhertance hierarchy of the type itself
			}
			@Override
			public <T> void visitCtClass(CtClass<T> ctClass) {
				finder = new TypeNameScope(parent, ctClass);
			}
			@Override
			public <T> void visitCtInterface(CtInterface<T> intrface) {
				finder = new TypeNameScope(parent, intrface);
			}
			@Override
			public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
				finder = new TypeNameScope(parent, ctEnum);
			}
			@Override
			public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
				finder = new TypeNameScope(parent, annotationType);
			}
			@Override
			public <T> void visitCtMethod(CtMethod<T> m) {
				finder = new NameScopeImpl(parent, m, m.getParameters());
			}
			@Override
			public <T> void visitCtConstructor(CtConstructor<T> c) {
				finder = new NameScopeImpl(parent, c, c.getParameters());
			}
			@Override
			public <T> void visitCtLambda(CtLambda<T> lambda) {
				finder = new NameScopeImpl(parent, lambda, lambda.getParameters());
			}
			@Override
			public void visitCtCatch(CtCatch catchBlock) {
				finder = new NameScopeImpl(parent, catchBlock).addNamedElement(catchBlock.getParameter());
			}
			@Override
			public <R> void visitCtBlock(CtBlock<R> block) {
				finder = new NameScopeImpl(parent, block);
			}
			@Override
			public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
				if (parent == null) {
					return;
				}
				parent.addNamedElement(localVariable);
			}
		};
		Visitor scanner = new Visitor();
		target.accept(scanner);
		return scanner.finder;
	}
}
