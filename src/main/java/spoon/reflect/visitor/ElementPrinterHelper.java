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
package spoon.reflect.visitor;

import spoon.compiler.Environment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.printer.CommentOffset;
import spoon.reflect.visitor.PrintingContext.Writable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import spoon.support.reflect.CtExtendedModifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;

public class ElementPrinterHelper {
	private final DefaultJavaPrettyPrinter prettyPrinter;
	private final Environment env;
	private TokenWriter printer;

	public ElementPrinterHelper(TokenWriter printerTokenWriter, DefaultJavaPrettyPrinter prettyPrinter, Environment env) {
		this.printer = printerTokenWriter;
		this.prettyPrinter = prettyPrinter;
		this.env = env;
	}

	/**
	 * Writes the annotations for the given element.
	 */
	public void writeAnnotations(CtElement element) {
		for (CtAnnotation<?> annotation : element.getAnnotations()) {

			// if element is a type reference and the parent is a typed element
			// which contains exactly the same annotation, then we are certainly in this case:
			// @myAnnotation String myField
			// in which case the annotation is attached to the type and the variable
			// in that case, we only print the annotation once.
			if (element.isParentInitialized() && element instanceof CtTypeReference && (element.getParent() instanceof CtTypedElement) && element.getParent().getAnnotations().contains(annotation)) {
					continue;
			}

			prettyPrinter.scan(annotation);
			printer.writeln();
		}
	}

	/**  writes the modifiers of this modifiable in a specific order */
	public void writeModifiers(CtModifiable modifiable) {
		// write the modifiers according to the convention on order
		List<String> firstPosition = new ArrayList<>(); // visibility: public, private, protected
		List<String> secondPosition = new ArrayList<>(); // keywords: static, abstract
		List<String> thirdPosition = new ArrayList<>(); // all other things

		for (CtExtendedModifier extendedModifier : modifiable.getExtendedModifiers()) {
			if (!extendedModifier.isImplicit()) {
				ModifierKind modifierKind = extendedModifier.getKind();
				if (modifierKind == ModifierKind.PUBLIC || modifierKind == ModifierKind.PRIVATE || modifierKind == ModifierKind.PROTECTED) {
					firstPosition.add(modifierKind.toString());
				} else if (modifierKind == ModifierKind.ABSTRACT || modifierKind == ModifierKind.STATIC) {
					secondPosition.add(modifierKind.toString());
				} else {
					thirdPosition.add(modifierKind.toString());
				}
			}
		}

		for (String s : firstPosition) {
			printer.writeKeyword(s).writeSpace();
		}

		for (String s : secondPosition) {
			printer.writeKeyword(s).writeSpace();
		}

		for (String s : thirdPosition) {
			printer.writeKeyword(s).writeSpace();
		}

		if (modifiable instanceof CtMethod) {
			CtMethod m = (CtMethod) modifiable;
			if (m.isDefaultMethod()) {
				printer.writeKeyword("default").writeSpace();
			}
		}
	}

	public void visitCtNamedElement(CtNamedElement namedElement, CtCompilationUnit sourceCompilationUnit) {
		writeAnnotations(namedElement);
		if (env.isPreserveLineNumbers()) {
			getPrinterHelper().adjustStartPosition(namedElement);
		}
	}

	public void writeExtendsClause(CtType<?> type) {
		if (type.getSuperclass() != null) {
			printer.writeSpace().writeKeyword("extends").writeSpace();
			prettyPrinter.scan(type.getSuperclass());
		}
	}

	/** writes the implemented interfaces with a ListPrinter */
	public void writeImplementsClause(CtType<?> type) {
		if (!type.getSuperInterfaces().isEmpty()) {
			printList(type.getSuperInterfaces(), "implements",
				false, null, false, true, ",", true, false, null,
				ref -> prettyPrinter.scan(ref));
		}
	}

	public void writeExecutableParameters(CtExecutable<?> executable) {
		printList(executable.getParameters(), null,
			false, "(", false, false, ",", true, false, ")",
			p -> prettyPrinter.scan(p));
	}

	/** writes the thrown exception with a ListPrinter */
	public void writeThrowsClause(CtExecutable<?> executable) {
		if (!executable.getThrownTypes().isEmpty()) {
			printList(executable.getThrownTypes(), "throws",
				false, null, false, false, ",", true, false, null,
				ref -> prettyPrinter.scan(ref));
		}
	}

	/**
	 * Writes a statement.
	 */
	public void writeStatement(CtStatement statement) {
		try (Writable _context = prettyPrinter.getContext().modify().setStatement(statement)) {
			prettyPrinter.scan(statement);
		}
	}

	public void writeElementList(List<CtTypeMember> elements) {
		for (CtTypeMember element : elements) {
			if (element instanceof CtConstructor && element.isImplicit()) {
				continue;
			}
			printer.writeln();
			prettyPrinter.scan(element);
			if (!env.isPreserveLineNumbers()) {
				printer.writeln();
			}
		}
	}

