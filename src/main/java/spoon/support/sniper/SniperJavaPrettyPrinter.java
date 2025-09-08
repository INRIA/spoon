/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.TokenWriter;
import spoon.support.Experimental;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.modelobs.ChangeCollector;
import spoon.support.reflect.declaration.CtCompilationUnitImpl;
import spoon.support.sniper.internal.ChangeResolver;
import spoon.support.sniper.internal.CollectionSourceFragment;
import spoon.support.sniper.internal.ElementPrinterEvent;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.sniper.internal.IndentationDetector;
import spoon.support.sniper.internal.ModificationStatus;
import spoon.support.sniper.internal.MutableTokenWriter;
import spoon.support.sniper.internal.PrinterEvent;
import spoon.support.sniper.internal.SourceFragment;
import spoon.support.sniper.internal.SourceFragmentPrinter;
import spoon.support.sniper.internal.SourceFragmentContextList;
import spoon.support.sniper.internal.SourceFragmentContextNormal;
import spoon.support.sniper.internal.DefaultSourceFragmentPrinter;
import spoon.support.sniper.internal.TokenPrinterEvent;
import spoon.support.sniper.internal.TokenType;
import spoon.support.sniper.internal.TokenWriterProxy;
import spoon.support.util.ModelList;

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

		// don't print redundant parentheses
		this.setMinimizeRoundBrackets(true);
	}

	/**
	 * @return {@link ChangeCollector} registered in spoon environment
	 */
	private ChangeCollector getChangeCollector() {
		ChangeCollector changeCollector = ChangeCollector.getChangeCollector(env);
		if (changeCollector == null) {
			throw new SpoonException(ChangeCollector.class.getSimpleName() + " was not attached to the Environment. "
					+ "This typically means that the Sniper printer was set after building the model. "
					+ "It must be set before building the model.");
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
	public String printTypes(CtType<?>... type) {
		CtCompilationUnit cu = getUnambiguousCompilationUnit(type);
		calculate(cu, Arrays.asList(type));
		return getResult();
	}

	private static CtCompilationUnit getUnambiguousCompilationUnit(CtType<?>[] type) {
		CtCompilationUnit sentinel = new CtCompilationUnitImpl();
		return Arrays.stream(type)
				.map(ctType -> (CtCompilationUnit) ctType.getFactory().CompilationUnit().getOrCreate(ctType))
				.reduce((prev, next) -> prev == next ? next : sentinel)
				.filter(unit -> unit != sentinel)
				.orElseThrow(() -> new IllegalArgumentException("mismatching or missing compilation unit"));
	}

	@Override
	public void calculate(CtCompilationUnit compilationUnit, List<CtType<?>> types) {
		checkGivenTypesMatchDeclaredTypes(compilationUnit, types);

		sourceCompilationUnit = compilationUnit;

		//use line separator of origin source file
		setLineSeparator(detectLineSeparator(compilationUnit.getOriginalSourceCode()));

		// use indentation style of origin source file for new elements
		Pair<Integer, Boolean> indentationInfo = IndentationDetector.detectIndentation(compilationUnit);
		mutableTokenWriter.setOriginSourceTabulationSize(indentationInfo.getLeft());
		mutableTokenWriter.setOriginSourceUsesTabulations(indentationInfo.getRight());

		runInContext(new SourceFragmentContextList(mutableTokenWriter,
				compilationUnit,
				Collections.singletonList(compilationUnit.getOriginalSourceFragment()),
				new ChangeResolver(getChangeCollector(), compilationUnit)),
		() -> {
			super.calculate(sourceCompilationUnit, types);;
		});
	}

	/** Throws an {@link IllegalArgumentException} if the given types do not exactly match the types of the CU. */
	private static void checkGivenTypesMatchDeclaredTypes(CtCompilationUnit cu, List<CtType<?>> types) {
		Set<CtType<?>> givenTypes = toIdentityHashSet(types);
		Set<CtType<?>> declaredTypes = toIdentityHashSet(cu.getDeclaredTypes());
		if (!givenTypes.equals(declaredTypes)) {
			throw new IllegalArgumentException(
					"Can only sniper print exactly all declared types of the compilation unit. Given types: "
							+ toNameList(givenTypes) + ". Declared types: " + toNameList(declaredTypes));
		}
	}

	private static List<String> toNameList(Collection<CtType<?>> types) {
		return types.stream().map(CtType::getQualifiedName).collect(Collectors.toList());
	}

	private static <T> Set<T> toIdentityHashSet(Collection<T> items) {
		Set<T> idHashSet = Collections.newSetFromMap(new IdentityHashMap<>());
		idHashSet.addAll(items);
		return idHashSet;
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
			public void printSourceFragment(SourceFragment fragment, ModificationStatus isModified) {
				if (isModified == ModificationStatus.UNKNOWN || isModified == ModificationStatus.MODIFIED) {
					printAction.run();
					return;
				} else {
					if (fragment instanceof CollectionSourceFragment) {
						//we started scanning of collection of elements
						SourceFragmentPrinter listContext = getCollectionContext(null, (CollectionSourceFragment) fragment, isModified.toBoolean());
						// we need to update the cursor (childFragmentIdx) with the current token
						listContext.update(this);
						//push the context of this collection
						pushContext(listContext);
					}
					mutableTokenWriter.directPrint(getSourceCodeForSniperPrinting(fragment));
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

				ElementSourceFragment esf = element.getOriginalSourceFragment();

				runInContext(
					new SourceFragmentContextList(mutableTokenWriter,
						element,
						Collections.singletonList(esf),
						new ChangeResolver(getChangeCollector(), element)),
					() -> executePrintEventInContext(createPrinterEvent(element))
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
			executePrintEventInContext(createPrinterEvent(element));
		}
		return this;
	}

	private PrinterEvent createPrinterEvent(CtElement element) {
		CtRole role = getRoleInCompilationUnit(element);
		return new ElementPrinterEvent(role, element) {

			@Override
			public void printSourceFragment(SourceFragment fragment, ModificationStatus isModified) {

				// we don't have any source fragment for this element, so we simply pretty-print it normally
				if (fragment == null) {
					superScanInContext(this.element, DefaultSourceFragmentPrinter.INSTANCE);
					return;
				} else if (fragment instanceof CollectionSourceFragment) {
					//we started scanning of collection of elements
					SourceFragmentPrinter listContext = getCollectionContext(this.element, (CollectionSourceFragment) fragment, isModified.toBoolean());
					//push the context of this collection
					pushContext(listContext);


					//and scan first element of that collection again in new context of that collection
					if (ModificationStatus.NOT_MODIFIED.equals(isModified)) {
						// we print the original source code
						mutableTokenWriter.directPrint(getSourceCodeForSniperPrinting(fragment));
					} else {
						// we print with the new list context
						listContext.print(this);
					}
				} else if (fragment instanceof ElementSourceFragment) {
					ElementSourceFragment sourceFragment = (ElementSourceFragment) fragment;
					if (isModified == ModificationStatus.NOT_MODIFIED) {
						//nothing is changed, we can print origin sources of this element
						mutableTokenWriter.directPrint(getSourceCodeForSniperPrinting(fragment));
						return;
					}

					//something is changed in this element, so we pretty-print it normally
					superScanInContext(this.element, new SourceFragmentContextNormal(mutableTokenWriter, sourceFragment, new ChangeResolver(getChangeCollector(), this.element)));
				} else {
					throw new SpoonException("Unsupported fragment type: " + fragment.getClass());
				}
			}
		};
	}

	/**
	 * Gets modified source code for pretty-printing when using the SniperJavaPrettyPrinter.
	 *
	 * This function handles some custom logic which might be needed for special types of code-fragments.
	 * For instance, we by default add parenthesis around the type while type-casting. The source code for the cast
	 * has parenthesis around it as well (which would be required for the original code to be valid Java code -- the
	 * assumption here is that the fragment supplied comes from valid/compilable Java code).
	 * Printing that source-code without any modification would then lead to non-compilable java code. This function
	 * can handle such special cases.
	 * */
	private String getSourceCodeForSniperPrinting(SourceFragment fragment) {
		if (fragment instanceof ElementSourceFragment elementFragment) {
			if (elementFragment.getRoleInParent() == CtRole.CAST) {
				return elementFragment.getSourceCode(elementFragment.getStart() + 1, elementFragment.getEnd() - 1);
			}
			return elementFragment.getSourceCode();
		} else if (fragment instanceof CollectionSourceFragment collectionSourceFragment) {
			StringBuilder sb = new StringBuilder();
			for (SourceFragment childSourceFragment : collectionSourceFragment.getItems()) {
				sb.append(getSourceCodeForSniperPrinting(childSourceFragment));
			}
			return sb.toString();
		} else {
			// We have a TokenSourceFragment.
			return fragment.getSourceCode();
		}
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
		while ((sfc = sourceFragmentContextStack.peek()) != null && !sfc.knowsHowToPrint(event)) {
			//this context handles only subset of roles, which just finished
			//leave it and return back to parent context
			sfc = popSourceFragmentContext();
		}
		return sfc;
	}

	private SourceFragmentPrinter getCollectionContext(CtElement element, CollectionSourceFragment csf, boolean isModified) {
		return new SourceFragmentContextList(mutableTokenWriter, element, csf.getItems(), getChangeResolver()) {
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

	// fix #3267
	// in sniper mode, we must visit the order in the same order
	// as the source code
	protected ModelList<CtImport> getImports(CtCompilationUnit compilationUnit) {
		ModelList<CtImport> imports = super.getImports(compilationUnit);
		imports.sort(new CtLineElementComparator());
		return imports;
	}

}
