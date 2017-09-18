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
package spoon.reflect.visitor;

import java.io.Closeable;

/**
 * Helper which assures consistent printing of lists
 * prefixed with `start`, separated by `next` and suffixed by `end`.<br>
 * If there is no item in the list then it prints `start` and then `end`<br>
 * If there is one item in the list then it prints `start`, item and then `end`<br>
 * If there is more then one items in the list then it prints `start`, items separated by `next` and then `end`
 */
public class ListPrinter implements Closeable {

	private final PrinterHelper printerHelper;
	private final String next;
	private final String end;
	private boolean isFirst = true;

	ListPrinter(PrinterHelper printerHelper, String start, String next, String end) {
		super();
		this.printerHelper = printerHelper;
		this.next = next;
		this.end = end;

		if (start != null && start.length() > 0) {
			printerHelper.write(start);
		}
	}

	/**
	 * Call that before printing of list item starts
	 */
	public void printSeparatorIfAppropriate() {
		if (isFirst) {
			/*
			 * we are starting first item. Do not print `next` separator yet
			 */
			isFirst = false;
		} else {
			/*
			 * we are starting next item. Print `next` separator now
			 */
			if (next != null && next.length() > 0) {
				printerHelper.write(next);
			}
		}
	}

	@Override
	public void close() {
		if (end != null && end.length() > 0) {
			printerHelper.write(end);
		}
	}
}
