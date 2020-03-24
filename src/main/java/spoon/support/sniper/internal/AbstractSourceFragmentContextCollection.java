/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import java.util.List;

import spoon.SpoonException;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.path.CtRole;

import static spoon.support.sniper.internal.ElementSourceFragment.isSpaceFragment;

/**
 * There are these types of collections: ordered or unordered and with separator and without separator
 * `ordered` collections have order of items defined by spoon model
 * `unordered` collection have order of items defined by some other internal rules.
 *   This order can be completely different to order of items in origin sources
 *
 * `with separator` collections have items separated by a separator. For example by a comma `,`
 * `without separator` collections have items separated by a whitespace char(s) only.
 */
abstract class AbstractSourceFragmentContextCollection extends AbstractSourceFragmentPrinter {

	protected AbstractSourceFragmentContextCollection(MutableTokenWriter mutableTokenWriter, List<SourceFragment> fragments, ChangeResolver changeResolver) {
		super(mutableTokenWriter, changeResolver, fragments);
	}

	@Override
	public boolean knowsHowToPrint(PrinterEvent event) {
		if (!hasNextChildToken()) {
			//there are no more tokens to process. Leave this context
			return false;
		}

		if (event instanceof TokenPrinterEvent) {
			TokenPrinterEvent tpe = (TokenPrinterEvent) event;
			if (tpe.isWhitespace()) {
				return true;
			}
			if (TokenType.KEYWORD.equals(tpe.getTokenType())) {
// we must have its fragment
				for (SourceFragment f : childFragments) {
					if (f.getSourceCode().contains(tpe.getToken())) {
						return true;
					}
				}
				return false;
			} else if (tpe.getType() == TokenType.IDENTIFIER) {
				return findIndexOfNextChildTokenByType(TokenType.IDENTIFIER) >= 0;
			}
			return findIndexOfNextChildTokenByValue(tpe.getToken()) >= 0;
		}
		CtRole role = event.getRole();
		if (role == CtRole.COMMENT) {
			return true;
		}
		if (role != null) {
			//the collection context accepts event as long as it is for same role
			return findIndexOfNextChildTokenOfRole(0, role) >= 0;
		} else if (event.getElement() instanceof CtCompilationUnit) {
			return findIndexOfNextChildTokenOfElement(event.getElement()) >= 0;
		}
		throw new SpoonException("Unexpected PrintEvent: " + event.getClass());
	}

	@Override
	protected Boolean isFragmentModified(SourceFragment fragment) {
		//we cannot fast detect if it is modified using our changeResolver.
		//So return null. The code later will detect it including modified roles.
		return null;
	}

	/**
	 * @return the suffix whitespaces at the end of collection of elements
	 */
	protected String getSuffixSpace() {
		if (childFragments.size() > 0) {
			SourceFragment item = childFragments.get(childFragments.size() - 1);
			if (isSpaceFragment(item)) {
				return item.getSourceCode();
			}
		}
		return null;
	}

	@Override
	public void onFinished() {
		//print list suffix
		String suffix = getSuffixSpace();
		if (suffix != null) {
			//we have origin source code for that list suffix
			mutableTokenWriter.writeCodeSnippet(suffix);
		}
	}
}
