/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.reflect.code.CtComment;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.TokenWriter;

/**
 * Wraps a `tokenWriter` by an implementation which intercepts all {@link TokenWriter} writeXxx(String) calls
 * and calls {@link TokenWriterProxy.Listener#onTokenWriterWrite(TokenType, String, CtComment, Runnable)}
 * where {@link Runnable} can be used to invoke same event on the wrapped {@link TokenWriter}
 */
public class TokenWriterProxy implements TokenWriter {
	/**
	 * Listens for each call of {@link TokenWriter}
	 */
	public interface Listener {
		/**
		 * Called once for each call of {@link TokenWriter} method
		 * @param tokenType identifies the called method
		 * @param token the sent token. May be null
		 * @param comment the sent comment. Is null for tokenType != {@link TokenType#COMMENT}
		 * @param printAction a {@link Runnable}, which can be used to run wrapped {@link TokenWriter} method
		 */
		void onTokenWriterWrite(TokenType tokenType, String token, CtComment comment, Runnable printAction);
	}

	private final Listener listener;
	private final TokenWriter delegate;

	public TokenWriterProxy(Listener listener, TokenWriter delegate) {
		this.listener = listener;
		this.delegate = delegate;
	}

	@Override
	public TokenWriter writeSeparator(String token) {
		this.listener.onTokenWriterWrite(TokenType.SEPARATOR, token, null, () -> delegate.writeSeparator(token));
		return this;
	}

	@Override
	public TokenWriter writeOperator(String token) {
		this.listener.onTokenWriterWrite(TokenType.OPERATOR, token, null, () -> delegate.writeOperator(token));
		return this;
	}

	@Override
	public TokenWriter writeLiteral(String token) {
		this.listener.onTokenWriterWrite(TokenType.LITERAL, token, null, () -> delegate.writeLiteral(token));
		return this;
	}

	@Override
	public TokenWriter writeKeyword(String token) {
		this.listener.onTokenWriterWrite(TokenType.KEYWORD, token, null, () -> delegate.writeKeyword(token));
		return this;
	}

	@Override
	public TokenWriter writeIdentifier(String token) {
		this.listener.onTokenWriterWrite(TokenType.IDENTIFIER, token, null, () -> delegate.writeIdentifier(token));
		return this;
	}

	@Override
	public TokenWriter writeCodeSnippet(String token) {
		this.listener.onTokenWriterWrite(TokenType.CODE_SNIPPET, token, null, () -> delegate.writeCodeSnippet(token));
		return this;
	}

	@Override
	public TokenWriter writeComment(CtComment comment) {
		this.listener.onTokenWriterWrite(TokenType.COMMENT, null, comment, () -> delegate.writeComment(comment));
		return this;
	}

	@Override
	public TokenWriter writeln() {
		this.listener.onTokenWriterWrite(TokenType.NEW_LINE, "\n", null, () -> delegate.writeln());
		return this;
	}

	@Override
	public TokenWriter incTab() {
		this.listener.onTokenWriterWrite(TokenType.INC_TAB, null, null, () -> delegate.incTab());
		return this;
	}

	@Override
	public TokenWriter decTab() {
		this.listener.onTokenWriterWrite(TokenType.DEC_TAB, null, null, () -> delegate.decTab());
		return this;
	}

	@Override
	public PrinterHelper getPrinterHelper() {
		return delegate.getPrinterHelper();
	}

	@Override
	public void reset() {
		delegate.reset();
	}

	@Override
	public TokenWriter writeSpace() {
		this.listener.onTokenWriterWrite(TokenType.SPACE, " ", null, () -> delegate.writeSpace());
		return this;
	}
}
