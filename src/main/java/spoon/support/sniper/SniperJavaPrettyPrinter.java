/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

import spoon.OutputType;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.TokenWriter;
import spoon.support.Experimental;
import spoon.support.modelobs.ChangeCollector;
import spoon.support.sniper.internal.ChangeResolver;
import spoon.support.sniper.internal.CollectionSourceFragment;
import spoon.support.sniper.internal.ElementPrinterEvent;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.sniper.internal.MutableTokenWriter;
import spoon.support.sniper.internal.PrinterEvent;
import spoon.support.sniper.internal.SourceFragment;
import spoon.support.sniper.internal.SourceFragmentContext;
import spoon.support.sniper.internal.SourceFragmentContextList;
import spoon.support.sniper.internal.SourceFragmentContextNormal;
import spoon.support.sniper.internal.SourceFragmentContextPrettyPrint;
import spoon.support.sniper.internal.SourceFragmentContextSet;
import spoon.support.sniper.internal.TokenPrinterEvent;
import spoon.support.sniper.internal.TokenType;
import spoon.support.sniper.internal.TokenWriterProxy;

/**
 * {@link PrettyPrinter} implementation, which copies as much as possible from the origin sources
 * and tries to only print the changed elements.
 */
@Experimental
public class SniperJavaPrettyPrinter extends DefaultJavaPrettyPrinter {

	private final MutableTokenWriter mutableTokenWriter;
	private ChangeResolver changeResolver;
	private final Deque<SourceFragmentContext> sourceFragmentContextStack = new ArrayDeque<>();

	/**
	 * Creates a new {@link PrettyPrinter} which copies origin sources and prints only changes.
	 */
	public SniperJavaPrettyPrinter(Environment env) {
		super(env);
		inlineElseIf = false;
		// required for sniper mode
		env.useTabulations(true);
		env.setCommentEnabled(true);
		env.setOutputType(OutputType.COMPILATION_UNITS);
		//create a TokenWriter which can be configured to ignore tokens coming from DJPP
		mutableTokenWriter = new MutableTokenWriter(env);
		//wrap that TokenWriter to listen on all incoming events and set wrapped version to DJPP
		setPrinterTokenWriter(createTokenWriterListener(mutableTokenWriter));
	}

	/**
	 * @return {@link ChangeCollector} registered in spoon environment
	 */
	private ChangeCollector getChangeCollector() {
		ChangeCollector changeCollector = ChangeCollector.getChangeCollector(env);
		if (changeCollector == null) {
			throw new SpoonException(ChangeCollector.class.getSimpleName() + " was not attached to the Environment");
		}
		return changeCollector;
	}

	/**
	 * @return new or cached instance of root ChangeResolver
	 */
	private ChangeResolver getChangeResolver() {
		if (changeResolver == null) {
			changeResolver = new ChangeResolver(getChangeCollector(), null);
		}
		return changeResolver;
	}

	/**
	 * wrap a `tokenWriter` by a proxy which intercepts all {@link TokenWriter} writeXxx(String) calls
	 * and calls {@link #onTokenWriterWrite(TokenType, String, CtComment, Runnable)}
	 * where {@link Runnable} can be used to invoke same event on the wrapped {@link TokenWriter}
	 * @param tokenWriter to be wrapped {@link TokenWriter}
	 * @return a proxy of {@link TokenWriter}
	 */
	private TokenWriter createTokenWriterListener(TokenWriter tokenWriter) {
		return new TokenWriterProxy(this::onTokenWriterWrite, tokenWriter);
	}

