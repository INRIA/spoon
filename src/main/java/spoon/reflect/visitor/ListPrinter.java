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
