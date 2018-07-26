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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spoon.SpoonException;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.printer.internal.SourceFragment;

import static spoon.reflect.visitor.printer.internal.ElementSourceFragment.findIndexOfNextFragment;
import static spoon.reflect.visitor.printer.internal.ElementSourceFragment.filter;
import static spoon.reflect.visitor.printer.internal.ElementSourceFragment.checkCollectionItems;
import static spoon.reflect.visitor.printer.internal.ElementSourceFragment.isSpaceFragment;

/**
 * Knows how to handle actually printed {@link CtElement} or it's part
 */
abstract class AbstractSourceFragmentContext implements SourceFragmentContext {
	protected final MutableTokenWriter mutableTokenWriter;
	protected final List<SourceFragment> childFragments;
	protected final ChangeResolver changeResolver;
	//no child fragment is current at the beginning
	private int childFragmentIdx = -1;
	//this list of skipped tokens, which writes spaces and EOL.
	//If next element is in origin, then use origin separator actions and ignore this list
	//If next element is new, then run collected separator actions to print DJPP separators
	protected final List<Runnable> separatorActions = new ArrayList<>();

	protected AbstractSourceFragmentContext(MutableTokenWriter mutableTokenWriter, ChangeResolver changeResolver, List<SourceFragment> childFragments) {
		this.mutableTokenWriter = mutableTokenWriter;
		this.changeResolver = changeResolver;
		this.childFragments = childFragments;
	}

	@Override
	public void onPrintEvent(PrinterEvent event) {
		if (event instanceof TokenPrinterEvent) {
			TokenPrinterEvent tpe = (TokenPrinterEvent) event;
			if (tpe.getType().isTab()) {
				//TODO may be we should send origin tabs from origin sources too?
				//to have printed elements aligned same like origin sources and not like DJPP would like it
				//but may be it is not good idea

				//send all inc/dec tab to printer helper to have configured expected indentation
				event.print(null);
				return;
			}
			if (tpe.getType().isWhiteSpace()) {
				//collect all DJPP separators for future use or ignore
				separatorActions.add(() -> event.print(false));
				return;
			}
		}
		int fragmentIndex = findIndexOfNextChildTokenOfEvent(event);
		if (fragmentIndex < 0) {
			/*
			 * the token did not exist in origin sources. Print spaces made by DJPP
			 * It can happen e.g. when type parameter like &lt;T&gt; was added. Then bracket tokens are not in origin sources
			 */
			printSpaces(-1);
			event.print(false);
			return;
		}
		//we have origin sources for this element
		if (event.getRole() == CtRole.COMMENT) {
			//else print comment at place where it belongs to - together with spaces
			//note: DJPP sends comments in wrong order/wrong place.
			//so skip printing of this comment
			return;
		}
		onPrintFoundFragment(event, fragmentIndex);
	}

	protected void onPrintFoundFragment(PrinterEvent event, int fragmentIndex) {
		printSpaces(fragmentIndex);
		setChildFragmentIdx(fragmentIndex);
		SourceFragment fragment = childFragments.get(fragmentIndex);
		event.printSourceFragment(fragment, isFragmentModified(fragment));
	}

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
				if (modified != Boolean.FALSE) {
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
	 * @param index of non white space fragment
	 */
	protected void printOriginSpacesUntilFragmentIndex(int index) {
		printOriginSpacesUntilFragmentIndex(childFragmentIdx + 1, index);
	}
	protected void printOriginSpacesUntilFragmentIndex(int fromIndex, int toIndex) {
		//print all not yet printed comments which still exist in parent
		boolean canPrintSpace = true;
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
				mutableTokenWriter.getPrinterHelper().directPrint(fragment.getSourceCode());
				//all whitespaces are in one fragment, so do not print next spaces without any comment in between
				canPrintSpace = false;
			}
		}
		setChildFragmentIdx(toIndex - 1);
		separatorActions.clear();
	}

	protected void setChildFragmentIdx(int idx) {
		childFragmentIdx = idx;
	}

	/**
	 * looks next child token which contains exactly same token
	 * @param token
	 * @return index of same token or -1 if not found
	 */
	protected int findIndexOfNextChildTokenByValue(String token) {
		return findIndexOfNextFragment(childFragments, childFragmentIdx + 1, fragment -> Objects.equals(token, fragment.getSourceCode()));
	}
	/**
	 * looks next child token which contains expected token type
	 * @param token
	 * @return index of same token or -1 if not found
	 */
	protected int findIndexOfNextChildTokenByType(TokenType type) {
		return findIndexOfNextFragment(childFragments, childFragmentIdx + 1, filter(TokenSourceFragment.class, fragment -> type == fragment.getType()));
	}
	/**
	 * looks next child token which has role `role`
	 * @param role
	 * @return index of same token or -1 if not found
	 */
	protected int findIndexOfNextChildTokenOfRole(int start, CtRole role) {
		return findIndexOfNextFragment(childFragments, start, checkCollectionItems(filter(ElementSourceFragment.class, elementFragment -> elementFragment.getRoleInParent() == role)));
	}

	/**
	 * looks next child token which has element `element
	 * @param element
	 * @return index of same token or -1 if not found
	 */
	protected int findIndexOfNextChildTokenOfElement(SourcePositionHolder element) {
		return findIndexOfNextFragment(childFragments, childFragmentIdx + 1, checkCollectionItems(filter(ElementSourceFragment.class, elementFragment -> elementFragment.getElement() == element)));
	}

	protected int findIndexOfNextChildTokenOfEvent(PrinterEvent event) {
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
			return findIndexOfNextChildTokenByValue(event.getToken());
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
}