	@Override
	protected void scanCompilationUnit(CtCompilationUnit compilationUnit) {
		//use line separator of origin source file
		setLineSeparator(detectLineSeparator(compilationUnit.getOriginalSourceCode()));
		runInContext(new SourceFragmentContextList(mutableTokenWriter,
				compilationUnit,
				Collections.singletonList(compilationUnit.getOriginalSourceFragment()),
				new ChangeResolver(getChangeCollector(), compilationUnit)),
		() -> {
			super.scanCompilationUnit(compilationUnit);
		});
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

	/**
	 * Called for each printed token
	 * @param tokenType the type of {@link TokenWriter} method
	 * @param token the actual token value. It may be null for some `tokenWriterMethodName`
	 * @param comment the comment when `tokenType` == `COMMENT`
	 * @param printAction the executor of the action, we are listening for. Call it send token to output
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
							? new SourceFragmentContextList(mutableTokenWriter, null, csf.getItems(), getChangeResolver())
							: new SourceFragmentContextSet(mutableTokenWriter, null, csf.getItems(), getChangeResolver());
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



	private static boolean hasImplicitAncestor(CtElement el) {
		if (el == null) {
			return false;
		}
		if (el == el.getFactory().getModel().getRootPackage()) {
			return false;
		} else if (el.isImplicit()) {
			return true;
		} else {
			return hasImplicitAncestor(el.getParent());
		}
	}

	/**
	 * SniperPrettyPrinter does not apply preprocessor to a CtElement when calling toString()
	 * @param element
	 * @return
	 */
	@Override
	public String printElement(CtElement element) {
		if (element != null && !hasImplicitAncestor(element)) {
			CompilationUnit compilationUnit = element.getPosition().getCompilationUnit();
			if (compilationUnit != null
					&& !(compilationUnit instanceof NoSourcePosition.NullCompilationUnit)) {

				//use line separator of origin source file
				setLineSeparator(detectLineSeparator(compilationUnit.getOriginalSourceCode()));

				CtRole role = getRoleInCompilationUnit(element);
				ElementSourceFragment esf = element.getOriginalSourceFragment();

				runInContext(
					new SourceFragmentContextList(mutableTokenWriter,
						element,
						Collections.singletonList(esf),
						new ChangeResolver(getChangeCollector(), element)),
					() -> onPrintEvent(new ElementPrinterEvent(role, element) {
						@Override
						public void print(Boolean muted) {
							superScanInContext(element, SourceFragmentContextPrettyPrint.INSTANCE, muted);
						}

						@Override
						public void printSourceFragment(SourceFragment fragment, Boolean isModified) {
							scanInternal(role, element, fragment, isModified);
						}
					})
				);
			}
		}

		return toString().replaceFirst("^\\s+", "");
	}


	/**
	 * Called whenever {@link DefaultJavaPrettyPrinter} scans/prints an element
	 * Warning: DO not call on a cloned element. Use scanClone instead.
	 */
	@Override
	public SniperJavaPrettyPrinter scan(CtElement element) {
		if (element != null) {
			CtRole role = getRoleInCompilationUnit(element);
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

	private CtRole getRoleInCompilationUnit(CtElement element) {
		CtRole role = element.getRoleInParent();
		if (role == CtRole.CONTAINED_TYPE) {
			role = CtRole.DECLARED_TYPE;
		}
		return role;
	}

	/**
	 * Called whenever {@link DefaultJavaPrettyPrinter} scans/prints an element or writes a token
	 */
	private void onPrintEvent(PrinterEvent event) {
		SourceFragmentContext sfc = detectCurrentContext(event);
		if (sfc == null) {
			throw new SpoonException("Missing SourceFragmentContext");
		}
		//there is an context let it handle scanning
		if (mutableTokenWriter.isMuted()) {
			//it is already muted by an parent. Simply scan and ignore all tokens,
			event.print(null);
			return;
		}
		//let context handle the event
		sfc.onPrintEvent(event);
	}

	/**
	 * Checks if `event` can be still handled by the current context.
	 * If not then finish current context and enter parent context
	 * @param event a to be processed {@link PrinterEvent}
	 * @return context which can be used to process `event`
	 */
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
	 * @param role {@link CtRole} of `element` in scope of it's parent
	 * @param element a scanned element
	 * @param fragment origin source fragment of element
	 * @param isFragmentModified true if any part of `fragment` is modified, false if whole fragment is not modified, null if caller doesn't know
	 */
	private void scanInternal(CtRole role, CtElement element, SourceFragment fragment, Boolean isFragmentModified) {
		if (mutableTokenWriter.isMuted()) {
			throw new SpoonException("Unexpected state of sniper pretty printer. TokenWriter is muted.");
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
					? new SourceFragmentContextList(mutableTokenWriter, element, csf.getItems(), getChangeResolver())
					: new SourceFragmentContextSet(mutableTokenWriter, element, csf.getItems(), getChangeResolver());
			//push the context of this collection
			sourceFragmentContextStack.push(listContext);
			//and scan first element of that collection again in new context of that collection
			if (Boolean.FALSE.equals(isFragmentModified)) {
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
			//changeResolver.hasChangedRole() is false when element is added
			//something is changed in this element
			superScanInContext(element, new SourceFragmentContextNormal(mutableTokenWriter, sourceFragment, changeResolver), false);
		} else {
			throw new SpoonException("Unsupported fragment type: " + fragment.getClass());
		}
	}

	/**
	 * Call normal java printing in defined `context`
	 * @param element to be printed element
	 * @param context to be used context
	 * @param muted whether it has to be processed using
	 * 	true - muted,
	 * 	false - not muted
	 * 	null - same like before
	 */
	private void superScanInContext(CtElement element, SourceFragmentContext context, Boolean muted) {
		runInContext(context,
				() -> runInMutedState(muted,
						() -> super.scan(element)));
	}

	/**
	 * Run `code` in scope of `context`
	 * @param context a to be entered `context`
	 * @param code a to be processed {@link Runnable}
	 */
	private void runInContext(SourceFragmentContext context, Runnable code) {
		sourceFragmentContextStack.push(context);
		try {
			code.run();
		} finally {
			//remove `context` and all it's child contexts
			while (true) {
				if (sourceFragmentContextStack.isEmpty()) {
					throw new SpoonException("Inconsistent sourceFragmentContextStack"); //NOSONAR
				}
				SourceFragmentContext c = sourceFragmentContextStack.pop();
				c.onFinished();
				if (c == context) {
					break;
				}
			}
		}
	}
	/**
	 * Run code using {@link MutableTokenWriter} in defined state.
	 * After this function leaves, the muted status is restored.
	 * @param muted required muted status
	 * @param code to be processed {@link Runnable}
	 */
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
					throw new SpoonException("Unexpected state: Token writer is muted after scanning"); //NOSONAR
				} else {
					throw new SpoonException("Unexpected state: Token writer is not muted after scanning"); //NOSONAR
				}
			}
			mutableTokenWriter.setMuted(originMuted);
		}
	}
}
