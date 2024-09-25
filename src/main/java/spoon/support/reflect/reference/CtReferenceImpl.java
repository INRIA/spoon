/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.JLSViolation;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.declaration.CtElementImpl;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static spoon.reflect.path.CtRole.NAME;

public abstract class CtReferenceImpl extends CtElementImpl implements CtReference {

	private static final long serialVersionUID = 1L;

	// See isKeyword for more information on keywords
	private static final Collection<String> baseKeywords = fillWithBaseKeywords();
	private static final Collection<String> java2Keywords = Collections.singleton("strictfp");
	private static final Collection<String> java4Keywords = Collections.singleton("assert");
	private static final Collection<String> java5Keywords = Collections.singleton("enum");
	private static final Collection<String> java9Keywords = Collections.singleton("_");

	@MetamodelPropertyField(role = NAME)
	protected String simplename = "";

	public CtReferenceImpl() {
	}

	protected abstract AnnotatedElement getActualAnnotatedElement();

	@Override
	public String getSimpleName() {
		return simplename;
	}

	@Override
	public <T extends CtReference> T setSimpleName(String simplename) {
		Factory factory = getFactory();
		checkIdentifierForJLSCorrectness(simplename);
		if (factory == null) {
			this.simplename = simplename;
			return (T) this;
		}
		if (factory instanceof FactoryImpl) {
			simplename = ((FactoryImpl) factory).dedup(simplename);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, NAME, simplename, this.simplename);
		this.simplename = simplename;
		return (T) this;
	}




	@UnsettableProperty
	@Override
	public <E extends CtElement> E setComments(List<CtComment> comments) {
		return (E) this;
	}


	@Override
	public CtReference clone() {
		return (CtReference) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CtReference) {
			CtReference ref = (CtReference) o;
			if (!Objects.equals(getSimpleName(), ref.getSimpleName())) {
				//fast fallback when simple names are not equal
				//it is much faster then EqualsVisitor
				return false;
			}
			return super.equals(o);
		}
		return false;
	}

	/*
	 * This method validates the simplename.
	 * spoon needs to allow more names that are allowed by the JLS, as
	 * - array references have a name, e.g. int[], where [] would not be allowed normally
	 * - ? is used as name for intersection types
	 * - <init>, <clinit>, <nulltype> are used to represent initializers and the null reference
	 * - anonymous/local classes start with numbers in spoon
	 * - simple names of packages are just their names, but they may contain '.'
	 * - the name can contain generics, e.g. List<String>[]
	 */
	private void checkIdentifierForJLSCorrectness(String simplename) {
		if (isSpecialType(simplename)) {
			return;
		}
		if (!checkAll(simplename)) {
			JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, "Not allowed javaletter or keyword in identifier found. See JLS for correct identifier. Identifier: " + simplename);
		}
	}

	/*
	 * returns true if the name is valid.
	 * this splits up the string into parts that need to be JLS compliant.
	 */
	private boolean checkAll(String name) {
		int i = 0;
		// leading digits come from anonymous/local classes. Skip them
		while (i < name.length() && Character.isDigit(name.charAt(i))) {
			i++;
		}
		int start = i; // used to mark the beginning of a part
		final char anything = 0;
		char expectNext = anything;
		for (; i < name.length(); i++) {
			if (expectNext != anything) {
				if (name.charAt(i) != expectNext) {
					return false;
				} else if (name.charAt(i) == expectNext) {
					expectNext = anything; // reset
					continue; // skip it, no further checks required
				}
			}
			switch (name.charAt(i)) {
				case '.':
				case '<':
				case '>':
					// we scanned a word of valid java identifiers (see default case) until one
					// of the special delimiting chars that are allowed in spoon
					// now we just need to make sure it is not a keyword
					if (isKeyword(name.substring(start, i))) {
						return false; // keyword -> not allowed
					}
					start = i + 1; // skip this special char
					break;
				case '[':
					expectNext = ']'; // next char *must* close
					break;
				default: // if we come across an illegal java identifier char here, it's not valid at all
					if (start == i && !Character.isJavaIdentifierStart(name.charAt(i))
							|| !Character.isJavaIdentifierPart(name.charAt(i))) {
						return false;
					}
					break;
			}
		}
		// make sure the end state is correct too
		if (expectNext != anything) {
			return false; // expected something that didn't appear anymore
		}
		// e.g. a name that only contains valid java identifiers will end up here (start will never be updated)
		// and we still need to make sure it is not a keyword.
		// as updating start uses i + 1, it might be out of bounds, so avoid SIOOBEs here
		if (start < name.length()) {
			return !isKeyword(name.substring(start));
		}
		return true;
	}

	private static boolean isSpecialType(String identifier) {
		return identifier.isEmpty()
				|| "?".equals(identifier) // is wildcard, used for intersection types
				|| (identifier.startsWith("<") && identifier.endsWith(">"));
	}

	/**
	 * Keywords list and history selected according to:
	 * https://docs.oracle.com/en/java/javase/15/docs/specs/sealed-classes-jls.html#jls-3.9
	 * https://en.wikipedia.org/wiki/List_of_Java_keywords (contains history of revisions)
	 * and https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html (history up to java 8)
	 *
	 * @param simplename
	 * @return true if simplename is a keyword in the current setting (compliance level), false if not
	 */
	private boolean isKeyword(String simplename) {
		int complianceLevel = getFactory().getEnvironment().getComplianceLevel();
		return (baseKeywords.contains(simplename)
				|| (complianceLevel >= 2 && java2Keywords.contains(simplename))
				|| (complianceLevel >= 4 && java4Keywords.contains(simplename))
				|| (complianceLevel >= 5 && java5Keywords.contains(simplename))
				|| (complianceLevel >= 9 && java9Keywords.contains(simplename)));
	}

	private static Collection<String> fillWithBaseKeywords() {
		// removed types because needed as ref: "int","short", "char", "void", "byte","float", "true","false","boolean","double","long","class", "null"
		// in the method isKeyword, more keywords are added to the checks based on the compliance level
		return Stream.of("abstract", "continue", "for", "new", "switch", "default", "if", "package", "synchronized",  "do", "goto", "private",
				"this", "break",  "implements", "protected", "throw", "else", "import", "public", "throws", "case", "instanceof", "return",
				"transient", "catch", "extends", "try", "final", "interface", "static", "finally", "volatile",
				"const",  "native", "super", "while")
				.collect(Collectors.toCollection(HashSet::new));
	}
}
