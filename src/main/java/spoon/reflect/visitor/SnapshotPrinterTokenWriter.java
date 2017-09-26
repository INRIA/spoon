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

import java.util.ArrayDeque;

import spoon.SpoonException;
import spoon.reflect.code.CtComment;

/**
 * The special {@link PrinterTokenWriter} implementation,
 * detects whether some identifier, keyword, literal, operator or separator.
 * Comments, white spaces, tabs and new lines are ignored, because they can appear in the middle of printed elements.
 * was written since last call of {@link #snapshotLength()}
 */
class SnapshotPrinterTokenWriter implements PrinterTokenWriter {

	private PrinterTokenWriter next;

	SnapshotPrinterTokenWriter(PrinterTokenWriter next) {
		this.next = next;
	}

	public PrinterTokenWriter getNext() {
		return next;
	}

	public void setNext(PrinterTokenWriter next) {
		if (next == null) {
			throw new SpoonException("Next PrinterTokenWriter must not be null");
		}
		this.next = next;
	}

	private int countOfTokens = 0;

	SnapshotPrinterTokenWriter writeSpace() {
		writeWhitespace(" ");
		return this;
	}


	@Override
	public SnapshotPrinterTokenWriter writeIdentifier(String token) {
		countOfTokens++;
		next.writeIdentifier(token);
		return this;
	}

	@Override
	public SnapshotPrinterTokenWriter writeKeyword(String token) {
		countOfTokens++;
		next.writeKeyword(token);
		return this;
	}

	@Override
	public SnapshotPrinterTokenWriter writeLiteral(String token) {
		countOfTokens++;
		next.writeLiteral(token);
		return this;
	}

	@Override
	public SnapshotPrinterTokenWriter writeOperator(String token) {
		countOfTokens++;
		next.writeOperator(token);
		return this;
	}

	@Override
	public SnapshotPrinterTokenWriter writeSeparator(String token) {
		countOfTokens++;
		next.writeSeparator(token);
		return this;
	}

	private ArrayDeque<Integer> lengths = new ArrayDeque<>();

	/** stores the length of the printer */
	public void snapshotLength() {
		//if the buffer contains some data then flush them first to send that token and use correct `countOfTokens`
		lengths.addLast(countOfTokens);
	}

	/** returns true if something has been written since the last call to snapshotLength() */
	public boolean hasNewContent() {
		return lengths.pollLast() < countOfTokens;
	}

	@Override
	public PrinterTokenWriter writeWhitespace(String token) {
		next.writeWhitespace(token);
		return this;
	}

	@Override
	public PrinterTokenWriter writeCodeSnippet(String token) {
		next.writeCodeSnippet(token);
		return this;
	}

	@Override
	public PrinterTokenWriter writeComment(CtComment comment) {
		next.writeComment(comment);
		return this;
	}

	@Override
	public PrinterTokenWriter writeln() {
		next.writeln();
		return this;
	}

	@Override
	public PrinterTokenWriter writeTabs() {
		next.writeTabs();
		return this;
	}

	@Override
	public PrinterTokenWriter incTab() {
		next.incTab();
		return this;
	}

	@Override
	public PrinterTokenWriter decTab() {
		next.decTab();
		return this;
	}

	@Override
	public PrinterHelper getPrinterHelper() {
		return next.getPrinterHelper();
	}

	@Override
	public void reset() {
		next.reset();
	}
}
