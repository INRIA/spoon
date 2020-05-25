/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;

/**
 * Responsible for writing a token while pretty-printing.
 * Default is {@link DefaultTokenWriter}, can be provided by client too.
 */
public interface TokenWriter {
	/**
	 * Writes one separator. It is -&gt; or :: or one of these characters: (){}[];,.:@=&lt;&gt;?&amp;|
	 */
	TokenWriter writeSeparator(String token);

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
	TokenWriter writeOperator(String token);

	/**
	 * writes literal. It can be a String, Character or an number
	 */
	TokenWriter writeLiteral(String token);

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
	TokenWriter writeKeyword(String token);

	/**
	 * writes a java identifier.
	 */
	TokenWriter writeIdentifier(String token);

	/**
	 * writes a code snippet - represents arbitrary code of {@link CtCodeSnippetExpression} or {@link CtCodeSnippetStatement}
	 */
	TokenWriter writeCodeSnippet(String token);

	/**
	 * writes a comment
	 */
	TokenWriter writeComment(CtComment comment);

	/**
	 * writes new line (EOL)
	 */
	TokenWriter writeln();
	/**
	 * increments indentation
	 */
	TokenWriter incTab();
	/**
	 * decrements indentation
	 */
	TokenWriter decTab();

	/**
	 * @return {@link PrinterHelper} used by this TokenWriter.
	 *
	 * Note that in the future, will return an interface eg IPrinterHelper instead.
	 */
	PrinterHelper getPrinterHelper();

	/**
	 * resets to the initial state
	 */
	void reset();

	/**
	 * Writes a single space.
	 */
	TokenWriter writeSpace();
}
