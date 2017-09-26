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

import spoon.reflect.code.CtComment;

/**
 * Implementation of {@link PrinterTokenWriter}, which writes all tokens to {@link PrinterHelper}
 */
public class SimplePrinterTokenWriter implements PrinterTokenWriter {

	private final PrinterHelper printerHelper;

	public SimplePrinterTokenWriter(PrinterHelper printerHelper) {
		this.printerHelper = printerHelper;
	}

	@Override
	public SimplePrinterTokenWriter writeOperator(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeWhitespace(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeSeparator(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeLiteral(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeKeyword(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeIdentifier(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeCodeSnippet(String token) {
		printerHelper.write(token);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeComment(CtComment comment) {
		CommentHelper.printComment(printerHelper, comment);
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeln() {
		printerHelper.writeln();
		return this;
	}

	@Override
	public SimplePrinterTokenWriter writeTabs() {
		printerHelper.writeTabs();
		return this;
	}

	@Override
	public SimplePrinterTokenWriter incTab() {
		printerHelper.incTab();
		return this;
	}

	@Override
	public SimplePrinterTokenWriter decTab() {
		printerHelper.decTab();
		return this;
	}

	@Override
	public void reset() {
		printerHelper.reset();
	}

	@Override
	public PrinterHelper getPrinterHelper() {
		return printerHelper;
	}
}