	/**
	 * Writes an annotation element.
	 */
	public void writeAnnotationElement(Factory factory, Object value) {
		if (value instanceof CtTypeAccess) {
			prettyPrinter.scan((CtTypeAccess) value);
			printer.writeSeparator(".").writeKeyword("class");
		} else if (value instanceof CtFieldReference) {
			prettyPrinter.scan(((CtFieldReference<?>) value).getDeclaringType());
			printer.writeSeparator(".").writeIdentifier(((CtFieldReference<?>) value).getSimpleName());
		} else if (value instanceof CtElement) {
			prettyPrinter.scan((CtElement) value);
		} else if (value instanceof String) {
			printer.writeLiteral("\"" + LiteralHelper.getStringLiteral((String) value, true) + "\"");
		} else if (value instanceof Collection) {
			printList((Collection<?>) value, null,
				false, "{", false, true, ",", false, false, "}",
				obj -> writeAnnotationElement(factory, obj));
		} else if (value instanceof Object[]) {
			printList(Arrays.asList((Object[]) value), null,
				false, "{", false, true, ",", false, false, "}",
				obj -> writeAnnotationElement(factory, obj));
		} else if (value instanceof Enum) {
			try (Writable c = prettyPrinter.getContext().modify().ignoreGenerics(true)) {
				prettyPrinter.scan(factory.Type().createReference(((Enum<?>) value).getDeclaringClass()));
			}
			printer.writeSeparator(".");
			printer.writeIdentifier(value.toString());
		} else {
			//it probably prints, boolean, number, ...
			printer.writeLiteral(value.toString());
		}
	}

	/**
	 * Writes formal type parameters given in parameter.
	 *
	 * @param ctFormalTypeDeclarer
	 * 		Reference with formal type arguments.
	 */
	public void writeFormalTypeParameters(CtFormalTypeDeclarer ctFormalTypeDeclarer) {
		final Collection<CtTypeParameter> parameters = ctFormalTypeDeclarer.getFormalCtTypeParameters();
		if (parameters == null) {
			return;
		}
		if (!parameters.isEmpty()) {
			printList(parameters,
				null,	false, "<", false, false, ",", true, false, ">",
				parameter -> prettyPrinter.scan(parameter));
		}
	}

	/**
	 * Writes actual type arguments in a {@link CtActualTypeContainer} element.
	 *
	 * @param ctGenericElementReference
	 * 		Reference with actual type arguments.
	 */
	public void writeActualTypeArguments(CtActualTypeContainer ctGenericElementReference) {
		final Collection<CtTypeReference<?>> arguments = ctGenericElementReference.getActualTypeArguments();
		if (arguments != null && !arguments.isEmpty()) {
			printList(arguments.stream().filter(a -> !a.isImplicit())::iterator,
				null, false, "<", false, false, ",", true, false, ">",
				argument -> {
					if (prettyPrinter.getContext().forceWildcardGenerics()) {
						printer.writeSeparator("?");
					} else {
						prettyPrinter.scan(argument);
					}
				});
		}
	}

	private boolean isJavaLangClasses(String importType) {
		return importType.matches("^(java\\.lang\\.)[^.]*$");
	}

	/** writes the imports in a specific order (eg all static imports together */
	public void writeImports(Collection<CtImport> imports) {
		Set<String> setImports = new HashSet<>();
		Set<String> setStaticImports = new HashSet<>();
		for (CtImport ctImport : imports) {
			String importTypeStr;
			switch (ctImport.getImportKind()) {
				case TYPE:
					CtTypeReference typeRef = (CtTypeReference) ctImport.getReference();
					importTypeStr = typeRef.getQualifiedName();
					if (!isJavaLangClasses(importTypeStr)) {
						setImports.add(this.removeInnerTypeSeparator(importTypeStr));
					}
					break;

				case ALL_TYPES:
					CtPackageReference packageRef = (CtPackageReference) ctImport.getReference();
					importTypeStr = packageRef.getQualifiedName() + ".*";
					if (!isJavaLangClasses(importTypeStr)) {
						setImports.add(this.removeInnerTypeSeparator(importTypeStr));
					}
					break;

				case METHOD:
					CtExecutableReference execRef = (CtExecutableReference) ctImport.getReference();
					if (execRef.getDeclaringType() != null) {
						setStaticImports.add(this.removeInnerTypeSeparator(execRef.getDeclaringType().getQualifiedName()) + "." + execRef.getSimpleName());
					}
					break;

				case FIELD:
					CtFieldReference fieldRef = (CtFieldReference) ctImport.getReference();
					setStaticImports.add(this.removeInnerTypeSeparator(fieldRef.getDeclaringType().getQualifiedName()) + "." + fieldRef.getSimpleName());
					break;

				case ALL_STATIC_MEMBERS:
					CtTypeReference typeStarRef = (CtTypeReference) ctImport.getReference();
					importTypeStr = typeStarRef.getQualifiedName();
					if (!isJavaLangClasses(importTypeStr)) {
						setStaticImports.add(this.removeInnerTypeSeparator(importTypeStr));
					}
					break;
			}
		}

		List<String> sortedImports = new ArrayList<>(setImports);
		Collections.sort(sortedImports);
		boolean isFirst = true;
		for (String importLine : sortedImports) {
			if (isFirst) {
				printer.writeln();
				printer.writeln();
				isFirst = false;
			}
			printer.writeKeyword("import").writeSpace();
			writeQualifiedName(importLine).writeSeparator(";").writeln();
		}
		if (!setStaticImports.isEmpty()) {
			if (isFirst) {
				printer.writeln();
			}
			printer.writeln();
			List<String> sortedStaticImports = new ArrayList<>(setStaticImports);
			Collections.sort(sortedStaticImports);
			for (String importLine : sortedStaticImports) {
				printer.writeKeyword("import").writeSpace().writeKeyword("static").writeSpace();
				writeQualifiedName(importLine).writeSeparator(";").writeln();
			}
		}
	}

