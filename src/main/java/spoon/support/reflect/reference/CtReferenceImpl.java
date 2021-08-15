/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static spoon.reflect.path.CtRole.NAME;

public abstract class CtReferenceImpl extends CtElementImpl implements CtReference, Serializable {

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
		checkIdentiferForJLSCorrectness(simplename);
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
	public abstract void accept(CtVisitor visitor);

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
	private void checkIdentiferForJLSCorrectness(String simplename) {
		/*
		 * At the level of the Java Virtual Machine, every constructor written in the Java programming language (JLS §8.8)
		 * appears as an instance initialization method that has the special name <init>.
		 * This name is supplied by a compiler. Because the name is not a valid identifier,
		 * it cannot be used directly in a program written in the Java programming language.
		 */
		//JDTTreeBuilderHelper.computeAnonymousName returns "$numbers$Name" so we have to skip them if they start with numbers
		//allow empty identifier because they are sometimes used.
		if (!simplename.matches("<.*>|\\d.*|^.{0}$")) {
			//split at "<" and ">" because "Iterator<Cache.Entry<K,Store.ValueHolder<V>>>" submits setSimplename ("Cache.Entry<K")
			String[] splittedSimplename = simplename.split("\\.|<|>");
			if (checkAllParts(splittedSimplename)) {
				throw new SpoonException("Not allowed javaletter or keyword in identifier found. See JLS for correct identifier. Identifier: " + simplename);
			}
		}
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

	private boolean checkAllParts(String[] simplenameParts) {
		for (String simpleName:simplenameParts) {
			//because arrays use e.g. int[] and @Number is used for instances of an object e.g. foo@1
			simpleName = simpleName.replaceAll("\\[\\]|@", "");
			if (isWildCard(simpleName)) {
				// because in intersection types a typeReference sometimes has '?' as simplename
				return false;
			}
			if (isKeyword(simpleName) || checkIdentifierChars(simpleName)) {
				return true;
			}
		}
		return false;
	}
	private boolean checkIdentifierChars(String simplename) {
		if (simplename.length() == 0) {
			return false;
		}
		return (!Character.isJavaIdentifierStart(simplename.charAt(0)))
			|| simplename.chars().anyMatch(letter -> !Character.isJavaIdentifierPart(letter)
		);
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

	/**
	 * checks if the input is a wildcard '?'. The method is not null safe.
	 * @return boolean true is input wildcard, false otherwise
	 */
	private boolean isWildCard(String name) {
		return name.equals("?");
	}
}
