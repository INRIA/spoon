/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.visitor.printer;

import org.apache.log4j.Level;
import spoon.compiler.Environment;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;

import java.util.HashMap;
import java.util.Map;

public class PrinterHelper {
	/**
	 * Line separator which is used by the system
	 */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

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
	 * Mapping for line numbers.
	 */
	private Map<Integer, Integer> lineNumberMapping = new HashMap<>();

	public PrinterHelper(Environment env) {
		this.env = env;
	}

	/**
	 * Outputs a string.
	 */
	public PrinterHelper write(String s) {
		if (s != null) {
			sbf.append(s);
		}
		return this;
	}

	/**
	 * Outputs a char.
	 */
	public PrinterHelper write(char c) {
		sbf.append(c);
		return this;
	}

	/**
	 * Generates a new line.
	 */
	public PrinterHelper writeln() {
		sbf.append(LINE_SEPARATOR);
		line++;
		return this;
	}

	public PrinterHelper writeTabs() {
		for (int i = 0; i < nbTabs; i++) {
			if (env.isUsingTabulations()) {
				sbf.append("\t");
			} else {
				for (int j = 0; j < env.getTabulationSize(); j++) {
					sbf.append(" ");
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
	 * Sets the current number of tabs.
	 */
	public PrinterHelper setTabCount(int tabCount) {
		nbTabs = tabCount;
		return this;
	}

	public void insertLine() {
		int i = sbf.length() - 1;
		while (i >= 0 && (sbf.charAt(i) == ' ' || sbf.charAt(i) == '\t')) {
			i--;
		}
		sbf.insert(i + 1, LINE_SEPARATOR);
		line++;
	}

	public boolean removeLine() {
		String ls = LINE_SEPARATOR;
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

	public void adjustPosition(CtElement e, CompilationUnit unitExpected) {
		if (e.getPosition() != null && !e.isImplicit() && e.getPosition().getCompilationUnit() != null && e.getPosition().getCompilationUnit() == unitExpected) {
			while (line < e.getPosition().getLine()) {
				insertLine();
			}
			while (line > e.getPosition().getLine()) {
				if (!removeLine()) {
					if (line > e.getPosition().getEndLine()) {
						final String message = "cannot adjust position of " + e.getClass().getSimpleName() + " '" //
								+ e.getShortRepresentation() + "' " + " to match lines: " + line + " > [" //
								+ e.getPosition().getLine() + ", " + e.getPosition().getEndLine() + "]"; //
						env.report(null, Level.WARN, e, message);
					}
					break;
				}
			}
		}
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
	 * Removes the last non-white character.
	 */
	public PrinterHelper removeLastChar() {
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			if (sbf.charAt(sbf.length() - 1) == '\n') {
				line--;
			}
			sbf.deleteCharAt(sbf.length() - 1);
		}
		sbf.deleteCharAt(sbf.length() - 1);
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			if (sbf.charAt(sbf.length() - 1) == '\n') {
				line--;
			}
			sbf.deleteCharAt(sbf.length() - 1);
		}
		return this;
	}

	/**
	 * Write a pre unary operator.
	 */
	public void preWriteUnaryOperator(UnaryOperatorKind o) {
		switch (o) {
		case POS:
			write("+");
			break;
		case NEG:
			write("-");
			break;
		case NOT:
			write("!");
			break;
		case COMPL:
			write("~");
			break;
		case PREINC:
			write("++");
			break;
		case PREDEC:
			write("--");
			break;
		default:
			// do nothing (this does not feel right to ignore invalid ops)
		}
	}

	/**
	 * Write a post unary operator.
	 */
	public void postWriteUnaryOperator(UnaryOperatorKind o) {
		switch (o) {
		case POSTINC:
			write("++");
			break;
		case POSTDEC:
			write("--");
			break;
		default:
			// do nothing (this does not feel right to ignore invalid ops)
		}
	}

	/**
	 * Writes a binary operator.
	 */
	public PrinterHelper writeOperator(BinaryOperatorKind o) {
		switch (o) {
		case OR:
			write("||");
			break;
		case AND:
			write("&&");
			break;
		case BITOR:
			write("|");
			break;
		case BITXOR:
			write("^");
			break;
		case BITAND:
			write("&");
			break;
		case EQ:
			write("==");
			break;
		case NE:
			write("!=");
			break;
		case LT:
			write("<");
			break;
		case GT:
			write(">");
			break;
		case LE:
			write("<=");
			break;
		case GE:
			write(">=");
			break;
		case SL:
			write("<<");
			break;
		case SR:
			write(">>");
			break;
		case USR:
			write(">>>");
			break;
		case PLUS:
			write("+");
			break;
		case MINUS:
			write("-");
			break;
		case MUL:
			write("*");
			break;
		case DIV:
			write("/");
			break;
		case MOD:
			write("%");
			break;
		case INSTANCEOF:
			write("instanceof");
			break;
		}
		return this;
	}

	public void writeStringLiteral(String value, boolean mayContainsSpecialCharacter) {
		if (!mayContainsSpecialCharacter) {
			write(value);
			return;
		}
		// handle some special char.....
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
				if (c < 0x10) {
					write("\\u000" + Integer.toHexString(c));
				} else if (c < 0x100) {
					write("\\u00" + Integer.toHexString(c));
				} else if (c < 0x1000) {
					write("\\u0" + Integer.toHexString(c));
				} else {
					write("\\u" + Integer.toHexString(c));
				}
				continue;
			}
			switch (c) {
			case '\b':
				write("\\b"); //$NON-NLS-1$
				break;
			case '\t':
				write("\\t"); //$NON-NLS-1$
				break;
			case '\n':
				write("\\n"); //$NON-NLS-1$
				break;
			case '\f':
				write("\\f"); //$NON-NLS-1$
				break;
			case '\r':
				write("\\r"); //$NON-NLS-1$
				break;
			case '\"':
				write("\\\""); //$NON-NLS-1$
				break;
			case '\'':
				write("\\'"); //$NON-NLS-1$
				break;
			case '\\': // take care not to display the escape as a potential
				// real char
				write("\\\\"); //$NON-NLS-1$
				break;
			default:
				write(value.charAt(i));
			}
		}
	}

	public Map<Integer, Integer> getLineNumberMapping() {
		return lineNumberMapping;
	}

	@Override
	public String toString() {
		return sbf.toString();
	}
}
