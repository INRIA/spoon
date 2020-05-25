/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.SpoonException;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.support.Experimental;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.cu.position.SourcePositionImpl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a part of source code of an {@link CtElement}
 * It is connected into a tree of {@link ElementSourceFragment}s.
 * Use {@link SourcePositionHolder#getOriginalSourceFragment()} to get it.
 */
@Experimental
public class ElementSourceFragment implements SourceFragment {

	/**
	 * represents an {@link ElementSourceFragment}, which doesn't exist
	 */
	public static final ElementSourceFragment NO_SOURCE_FRAGMENT = new ElementSourceFragment(null, null);

	private final SourcePositionHolder element;
	private final RoleHandler roleHandlerInParent;
	private ElementSourceFragment nextSibling;
	private ElementSourceFragment firstChild;

	/**
	 * Creates a source fragment of {@link SourcePositionHolder}
	 *
	 * @param element target {@link SourcePositionHolder}
	 * @param roleHandlerInParent The {@link RoleHandler}, which defines role of target `element` in it's parent
	 */
	public ElementSourceFragment(SourcePositionHolder element, RoleHandler roleHandlerInParent) {
		this.element = element;
		this.roleHandlerInParent = roleHandlerInParent;
	}

	/**
	 * @return offset of first character which belongs to this fragment
	 */
	public int getStart() {
		if (firstChild != null) {
			return Math.min(getSourcePosition().getSourceStart(), firstChild.getStart());
		}
		return getSourcePosition().getSourceStart();
	}

	/**
	 * @return offset of character after this fragment
	 */
	public int getEnd() {
		if (firstChild != null) {
			return Math.max(getSourcePosition().getSourceEnd() + 1, firstChild.getLastSibling().getEnd());
		}
		return getSourcePosition().getSourceEnd() + 1;
	}

	/**
	 * @return {@link SourcePosition} of this fragment
	 */
	public SourcePosition getSourcePosition() {
		return element.getPosition();
	}

	@Override
	public String toString() {
		String result = "|" + getStart() + ", " + getEnd() + "|" + getSourceCode() + "|";

		if (element instanceof CtElement) {
			// enrich the toString to facilitate debug
			return ((CtElement) element).toStringDebug() + result;
		}

		return result;
	}

	/**
	 * @return origin source code of this fragment
	 */
	@Override
	public String getSourceCode() {
		return getSourceCode(getStart(), getEnd());
	}

	/**
	 * @param start start offset relative to compilation unit
	 * @param end end offset (after last character) relative to compilation unit
	 * @return source code of this Fragment between start/end offsets
	 */
	public String getSourceCode(int start, int end) {
		String src = getOriginalSourceCode();
		if (src != null) {
			return src.substring(start, end);
		}
		return null;
	}

	/**
	 * @return true if position points to same compilation unit (source file) as this SourceFragment
	 */
	private boolean isFromSameSource(SourcePosition position) {
		return getSourcePosition().getCompilationUnit().equals(position.getCompilationUnit());
	}

	/**
	 * Builds tree of {@link SourcePosition}s of `element` and all it's children
	 * @param element the root element of the tree
	 */
	public void addTreeOfSourceFragmentsOfElement(CtElement element) {
		Deque<ElementSourceFragment> parents = new ArrayDeque<>();
		parents.push(this);
		/*
		 * scan all children of `element` and build tree of SourceFragments
		 * Note: we cannot skip implicit elements,
		 * because CtBlock can be implicit but contains non implicit elements, which has to be processed.
		 */
		new EarlyTerminatingScanner<Void>() {
			@Override
			public <T> void visitCtFieldReference(final CtFieldReference<T> reference) {
				// bug 3133: we must must not visit the type of a field reference
				enter(reference);
				scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
				scan(CtRole.ANNOTATION, reference.getAnnotations());
				exit(reference);
			}

			@Override
			public <T> void visitCtLocalVariableReference(final CtLocalVariableReference<T> reference) {
				// bug 3154: we must must not visit the type of a local var reference
				enter(reference);
				exit(reference);
			}

			@Override
			protected void enter(CtElement e) {
				if (e instanceof CtCompilationUnit) {
					return;
				}
				ElementSourceFragment newFragment = addChild(parents.peek(), scannedRole, e);
				if (newFragment != null) {
					parents.push(newFragment);
					if (e instanceof CtModifiable) {
						CtModifiable modifiable = (CtModifiable) e;
						Set<CtExtendedModifier> modifiers = modifiable.getExtendedModifiers();
						for (CtExtendedModifier ctExtendedModifier : modifiers) {
							addChild(newFragment, CtRole.MODIFIER, ctExtendedModifier);
						}
					}
				} else {
					// if this happens, this means that some fragments are wrong
					// and we'll get intro trouble later
					// see bugs 3133 and 3154, crashing in addChild
				}
			}
			@Override
			protected void exit(CtElement e) {
				if (e instanceof CtCompilationUnit) {
					return;
				}
				ElementSourceFragment topFragment = parents.peek();
				if (topFragment != null && topFragment.getElement() == e) {
					parents.pop();
				}
			}
		}
		.setVisitCompilationUnitContent(true)
		.scan(element.getRoleInParent(), element);
	}
	/**
	 * @param parentFragment the parent {@link ElementSourceFragment}, which will receive {@link ElementSourceFragment} made for `otherElement`
	 * @param roleInParent the {@link CtRole} of `otherElement` in scope of element of `parentFragment`
	 * @param otherElement {@link SourcePositionHolder} whose {@link ElementSourceFragment} has to be added to `parentFragment`
	 * @return new {@link ElementSourceFragment} created for `otherElement` or null if `otherElement` has no source position or doesn't belong to the same compilation unit
	 */
	private ElementSourceFragment addChild(ElementSourceFragment parentFragment, CtRole roleInParent, SourcePositionHolder otherElement) {
		SourcePosition otherSourcePosition = otherElement.getPosition();
		if (otherSourcePosition instanceof SourcePositionImpl && !(otherSourcePosition.getCompilationUnit() instanceof NoSourcePosition.NullCompilationUnit)) {
			if (parentFragment.isFromSameSource(otherSourcePosition)) {
				ElementSourceFragment otherFragment = new ElementSourceFragment(otherElement, parentFragment.getRoleHandler(roleInParent, otherElement));
				//parent and child are from the same file. So we can connect their positions into one tree
				CMP cmp = parentFragment.compare(otherFragment);
				if (cmp == CMP.OTHER_IS_CHILD) {
					//child belongs under parent - OK
					parentFragment.addChild(otherFragment);
					return otherFragment;
				} else {
					if (cmp == CMP.OTHER_IS_AFTER || cmp == CMP.OTHER_IS_BEFORE) {
						if (otherElement instanceof CtComment) {
							/*
							 * comments of elements are sometime not included in source position of element.
							 * because comments are ignored tokens for java compiler, which computes start/end of elements
							 * Example:
							 *
							 * 		//a comment
							 * 		aStatement();
							 *
							 */
							if (otherFragment.getStart() == 0) {
								//it is CompilationUnit comment, which is before package and imports, so it doesn't belong to class
								//No problem. Simply add comment at correct position into SourceFragment tree, starting from ROOT
								addChild(otherFragment);
								return otherFragment;
							}
							//add this child into parent's source fragment and extend that parent source fragment
							parentFragment.addChild(otherFragment);
							return otherFragment;
						}
						throw new SpoonException("otherFragment (" + otherElement.getPosition() + ") " + cmp.toString() + " of " + parentFragment.getSourcePosition());

					}
					//the source position of child element is not included in source position of parent element
					//I (Pavel) am not sure how to handle it, so let's wait until it happens...
//						if (otherElement instanceof CtAnnotation<?>) {
//							/*
//							 * it can happen for annotations of type TYPE_USE and FIELD
//							 * In such case the annotation belongs to 2 elements
//							 * And one of them cannot have matching source position - OK
//							 */
//							return null;
//						}
					//It happened... See spoon.test.issue3321.SniperPrettyPrinterJavaxTest
					//something is wrong ...
					throw new SpoonException("The SourcePosition of elements are not consistent\nparentFragment: " + parentFragment + "\notherFragment: " + otherElement.getPosition());
				}
			} else {
				throw new SpoonException("SourcePosition from unexpected compilation unit: " + otherSourcePosition + " expected is: " + parentFragment.getSourcePosition());
			}
		}
		//do not connect that undefined source position
		return null;
	}

	private RoleHandler getRoleHandler(CtRole roleInParent, SourcePositionHolder otherElement) {
		SourcePositionHolder parent = element;
		if (parent == null) {
			if (otherElement instanceof CtElement) {
				parent = ((CtElement) otherElement).getParent();
			}
		}
		if (parent instanceof CtElement) {
			CtElement ele = (CtElement) parent;
			return RoleHandlerHelper.getRoleHandler(ele.getClass(), roleInParent);
		}
		return null;
	}

	/**
	 * adds `other` {@link ElementSourceFragment} into tree of {@link ElementSourceFragment}s represented by this root element
	 *
	 * @param other to be added {@link ElementSourceFragment}
	 * @return new root of the tree of the {@link ElementSourceFragment}s. It can be be this or `other`
	 */
	public ElementSourceFragment add(ElementSourceFragment other) {
		if (this == other) {
			throw new SpoonException("SourceFragment#add must not be called twice for the same SourceFragment");
			//optionally we might accept that and simply return this
		}
		CMP cmp = this.compare(other);
		switch (cmp) {
		case OTHER_IS_AFTER:
			//other is after this
			addNextSibling(other);
			return this;
		case OTHER_IS_BEFORE:
			//other is before this
			other.addNextSibling(this);
			return other;
		case OTHER_IS_CHILD:
			//other is child of this
			addChild(other);
			return this;
		case OTHER_IS_PARENT:
			//other is parent of this, merge this and all siblings of `this` as children and siblings of `other`
			other.merge(this);
			return other;
		}
		throw new SpoonException("Unexpected compare result: " + cmp);
	}

	private void merge(ElementSourceFragment tobeMerged) {
		while (tobeMerged != null) {
			ElementSourceFragment nextTobeMerged = tobeMerged.getNextSibling();
			//disconnect tobeMerged from nextSiblings before we add it. So it is added individually and not with wrong siblings too
			tobeMerged.nextSibling = null;
			add(tobeMerged);
			tobeMerged = nextTobeMerged;
		}
	}

	/**
	 * adds `fragment` as child fragment of this fragment. If child is located before or after this fragment,
	 * then start/end of this fragment is moved
	 * @param fragment to be added
	 */
	public void addChild(ElementSourceFragment fragment) {
		if (firstChild == null) {
			firstChild = fragment;
		} else {
			firstChild = firstChild.add(fragment);
		}
	}

	private void addNextSibling(ElementSourceFragment sibling) {
		if (nextSibling == null) {
			nextSibling = sibling;
		} else {
			nextSibling = nextSibling.add(sibling);
		}
	}

	private ElementSourceFragment getLastSibling() {
		ElementSourceFragment lastSibling = this;
		while (lastSibling.nextSibling != null) {
			lastSibling = lastSibling.nextSibling;
		}
		return lastSibling;
	}

	private enum CMP {
		OTHER_IS_BEFORE,
		OTHER_IS_AFTER,
		OTHER_IS_CHILD,
		OTHER_IS_PARENT
	}

	/**
	 * compares this and other
	 * @param other other {@link SourcePosition}
	 * @return CMP
	 * throws {@link SpoonException} if intervals overlap or start/end is negative
	 */
	private CMP compare(ElementSourceFragment other) {
		if (other == this) {
			throw new SpoonException("SourcePositionImpl#addNextSibling must not be called twice for the same SourcePosition");
		}
		if (getEnd() <= other.getStart()) {
			//other is after this
			return CMP.OTHER_IS_AFTER;
		}
		if (other.getEnd() <= getStart()) {
			//other is before this
			return CMP.OTHER_IS_BEFORE;
		}
		if (getStart() <= other.getStart() && getEnd() >= other.getEnd()) {
			//other is child of this
			return CMP.OTHER_IS_CHILD;
		}
		if (getStart() >= other.getStart() && getEnd() <= other.getEnd()) {
			//other is parent of this
			return CMP.OTHER_IS_PARENT;
		}
		//the fragments overlap - it is not allowed
		throw new SpoonException("Cannot compare this: [" + getStart() + ", " + getEnd() + "] with other: [\"" + other.getStart() + "\", \"" + other.getEnd() + "\"]");
	}

	/**
	 * @return {@link ElementSourceFragment} which belongs to the same parent and is next in the sources
	 */
	public ElementSourceFragment getNextSibling() {
		return nextSibling;
	}

	/**
	 * @return {@link ElementSourceFragment}, which is first child of this fragment
	 */
	public ElementSourceFragment getFirstChild() {
		return firstChild;
	}

	/**
	 * Searches the tree of fragments for the {@link ElementSourceFragment} with expected `element`,
	 * which contains `start` and `end` source interval.
	 * It searches in siblings and children of this {@link ElementSourceFragment} recursively.
	 * @param element the {@link SourcePositionHolder} of fragment it is looking for or null for any element
	 * @param start the start offset of searched fragment
	 * @param end the offset of next character after the end of searched fragment
	 *
	 * @return {@link ElementSourceFragment} which represents the root of the CtElement whose sources are in interval [start, end]
	 */
	public ElementSourceFragment getSourceFragmentOf(SourcePositionHolder element, int start, int end) {
		int myEnd = getEnd();
		if (myEnd <= start) {
			//search in next sibling
			if (nextSibling == null) {
				return null;
			}
			return getRootFragmentOfElement(nextSibling.getSourceFragmentOf(element, start, end));
		}
		int myStart = getStart();
		if (myStart <= start) {
			if (myEnd >= end) {
				if (myStart == start && myEnd == end) {
					//we have found exact match
					if (element != null && getElement() != element) {
						if (firstChild == null) {
							throw new SpoonException("There is no source fragment for element " + element.toString() + ". There is one for class " + getElement().toString());
						}
						return firstChild.getSourceFragmentOf(element, start, end);
					}
					return this;
				}
				//it is the child
				if (firstChild == null) {
					if (element != null && getElement() != element) {
						throw new SpoonException("There is no source fragment for element " + element.getClass() + ". There is one for class " + getElement().getClass());
					}
					return this;
				}
				ElementSourceFragment child = getRootFragmentOfElement(firstChild.getSourceFragmentOf(element, start, end));
				if (child != null) {
					//all children are smaller then this element
					return child;
				}
				//so this fragment is last one which wraps whole element
				if (element != null && getElement() != element) {
					throw new SpoonException("There is no source fragment for element " + element.getClass() + ". There is one for class " + getElement().getClass());
				}
				return this;
			}
			//start - end overlaps over multiple fragments
			throw new SpoonException("Invalid start/end interval. It overlaps multiple fragments.");
		}
		return null;
	}

	private ElementSourceFragment getRootFragmentOfElement(ElementSourceFragment childFragment) {
		if (childFragment != null && getElement() != null && childFragment.getElement() == getElement()) {
			//child fragment and this fragment have same element. Return this fragment,
			//because we have to return root fragment of CtElement
			return this;
		}
		return childFragment;
	}
	/**
	 * @return {@link CtElement} whose source code is contained in this fragment.
	 * May be null
	 */
	public SourcePositionHolder getElement() {
		return element;
	}

	/**
	 * Note: the List of children is flat. The child fragments of collections (parameters, type members, ...) are next to each other.
	 * @return list of child fragments of this {@link ElementSourceFragment}.
	 */
	public List<SourceFragment> getChildrenFragments() {
		if (element instanceof CtLiteral) {
			return Collections.singletonList(new TokenSourceFragment(getSourceCode(), TokenType.LITERAL));
		}
		List<SourceFragment> children = new ArrayList<>();
		int off = getStart();
		ElementSourceFragment child = getFirstChild();
		while (child != null) {
			forEachConstantFragment(off, child.getStart(), cf -> children.add(cf));
			children.add(child);
			off = child.getEnd();
			child = child.getNextSibling();
		}
		forEachConstantFragment(off, getEnd(), cf -> children.add(cf));
		return children;
	}

	/**
	 * Detects all child fragments of this {@link ElementSourceFragment}.
	 * Note: the List of children contains one {@link CollectionSourceFragment} for each collection of fragments (parameters, type members, ...).
	 * Note: the {@link CollectionSourceFragment} may contain a mix of fragments of different roles, when they overlap.
	 * For example this code contains mix of annotations and modifiers
	 * <code>public @Test static @Ignored void method()</code>
	 * @return list of child fragments of this {@link ElementSourceFragment} where fragments,
	 * which belongs to the same collection are grouped into {@link CollectionSourceFragment}
	 */
	public List<SourceFragment> getGroupedChildrenFragments() {
		List<SourceFragment> flatChildren = getChildrenFragments();
		List<SourceFragment> result = new ArrayList<>();
		int i = 0;
		while (i < flatChildren.size()) {
			SourceFragment child = flatChildren.get(i);
			if (child instanceof TokenSourceFragment) {
				result.add(child);
				i++;
				continue;
			} else if (child instanceof ElementSourceFragment) {
				ElementSourceFragment esf = (ElementSourceFragment) child;
				ContainerKind kind = esf.getContainerKindInParent();
				if (kind == ContainerKind.SINGLE) {
					//it is root element or there is always only one child instance in parent
					result.add(child);
					i++;
					continue;
				}
				//there can be 0, 1 or more items of children of the same role
				//search for another element of the same role
				Set<CtRole> foundRoles = new HashSet<>();
				foundRoles.add(checkNotNull(esf.getRoleInParent()));
				List<SourceFragment> childrenInSameCollection = new ArrayList<>();
				//but first include prefix whitespace
				SourceFragment spaceChild = removeSuffixSpace(result);
				if (spaceChild != null) {
					childrenInSameCollection.add(spaceChild);
				}
				childrenInSameCollection.add(esf);
				int lastOfSameRole = findIndexOfLastChildTokenOfRoleHandler(flatChildren, i, esf.getRoleInParent());
				//search for other roles in that interval
				i++;
				while (i <= lastOfSameRole) {
					child = flatChildren.get(i);
					childrenInSameCollection.add(child);
					CtRole role = null;
					if (child instanceof ElementSourceFragment) {
						ElementSourceFragment esf2 = (ElementSourceFragment) child;
						role = esf2.getRoleInParent();
					}
					if (role != null && role != CtRole.COMMENT && foundRoles.add(role)) {
						//there is another role in same block, search for last one
						lastOfSameRole = Math.max(lastOfSameRole, findIndexOfLastChildTokenOfRoleHandler(flatChildren, i + 1, role));
					}
					i++;
				}
				//add suffix space
				/*
				if (i < flatChildren.size()) {
					SourceFragment nextChild = flatChildren.get(i);
					if (isSpaceFragment(nextChild)) {
						childrenInSameCollection.add(nextChild);
						i++;
					}
				}
				*/
				result.add(new CollectionSourceFragment(childrenInSameCollection));
			} else {
				throw new SpoonException("Unexpected SourceFragment of type " + child.getClass());
			}
		}
		return result;
	}

	private SourceFragment removeSuffixSpace(List<SourceFragment> list) {
		if (list.size() > 0) {
			SourceFragment lastChild = list.get(list.size() - 1);
			if (isSpaceFragment(lastChild)) {
				list.remove(list.size() - 1);
				return lastChild;
			}
		}
		return null;
	}

	private <T> T checkNotNull(T o) {
		if (o == null) {
			throw new SpoonException("Unexpected null value");
		}
		return o;
	}

	private static int findIndexOfLastChildTokenOfRoleHandler(List<SourceFragment> childFragments, int start, CtRole role) {
		return findIndexOfPreviousFragment(childFragments, start,
				filter(ElementSourceFragment.class, fragment -> fragment.getRoleInParent() == role));
	}

	private enum CharType {
		SPACE,
		NON_SPACE;

		static CharType fromChar(char c) {
			return Character.isWhitespace(c) ? SPACE : NON_SPACE;
		}
	}

	private static final Set<String> separators = new HashSet<>(Arrays.asList("->", "::", "..."));
	static {
		"(){}[];,.:@=<>?&|".chars().forEach(c -> separators.add(new String(Character.toChars(c))));
	}
	private static final Set<String> operators = new HashSet<>(Arrays.asList(
			"=",
			">",
			"<",
			"!",
			"~",
			"?",
			":",
			"==",
			"<=",
			">=",
			"!=",
			"&&",
			"||",
			"++",
			"--",
			"+",
			"-",
			"*",
			"/",
			"&",
			"|",
			"^",
			"%",
			"<<", ">>", ">>>",

			"+=",
			"-=",
			"*=",
			"/=",
			"&=",
			"|=",
			"^=",
			"%=",
			"<<=",
			">>=",
			">>>="/*,
			it is handled as keyword here
			"instanceof"
			*/
	));

	private static final String[] javaKeywordsJoined = {
			"abstract continue for new switch",
			"assert default goto package synchronized",
			"boolean do if private this",
			"break double implements protected throw",
			"byte else import public throws",
			"case enum instanceof return transient",
			"catch extends int short try",
			"char final interface static void",
			"class finally long strictfp volatile",
			"const float native super while"
	};

	private static final Set<String> javaKeywords = new HashSet<>();
	static {
		for (String str : javaKeywordsJoined) {
			StringTokenizer st = new StringTokenizer(str, " ");
			while (st.hasMoreTokens()) {
				javaKeywords.add(st.nextToken());
			}
		}
	}

	private static final List<StringMatcher> matchers = new ArrayList<>();
	static {
		separators.forEach(s -> matchers.add(new StringMatcher(s, TokenType.SEPARATOR)));
		operators.forEach(s -> matchers.add(new StringMatcher(s, TokenType.OPERATOR)));
	}

	/**
	 * Calls `consumer` once for each constant {@link SourceFragment} found in source code between `start` and `end`
	 */
	private void forEachConstantFragment(int start, int end, Consumer<SourceFragment> consumer) {
		if (start == end) {
			return;
		}
		if (start > end) {
			throw new SpoonException("Inconsistent start/end. Start=" + start + " is greater then End=" + end);
		}
		String sourceCode = getOriginalSourceCode();
		StringBuilder buff = new StringBuilder();
		CharType lastType = null;
		int off = start;
		while (off < end) {
			char c = sourceCode.charAt(off);
			CharType type = CharType.fromChar(c);
			if (type != lastType) {
				if (lastType != null) {
					onCharSequence(lastType, buff, consumer);
					buff.setLength(0);
				}
				lastType = type;
			}
			buff.append(c);
			off++;
		}
		onCharSequence(lastType, buff, consumer);
	}

	private void onCharSequence(CharType type, StringBuilder buff, Consumer<SourceFragment> consumer) {
		if (type == CharType.SPACE) {
			consumer.accept(new TokenSourceFragment(buff.toString(), TokenType.SPACE));
			return;
		}
		char[] str = new char[buff.length()];
		buff.getChars(0, buff.length(), str, 0);
		int off = 0;
		while (off < str.length) {
			//detect java identifier or keyword
			int lenOfIdentifier = detectJavaIdentifier(str, off);
			if (lenOfIdentifier > 0) {
				String identifier = new String(str, off, lenOfIdentifier);
				if (javaKeywords.contains(identifier)) {
					//it is a java keyword
					consumer.accept(new TokenSourceFragment(identifier, TokenType.KEYWORD));
				} else {
					//it is a java identifier
					consumer.accept(new TokenSourceFragment(identifier, TokenType.IDENTIFIER));
				}
				off += lenOfIdentifier;
				continue;
			}
			//detect longest match in matchers
			StringMatcher longestMatcher = null;
			for (StringMatcher strMatcher : matchers) {
				if (strMatcher.isMatch(str, off)) {
					longestMatcher = strMatcher.getLonger(longestMatcher);
				}
			}
			if (longestMatcher == null) {
				throw new SpoonException("Unexpected source text: " + buff.toString());
			}
			consumer.accept(new TokenSourceFragment(longestMatcher.toString(), longestMatcher.getType()));
			off += longestMatcher.getLength();
		}
	}

	/**
	 * @return number of characters in buff starting from start which are java identifier
	 */
	private int detectJavaIdentifier(char[] buff, int start) {
		int len = buff.length;
		int o = start;
		if (start <= len) {
			char c = buff[o];
			if (Character.isJavaIdentifierStart(c)) {
				o++;
				while (o < len) {
					c = buff[o];
					if (Character.isJavaIdentifierPart(c) == false) {
						break;
					}
					o++;
				}
			}
		}
		return o - start;
	}

	private String getOriginalSourceCode() {
		CompilationUnit cu = getSourcePosition().getCompilationUnit();
		if (cu != null) {
			return cu.getOriginalSourceCode();
		}
		return null;
	}

	private static final class StringMatcher {
		private final TokenType type;
		private final char[] chars;

		private StringMatcher(final String str, TokenType type) {
			this.type = type;
			chars = str.toCharArray();
		}

		public boolean isMatch(final char[] buffer, int pos) {
			final int len = chars.length;
			if (pos + len > buffer.length) {
				return false;
			}
			for (int i = 0; i < chars.length; i++, pos++) {
				if (chars[i] != buffer[pos]) {
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString() {
			return new String(chars);
		}

		public int getLength() {
			return chars.length;
		}

		public StringMatcher getLonger(StringMatcher m) {
			if (m != null && m.getLength() > getLength()) {
				return m;
			}
			return this;
		}

		public TokenType getType() {
			return type;
		}
	}

	/**
	 * @return role of the element of this fragment in scope of it's parent
	 */
	public CtRole getRoleInParent() {
		return roleHandlerInParent != null ? roleHandlerInParent.getRole() : null;
	}

	/**
	 * @return the {@link ContainerKind} of the attribute which holds the element of this fragment in it's parent
	 */
	public ContainerKind getContainerKindInParent() {
		if (roleHandlerInParent != null) {
			if (roleHandlerInParent.getRole() != CtRole.COMMENT) {
				return roleHandlerInParent.getContainerKind();
			}
		}
		return ContainerKind.SINGLE;
	}
	/**
	 * looks for next fragment whose {@link Predicate} `test` returns true
	 * @param start - the index of first to be checked fragment
	 * @return index of found fragment, or -1 if not found
	 */
	static int findIndexOfNextFragment(List<SourceFragment> fragments, int start, Predicate<SourceFragment> test) {
		while (start < fragments.size()) {
			SourceFragment fragment = fragments.get(start);
			if (test.test(fragment)) {
				return start;
			}
			start++;
		}
		return -1;
	}

	/**
	 * @param start the index of element with lower index which is checked and may be returned
	 * @param test a {@link Predicate}, which is evaluated for each item of `fragments` starting from last one and ending with item in index `start`
	 * @return index of found fragment, or -1 if not found
	 */
	static int findIndexOfPreviousFragment(List<SourceFragment> fragments, int start, Predicate<SourceFragment> test) {
		int i = fragments.size() - 1;
		while (i >= start) {
			if (test.test(fragments.get(i))) {
				return i;
			}
			i--;
		}
		return -1;
	}

	/**
	 * @param predicate the {@link Predicate}, which has to be checkd for each item of {@link CollectionSourceFragment}
	 * @return {@link Predicate} which calls `predicate` for each item of {@link CollectionSourceFragment}
	 * Returned {@link Predicate} returns true only if `predicate` returns true on at least one item
	 */
	static Predicate<SourceFragment> checkCollectionItems(Predicate<SourceFragment> predicate) {
		return (SourceFragment fragment) -> {
			if (fragment instanceof CollectionSourceFragment) {
				CollectionSourceFragment collectionFragment = (CollectionSourceFragment) fragment;
				for (SourceFragment itemFragment : collectionFragment.getItems()) {
					if (predicate.test(itemFragment)) {
						return true;
					}
				}
				return false;
			} else {
				return predicate.test(fragment);
			}
		};
	}

	/**
	 * @param predicate to be called {@link Predicate}
	 * @return {@link Predicate} which calls `predicate` only for {@link SourceFragment}s of of type `clazz` and returns false for others
	 */
	static <T extends SourceFragment> Predicate<SourceFragment> filter(Class<T> clazz, Predicate<T> predicate) {
		return fragment -> {
			if (clazz.isInstance(fragment)) {
				return predicate.test((T) fragment);
			}
			return false;
		};
	}

	/**
	 * @return true if {@link SourceFragment} represents a white space
	 */
	static boolean isSpaceFragment(SourceFragment fragment) {
		return fragment instanceof TokenSourceFragment && ((TokenSourceFragment) fragment).getType() == TokenType.SPACE;
	}
}
