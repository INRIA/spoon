/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.LiteralBase;
import spoon.reflect.cu.SourcePosition;

/**
 * Computes source code representation of the literal
 */
abstract class LiteralHelper {

	private LiteralHelper() {
	}

	private static String getBasedString(Integer value, LiteralBase base) {
		if (base == LiteralBase.BINARY) {
			return "0b" + Integer.toBinaryString(value);
		} else if (base == LiteralBase.OCTAL) {
			return "0" + Integer.toOctalString(value);
		} else if (base == LiteralBase.HEXADECIMAL) {
			return "0x" + Integer.toHexString(value);
		}
		return Integer.toString(value);
	}

	private static String getBasedString(Long value, LiteralBase base) {
		if (base == LiteralBase.BINARY) {
			return "0b" + Long.toBinaryString(value) + "L";
		} else if (base == LiteralBase.OCTAL) {
			return "0" + Long.toOctalString(value) + "L";
		} else if (base == LiteralBase.HEXADECIMAL) {
			return "0x" + Long.toHexString(value) + "L";
		}
		return Long.toString(value) + "L";
	}

	private static String getBasedString(Float value, LiteralBase base) {
		if (base == LiteralBase.HEXADECIMAL) {
			return Float.toHexString(value) + "F";
		}
		return Float.toString(value) + "F";
	}

	private static String getBasedString(Double value, LiteralBase base) {
		if (base == LiteralBase.HEXADECIMAL) {
			return Double.toHexString(value);
		}
		return Double.toString(value);
	}

	/**
	 * @param literal to be converted literal
	 * @return source code representation of the literal
	 */
	public static <T> String getLiteralToken(CtLiteral<T> literal) {
		if (literal.getValue() == null) {
			return "null";
		} else if (literal.getValue() instanceof Integer) {
			return getBasedString((Integer) literal.getValue(), literal.getBase());
		} else if (literal.getValue() instanceof Long) {
			return getBasedString((Long) literal.getValue(), literal.getBase());
		} else if (literal.getValue() instanceof Float) {
			return getBasedString((Float) literal.getValue(), literal.getBase());
		} else if (literal.getValue() instanceof Double) {
			return getBasedString((Double) literal.getValue(), literal.getBase());
		} else if (literal.getValue() instanceof Character) {

			boolean mayContainsSpecialCharacter = true;

			SourcePosition position = literal.getPosition();
			if (position.isValidPosition()) {
				// the size of the string in the source code, the -1 is the size of the ' or " in the source code
				int stringLength = position.getSourceEnd() - position.getSourceStart() - 1;
				// if the string in the source is not the same as the string in the literal, the string may contains special characters
				mayContainsSpecialCharacter = stringLength != 1;
			}
			StringBuilder sb = new StringBuilder(10);
			sb.append('\'');
			appendCharLiteral(sb, (Character) literal.getValue(), mayContainsSpecialCharacter);
			sb.append('\'');
			return sb.toString();
		} else if (literal.getValue() instanceof String) {
			boolean mayContainsSpecialCharacters = true;

			SourcePosition position = literal.getPosition();
			if (position.isValidPosition()) {
				// the size of the string in the source code, the -1 is the size of the ' or " in the source code
				int stringLength = position.getSourceEnd() - position.getSourceStart() - 1;
				// if the string in the source is not the same as the string in the literal, the string may contains special characters
				mayContainsSpecialCharacters = ((String) literal.getValue()).length() != stringLength;
			}
			return "\"" + getStringLiteral((String) literal.getValue(), mayContainsSpecialCharacters) + "\"";
		} else if (literal.getValue() instanceof Class) {
			return ((Class<?>) literal.getValue()).getName();
		} else {
			return literal.getValue().toString();
		}
	}

	static void appendCharLiteral(StringBuilder sb, Character c, boolean mayContainsSpecialCharacter) {
		if (!mayContainsSpecialCharacter) {
			sb.append(c);
		} else {
			switch (c) {
				case '\b':
					sb.append("\\b"); //$NON-NLS-1$
					break;
				case '\t':
					sb.append("\\t"); //$NON-NLS-1$
					break;
				case '\n':
					sb.append("\\n"); //$NON-NLS-1$
					break;
				case '\f':
					sb.append("\\f"); //$NON-NLS-1$
					break;
				case '\r':
					sb.append("\\r"); //$NON-NLS-1$
					break;
				case '\"':
					sb.append("\\\""); //$NON-NLS-1$
					break;
				case '\'':
					sb.append("\\'"); //$NON-NLS-1$
					break;
				case '\\': // take care not to display the escape as a potential
					// real char
					sb.append("\\\\"); //$NON-NLS-1$
					break;
				default:
					sb.append(Character.isISOControl(c) ? String.format("\\u%04x", (int) c) : Character.toString(c));
			}
		}
	}

	static String getStringLiteral(String value, boolean mayContainsSpecialCharacter) {
		if (!mayContainsSpecialCharacter) {
			return value;
		} else {
			StringBuilder sb = new StringBuilder(value.length() * 2);
			for (int i = 0; i < value.length(); i++) {
				appendCharLiteral(sb, value.charAt(i), mayContainsSpecialCharacter);
			}
			return sb.toString();
		}
	}
}
