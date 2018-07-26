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
package spoon.reflect.visitor.printer.internal;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.TokenWriter;
import spoon.reflect.visitor.printer.internal.SourceFragment;
import spoon.support.modelobs.ChangeCollector;

/**
 * {@link PrettyPrinter} implementation which copies as much as possible from origin sources
 * and prints only changed elements
 */
public class SniperJavaPrettyPrinter extends DefaultJavaPrettyPrinter {

	private final MutableTokenWriter mutableTokenWriter;
	private ChangeResolver changeResolver;
	private final Deque<SourceFragmentContext> sourceFragmentContextStack = new ArrayDeque<>();
	private final Environment env;

	/**
	 * Creates a new {@link PrettyPrinter} which copies origin sources and prints only changes.
	 */
	public SniperJavaPrettyPrinter(Environment env) {
		super(env);
		this.env = env;
		//create a TokenWriter which can be configured to ignore tokens coming from DJPP
		mutableTokenWriter = new MutableTokenWriter(env);
		//wrap that TokenWriter to listen on all incoming events and set wrapped version to DJPP
		setPrinterTokenWriter(createTokenWriterListener(mutableTokenWriter));
	}

	private ChangeCollector getChangeCollector() {
		return getChangeResolver().getChangeCollector();
	}

	private ChangeResolver getChangeResolver() {
		if (changeResolver == null) {
			ChangeCollector changeCollector = ChangeCollector.getChangeCollector(env);
			if (changeCollector == null) {
				throw new SpoonException(ChangeCollector.class.getSimpleName() + " was not attached to the Environment");
			}
			changeResolver = new ChangeResolver(changeCollector, null);
		}
		return changeResolver;
	}

	/**
	 * wrap a `tokenWriter` by a proxy which intercepts all {@link TokenWriter} writeXxx(String) calls
	 * and first calls {@link #onTokenWriterWrite(String, String)} and then calls origin `tokenWriter` method
	 * @param tokenWriter to be wrapped {@link TokenWriter}
	 * @return a wrapped {@link TokenWriter}
	 */
	private TokenWriter createTokenWriterListener(TokenWriter tokenWriter) {
		return new TokenWriterProxy(this::onTokenWriterWrite, tokenWriter);
	}

