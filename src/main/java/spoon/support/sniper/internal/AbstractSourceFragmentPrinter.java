/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spoon.SpoonException;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.path.CtRole;

import static spoon.support.sniper.internal.ElementSourceFragment.findIndexOfNextFragment;
import static spoon.support.sniper.internal.ElementSourceFragment.filter;
import static spoon.support.sniper.internal.ElementSourceFragment.checkCollectionItems;
import static spoon.support.sniper.internal.ElementSourceFragment.isSpaceFragment;

/**
 *
 * The implementation of {@link SourceFragmentPrinter} that knows how to handle list of fragments.
 *
 * The method that concrete subclasses must implement is {@link #knowsHowToPrint(PrinterEvent)}
 * This method is key, it drives our the context of pushed out of the stask of printer in the sniper printer.
 */
abstract class AbstractSourceFragmentPrinter implements SourceFragmentPrinter {
	protected final MutableTokenWriter mutableTokenWriter;
	protected final List<SourceFragment> childFragments;
	protected final ChangeResolver changeResolver;
	//no child fragment is current at the beginning
	private int childFragmentIdx = -1;
	//this list of skipped tokens, which writes spaces and EOL.
	//If next element is in origin, then use origin separator actions and ignore this list
	//If next element is new, then run collected separator actions to print DJPP separators
	protected final List<Runnable> separatorActions = new ArrayList<>();

	protected AbstractSourceFragmentPrinter(MutableTokenWriter mutableTokenWriter, ChangeResolver changeResolver, List<SourceFragment> childFragments) {
		this.mutableTokenWriter = mutableTokenWriter;
		this.changeResolver = changeResolver;
		this.childFragments = childFragments;
	}

	@Override
	public void print(PrinterEvent event) {
		int prevIndex = childFragmentIdx;
		int index = update(event);
		if (index != -1) { // means we have found a source code fragment corresponding to this event

			// handling of spaces
			// hacky but works for now
			// TODO a refactoring of printSpaces would be better
			// but there are other bugs of higher priority now
			childFragmentIdx = prevIndex;
			printSpaces(index);
			childFragmentIdx = index;

			SourceFragment fragment = childFragments.get(index);
			event.printSourceFragment(fragment, isFragmentModified(fragment));
		}
	}

	@Override
	public int update(PrinterEvent event) {
		// case 1: we print a token
		if (event instanceof TokenPrinterEvent) {
			TokenPrinterEvent tpe = (TokenPrinterEvent) event;
			if (tpe.getType().isTab()) {
				//TODO may be we should send origin tabs from origin sources too?
				//to have printed elements aligned same like origin sources and not like DJPP would like it
				//but may be it is not good idea

				//send all inc/dec tab to printer helper to have configured expected indentation
				event.print();
				return -1;
			}
			if (tpe.getType().isWhiteSpace()) {
				//collect all DJPP separators for future use or ignore
				separatorActions.add(() -> event.print());
				return -1;
			}
		}
		int fragmentIndex = findIFragmentIndexCorrespondingToEvent(event);
		if (fragmentIndex < 0) {
			/*
			 * the token did not exist in origin sources. Print spaces made by DJPP
			 * It can happen e.g. when type parameter like &lt;T&gt; was added. Then bracket tokens are not in origin sources
			 */
			printSpaces(-1);
			event.print();
			return -1;
		}
		// case 2: it's an element printer
		//we have origin sources for this element
		if (event.getRole() == CtRole.COMMENT) {
			//note: DJPP sends comments in wrong order/wrong place.
			//so skip printing of this comment
			//comment will be printed at place where it belongs to - together with spaces
			return -1;
		}
		setChildFragmentIdx(fragmentIndex);
		return fragmentIndex;
	}

	@Override
	public void onFinished() {

	}

	/**
	 * Prints spaces before fragment with index `fragmentIndex`
	 * @param fragmentIndex index of fragment whose prefix spaces has to be printed or -1 if origin source fragment was not found
	 */
	protected void printSpaces(int fragmentIndex) {
		if (fragmentIndex < 0) {
			/*
			 * the token did not exist in origin sources. Print spaces made by DJPP
			 * It can happen e.g. when type parameter like &lt;T&gt; was added. Then bracket tokens are not in origin sources
			 */
			printStandardSpaces();
		} else {
			printOriginSpacesUntilFragmentIndex(fragmentIndex);
		}
	}

	/**
	 * @param fragment
	 * @return true if at least part of `fragment` is modified.
	 * 	false if whole `fragment` is not modified.
	 * 	null if it is not possible to detect it here. Then it will be detected later.
	 */
	protected Boolean isFragmentModified(SourceFragment fragment) {
		if (fragment instanceof TokenSourceFragment) {
			switch (((TokenSourceFragment) fragment).getType()) {
			//we do not know the role of the identifier token, so we do not know whether it is modified or not
			case IDENTIFIER:
				return null;
			case COMMENT:
				return null;
			default:
				//all others are constant tokens, which cannot be modified
				return Boolean.FALSE;
			}
		} else if (fragment instanceof ElementSourceFragment) {
			return changeResolver.isRoleModified(((ElementSourceFragment) fragment).getRoleInParent());
		} else if (fragment instanceof CollectionSourceFragment) {
			CollectionSourceFragment csf = (CollectionSourceFragment) fragment;
			for (SourceFragment sourceFragment : csf.getItems()) {
				Boolean modified = isFragmentModified(sourceFragment);
				if (!Boolean.FALSE.equals(modified)) {
					return modified;
				}
			}
			return Boolean.FALSE;
		} else {
			throw new SpoonException("Unexpected SourceFragment type " + fragment.getClass());
		}
	}

