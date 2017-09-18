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

import spoon.compiler.Environment;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrinterHelper {
	/**
	 * Line separator which is used by the printer helper.
	 * By default the system line separator is used
	 */
	private String lineSeparator = System.getProperty("line.separator");

	/**
	 * Environment which Spoon is executed.
	 */
	private Environment env;

	/**
	 * The string buffer in which the code is generated.
	 */
	private final StringBuffer sbf = new StringBuffer();

	/**
	 * Number of tabs when we print the source code.
	 */
	private int nbTabs = 0;

	/**
	 * Current line number.
	 */
	private int line = 1;

	/**
	 * Current column number
	 * Not used yet, but this shows the advantage of encapsulating all sbf.append in calls to {@link #write(char)} or {@link #write(String)}.
	 * This will be used for sniper mode later
	 */
	private int column = 1;

	/**
	 * Mapping for line numbers.
	 */
	private Map<Integer, Integer> lineNumberMapping = new HashMap<>();

	public PrinterHelper(Environment env) {
		this.env = env;
	}

	/**
	 * resets to the initial state
	 */
	public void reset() {
		sbf.setLength(0);
		nbTabs = 0;
		line = 1;
		column = 1;
		//create new map, because clients keeps reference to it
		lineNumberMapping = new HashMap<>();
	}

	/**
	 * Outputs a string.
	 */
	public PrinterHelper write(String s) {
		if (s != null) {
			sbf.append(s);
			column += s.length();
		}
		return this;
	}

	/**
	 * Outputs a char.
	 */
	public PrinterHelper write(char c) {
		sbf.append(c);
		column += 1;
		return this;
	}

	/**
	 * Generates a new line.
	 */
	public PrinterHelper writeln() {
		write(lineSeparator);
		line++;
		// reset the column index
		column = 1;
		return this;
	}

	public PrinterHelper writeTabs() {
		for (int i = 0; i < nbTabs; i++) {
			if (env.isUsingTabulations()) {
				write('\t');
			} else {
				for (int j = 0; j < env.getTabulationSize(); j++) {
					write(' ');
				}
			}
		}
		return this;
	}

	/**
	 * Increments the current number of tabs.
	 */
	public PrinterHelper incTab() {
		nbTabs++;
		return this;
	}

	/**
	 * Decrements the current number of tabs.
	 */
	public PrinterHelper decTab() {
		nbTabs--;
		return this;
	}

	/**
	 * @return the current number of tabs.
	 */
	public int getTabCount() {
		return nbTabs;
	}

	/**
	 * Sets the current number of tabs.
	 */
	public PrinterHelper setTabCount(int tabCount) {
		nbTabs = tabCount;
		return this;
	}

	public boolean removeLine() {
		String ls = lineSeparator;
		int i = sbf.length() - ls.length();
		boolean hasWhite = false;
		while (i > 0 && !ls.equals(sbf.substring(i, i + ls.length()))) {
			if (!isWhite(sbf.charAt(i))) {
				return false;
			}
			hasWhite = true;
			i--;
		}
		if (i <= 0) {
			return false;
		}
		hasWhite = hasWhite || isWhite(sbf.charAt(i - 1));
		sbf.replace(i, i + ls.length(), hasWhite ? "" : " ");
		line--;
		return true;
	}

	private boolean isWhite(char c) {
		return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
	}

	public PrinterHelper adjustStartPosition(CtElement e) {
		if (e.getPosition() != null && !e.isImplicit()) {
			// we should add some lines
			while (line < e.getPosition().getLine()) {
				writeln();
			}
			// trying to remove some lines
			while (line > e.getPosition().getLine()) {
				if (!removeLine()) {
					break;
				}
			}
		}
		return this;
	}

	public PrinterHelper adjustEndPosition(CtElement e) {
		if (env.isPreserveLineNumbers() && e.getPosition() != null) {
			// let's add lines if required
			while (line < e.getPosition().getEndLine()) {
				writeln();
			}
		}
		return this;
	}

	public void undefineLine() {
		if (lineNumberMapping.get(line) == null) {
			putLineNumberMapping(0);
		}
	}

	public void mapLine(CtElement e, CompilationUnit unitExpected) {
		if ((e.getPosition() != null) && (e.getPosition().getCompilationUnit() == unitExpected)) {
			// only map elements coming from the source CU
			putLineNumberMapping(e.getPosition().getLine());
		} else {
			undefineLine();
		}
	}

	public void putLineNumberMapping(int valueLine) {
		lineNumberMapping.put(this.line, valueLine);
	}

	/**
	 * Write a pre unary operator.
	 */
	public void preWriteUnaryOperator(UnaryOperatorKind o) {
		if (OperatorHelper.isPrefixOperator(o)) {
			write(OperatorHelper.getOperatorText(o));
		}
	}

	/**
	 * Write a post unary operator.
	 */
	public void postWriteUnaryOperator(UnaryOperatorKind o) {
		if (OperatorHelper.isSufixOperator(o)) {
			write(OperatorHelper.getOperatorText(o));
		}
	}

	/**
	 * Writes a binary operator.
	 */
	public PrinterHelper writeOperator(BinaryOperatorKind o) {
		write(OperatorHelper.getOperatorText(o));
		return this;
	}

	public void writeCharLiteral(Character c, boolean mayContainsSpecialCharacter) {
		StringBuilder sb = new StringBuilder(10);
		LiteralHelper.appendCharLiteral(sb, c, mayContainsSpecialCharacter);
		write(sb.toString());
	}

	public void writeStringLiteral(String value, boolean mayContainsSpecialCharacter) {
		write(LiteralHelper.getStringLiteral(value, mayContainsSpecialCharacter));
	}

	public Map<Integer, Integer> getLineNumberMapping() {
		return Collections.unmodifiableMap(lineNumberMapping);
	}

	@Override
	public String toString() {
		return sbf.toString();
	}

	private ArrayDeque<Integer> lengths = new ArrayDeque<>();

	/** stores the length of the printer */
	public void snapshotLength() {
		lengths.addLast(toString().length());
	}

	/** returns true if something has been written since the last call to snapshotLength() */
	public boolean hasNewContent() {
		return lengths.pollLast() < toString().length();
	}

	/**
	 * Creates new handler which assures consistent printing of lists
	 * prefixed with `start`, separated by `next` and suffixed by `end`
	 * @param start the string which has to be printed at the beginning of the list
	 * @param next the string which has to be used as separator before each next item
	 * @param end the string which has to be printed after the list
	 * @return the {@link ListPrinter} whose {@link ListPrinter#printSeparatorIfAppropriate()} has to be called
	 * before printing of each item.
	 */
	public ListPrinter createListPrinter(String start, String next, String end) {
		return new ListPrinter(this, start, next, end);
	}

	/**
	 * @return current line separator. By default there is CR LF, LF or CR depending on the Operation system
	 * defined by System.getProperty("line.separator")
	 */
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * @param lineSeparator characters which will be printed as End of line.
	 * By default there is System.getProperty("line.separator")
	 */
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
}
