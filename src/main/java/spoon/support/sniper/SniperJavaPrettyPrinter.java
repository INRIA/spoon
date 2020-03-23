/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import spoon.OutputType;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
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
import spoon.support.sniper.internal.SourceFragmentPrinter;
import spoon.support.sniper.internal.SourceFragmentContextList;
import spoon.support.sniper.internal.SourceFragmentContextNormal;
import spoon.support.sniper.internal.DefaultSourceFragmentPrinter;
import spoon.support.sniper.internal.SourceFragmentContextSet;
import spoon.support.sniper.internal.TokenPrinterEvent;
import spoon.support.sniper.internal.TokenType;
import spoon.support.sniper.internal.TokenWriterProxy;

/**
 * {@link PrettyPrinter} implementation, which copies as much as possible from the origin sources
 * and tries to only print the changed elements.
 */
@Experimental
public class SniperJavaPrettyPrinter extends DefaultJavaPrettyPrinter implements TokenWriterProxy.Listener {

	private final MutableTokenWriter mutableTokenWriter;
	private ChangeResolver changeResolver;
	private final Deque<SourceFragmentPrinter> sourceFragmentContextStack = new ArrayDeque<>();

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

		// newly added elements are not fully qualified
		this.setIgnoreImplicit(false);
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
		return new TokenWriterProxy(this, tokenWriter);
	}

	@Override
	public void calculate(CtCompilationUnit compilationUnit, List<CtType<?>> types) {
		//use line separator of origin source file
		setLineSeparator(detectLineSeparator(compilationUnit.getOriginalSourceCode()));
		runInContext(new SourceFragmentContextList(mutableTokenWriter,
				compilationUnit,
				Collections.singletonList(compilationUnit.getOriginalSourceFragment()),
				new ChangeResolver(getChangeCollector(), compilationUnit)),
		() -> {
			super.calculate(sourceCompilationUnit, types);;
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
	public void onTokenWriterWrite(TokenType tokenType, String token, CtComment comment, Runnable printAction) {
		executePrintEventInContext(new TokenPrinterEvent(tokenType, token, comment) {
			@Override
			public void print() {
				printAction.run();
			}
			@Override
			public void printSourceFragment(SourceFragment fragment, Boolean isModified) {
				if (isModified == null || isModified) {
					printAction.run();
					return;
				} else {
					if (fragment instanceof CollectionSourceFragment) {
						//we started scanning of collection of elements
						SourceFragmentPrinter listContext = getCollectionContext(null, (CollectionSourceFragment) fragment, isModified);
						// we need to update the cursor (childFragmentIdx) with the current token
						listContext.update(this);
						//push the context of this collection
						pushContext(listContext);
					}
					mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
				}
			}
		});
	}

	private void pushContext(SourceFragmentPrinter listContext) {
		listContext.onPush();
		sourceFragmentContextStack.push(listContext);
	}

	/** Warning, not in the API, public for testing purposes */
	public static boolean hasImplicitAncestor(CtElement el) {
		if (el == null || !el.isParentInitialized()) {
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
	 * The sniper mode only works from JavaOutputProcessor
	 */
	@Override
	public String printElement(CtElement element) {
		return element.toStringDebug();
	}

	/** Warning: debug and test method only, not part of the public API */
	public String printElementSniper(CtElement element) {
		reset();
		if (!hasImplicitAncestor(element)) {
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
					() -> executePrintEventInContext(createPrinterEvent(element, role))
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
			executePrintEventInContext(createPrinterEvent(element, role));
		}
		return this;
	}

	private PrinterEvent createPrinterEvent(CtElement element, CtRole role) {
		return new ElementPrinterEvent(role, element) {
			@Override
			public void print() {
				superScanInContext(element, DefaultSourceFragmentPrinter.INSTANCE);
			}

			@Override
			public void printSourceFragment(SourceFragment fragment, Boolean isModified) {
				scanInternal(role, element, fragment, isModified);
			}
		};
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
	private void executePrintEventInContext(PrinterEvent event) {
		SourceFragmentPrinter sfc = detectCurrentContext(event);
		if (sfc == null) {
			throw new SpoonException("Missing SourceFragmentContext");
		}
		if (mutableTokenWriter.isMuted()) {
			// the printer may require to update its state based on this event
			sfc.update(event);
			return;
		}
		// the context-dependent printer handles the event
		sfc.print(event);
	}

	/**
	 * Checks if `event` can be still handled by the current context.
	 * If not then finish current context and enter parent context
	 * @param event a to be processed {@link PrinterEvent}
	 * @return context which can be used to process `event`
	 */
	private SourceFragmentPrinter detectCurrentContext(PrinterEvent event) {
		SourceFragmentPrinter sfc;
		while ((sfc = sourceFragmentContextStack.peek()) != null && sfc.knowsHowToPrint(event) == false) {
			//this context handles only subset of roles, which just finished
			//leave it and return back to parent context
			sfc = popSourceFragmentContext();
		}
		return sfc;
	}

	/**
	 * scans the `element` which exist on `role` in its parent
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
			//we started scanning of collection of elements
			SourceFragmentPrinter listContext = getCollectionContext(element, (CollectionSourceFragment) fragment, isFragmentModified);
			//push the context of this collection
			pushContext(listContext);


			//and scan first element of that collection again in new context of that collection
			if (Boolean.FALSE.equals(isFragmentModified)) {
				// we print the original source code
				mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
			} else {
				// we print it normally
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
				return;
			}
			//check what roles of this element are changed
			if (changeResolver == null) {
				changeResolver = new ChangeResolver(getChangeCollector(), element);
			}
			//changeResolver.hasChangedRole() is false when element is added
			//something is changed in this element
			superScanInContext(element, new SourceFragmentContextNormal(mutableTokenWriter, sourceFragment, changeResolver));
		} else {
			throw new SpoonException("Unsupported fragment type: " + fragment.getClass());
		}
	}

	private SourceFragmentPrinter getCollectionContext(CtElement element, CollectionSourceFragment csf, boolean isModified) {
		return csf.isOrdered()
				? new SourceFragmentContextList(mutableTokenWriter, element, csf.getItems(), getChangeResolver()) {
			@Override
			public void onPush() {
				super.onPush();
				if (!isModified) {
					mutableTokenWriter.setMuted(true);
				}
			}

			@Override
			public void onFinished() {
				super.onFinished();
				if (!isModified) {
					mutableTokenWriter.setMuted(false);
				}
			}

		}
				: new SourceFragmentContextSet(mutableTokenWriter, element, csf.getItems(), getChangeResolver()) {
			@Override
			public void onPush() {
				super.onPush();
				if (!isModified) {
					mutableTokenWriter.setMuted(true);
				}
			}

			@Override
			public void onFinished() {
				super.onFinished();
				if (!isModified) {
					mutableTokenWriter.setMuted(false);
				}
			}
		};
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
	private void superScanInContext(CtElement element, SourceFragmentPrinter context) {
			runInContext(context, () -> super.scan(element));
	}

	/**
	 * Run `code` in scope of `context`
	 * @param context a to be entered `context`
	 * @param code a to be processed {@link Runnable}
	 */
	private void runInContext(SourceFragmentPrinter context, Runnable code) {
		pushContext(context);
		try {
			code.run();
		} finally {
			// we make sure to remove all contexts that have been pushed so far
			// and we also remove parameter `context`
			// so that we can leave the sourceFragmentContextStack clean
			while (true) {
				if (sourceFragmentContextStack.isEmpty()) {
					throw new SpoonException("Inconsistent sourceFragmentContextStack"); //NOSONAR
				}
				SourceFragmentPrinter c = popSourceFragmentContext();
				if (c == context) {
					break;
				}
			}
		}
	}

	/** makes the two atomic operations together pop+finish to maintain core contracts */
	private SourceFragmentPrinter popSourceFragmentContext() {
		SourceFragmentPrinter c = sourceFragmentContextStack.pop();
		c.onFinished();
		return c;
	}
}
