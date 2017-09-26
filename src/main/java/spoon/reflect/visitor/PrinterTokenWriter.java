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

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;

/**
 * The token based and context aware printer.
 */
public interface PrinterTokenWriter {
	/**
	 * Writes one whitespace token.Token can be one or more spaces.
	 */
	PrinterTokenWriter writeWhitespace(String token);

	/**
	 * Writes one separator. It is -&gt; or :: or one of these characters: (){}[];,.:@=&lt;&gt;?&amp;|
	 */
	PrinterTokenWriter writeSeparator(String token);

	/**
	 * Writes one operator.
	 *		=
	 *		&gt;
	 *		&lt;
	 *		!
	 *		~
	 *		?
	 *		:
	 *		==
	 *		&lt;=
	 *		&gt;=
	 *		!=
	 *		&amp;&amp;
	 *		||
	 *		++
	 *		--
	 *		+
	 *		-
	 *		*
	 *		/
	 *		&amp;
	 *		|
	 *		^
	 *		%
	 *		&lt;&lt;
	 *		&gt;&gt;
	 *		&gt;&gt;&gt;
	 *		+=
	 *		-=
	 *		*=
	 *		/=
	 *		&amp;=
	 *		|=
	 *		^=
	 *		%=
	 *		&lt;&lt;=
	 *		&gt;&gt;=
	 *		&gt;&gt;&gt;=
	 *		instanceof
	 */
	PrinterTokenWriter writeOperator(String token);

	/**
	 * writes literal. It can be a String, Character or an number
	 */
	PrinterTokenWriter writeLiteral(String token);

	/**
	 * writes a keyword
	 *		abstract continue for new switch
	 *		assert default goto package synchronized
	 *		boolean do if private this
	 *		break double implements protected throw
	 *		byte else import public throws
	 *		case enum instanceof return transient
	 *		catch extends int short try
	 *		char final interface static void
	 *		class finally long strictfp volatile
	 *		const float native super while
	 */
	PrinterTokenWriter writeKeyword(String token);

	/**
	 * writes a java identifier.
	 */
	PrinterTokenWriter writeIdentifier(String token);

	/**
	 * writes a code snippet - represents arbitrary code of {@link CtCodeSnippetExpression} or {@link CtCodeSnippetStatement}
	 */
	PrinterTokenWriter writeCodeSnippet(String token);

	/**
	 * writes a comment
	 */
	PrinterTokenWriter writeComment(CtComment comment);

	/**
	 * writes new line (EOL)
	 */
	PrinterTokenWriter writeln();
	/**
	 * writes indentation
	 */
	PrinterTokenWriter writeTabs();
	/**
	 * increments indentation
	 */
	PrinterTokenWriter incTab();
	/**
	 * decrements indentation
	 */
	PrinterTokenWriter decTab();

	/**
	 * @return {@link PrinterHelper} used by this PrinterTokenWriter
	 */
	PrinterHelper getPrinterHelper();

	/**
	 * resets to the initial state
	 */
	void reset();
}