	/**
	 * Write the compilation unit header.
	 */
	public void writeHeader(List<CtType<?>> types, Collection<CtImport> imports) {
		if (!types.isEmpty()) {
			for (CtType<?> ctType : types) {
				writeComment(ctType, CommentOffset.TOP_FILE);
			}
			// writing the header package
			if (!types.get(0).getPackage().isUnnamedPackage()) {
				writePackageLine(types.get(0).getPackage().getQualifiedName());
			}
			this.writeImports(imports);
			printer.writeln();
			printer.writeln();
		}
	}

	/**
	 * Write the compilation unit footer.
	 */
	public void writeFooter(List<CtType<?>> types) {
		if (!types.isEmpty()) {
			for (CtType<?> ctType : types) {
				writeComment(ctType, CommentOffset.BOTTOM_FILE);
			}
		}
	}

	public void writePackageLine(String packageQualifiedName) {
		printer.writeKeyword("package").writeSpace();
		writeQualifiedName(packageQualifiedName).writeSeparator(";").writeln();
	}

	private String removeInnerTypeSeparator(String fqn) {
		return fqn.replace(CtType.INNERTTYPE_SEPARATOR, ".");
	}

	public void writeComment(CtComment comment) {
		if (!env.isCommentsEnabled() || comment == null) {
			return;
		}
		prettyPrinter.scan(comment);
		printer.writeln();
	}

	private void writeComment(List<CtComment> comments) {
		if (!env.isCommentsEnabled() || comments == null) {
			return;
		}
		for (CtComment comment : comments) {
			writeComment(comment);
		}
	}

	public void writeComment(CtElement element) {
		if (element == null) {
			return;
		}
		writeComment(element.getComments());
	}

	public void writeComment(CtElement element, CommentOffset offset) {
		writeComment(getComments(element, offset));
	}

	public List<CtComment> getComments(CtElement element, CommentOffset offset) {
		List<CtComment> commentsToPrint = new ArrayList<>();
		if (!env.isCommentsEnabled() || element == null) {
			return commentsToPrint;
		}
		for (CtComment comment : element.getComments()) {
			if (comment.getCommentType() == CtComment.CommentType.FILE && offset == CommentOffset.TOP_FILE && element.getPosition().getSourceEnd() > comment.getPosition().getSourceStart()) {
				commentsToPrint.add(comment);
				continue;
			}
			if (comment.getCommentType() == CtComment.CommentType.FILE && offset == CommentOffset.BOTTOM_FILE && element.getPosition().getSourceEnd() < comment.getPosition().getSourceStart()) {
				commentsToPrint.add(comment);
				continue;
			}
			if (comment.getCommentType() == CtComment.CommentType.FILE) {
				continue;
			}
			if (comment.getPosition().isValidPosition() == false || element.getPosition().isValidPosition() == false) {
				if (offset == CommentOffset.BEFORE) {
					commentsToPrint.add(comment);
				}
				continue;
			}
			final int line = element.getPosition().getLine();
			final int sourceEnd = element.getPosition().getSourceEnd();
			final int sourceStart = element.getPosition().getSourceStart();
			if (offset == CommentOffset.BEFORE && (comment.getPosition().getLine() < line || (sourceStart <= comment.getPosition().getSourceStart() && sourceEnd > comment.getPosition().getSourceEnd()))) {
				commentsToPrint.add(comment);
			} else if (offset == CommentOffset.AFTER && (comment.getPosition().getSourceStart() > sourceEnd || comment.getPosition().getSourceEnd() == sourceEnd)) {
				commentsToPrint.add(comment);
			} else {
				final int endLine = element.getPosition().getEndLine();
				if (offset == CommentOffset.INSIDE && comment.getPosition().getLine() >= line && comment.getPosition().getEndLine() <= endLine) {
					commentsToPrint.add(comment);
				}
			}
		}
		return commentsToPrint;
	}

