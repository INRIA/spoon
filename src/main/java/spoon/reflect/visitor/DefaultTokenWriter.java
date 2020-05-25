/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtComment;

/**
 * Implementation of {@link TokenWriter}, which writes all tokens to {@link PrinterHelper}
 */
public class DefaultTokenWriter implements TokenWriter {

	private final PrinterHelper printerHelper;

	public DefaultTokenWriter(PrinterHelper printerHelper) {
		this.printerHelper = printerHelper;
	}

	@Override
	public DefaultTokenWriter writeOperator(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public DefaultTokenWriter writeSeparator(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public DefaultTokenWriter writeLiteral(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public DefaultTokenWriter writeKeyword(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public DefaultTokenWriter writeIdentifier(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public DefaultTokenWriter writeCodeSnippet(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public DefaultTokenWriter writeComment(CtComment comment) {
		CommentHelper.printComment(printerHelper, comment);
		return this;
	}

	@Override
	public DefaultTokenWriter writeln() {
		printerHelper.writeln();
		return this;
	}

	@Override
	public DefaultTokenWriter incTab() {
		printerHelper.incTab();
		return this;
	}

	@Override
	public DefaultTokenWriter decTab() {
		printerHelper.decTab();
		return this;
	}

	@Override
	public void reset() {
		printerHelper.reset();
	}

	@Override
	public TokenWriter writeSpace() {
		printerHelper.writeSpace();
		return this;
	}

	@Override
	public PrinterHelper getPrinterHelper() {
		return printerHelper;
	}

	@Override
	public String toString() {
		return printerHelper.toString();
	}
}
