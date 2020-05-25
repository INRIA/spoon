/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.compiler.Environment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Supports configurable printing of text with indentations and line and column counting
 */
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
	protected final StringBuffer sbf = new StringBuffer();

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

	/*
	 * each writeln() sets this to true.
	 * if true then first call of write first writes tabs and then resets this to false
	 */
	protected boolean shouldWriteTabs = true;
	/*
	 * true if last written character was \r
	 * It helps to detect windows EOL, which is \r\n
	 */
	private boolean lastCharWasCR = false;

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
		shouldWriteTabs = true;
		//create new map, because clients keeps reference to it
		lineNumberMapping = new HashMap<>();
	}

	/**
	 * Outputs a string.
	 */
	public PrinterHelper write(String s) {
		if (s != null) {
			int len = s.length();
			for (int i = 0; i < len; i++) {
				write(s.charAt(i));
			}
		}
		return this;
	}

	/**
	 * Outputs a char.
	 */
	public PrinterHelper write(char c) {
		if (c == '\r') {
			sbf.append(c);
			line++;
			// reset the column index
			column = 1;
			shouldWriteTabs = true;
			lastCharWasCR = true;
			return this;
		}
		if (c == '\n') {
			sbf.append(c);
			if (lastCharWasCR) {
				//increment line only once in sequence of \r\n.
				//last was \r, so nothing to do
			} else {
				//increment line only once in sequence of \r\n.
				//last was NOT \r, so do it now
				line++;
				// reset the column index
				column = 1;
				shouldWriteTabs = true;
			}
			lastCharWasCR = false;
			return this;
		}
		autoWriteTabs();
		sbf.append(c);
		column += 1;
		lastCharWasCR = false;
		return this;
	}

	/**
	 * Generates a new line.
	 */
	public PrinterHelper writeln() {
		write(lineSeparator);
		return this;
	}

	private void writeTabsInternal() {
		for (int i = 0; i < nbTabs; i++) {
			if (env.isUsingTabulations()) {
				sbf.append('\t');
				column += 1;
			} else {
				for (int j = 0; j < env.getTabulationSize(); j++) {
					sbf.append(' ');
					column += 1;
				}
			}
		}
	}

	protected void autoWriteTabs() {
		if (shouldWriteTabs) {
			writeTabsInternal();
			shouldWriteTabs = false;
		}
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

	/** writes as many newlines as needed to align the line number again between the element position and the current line number */
	public PrinterHelper adjustStartPosition(CtElement e) {
		if (!e.isImplicit() && e.getPosition().isValidPosition()) {
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
		if (env.isPreserveLineNumbers() && e.getPosition().isValidPosition()) {
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

	public void mapLine(CtElement e, CtCompilationUnit unitExpected) {
		SourcePosition sp = e.getPosition();
		if ((sp.isValidPosition())
				&& (sp.getCompilationUnit() == unitExpected)
				&& (sp instanceof PartialSourcePositionImpl) == false) {
			// only map elements coming from the source CU
			putLineNumberMapping(e.getPosition().getLine());
		} else {
			undefineLine();
		}
	}

	public void putLineNumberMapping(int valueLine) {
		lineNumberMapping.put(this.line, valueLine);
	}

	public Map<Integer, Integer> getLineNumberMapping() {
		return Collections.unmodifiableMap(lineNumberMapping);
	}

	@Override
	public String toString() {
		return sbf.toString();
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

	/** writes a space ' ' */
	public void writeSpace() {
		this.write(' ');
	}
}
