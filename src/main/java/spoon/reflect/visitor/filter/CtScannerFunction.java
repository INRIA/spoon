/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * Returns all children of an element.
 * More than this, it is a parameterizable class to be subclassed which provides all the power of {@link spoon.reflect.visitor.CtScanner} in the context of queries.
 * <br>
 * In particular, one can a register a {@link CtScannerListener}, it is called-back when entering/exiting each scanned AST node
 * and it drives the scanning process (see {@link ScanningMode}).
 */
public class CtScannerFunction implements CtConsumableFunction<CtElement>, CtQueryAware {

	private final Scanner scanner;
	private boolean includingSelf = true;

	public CtScannerFunction() {
		scanner = new Scanner();
	}

	/**
	 * @param includingSelf if true then input element is sent to output too. By default it is false.
	 */
	public CtScannerFunction includingSelf(boolean includingSelf) {
		this.includingSelf = includingSelf;
		return this;
	}

	/**
	 * @param listener the implementation of {@link CtScannerListener}, which will listen for enter/exit of nodes during scanning of AST
	 * @return this to support fluent API
	 */
	public CtScannerFunction setListener(CtScannerListener listener) {
		scanner.setListener(listener);
		return this;
	}

	@Override
	public void apply(CtElement input, CtConsumer<Object> outputConsumer) {
		scanner.next = outputConsumer;
		if (this.includingSelf) {
			scanner.scan(input);
		} else {
			input.accept(scanner);
		}
	}

	/*
	 * it is called automatically by CtQuery implementation,
	 * when this mapping function is added.
	 */
	@Override
	public void setQuery(CtQuery query) {
		scanner.query = query;
	}

	private static class Scanner extends EarlyTerminatingScanner<Void> {
		protected CtConsumer<Object> next;
		private CtQuery query;

		@Override
		protected void onElement(CtRole role, CtElement element) {
			next.accept(element);
			super.onElement(role, element);
		}

		/*
		 * override {@link EarlyTerminatingScanner#isTerminated()} and let it stop when query is terminated
		 */
		@Override
		protected boolean isTerminated() {
			return query.isTerminated();
		}
	}
}
