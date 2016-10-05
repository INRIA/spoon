/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.visitor.printer;

import spoon.compiler.Environment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ElementPrinterHelper {
	private final DefaultJavaPrettyPrinter prettyPrinter;
	private final Environment env;
	private PrinterHelper printer;

	public ElementPrinterHelper(PrinterHelper printerHelper, DefaultJavaPrettyPrinter prettyPrinter, Environment env) {
		this.printer = printerHelper;
		this.prettyPrinter = prettyPrinter;
		this.env = env;
	}

	public void setPrinter(PrinterHelper printer) {
		this.printer = printer;
	}

	/**
	 * Writes the annotations for the given element.
	 */
	public void writeAnnotations(CtElement element) {
		for (CtAnnotation<?> annotation : element.getAnnotations()) {
			prettyPrinter.scan(annotation);
		}
	}

	public void writeModifiers(CtModifiable modifiable) {
		for (ModifierKind modifierKind : modifiable.getModifiers()) {
			printer.write(modifierKind.toString() + " ");
		}
	}

	public void visitCtNamedElement(CtNamedElement namedElement, CompilationUnit sourceCompilationUnit) {
		writeAnnotations(namedElement);
		if (env.isPreserveLineNumbers()) {
			printer.adjustPosition(namedElement, sourceCompilationUnit);
		}
	}

	public void writeExtendsClause(CtType<?> type) {
		if (type.getSuperclass() != null) {
			printer.write(" extends ");
			prettyPrinter.scan(type.getSuperclass());
		}
	}

	public void writeImplementsClause(CtType<?> type) {
		if (type.getSuperInterfaces().size() > 0) {
			printer.write(" implements ");
			for (CtTypeReference<?> ref : type.getSuperInterfaces()) {
				prettyPrinter.scan(ref);
				printer.write(" , ");
			}
			printer.removeLastChar();
		}
	}

	public void writeExecutableParameters(CtExecutable<?> executable) {
		printer.write("(");
		if (executable.getParameters().size() > 0) {
			for (CtParameter<?> p : executable.getParameters()) {
				prettyPrinter.scan(p);
				printer.write(", ");
			}
			printer.removeLastChar();
		}
		printer.write(")");
	}

	public void writeThrowsClause(CtExecutable<?> executable) {
		if (executable.getThrownTypes().size() > 0) {
			printer.write(" throws ");
			for (CtTypeReference<?> ref : executable.getThrownTypes()) {
				prettyPrinter.scan(ref);
				printer.write(", ");
			}
			printer.removeLastChar();
		}
	}

	/**
	 * Writes a statement.
	 */
	public void writeStatement(CtStatement statement) {
		prettyPrinter.scan(statement);
		if (!(statement instanceof CtBlock || statement instanceof CtIf || statement instanceof CtFor || statement instanceof CtForEach || statement instanceof CtWhile || statement instanceof CtTry
				|| statement instanceof CtSwitch || statement instanceof CtSynchronized || statement instanceof CtClass || statement instanceof CtComment)) {
			printer.write(";");
		}
		writeComment(statement, CommentOffset.AFTER);
	}

	public void writeElementList(List<CtTypeMember> elements) {
		for (CtTypeMember element : elements) {
			if (element instanceof CtConstructor && element.isImplicit()) {
				continue;
			}
			printer.writeln().writeTabs();
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
			printer.write(".class");
		} else if (value instanceof CtFieldReference) {
			prettyPrinter.scan(((CtFieldReference<?>) value).getDeclaringType());
			printer.write("." + ((CtFieldReference<?>) value).getSimpleName());
		} else if (value instanceof CtElement) {
			prettyPrinter.scan((CtElement) value);
		} else if (value instanceof String) {
			printer.write("\"" + value.toString() + "\"");
		} else if (value instanceof Collection) {
			printer.write("{");
			if (!((Collection<?>) value).isEmpty()) {
				for (Object obj : (Collection<?>) value) {
					writeAnnotationElement(factory, obj);
					printer.write(" ,");
				}
				printer.removeLastChar();
			}
			printer.write("}");
		} else if (value instanceof Object[]) {
			printer.write("{");
			if (((Object[]) value).length > 0) {
				for (Object obj : (Object[]) value) {
					writeAnnotationElement(factory, obj);
					printer.write(" ,");
				}
				printer.removeLastChar();
			}
			printer.write("}");
		} else if (value instanceof Enum) {
			prettyPrinter.getContext().enterIgnoreGenerics();
			prettyPrinter.scan(factory.Type().createReference(((Enum<?>) value).getDeclaringClass()));
			prettyPrinter.getContext().exitIgnoreGenerics();
			printer.write(".");
			printer.write(value.toString());
		} else {
			printer.write(value.toString());
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
		if (parameters.size() > 0) {
			printer.write('<');
			for (CtTypeParameter parameter : parameters) {
				prettyPrinter.scan(parameter);
				printer.write(", ");
			}
			printer.removeLastChar();
			printer.write('>');
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
		if (arguments != null && arguments.size() > 0) {
			printer.write("<");
			boolean isImplicitTypeReference = true;
			for (CtTypeReference<?> argument : arguments) {
				if (!argument.isImplicit()) {
					isImplicitTypeReference = false;
					prettyPrinter.scan(argument);
					printer.write(", ");
				}
			}
			if (!isImplicitTypeReference) {
				printer.removeLastChar();
			}
			printer.write(">");
		}
	}

	/**
	 * Write the compilation unit header.
	 */
	public void writeHeader(List<CtType<?>> types, Collection<CtTypeReference<?>> imports) {
		if (!types.isEmpty()) {
			for (CtType<?> ctType : types) {
				writeComment(ctType, CommentOffset.TOP_FILE);
				printer.writeln().writeln().writeTabs();
			}
			prettyPrinter.scan(types.get(0).getPackage());
			printer.writeln().writeln().writeTabs();
			if (env.isAutoImports()) {
				for (CtTypeReference<?> ref : imports) {
					printer.write("import " + ref.getQualifiedName() + ";").writeln().writeTabs();
				}
			}
			printer.writeln().writeTabs();
		}
	}

	public void writeComment(CtComment comment) {
		if (!env.isCommentsEnabled() || comment == null) {
			return;
		}
		prettyPrinter.scan(comment);
		printer.writeln().writeTabs();
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
			if (comment.getCommentType() == CtComment.CommentType.FILE && offset == CommentOffset.TOP_FILE) {
				commentsToPrint.add(comment);
				continue;
			}
			if (comment.getCommentType() == CtComment.CommentType.FILE) {
				continue;
			}
			if (comment.getPosition() == null || element.getPosition() == null) {
				if (offset == CommentOffset.BEFORE) {
					commentsToPrint.add(comment);
				}
				continue;
			}
			final int line = element.getPosition().getLine();
			final int sourceEnd = element.getPosition().getSourceEnd();
			if (offset == CommentOffset.BEFORE && (comment.getPosition().getLine() < line || sourceEnd >= comment.getPosition().getSourceEnd())) {
				commentsToPrint.add(comment);
			} else if (offset == CommentOffset.AFTER && comment.getPosition().getSourceStart() > sourceEnd) {
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

	public void writeIfOrLoopBlock(CtStatement block) {
		if (block != null) {
			if (!block.isImplicit() && (block instanceof CtBlock || block instanceof CtIf)) {
				printer.write(" ");
			}
			if (!(block instanceof CtBlock) && !(block instanceof CtIf)) {
				printer.incTab();
				printer.writeln().writeTabs();
			}
			writeStatement(block);
			if (!(block instanceof CtBlock) && !(block instanceof CtIf)) {
				printer.decTab().writeln().writeTabs();
			}
			if (!block.isImplicit()) {
				if (!block.isParentInitialized() || (!(block.getParent() instanceof CtFor) && !(block.getParent() instanceof CtForEach) && !(block.getParent() instanceof CtIf))) {
					printer.write(" ");
				}
			}
		} else {
			printer.write(";");
		}
	}
}
