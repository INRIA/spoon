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

import spoon.reflect.code.CtLiteral;
import spoon.reflect.cu.SourcePosition;

/**
 * Computes source code representation of the literal
 */
abstract class LiteralHelper {

	private LiteralHelper() {
	}

	/**
	 * @param literal to be converted literal
	 * @return source code representation of the literal
	 */
	public static <T> String getLiteralToken(CtLiteral<T> literal) {
		if (literal.getValue() == null) {
			return "null";
		} else if (literal.getValue() instanceof Long) {
			return literal.getValue() + "L";
		} else if (literal.getValue() instanceof Float) {
			return literal.getValue() + "F";
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
		} else if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
			if (c < 0x10) {
				sb.append("\\u000" + Integer.toHexString(c));
			} else if (c < 0x100) {
				sb.append("\\u00" + Integer.toHexString(c));
			} else if (c < 0x1000) {
				sb.append("\\u0" + Integer.toHexString(c));
			} else {
				sb.append("\\u" + Integer.toHexString(c));
			}
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