	/**
	 * Prints origin whitespaces including comments which prefixes the fragment on index `index`,
	 * starting with not yet processed spaces
	 * @param index of non white space fragment
	 */
	protected void printOriginSpacesUntilFragmentIndex(int index) {
		printOriginSpacesUntilFragmentIndex(childFragmentIdx + 1, index);
	}

	/**
	 * Prints origin whitespaces including comments which prefixes the fragment on index `index`,
	 * starting with fragment on `fromIndex`
	 * @param fromIndex index of first processed fragment
	 * @param toIndex index of first not processed fragment.
	 */
	protected void printOriginSpacesUntilFragmentIndex(int fromIndex, int toIndex) {
		//print all not yet printed comments which still exist in parent
		boolean canPrintSpace = true;
		boolean skipSpaceAfterDeletedElement = false;
		for (int i = fromIndex; i < toIndex; i++) {
			SourceFragment fragment = childFragments.get(i);
			if (fragment instanceof ElementSourceFragment) {
				ElementSourceFragment sourceFragment = (ElementSourceFragment) fragment;
				if (sourceFragment.getElement() instanceof CtComment) {
					CtComment comment = (CtComment) sourceFragment.getElement();
					if (changeResolver.isElementExists(comment)) {
						//comment still exist
						if (changeResolver.getChanges(comment).size() > 0) {
							//comment is modified print it normally
							mutableTokenWriter.writeComment(comment);
						} else {
							//comment is not modified write origin sources
							mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
						}
						//we printed the comment, so we can print next space too
						canPrintSpace = true;
					} else {
						//comment was remove
						//avoid printing of spaces between removed comments
						canPrintSpace = false;
					}
				}
			} else if (isSpaceFragment(fragment) && canPrintSpace) {
				if (!skipSpaceAfterDeletedElement) {
					mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
				} else {
					skipSpaceAfterDeletedElement = false;
				}
				//all whitespaces are in one fragment, so do not print next spaces without any comment in between
				canPrintSpace = false;
			} else {
				//there are some non empty tokens, which were not printed (were removed from model)
				//do not print next space, which represents the separator between removed tokens and next token
				skipSpaceAfterDeletedElement = true;
			}
		}
		setChildFragmentIdx(toIndex - 1);
		separatorActions.clear();
	}

	/**
	 * Remembers index of last processed fragment
	 * @param idx index of last processed fragment
	 */
	protected void setChildFragmentIdx(int idx) {
		childFragmentIdx = idx;
	}

	/**
	 * @return true if there is more child tokens to be processed.
	 * 	false if all tokens were processed
	 */
	protected boolean hasNextChildToken() {
		return childFragmentIdx + 1 < childFragments.size();
	}

	/**
	 * looks for next child token which contains exactly same token
	 * @param token String of searched token
	 * @return index of first same token or -1 if not found
	 */
	protected int findIndexOfNextChildTokenByValue(String token) {
		return findIndexOfNextFragment(childFragments, childFragmentIdx + 1, fragment -> Objects.equals(token, fragment.getSourceCode()));
	}
	/**
	 * looks for next child token which contains expected token type
	 * @param type {@link TokenType} of search token
	 * @return index of token with same type or -1 if not found
	 */
	protected int findIndexOfNextChildTokenByType(TokenType type) {
		return findIndexOfNextFragment(childFragments, childFragmentIdx + 1, filter(TokenSourceFragment.class, fragment -> type == fragment.getType()));
	}
	/**
	 * looks for next child token which has role `role`
	 * @param role {@link CtRole} of searched token
	 * @return index of first token with same role or -1 if not found
	 */
	protected int findIndexOfNextChildTokenOfRole(int start, CtRole role) {
		return findIndexOfNextFragment(childFragments, start, checkCollectionItems(filter(ElementSourceFragment.class, elementFragment -> elementFragment.getRoleInParent() == role)));
	}

	/**
	 * looks for next child token, which has element `element
	 * @param element element of searched token
	 * @return index of first token with same element or -1 if not found
	 */
	protected int findIndexOfNextChildTokenOfElement(SourcePositionHolder element) {
		return findIndexOfNextFragment(childFragments, childFragmentIdx + 1, checkCollectionItems(filter(ElementSourceFragment.class, elementFragment -> elementFragment.getElement() == element)));
	}

	/**
	 * Looks for the child token which fits to {@link PrinterEvent} `event`
	 * @param event {@link PrinterEvent} whose token it searches for
	 * @return index of first token which fits to {@link PrinterEvent} or -1 if not found
	 */
	protected int findIFragmentIndexCorrespondingToEvent(PrinterEvent event) {
		CtRole role = event.getRole();
		if (role != null) {
			if (role == CtRole.COMMENT) {
				return findIndexOfNextChildTokenOfElement(event.getElement());
			}
			return findIndexOfNextChildTokenOfRole(childFragmentIdx + 1, role);
		}
		if (event instanceof TokenPrinterEvent) {
			TokenPrinterEvent tpe = (TokenPrinterEvent) event;
			if (tpe.getType() == TokenType.IDENTIFIER) {
				return findIndexOfNextChildTokenByType(TokenType.IDENTIFIER);
			}
			return findIndexOfNextChildTokenByValue(tpe.getToken());
		} else {
			throw new SpoonException("Unexpected PrintEvent: " + event.getClass());
		}
	}

	/**
	 * print all tokens, which represents white spaces
	 * and then forget them, so we can collect next white spaces
	 */
	protected void printStandardSpaces() {
		for (Runnable runnable : separatorActions) {
			runnable.run();
		}
		separatorActions.clear();
	}

	@Override
	public void onPush() {
	}
}
