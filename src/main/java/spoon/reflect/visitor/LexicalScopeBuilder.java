package spoon.reflect.visitor;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

import spoon.SpoonException;
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
import spoon.reflect.declaration.CtNamedElement;

/**
 * Responsible for building lexical scopes.
 *
 */
public class LexicalScopeBuilder extends EarlyTerminatingScanner<Object> {
	private final List<LexicalScope> allScopes = new ArrayList<>();
	private final Deque<LexicalScope> scopes = new ArrayDeque<>();
	protected void enter(spoon.reflect.declaration.CtElement e) {
		LexicalScope newFinder = onElement(scopes.peek(), e);
		if (newFinder != null) {
			scopes.push(newFinder);
			allScopes.add(newFinder);
		}
	}
	protected void exit(spoon.reflect.declaration.CtElement e) {
		LexicalScope topFinder = scopes.peek();
		if (topFinder != null && topFinder.getScopeElement() == e) {
			//we are living scope of this ConflictFinder. Pop it
			scopes.pop();
		}
	}
	private static AbstractNameScope EMPTY = new AbstractNameScope(null, null) {
		@Override
		protected <T> T forEachLocalElementByName(String name, Function<? super CtNamedElement, T> consumer) {
			return null;
		}
	};
	/**
	 * @return {@link LexicalScope} of actually scanned element. The {@link LexicalScope#forEachElementByName(String, java.util.function.Function)} can be used
	 * to get all {@link CtElement}s which are mapped to that simple name
	 */
	public LexicalScope getCurrentNameScope() {
		LexicalScope ns = scopes.peek();
		return ns == null ? EMPTY : ns;
	}

	/**
	 * Returns all the collected name scopes
	 *
	 */
	public List<LexicalScope> getNameScopes() {
		return Collections.unmodifiableList(allScopes);
	}

	/**
	 * Call it for each visited CtElement
	 * @param parent the parent ConflictFinder
	 * @param target an element
	 * @return new {@link AbstractNameScope} if `target` element declares new naming scope or null if there is no new scope
	 */
	private AbstractNameScope onElement(LexicalScope parent, CtElement target) {
		class Visitor extends CtAbstractVisitor {
			AbstractNameScope finder = null;
			@Override
			public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
				//compilation unit items are added in NameScopeOfType, because they depend on the inhertance hierarchy of the type itself
			}
			@Override
			public <T> void visitCtClass(CtClass<T> ctClass) {
				finder = new NameScopeOfType(parent, ctClass);
			}
			@Override
			public <T> void visitCtInterface(CtInterface<T> intrface) {
				finder = new NameScopeOfType(parent, intrface);
			}
			@Override
			public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
				finder = new NameScopeOfType(parent, ctEnum);
			}
			@Override
			public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
				finder = new NameScopeOfType(parent, annotationType);
			}
			@Override
			public <T> void visitCtMethod(CtMethod<T> m) {
				finder = new SimpleNameScope(parent, m, m.getParameters());
			}
			@Override
			public <T> void visitCtConstructor(CtConstructor<T> c) {
				finder = new SimpleNameScope(parent, c, c.getParameters());
			}
			@Override
			public <T> void visitCtLambda(CtLambda<T> lambda) {
				finder = new SimpleNameScope(parent, lambda, lambda.getParameters());
			}
			@Override
			public void visitCtCatch(CtCatch catchBlock) {
				finder = new SimpleNameScope(parent, catchBlock).addVariable(catchBlock.getParameter());
			}
			@Override
			public <R> void visitCtBlock(CtBlock<R> block) {
				finder = new SimpleNameScope(parent, block);
			}
			@Override
			public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
				if (parent instanceof SimpleNameScope) {
					((SimpleNameScope) parent).addVariable(localVariable);
				} else {
					throw new SpoonException("Cannot add local variable when parent is missing");
				}
			}
		};
		Visitor scanner = new Visitor();
		target.accept(scanner);
		return scanner.finder;
	}
}
