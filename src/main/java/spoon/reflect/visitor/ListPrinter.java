/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.io.Closeable;

/**
 * Helper which assures consistent printing of lists
 * prefixed with `start`, separated by `separator` and suffixed by `end`.<br>
 * If there is no item in the list then it prints `start` and then `end`<br>
 * If there is one item in the list then it prints `start`, item and then `end`<br>
 * If there is more then one items in the list then it prints `start`, items separated by `separator` and then `end`
 */
public class ListPrinter implements Closeable {

	private final TokenWriter printerTokenWriter;
	private final boolean nextPrefixSpace;
	private final String separator;
	private final boolean nextSuffixSpace;
	private final boolean endPrefixSpace;
	private final String end;
	private boolean isFirst = true;

	public ListPrinter(TokenWriter printerHelper, boolean startPrefixSpace, String start, boolean startSuffixSpace, boolean nextPrefixSpace, String next, boolean nextSuffixSpace, boolean endPrefixSpace, String end) {
		this.printerTokenWriter = printerHelper;
		this.nextPrefixSpace = nextPrefixSpace;
		this.separator = next;
		this.nextSuffixSpace = nextSuffixSpace;
		this.endPrefixSpace = endPrefixSpace;
		this.end = end;

		if (startPrefixSpace) {
			printerHelper.writeSpace();
		}
		if (start != null && !start.isEmpty()) {
			printerTokenWriter.writeSeparator(start);
		}
		if (startSuffixSpace) {
			printerHelper.writeSpace();
		}
	}

	/**
	 * Call that before printing of list item starts
	 */
	public void printSeparatorIfAppropriate() {
		if (isFirst) {
			/*
			 * we are starting first item. Do not print `separator` separator yet
			 */
			isFirst = false;
		} else {
			/*
			 * we are starting separator item. Print `separator` separator now
			 */
			if (nextPrefixSpace) {
				printerTokenWriter.writeSpace();
			}
			if (separator != null && !separator.isEmpty()) {
				printerTokenWriter.writeSeparator(separator);
			}
			if (nextSuffixSpace) {
				printerTokenWriter.writeSpace();
			}
		}
	}

	@Override
	public void close() {
		if (endPrefixSpace) {
			printerTokenWriter.writeSpace();
		}
		if (end != null && !end.isEmpty()) {
			printerTokenWriter.writeSeparator(end);
		}
	}
}