	@Override
	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		//use line separator of origin source file
		setLineSeparator(detectLineSeparator(sourceCompilationUnit.getOriginalSourceCode()));
		super.calculate(sourceCompilationUnit, types);
	}

	private static final String CR = "\r";
	private static final String CRLF = "\r\n";
	private static final String LF = "\n";

	/**
	 * Detect line separator used in origin code
	 * @return character sequence used as line separator in `text`
	 */
	private String detectLineSeparator(String text) {
		if (text != null) {
			int len = text.length();
			for (int i = 0; i < len; i++) {
				char c = text.charAt(i);
				if (c == '\n') {
					return LF;
				} else if (c == '\r') {
					i++;
					if (i < len && text.charAt(i) == '\n') {
						return CRLF;
					}
					return CR;
				}
			}
		}
		return System.getProperty("line.separator");
	}

	@Override
	public DefaultJavaPrettyPrinter writeHeader(List<CtType<?>> types, Collection<CtImport> imports) {
		runInContext(SourceFragmentContextPrettyPrint.INSTANCE,
				() -> super.writeHeader(types, imports));
		return this;
	}

	@Override
	protected void printTypes(List<CtType<?>> types) {
		ElementSourceFragment rootFragment = (ElementSourceFragment) sourceCompilationUnit.getOriginalSourceFragment();
		runInContext(new SourceFragmentContextList(mutableTokenWriter, null, rootFragment.getChildrenFragments(), getChangeResolver()),
				() -> {
					for (CtType<?> t : types) {
						scan(t);
					}
				});
	}

	/**
	 * Is called for each printed token
	 * @param tokenWriterMethodName the name of {@link TokenWriter} method
	 * @param token the actual token value. It may be null for some `tokenWriterMethodName`
	 * @param comment the comment when `tokenWriterMethodName` == `writeComment`
	 * @param printAction the executor of the action, we are listening for.
	 */
	void onTokenWriterWrite(TokenType tokenType, String token, CtComment comment, Runnable printAction) {
		onPrintEvent(new TokenPrinterEvent(tokenType, token, comment) {
			@Override
			public void print(Boolean muted) {
				runInMutedState(muted, printAction);
			}
			@Override
			public void printSourceFragment(SourceFragment fragment, Boolean isModified) {
				boolean isCollectionStarted = false;
				if (fragment instanceof CollectionSourceFragment) {
					CollectionSourceFragment csf = (CollectionSourceFragment) fragment;
					//we started scanning of collection of elements
					SourceFragmentContext listContext = csf.isOrdered()
							? new SourceFragmentContextList(mutableTokenWriter, null, csf.getItems(), changeResolver)
							: new SourceFragmentContextSet(mutableTokenWriter, null, csf.getItems(), changeResolver);
					//push the context of this collection
					sourceFragmentContextStack.push(listContext);
					isCollectionStarted = true;
				}
				if (isModified == null || isModified) {
					//print origin token
					printAction.run();
					return;
				}
				mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
				if (isCollectionStarted) {
					mutableTokenWriter.setMuted(true);
				}
			}
		});
	}


	/**
	 * Called whenever {@link DefaultJavaPrettyPrinter} scans/prints an element
	 */
	@Override
	public SniperJavaPrettyPrinter scan(CtElement element) {
		if (element != null) {
			CtRole role = element.getRoleInParent();
			onPrintEvent(new ElementPrinterEvent(role, element) {
				@Override
				public void print(Boolean muted) {
					superScanInContext(element, SourceFragmentContextPrettyPrint.INSTANCE, muted);
				}
				@Override
				public void printSourceFragment(SourceFragment fragment, Boolean isModified) {
					scanInternal(role, element, fragment, isModified);
				}
			});
		}
		return this;
	}

	/**
	 * Called whenever {@link DefaultJavaPrettyPrinter} scans/prints an element
	 */
	private void onPrintEvent(PrinterEvent event) {
		SourceFragmentContext sfc = detectCurrentContext(event);
		if (sfc != null) {
			//there is an context let it handle scanning
			if (mutableTokenWriter.isMuted()) {
				//it is already muted by an parent. Simply scan and ignore all tokens,
				event.print(null);
				return;
			}
			sfc.onPrintEvent(event);
			return;
		}
		throw new SpoonException("Missing SourceFragmentContext");
	}

	private SourceFragmentContext detectCurrentContext(PrinterEvent event) {
		SourceFragmentContext sfc;
		while ((sfc = sourceFragmentContextStack.peek()) != null && sfc.matchesPrinterEvent(event) == false) {
			//this context handles only subset of roles, which just finished
			//leave it and return back to parent context
			sourceFragmentContextStack.pop();
			sfc.onFinished();
		}
		return sfc;
	}

	/**
	 * scans the `element` which exist on `role` in it's parent
	 * @param role
	 * @param element
	 * @param fragment
	 */
	private void scanInternal(CtRole role, CtElement element, SourceFragment fragment, Boolean isFragmentModified) {
		//TODO may be this IF is not needed
		if (mutableTokenWriter.isMuted()) {
			//it is already muted by an parent. Simply scan and ignore all tokens,
//			superScanInContext(element, SourceFragmentContextPrettyPrint.INSTANCE, true);
//			return;
			throw new SpoonException("is it needed ... TODO remove it");
		}
		//it is not muted yet, so this element or any sibling is modified
		if (fragment == null) {
			throw new SpoonException("Missing source fragment. Call PrintEvent#print instead.");
		}
		//we have sources of fragment
		if (fragment instanceof CollectionSourceFragment) {
			CollectionSourceFragment csf = (CollectionSourceFragment) fragment;
			//we started scanning of collection of elements
			SourceFragmentContext listContext = csf.isOrdered()
					? new SourceFragmentContextList(mutableTokenWriter, element, csf.getItems(), changeResolver)
					: new SourceFragmentContextSet(mutableTokenWriter, element, csf.getItems(), changeResolver);
			//push the context of this collection
			sourceFragmentContextStack.push(listContext);
			//and scan first element of that collection again in new context of that collection
			if (isFragmentModified == Boolean.FALSE) {
				mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
				//and mute the token writer and let DJPP scan it and ignore everything
				mutableTokenWriter.setMuted(true);
				//TODO check if DJPP needs this call somewhere (because of some state)... may be we can skip this scan completely??
				scan(element);
				//and keep it muted until SourceFragmentContextList is finished
			} else {
				scan(element);
			}
		} else if (fragment instanceof ElementSourceFragment) {
			ElementSourceFragment sourceFragment = (ElementSourceFragment) fragment;
			//it is fragment with single value
			ChangeResolver changeResolver = null;
			if (isFragmentModified == null) {
				changeResolver = new ChangeResolver(getChangeCollector(), element);
				isFragmentModified = changeResolver.hasChangedRole();
			}
			if (isFragmentModified == false) {
				//nothing is changed, we can print origin sources of this element
				mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
				//and mute the token writer and let DJPP scan it and ignore everything
				//TODO check if DJPP needs this call somewhere (because of some state)... may be we can skip this scan completely??
				superScanInContext(element, SourceFragmentContextPrettyPrint.INSTANCE, true);
				return;
			}
			//check what roles of this element are changed
			if (changeResolver == null) {
				changeResolver = new ChangeResolver(getChangeCollector(), element);
			}
			if (changeResolver.hasChangedRole() == false) {
				throw new SpoonException("The modified fragment has no changed roles?");
			}
			//something is changed in this element
			superScanInContext(element, new SourceFragmentContextNormal(mutableTokenWriter, element, sourceFragment, changeResolver), false);
		} else {
			throw new SpoonException("Unsupported fragment type: " + fragment.getClass());
		}
	}

	/**
	 * Call normal java printing in defined `context`
	 * @param element to be printed element
	 * @param context to be used context
	 * @param muted TODO
	 */
	private void superScanInContext(CtElement element, SourceFragmentContext context, Boolean muted) {
		runInContext(context,
				() -> runInMutedState(muted,
						() -> super.scan(element)));
	}
	private void runInContext(SourceFragmentContext context, Runnable code) {
		sourceFragmentContextStack.push(context);
		try {
			code.run();
		} finally {
			//remove `context` and all it's child contexts
			while (true) {
				if (sourceFragmentContextStack.isEmpty()) {
					throw new SpoonException("Inconsistent sourceFragmentContextStack");
				}
				SourceFragmentContext c = sourceFragmentContextStack.pop();
				c.onFinished();
				if (c == context) {
					break;
				}
			}
		}
	}
	private void runInMutedState(Boolean muted, Runnable code) {
		boolean originMuted = mutableTokenWriter.isMuted();
		if (muted == null) {
			muted = originMuted;
		}
		try {
			mutableTokenWriter.setMuted(muted);
			code.run();
		} finally {
			//assure that muted status did not changed in between
			if (mutableTokenWriter.isMuted() != muted) {
				if (mutableTokenWriter.isMuted()) {
					throw new SpoonException("Unexpected state: Token writer is muted after scanning");
				} else {
					throw new SpoonException("Unexpected state: Token writer is not muted after scanning");
				}
			}
			mutableTokenWriter.setMuted(originMuted);
		}
	}
}
