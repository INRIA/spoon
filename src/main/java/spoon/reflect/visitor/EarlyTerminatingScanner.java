/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Extends {@link CtScanner}, to support early termination of scanning process and scan listeners.
 * It is useful when your algorithm is searching for a specific node only.
 * In this case, you can call {@link #terminate()}, which ensures that no more AST nodes are visited,
 *<br>
 * It is possible to register an implementation of {@link CtScannerListener},
 * whose {@link CtScannerListener#enter(CtElement)}/{@link CtScannerListener#exit(CtElement)}
 * methods are called before/after each AST node is visited.<br>
 *
 * @param <T> the type of the result produced by this scanner.
 */
public class EarlyTerminatingScanner<T> extends CtScanner {

	private boolean terminate = false;
	private T result;
	private CtScannerListener listener;
	protected CtRole scannedRole;
	private boolean visitCompilationUnitContent = false;

	protected void terminate() {
		terminate = true;
	}

	protected boolean isTerminated() {
		return terminate;
	}

	protected void setResult(T result) {
		this.result = result;
	}

	/**
	 * @return the result of scanning - the value, which was stored by a previous call of {@link #setResult(Object)}
	 */
	public T getResult() {
		return result;
	}

	/**
	 * @return null or the implementation of {@link CtScannerListener}, which is registered to listen for enter/exit of nodes during scanning of the AST
	 */
	public CtScannerListener getListener() {
		return listener;
	}

	/**
	 * @param listener the implementation of {@link CtScannerListener}, which will be called back when entering/exiting
	 * odes during scanning.
	 * @return this to support fluent API
	 */
	public EarlyTerminatingScanner<T> setListener(CtScannerListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public void scan(CtRole role, Collection<? extends CtElement> elements) {
		if (isTerminated() || elements == null) {
			return;
		}
		// we use defensive copy so as to be able to change the class while scanning
		// otherwise one gets a ConcurrentModificationException
		for (CtElement e : new ArrayList<>(elements)) {
			scan(role, e);
			if (isTerminated()) {
				return;
			}
		}
	}

	@Override
	public void scan(CtRole role, Map<String, ? extends CtElement> elements) {
		if (isTerminated() || elements == null) {
			return;
		}
		for (CtElement obj : elements.values()) {
			scan(role, obj);
			if (isTerminated()) {
				return;
			}
		}
	}

	@Override
	public void scan(CtRole role, CtElement element) {
		scannedRole = role;
		super.scan(role, element);
	}

	/*
	 * we cannot override scan(CtRole role, CtElement element) directly
	 * because some implementations needs scan(CtElement element), which must be called too
	 */
	@Override
	public void scan(CtElement element) {
		if (element == null || isTerminated()) {
			return;
		}
		if (listener == null) {
			//the listener is not defined
			//visit this element and may be children
			doScan(scannedRole, element, ScanningMode.NORMAL);
		} else {
			//the listener is defined, call it's enter method first
			ScanningMode mode = listener.enter(scannedRole, element);
			if (mode != ScanningMode.SKIP_ALL) {
				//the listener decided to visit this element and may be children
				doScan(scannedRole, element, mode);
				//then call exit, only if enter returned true
				listener.exit(scannedRole, element);
			} //else the listener decided to skip this element and all children. Do not call exit.
		}
	}

	/**
	 * This method is called ONLY when the listener decides that the current element and children should be visited.
	 * Subclasses can override it to react accordingly.
	 */
	protected void doScan(CtRole role, CtElement element, ScanningMode mode) {
		//send input to output
		if (mode.visitElement) {
			onElement(role, element);
		}
		if (mode.visitChildren) {
			//do not call scan(CtElement) nor scan(CtRole, CtElement), because they would cause StackOverflowError
			element.accept(this);
		}
	}

	@Override
	public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
		if (isVisitCompilationUnitContent()) {
			enter(compilationUnit);
			scan(CtRole.COMMENT, compilationUnit.getComments());
			scan(CtRole.ANNOTATION, compilationUnit.getAnnotations());
			scan(CtRole.PACKAGE_DECLARATION, compilationUnit.getPackageDeclaration());
			scan(CtRole.DECLARED_IMPORT, compilationUnit.getImports());
			//visit directly the module (instead of reference only)
			scan(CtRole.DECLARED_MODULE, compilationUnit.getDeclaredModule());
			//visit directly the types (instead of references only)
			scan(CtRole.DECLARED_TYPE, compilationUnit.getDeclaredTypes());
			exit(compilationUnit);
		} else {
			super.visitCtCompilationUnit(compilationUnit);
		}
	}

	/**
	 * Called for each scanned element. The call of this method is influenced by {@link ScanningMode} defined by {@link CtScannerListener}
	 * @param role a role of `element` in parent
	 * @param element a scanned element
	 */
	protected void onElement(CtRole role, CtElement element) {
	}

	@Override
	public void scan(CtRole role, Object o) {
		if (isTerminated() || o == null) {
			return;
		}
		if (o instanceof CtElement) {
			scan(role, (CtElement) o);
		} else if (o instanceof Collection<?>) {
			scan(role, (Collection<? extends CtElement>) o);
		} else if (o instanceof Map<?, ?>) {
			for (Object obj : ((Map) o).values()) {
				scan(role, obj);
				if (isTerminated()) {
					return;
				}
			}
		}
	}

	/**
	 * @return true if types and modules are visited. false if only their references are visited. false is default
	 */
	public boolean isVisitCompilationUnitContent() {
		return visitCompilationUnitContent;
	}

	/**
	 * @param visitCompilationUnitContent use true if types and modules have to be visited. false if only their references have to be visited.
	 * false is default
	 */
	public EarlyTerminatingScanner<T> setVisitCompilationUnitContent(boolean visitCompilationUnitContent) {
		this.visitCompilationUnitContent = visitCompilationUnitContent;
		return this;
	}
}
