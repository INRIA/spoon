/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.compiler.Environment;
import spoon.reflect.code.CtComment;
import spoon.reflect.visitor.DefaultTokenWriter;
import spoon.reflect.visitor.TokenWriter;

/**
 * {@link TokenWriter} which simply delegates
 * to {@link DefaultTokenWriter} with the decorator pattern, until {@link #setMuted(boolean)} is called with true
 * Then all tokens are ignored.
 */
public class MutableTokenWriter implements TokenWriter {
	private final TokenWriter delegate;
	private boolean muted = false;

	public MutableTokenWriter(Environment env) {
		this.delegate = new DefaultTokenWriter(new DirectPrinterHelper(env));
	}

	/**
	 * @return true if tokens are ignored. false if they are forwarded to `delegate`
	 */
	public boolean isMuted() {
		return muted;
	}

	/**
	 * @param muted true if tokens are ignored. false if they are forwarded to `delegate`
	 */
	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	@Override
	public TokenWriter writeSeparator(String token) {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeSeparator(token);
		return this;
	}
	@Override
	public TokenWriter writeOperator(String token) {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeOperator(token);
		return this;
	}
	@Override
	public TokenWriter writeLiteral(String token) {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeLiteral(token);
		return this;
	}
	@Override
	public TokenWriter writeKeyword(String token) {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeKeyword(token);
		return this;
	}
	@Override
	public TokenWriter writeIdentifier(String token) {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeIdentifier(token);
		return this;
	}
	@Override
	public TokenWriter writeCodeSnippet(String token) {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeCodeSnippet(token);
		return this;
	}
	@Override
	public TokenWriter writeComment(CtComment comment) {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeComment(comment);
		return this;
	}
	@Override
	public TokenWriter writeln() {
		if (isMuted()) {
			//if new line is muted, then direct printer helper already wrote EOL and indentation
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeln();
		return this;
	}
	@Override
	public TokenWriter incTab() {
		if (isMuted()) {
			return this;
		}
		delegate.incTab();
		return this;
	}
	@Override
	public TokenWriter decTab() {
		if (isMuted()) {
			return this;
		}
		delegate.decTab();
		return this;
	}
	@Override
	public DirectPrinterHelper getPrinterHelper() {
		return (DirectPrinterHelper) delegate.getPrinterHelper();
	}
	@Override
	public void reset() {
		if (isMuted()) {
			return;
		}
		delegate.reset();
	}
	@Override
	public TokenWriter writeSpace() {
		if (isMuted()) {
			getPrinterHelper().setShouldWriteTabs(false);
			return this;
		}
		delegate.writeSpace();
		return this;
	}
	@Override
	public String toString() {
		return delegate.toString();
	}
}
