/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static spoon.reflect.path.CtRole.NAME;

public abstract class CtReferenceImpl extends CtElementImpl implements CtReference, Serializable {

	private static final long serialVersionUID = 1L;
	private static Collection<String> keywords = fillWithKeywords();

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
		if (simplename.length() == 0) {
			throw new IllegalArgumentException("empty identifier found. See JLS for correct identifier");
		}
		if (checkIdentifierChars(simplename) || isKeyword(simplename)) {
			throw new IllegalArgumentException("Not allowed javaletter or keyword in identifier found. See JLS for correct identifier");
		}
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
	private boolean isKeyword(String simplename) {
		return keywords.contains(simplename);
	}

	private boolean checkIdentifierChars(String simplename) {
		return (!Character.isJavaIdentifierStart(simplename.charAt(0))) || simplename.chars().anyMatch(letter -> !Character.isJavaIdentifierPart(letter));
	}
	private static Collection<String> fillWithKeywords() {
	return Stream.of("abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package", "synchronized", "boolean", "do", "goto", "private",
	"this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return",
	"transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile",
	"const", "float", "native", "super", "while", "_", "true", "false", "null")
	.collect(Collectors.toCollection(HashSet::new));
	}
}
