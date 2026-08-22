/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
				// we must have a child fragment that contains this keyword as a standalone token
				for (SourceFragment f : childFragments) {
					if (containsKeywordToken(f.getSourceCode(), tpe.getToken())) {
						return true;
					}
				}
				return false;
			} else if (tpe.getType() == TokenType.IDENTIFIER) {
				return findIndexOfNextChildTokenByType(TokenType.IDENTIFIER) >= 0;
			} else if (tpe.getToken().equals(";") && anyChildFragmentHasRole(CtRole.TRY_RESOURCE)) {
				// We check for ; printed using printList in DJPP#visitCtTryWithResource because if we do not, the context
				// gets popped from the stack and printer messes up printing of the latter elements.
				// See: https://github.com/INRIA/spoon/pull/4309.
				return true;
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

	/**
	 * @return true if {@code keyword} occurs in {@code code} as a standalone keyword token, i.e. not
	 * as part of a larger identifier and not as a class-literal / qualified-name suffix such as
	 * {@code String.class}. A plain substring match is not enough: an annotation argument like
	 * {@code Foo.class} contains the substring {@code class}, which would make a (muted) annotation
	 * collection wrongly claim the {@code class} keyword of a following type declaration and cause the
	 * sniper printer to silently drop it ("@X class Foo" -> "@X Foo").
	 */
	private static boolean containsKeywordToken(String code, String keyword) {
		if (code == null) {
			return false;
		}
		int from = 0;
		while (true) {
			int idx = code.indexOf(keyword, from);
			if (idx < 0) {
				return false;
			}
			int before = idx - 1;
			int after = idx + keyword.length();
			// a keyword token is never part of a larger identifier, nor preceded by '.'
			// (the only keywords following '.' are the class/new/this/super of member expressions)
			boolean boundaryBefore = before < 0
					|| (!Character.isJavaIdentifierPart(code.charAt(before)) && code.charAt(before) != '.');
			boolean boundaryAfter = after >= code.length()
					|| !Character.isJavaIdentifierPart(code.charAt(after));
			if (boundaryBefore && boundaryAfter) {
				return true;
			}
			from = idx + 1;
		}
	}

	private boolean anyChildFragmentHasRole(CtRole role) {
		return childFragments.stream()
				.filter(ElementSourceFragment.class::isInstance)
				.map(ElementSourceFragment.class::cast)
				.map(ElementSourceFragment::getRoleInParent)
				.map(role::equals)
				.findAny()
				.orElse(false);
	}

	@Override
	protected ModificationStatus isFragmentModified(SourceFragment fragment) {
		//we cannot fast detect if it is modified using our changeResolver.
		// The code later will detect it including modified roles.
		return ModificationStatus.UNKNOWN;
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