	/** write all non-implicit parts of a block, with special care for indentation */
	public void writeIfOrLoopBlock(CtStatement block) {
		if (block != null) {
			if (!block.isImplicit() && (block instanceof CtBlock || block instanceof CtIf)) {
				printer.writeSpace();
			}
			if (!(block instanceof CtBlock) && !(block instanceof CtIf)) {
				printer.incTab();
				printer.writeln();
			}
			writeStatement(block);
			if (!(block instanceof CtBlock) && !(block instanceof CtIf)) {
				printer.decTab().writeln();
			}
			if (!block.isImplicit()) {
				if (!block.isParentInitialized() || (!(block.getParent() instanceof CtFor) && !(block.getParent() instanceof CtForEach) && !(block.getParent() instanceof CtIf))) {
					printer.writeSpace();
				}
			}
		} else {
			printer.writeSeparator(";");
		}
	}
	/**
	 * Creates new handler which assures consistent printing of lists
	 * prefixed with `start`, separated by `next` and suffixed by `end`
	 * @param startPrefixSpace if true then `start` token is prefixed with space
	 * @param start the string which has to be printed at the beginning of the list
	 * @param startSufficSpace if true then `start` token is suffixed with space
	 * @param nextPrefixSpace if true then `next` token is prefixed with space
	 * @param next the string which has to be used as separator before each next item
	 * @param nextSuffixSpace if true then `next` token is suffixed with space
	 * @param endPrefixSpace if true then `end` token is prefixed with space
	 * @param end the string which has to be printed after the list
	 * @return the {@link ListPrinter} whose {@link ListPrinter#printSeparatorIfAppropriate()} has to be called
	 * before printing of each item.
	 */
	private ListPrinter createListPrinter(boolean startPrefixSpace, String start, boolean startSufficSpace, boolean nextPrefixSpace, String next, boolean nextSuffixSpace, boolean endPrefixSpace, String end) {
		return new ListPrinter(printer, startPrefixSpace, start, startSufficSpace, nextPrefixSpace, next, nextSuffixSpace, endPrefixSpace, end);
	}

	private static final String QUALIFIED_NAME_SEPARATORS = ".$";

	/**
	 * splits qualified name to primitive tokens and sends them to TokenWriter individually
	 * @param qualifiedName to be sent qualified name
	 */
	public TokenWriter writeQualifiedName(String qualifiedName) {
		StringTokenizer st = new StringTokenizer(qualifiedName, QUALIFIED_NAME_SEPARATORS, true);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.length() == 1 && QUALIFIED_NAME_SEPARATORS.indexOf(token.charAt(0)) >= 0) {
				printer.writeSeparator(token);
			} else {
				printer.writeIdentifier(token);
			}
		}
		return printer;
	}

	private PrinterHelper getPrinterHelper() {
		return printer.getPrinterHelper();
	}

	/**
	 * Prints list of elements with defined delimiters using `printer`
	 * @param iterable the iterable of to be printed elements
	 * @param startKeyword the optional start keyword. It is always printed if the value is not null
	 * @param startPrefixSpace if true then `start` token is prefixed with space
	 * @param start the string which has to be printed at the beginning of the list
	 * @param startSuffixSpace if true then `start` token is suffixed with space
	 * @param nextPrefixSpace if true then `next` token is prefixed with space
	 * @param next the string which has to be used as separator before each next item
	 * @param nextSuffixSpace if true then `next` token is suffixed with space
	 * @param endPrefixSpace if true then `end` token is prefixed with space
	 * @param end the string which has to be printed after the list
	 * @param elementPrinter the {@link Consumer}, which is called once for each printer element of the `iterable`
	 */
	public <T> void printList(Iterable<T> iterable,
			String startKeyword,
			boolean startPrefixSpace, String start, boolean startSuffixSpace,
			boolean nextPrefixSpace, String next, boolean nextSuffixSpace,
			boolean endPrefixSpace, String end,
			Consumer<T> elementPrinter) {

		if (startKeyword != null) {
			printer.writeSpace().writeKeyword(startKeyword).writeSpace();
		}
		try (spoon.reflect.visitor.ListPrinter lp = createListPrinter(
				startPrefixSpace, start, startSuffixSpace,
				nextPrefixSpace, next, nextSuffixSpace,
				endPrefixSpace, end
			)) {
			for (T item : iterable) {
				lp.printSeparatorIfAppropriate();
				elementPrinter.accept(item);
			}
		}
	}
}
