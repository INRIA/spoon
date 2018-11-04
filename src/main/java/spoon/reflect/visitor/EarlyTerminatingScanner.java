/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.reflect.visitor;

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
}
