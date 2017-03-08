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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * A mapping function, which scans all children of input element.<br>
 * There is possible to register {@link CtScannerListener} to listen for enter/exit of each scanned AST node
 * and optionally to skip processing of children.<br>
 * There is possible to register {@link Filter} to filter AST nodes which are sent to the next step<br>
 * Note: The listener is called for each scanned AST node, but the filter is called only for nodes where {@link CtScannerListener#enter(CtElement)} returns true
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
		protected void doScan(CtElement element, ScanningMode mode) {
			//send input to output
			if (mode.visitElement) {
				next.accept(element);
			}
			if (mode.visitChildren) {
				element.accept(this);
			}
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
