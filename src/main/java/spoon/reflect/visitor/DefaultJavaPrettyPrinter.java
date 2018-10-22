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

import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.PrintingContext.Writable;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;
import spoon.reflect.visitor.printer.CommentOffset;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A visitor for generating Java code from the program compile-time model.
 */
public class DefaultJavaPrettyPrinter implements CtVisitor, PrettyPrinter {

	/**
	 * Java file extension (.java).
	 */
	public static final String JAVA_FILE_EXTENSION = ".java";

	/**
	 * Package declaration file name.
	 */
	public static final String JAVA_PACKAGE_DECLARATION = "package-info" + JAVA_FILE_EXTENSION;

	/**
	 * Module declaration file name.
	 */
	public static final String JAVA_MODULE_DECLARATION = "module-info" + JAVA_FILE_EXTENSION;

	/**
	 * Line separator which is used by the system
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * The star at the beginning of a block/JavaDoc comment line
	 */
	public static final String COMMENT_STAR = " * ";

	/**
	 * The end of a block/JavaDoc comment
	 */
	public static final String BLOCK_COMMENT_END = " */";

	/**
	 * The beginning of a JavaDoc comment
	 */
	public static final String JAVADOC_START = "/**";

	/**
	 * The beginning of an inline comment
	 */
	public static final String INLINE_COMMENT_START = "// ";

	/**
	 * The beginning of a block comment
	 */
	public static final String BLOCK_COMMENT_START = "/* ";

	/**
	 * The printing context.
	 *
	 * since Spoon 7.1.0, use {{@link #getContext()}}
	 */
	private PrintingContext context = new PrintingContext();

	/** get the import scanner of this pretty printer */
	public ImportScanner getImportsContext() {
		return importsContext;
	}

	/**
	 * Handle imports of classes.
	 */
	private ImportScanner importsContext;

	/**
	 * Environment which Spoon is executed.
	 */
	protected Environment env;

	/**
	 * Token detector, which delegates tokens to {@link TokenWriter}
	 */
	private TokenWriter printer;

	/**
	 * Element printer helper.
	 */
	private ElementPrinterHelper elementPrinterHelper;

	/**
	 * Compilation unit we are printing.
	 */
	protected CompilationUnit sourceCompilationUnit;

	/**
	 * Imports computed
	 */
	Set<CtImport> imports;

	/**
	 * Creates a new code generator visitor.
	 */
	public DefaultJavaPrettyPrinter(Environment env) {
		this.env = env;
		this.imports = new HashSet<>();
		setPrinterTokenWriter(new DefaultTokenWriter(new PrinterHelper(env)));
		if (env.isAutoImports()) {
			this.importsContext = new ImportScannerImpl();
		} else {
			this.importsContext = new MinimalImportScanner();
		}
	}

	/**
	 * @return current line separator. By default there is CR LF, LF or CR depending on the Operation system
	 * defined by System.getProperty("line.separator")
	 */
	public String getLineSeparator() {
		return getPrinterHelper().getLineSeparator();
	}

	/**
	 * @param lineSeparator characters which will be printed as End of line.
	 * By default there is System.getProperty("line.separator")
	 */
	public DefaultJavaPrettyPrinter setLineSeparator(String lineSeparator) {
		getPrinterHelper().setLineSeparator(lineSeparator);
		return this;
	}

	/**
	 * Enters an expression.
	 */
	protected void enterCtExpression(CtExpression<?> e) {
		if (!(e instanceof CtStatement)) {
			elementPrinterHelper.writeComment(e, CommentOffset.BEFORE);
		}
		getPrinterHelper().mapLine(e, sourceCompilationUnit);
		if (shouldSetBracket(e)) {
			context.parenthesedExpression.push(e);
			printer.writeSeparator("(");
		}
		if (!e.getTypeCasts().isEmpty()) {
			for (CtTypeReference<?> r : e.getTypeCasts()) {
				printer.writeSeparator("(");
				scan(r);
				printer.writeSeparator(")").writeSpace();
				printer.writeSeparator("(");
				context.parenthesedExpression.push(e);
			}
		}
	}

	/**
	 * Enters a statement.
	 */
	protected void enterCtStatement(CtStatement s) {
		elementPrinterHelper.writeComment(s, CommentOffset.BEFORE);
		getPrinterHelper().mapLine(s, sourceCompilationUnit);
		if (!context.isNextForVariable()) {
			//TODO AnnotationLoopTest#testAnnotationDeclaredInForInit expects that annotations of next for variables are not printed
			//but may be correct is that the next variables are not annotated, because they might have different annotation then first param!
			elementPrinterHelper.writeAnnotations(s);
		}
		if (!context.isFirstForVariable() && !context.isNextForVariable()) {
			if (s.getLabel() != null) {
				printer.writeIdentifier(s.getLabel()).writeSpace().writeSeparator(":").writeSpace();
			}
		}
	}

	/**
	 * Exits a statement.
	 */
	protected void exitCtStatement(CtStatement statement) {
		if (!(statement instanceof CtBlock || statement instanceof CtIf || statement instanceof CtFor || statement instanceof CtForEach || statement instanceof CtWhile || statement instanceof CtTry
				|| statement instanceof CtSwitch || statement instanceof CtSynchronized || statement instanceof CtClass || statement instanceof CtComment)) {
			if (context.isStatement(statement) && !context.isFirstForVariable() && !context.isNextForVariable()) {
				printer.writeSeparator(";");
			}
		}
		elementPrinterHelper.writeComment(statement, CommentOffset.AFTER);
	}

	/**
	 * Exits an expression.
	 */
	protected void exitCtExpression(CtExpression<?> e) {
		while ((!context.parenthesedExpression.isEmpty()) && e == context.parenthesedExpression.peek()) {
			context.parenthesedExpression.pop();
			printer.writeSeparator(")");
		}
		if (!(e instanceof CtStatement)) {
			elementPrinterHelper.writeComment(e, CommentOffset.AFTER);
		}
	}

	/**
	 * Make the imports for a given type.
     *
	 */
	private Collection<CtImport> computeImports(CtType<?> type) {
		context.currentTopLevel = type;
		importsContext.computeImports(context.currentTopLevel);
		return importsContext.getAllImports();
	}

	/**
	 * This method is called by {@link #scan(CtElement)} when entering a scanned element.
	 * To be overridden to implement specific behavior.
	 *
	 * Same KISS design as for {@link CtScanner}.
	 */
	protected void enter(CtElement e) {
	}

	/**
	 * This method is called by {@link #scan(CtElement)} when entering a scanned element.
	 * To be overridden to implement specific behavior.
	 */
	protected void exit(CtElement e) {
	}

	/**
	 * The generic scan method for an element.
	 */
	public DefaultJavaPrettyPrinter scan(CtElement e) {
		if (e != null) {
			enter(e);
			context.elementStack.push(e);
			if (env.isPreserveLineNumbers()) {
				if (!(e instanceof CtNamedElement)) {
					getPrinterHelper().adjustStartPosition(e);
				}
			}
			try {
				e.accept(this);
			} catch (SpoonException ex) {
				throw ex;
			} catch (Exception ex) {
				String elementInfo = e.getClass().getName();
				elementInfo += " on path " + getPath(e) + "\n";
				if (e.getPosition().isValidPosition()) {
					elementInfo += "at position " + e.getPosition().toString() + " ";
				}
				throw new SpoonException("Printing of " + elementInfo + "failed", ex);
			}
			context.elementStack.pop();
			exit(e);
		}
		return this;
	}

	private static String getPath(CtElement ele) {
		StringBuilder sb = new StringBuilder();
		addParentPath(sb, ele);
		if (ele instanceof CtVariableAccess) {
			sb.append(':').append(((CtVariableAccess) ele).getVariable().getSimpleName());
		}
		return sb.toString();
	}
	private static void addParentPath(StringBuilder sb, CtElement ele) {
		if (ele == null || (ele instanceof CtPackage && ((CtPackage) ele).isUnnamedPackage())) {
			return;
		}
		if (ele.isParentInitialized()) {
			addParentPath(sb, ele.getParent());
		}
		sb.append("\n\t").append(ele.getClass().getSimpleName());
		if (ele instanceof CtNamedElement) {
			sb.append(":").append(((CtNamedElement) ele).getSimpleName());
		} else if (ele instanceof CtReference) {
			sb.append(":").append(((CtReference) ele).getSimpleName());
		}
	}

	private boolean shouldSetBracket(CtExpression<?> e) {
		if (!e.getTypeCasts().isEmpty()) {
			return true;
		}
		try {
			if ((e.getParent() instanceof CtBinaryOperator) || (e.getParent() instanceof CtUnaryOperator)) {
				return (e instanceof CtTargetedExpression) || (e instanceof CtAssignment) || (e instanceof CtConditional) || (e instanceof CtUnaryOperator) || e instanceof CtBinaryOperator;
			}
			if (e.getParent() instanceof CtTargetedExpression) {
				return (e instanceof CtBinaryOperator) || (e instanceof CtAssignment) || (e instanceof CtConditional) || (e instanceof CtUnaryOperator);
			}
		} catch (ParentNotInitializedException ex) {
			// nothing we accept not to have a parent
		}
		return false;
	}

	/**
	 * Gets the currently pretty-printed string.
	 */
	@Override
	public String toString() {
		return printer.getPrinterHelper().toString();
	}

	@Override
	public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		elementPrinterHelper.writeAnnotations(annotation);
		printer.writeSeparator("@");
		scan(annotation.getAnnotationType());
		if (!annotation.getValues().isEmpty()) {
			elementPrinterHelper.printList(annotation.getValues().entrySet(),
				null, false, "(", false, false, ",", true, false, ")",
				e -> {
					if ((annotation.getValues().size() == 1 && "value".equals(e.getKey())) == false) {
						//it is not a default value attribute. We must print a attribute name too.
						printer.writeIdentifier(e.getKey()).writeSpace().writeOperator("=").writeSpace();
					}
					elementPrinterHelper.writeAnnotationElement(annotation.getFactory(), e.getValue());
				});
		}
	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
		visitCtType(annotationType);
		printer.writeSeparator("@").writeKeyword("interface")
			.writeSpace().writeIdentifier(annotationType.getSimpleName())
			.writeSpace().writeSeparator("{").incTab();

		elementPrinterHelper.writeElementList(annotationType.getTypeMembers());
		printer.decTab().writeSeparator("}");
	}

	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable impl) {
		elementPrinterHelper.writeComment(impl);
		elementPrinterHelper.writeAnnotations(impl);
		elementPrinterHelper.writeModifiers(impl);
		scan(impl.getBody());
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		printCtArrayAccess(arrayRead);
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		printCtArrayAccess(arrayWrite);
	}

	private <T, E extends CtExpression<?>> void printCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		enterCtExpression(arrayAccess);
		scan(arrayAccess.getTarget());
		printer.writeSeparator("[");
		scan(arrayAccess.getIndexExpression());
		printer.writeSeparator("]");
		exitCtExpression(arrayAccess);
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		if (reference.isImplicit()) {
			return;
		}
		scan(reference.getComponentType());
		if (!context.skipArray()) {
			printer.writeSeparator("[").writeSeparator("]");
		}
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
		enterCtStatement(asserted);
		printer.writeKeyword("assert").writeSpace();
		scan(asserted.getAssertExpression());
		if (asserted.getExpression() != null) {
			printer.writeSpace().writeSeparator(":").writeSpace();
			scan(asserted.getExpression());
		}
		exitCtStatement(asserted);
	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
		enterCtStatement(assignement);
		enterCtExpression(assignement);
		scan(assignement.getAssigned());
		printer.writeSpace().writeOperator("=").writeSpace();
		scan(assignement.getAssignment());
		exitCtExpression(assignement);
		exitCtStatement(assignement);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		enterCtExpression(operator);
		scan(operator.getLeftHandOperand());
		printer.writeSpace();
		printer.writeOperator(OperatorHelper.getOperatorText(operator.getKind()));
		printer.writeSpace();
		try (Writable _context = context.modify()) {
			if (operator.getKind() == BinaryOperatorKind.INSTANCEOF) {
				_context.forceWildcardGenerics(true);
			}
			scan(operator.getRightHandOperand());
		}
		exitCtExpression(operator);
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		enterCtStatement(block);
		if (!block.isImplicit()) {
			printer.writeSeparator("{");
		}
		printer.incTab();
		for (CtStatement statement : block.getStatements()) {
			if (!statement.isImplicit()) {
				printer.writeln();
				elementPrinterHelper.writeStatement(statement);
			}
		}
		printer.decTab();
		getPrinterHelper().adjustEndPosition(block);
		if (env.isPreserveLineNumbers()) {
			if (!block.isImplicit()) {
				printer.writeSeparator("}");
			}
		} else {
			printer.writeln();
			if (!block.isImplicit()) {
				printer.writeSeparator("}");
			}
		}
		exitCtStatement(block);
	}

	@Override
	public void visitCtBreak(CtBreak breakStatement) {
		enterCtStatement(breakStatement);
		printer.writeKeyword("break");
		if (breakStatement.getTargetLabel() != null) {
			printer.writeSpace().writeKeyword(breakStatement.getTargetLabel());
		}
		exitCtStatement(breakStatement);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		enterCtStatement(caseStatement);
		if (caseStatement.getCaseExpression() != null) {
			printer.writeKeyword("case").writeSpace();
			// writing enum case expression
			if (caseStatement.getCaseExpression() instanceof CtFieldAccess) {
				final CtFieldReference variable = ((CtFieldAccess) caseStatement.getCaseExpression()).getVariable();
				// In noclasspath mode, we don't have always the type of the declaring type.
				if (variable.getType() != null
						&& variable.getDeclaringType() != null
						&& variable.getType().getQualifiedName().equals(variable.getDeclaringType().getQualifiedName())) {
					printer.writeIdentifier(variable.getSimpleName());
				} else {
					scan(caseStatement.getCaseExpression());
				}
			} else {
				scan(caseStatement.getCaseExpression());
			}
		} else {
			printer.writeKeyword("default");
		}
		printer.writeSpace().writeSeparator(":").incTab();

		for (CtStatement statement : caseStatement.getStatements()) {
			printer.writeln();
			elementPrinterHelper.writeStatement(statement);
		}
		printer.decTab();
		exitCtStatement(caseStatement);
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		elementPrinterHelper.writeComment(catchBlock, CommentOffset.BEFORE);
		printer.writeSpace().writeKeyword("catch").writeSpace().writeSeparator("(");
		CtCatchVariable<? extends Throwable> parameter = catchBlock.getParameter();
		if (parameter != null && parameter.getMultiTypes().size() > 1) {
			elementPrinterHelper.printList(parameter.getMultiTypes(),
					null, false, null, false, true, "|", true, false, null,
					type -> scan(type));
			printer.writeSpace().writeIdentifier(parameter.getSimpleName());
		} else {
			scan(parameter);
		}
		printer.writeSeparator(")").writeSpace();
		scan(catchBlock.getBody());
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		context.pushCurrentThis(ctClass);
		if (ctClass.getSimpleName() != null && !CtType.NAME_UNKNOWN.equals(ctClass.getSimpleName()) && !ctClass.isAnonymous()) {
			visitCtType(ctClass);
			if (ctClass.isLocalType()) {
				printer.writeKeyword("class").writeSpace().writeIdentifier(ctClass.getSimpleName().replaceAll("^[0-9]*", ""));
			} else {
				printer.writeKeyword("class").writeSpace().writeIdentifier(ctClass.getSimpleName());
			}

			elementPrinterHelper.writeFormalTypeParameters(ctClass);
			elementPrinterHelper.writeExtendsClause(ctClass);
			elementPrinterHelper.writeImplementsClause(ctClass);
		}
		printer.writeSpace().writeSeparator("{").incTab();
		elementPrinterHelper.writeElementList(ctClass.getTypeMembers());
		getPrinterHelper().adjustEndPosition(ctClass);
		printer.decTab().writeSeparator("}");
		context.popCurrentThis();
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		elementPrinterHelper.writeAnnotations(typeParameter);
		printer.writeIdentifier(typeParameter.getSimpleName());
		if (typeParameter.getSuperclass() != null && typeParameter.getSuperclass().isImplicit() == false) {
			printer.writeSpace().writeKeyword("extends").writeSpace();
			scan(typeParameter.getSuperclass());
		}
	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		enterCtExpression(conditional);
		CtExpression<Boolean> condition = conditional.getCondition();
		boolean parent;
		try {
			parent = conditional.getParent() instanceof CtAssignment || conditional.getParent() instanceof CtVariable;
		} catch (ParentNotInitializedException ex) {
			// nothing if we have no parent
			parent = false;
		}
		if (parent) {
			printer.writeSeparator("(");
		}
		scan(condition);
		if (parent) {
			printer.writeSeparator(")");
		}
		printer.writeSpace().writeOperator("?").writeSpace();
		CtExpression<T> thenExpression = conditional.getThenExpression();
		scan(thenExpression);
		printer.writeSpace().writeOperator(":").writeSpace();

		CtExpression<T> elseExpression = conditional.getElseExpression();
		boolean isAssign;
		if ((isAssign = elseExpression instanceof CtAssignment)) {
			printer.writeSeparator("(");
		}
		scan(elseExpression);
		if (isAssign) {
			printer.writeSeparator(")");
		}
		exitCtExpression(conditional);
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> constructor) {
		elementPrinterHelper.writeComment(constructor);
		elementPrinterHelper.visitCtNamedElement(constructor, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(constructor);
		elementPrinterHelper.writeFormalTypeParameters(constructor);
		if (!constructor.getFormalCtTypeParameters().isEmpty()) {
			printer.writeSpace();
		}
		if (constructor.getDeclaringType() != null) {
			if (constructor.getDeclaringType().isLocalType()) {
				printer.writeIdentifier(constructor.getDeclaringType().getSimpleName().replaceAll("^[0-9]*", ""));
			} else {
				printer.writeIdentifier(constructor.getDeclaringType().getSimpleName());
			}
		}
		elementPrinterHelper.writeExecutableParameters(constructor);
		elementPrinterHelper.writeThrowsClause(constructor);
		printer.writeSpace();
		scan(constructor.getBody());
	}

	@Override
	public void visitCtContinue(CtContinue continueStatement) {
		enterCtStatement(continueStatement);
		printer.writeKeyword("continue");
		if (continueStatement.getTargetLabel() != null) {
			printer.writeSpace().writeIdentifier(continueStatement.getTargetLabel());
		}
		exitCtStatement(continueStatement);
	}

	@Override
	public void visitCtDo(CtDo doLoop) {
		enterCtStatement(doLoop);
		printer.writeKeyword("do");
		elementPrinterHelper.writeIfOrLoopBlock(doLoop.getBody());
		printer.writeKeyword("while").writeSpace().writeSeparator("(");
		scan(doLoop.getLoopingExpression());
		printer.writeSpace().writeSeparator(")");
		exitCtStatement(doLoop);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		visitCtType(ctEnum);
		printer.writeKeyword("enum").writeSpace().writeIdentifier(ctEnum.getSimpleName());
		elementPrinterHelper.writeImplementsClause(ctEnum);
		context.pushCurrentThis(ctEnum);
		printer.writeSpace().writeSeparator("{").incTab().writeln();

		if (ctEnum.getEnumValues().isEmpty()) {
			printer.writeSeparator(";").writeln();
		} else {
			elementPrinterHelper.printList(ctEnum.getEnumValues(),
					null, false, null, false, false, ",", false, false, ";",
					enumValue -> {
						printer.writeln();
						scan(enumValue);
					});
		}

		elementPrinterHelper.writeElementList(ctEnum.getTypeMembers());
		printer.decTab().writeSeparator("}");
		context.popCurrentThis();
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		//it is not called during printing of sources. Use shortcut and print directly to PrinterHelper
		printer.getPrinterHelper().write(reference.getSignature());
	}

	@Override
	public <T> void visitCtField(CtField<T> f) {
		elementPrinterHelper.writeComment(f, CommentOffset.BEFORE);
		elementPrinterHelper.visitCtNamedElement(f, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(f);
		scan(f.getType());
		printer.writeSpace();
		printer.writeIdentifier(f.getSimpleName());

		if (f.getDefaultExpression() != null) {
			printer.writeSpace().writeOperator("=").writeSpace();
			scan(f.getDefaultExpression());
		}
		printer.writeSeparator(";");
		elementPrinterHelper.writeComment(f, CommentOffset.AFTER);
	}

	@Override
	public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {
		elementPrinterHelper.visitCtNamedElement(enumValue, sourceCompilationUnit);
		elementPrinterHelper.writeComment(enumValue, CommentOffset.BEFORE);
		printer.writeIdentifier(enumValue.getSimpleName());
		if (enumValue.getDefaultExpression() != null) {
			CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) enumValue.getDefaultExpression();
			if (!constructorCall.isImplicit()) {
				elementPrinterHelper.printList(constructorCall.getArguments(), null, false, "(", false, false, ",", true, false, ")", expr -> scan(expr));
			}
			if (constructorCall instanceof CtNewClass) {
				scan(((CtNewClass<?>) constructorCall).getAnonymousClass());
			}
		}
	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		printCtFieldAccess(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		printCtFieldAccess(fieldWrite);
	}

	private boolean isImported(CtFieldReference fieldReference) {
		CtImport fieldImport = fieldReference.getFactory().createImport(fieldReference);

		if (this.imports.contains(fieldImport)) {
			return true;
		} else {
			if (fieldReference.getDeclaringType() == null) {
				return false;
			}
			CtTypeReference staticTypeMemberReference = fieldReference.getFactory().Type().createWildcardStaticTypeMemberReference(fieldReference.getDeclaringType());
			CtImport staticClassImport = fieldReference.getFactory().createImport(staticTypeMemberReference);
			return this.imports.contains(staticClassImport);
		}
	}

	private boolean isImported(CtExecutableReference executableReference) {
		CtImport executableImport = executableReference.getFactory().createImport(executableReference);

		if (this.imports.contains(executableImport)) {
			return true;
		} else {
			if (executableReference.getDeclaringType() == null) {
				return false;
			}
			CtTypeReference staticTypeMemberReference = executableReference.getFactory().Type().createWildcardStaticTypeMemberReference(executableReference.getDeclaringType());
			CtImport staticClassImport = executableReference.getFactory().createImport(staticTypeMemberReference);
			return this.imports.contains(staticClassImport);
		}
	}

	private <T> void printCtFieldAccess(CtFieldAccess<T> f) {
		enterCtExpression(f);
		try (Writable _context = context.modify()) {
			if ((f.getVariable().isStatic() || "class".equals(f.getVariable().getSimpleName())) && f.getTarget() instanceof CtTypeAccess) {
				_context.ignoreGenerics(true);
			}
			CtExpression<?> target = f.getTarget();
			if (target != null) {
				boolean isInitializeStaticFinalField = isInitializeStaticFinalField(f.getTarget());
				boolean isStaticField = f.getVariable().isStatic();
				boolean isImportedField = this.isImported(f.getVariable());

				if (!isInitializeStaticFinalField && !(isStaticField && isImportedField)) {
					if (target.isImplicit() && !(f.getVariable().getFieldDeclaration() == null && this.env.getNoClasspath())) {
						/*
						 * target is implicit, check whether there is no conflict with an local variable, catch variable or parameter
						 * in case of conflict make it explicit, otherwise the field access is shadowed by that variable.
						 * Search for potential variable declaration until we found a class which declares or inherits this field
						 */
						final CtField<?> field = f.getVariable().getFieldDeclaration();
						if (field != null) {
							final String fieldName = field.getSimpleName();
							CtVariable<?> var = f.getVariable().map(new PotentialVariableDeclarationFunction(fieldName)).first();
							if (var != field) {
								//another variable declaration was found which is hiding the field declaration for this field access. Make the field access explicit
								target.setImplicit(false);
							}
						} else {
							//There is a model inconsistency
							printer.writeComment(f.getFactory().createComment("ERROR: Missing field \"" + f.getVariable().getSimpleName() + "\", please check your model. The code may not compile.", CommentType.BLOCK)).writeSpace();
						}
					}
					// the implicit drives the separator
					if (!target.isImplicit()) {
						scan(target);
						printer.writeSeparator(".");
					}
				}
				_context.ignoreStaticAccess(true);
			}
			scan(f.getVariable());
		}
		exitCtExpression(f);
	}

	/**
	 * Check if the target expression is a static final field initialized in a static anonymous block.
	 */
	private <T> boolean isInitializeStaticFinalField(CtExpression<T> targetExp) {
		final CtElement parent;
		final CtAnonymousExecutable anonymousParent;
		try {
			parent = targetExp.getParent();
			anonymousParent = targetExp.getParent(CtAnonymousExecutable.class);
		} catch (ParentNotInitializedException e) {
			return false;
		}
		return parent instanceof CtFieldWrite
				&& targetExp.equals(((CtFieldWrite) parent).getTarget())
				&& anonymousParent != null
				&& ((CtFieldWrite) parent).getVariable() != null
				&& ((CtFieldWrite) parent).getVariable().getModifiers().contains(ModifierKind.STATIC)
				&& ((CtFieldWrite) parent).getVariable().getModifiers().contains(ModifierKind.FINAL);
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		try {
			enterCtExpression(thisAccess);

			// we only write qualified this when this is required
			// this is good both in fully-qualified mode and in readable (with-imports) mode
			// the implicit information is used for analysis (e.g. is visibility caused by implicit bugs?) but
			// not for pretty-printing
			CtTypeAccess target = (CtTypeAccess) thisAccess.getTarget();
			CtTypeReference targetType = target.getAccessedType();

			// readable mode as close as possible to the original code
			if (thisAccess.isImplicit()) {
				// write nothing, "this" is implicit and we unfortunately cannot always know
				// what the good target is in JDTTreeBuilder
				return;
			}

			// the simplest case: we always print "this" if we're in the top-level class,
			// this is shorter (no qualified this), explicit, and less fragile wrt transformation
			if (targetType == null || (thisAccess.getParent(CtType.class) != null && thisAccess.getParent(CtType.class).isTopLevel())) {
				printer.writeKeyword("this");
				return; // still go through finally block below
			}

			// we cannot have fully-qualified this in anonymous classes
			// we simply print "this" and it always works
			// this has to come after the implicit test just before
			if (targetType.isAnonymous()) {
				printer.writeKeyword("this");
				return;
			}

			// complex case of qualified this
			if (!context.currentThis.isEmpty()) {

				CtType lastType = context.currentThis.peekFirst().type;
				String lastTypeQualifiedName = lastType.getQualifiedName();
				String targetTypeQualifiedName = targetType.getQualifiedName();

				if (!lastTypeQualifiedName.equals(targetTypeQualifiedName)) {
					if (!targetType.isImplicit()) {
						visitCtTypeReferenceWithoutGenerics(targetType);
						printer.writeSeparator(".");
					}
					printer.writeKeyword("this");
					return;
				}
			}

			// the default super simple case only comes at the end
			printer.writeKeyword("this");
		} finally {
			exitCtExpression(thisAccess);
		}
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		enterCtExpression(f);
		if (f.getTarget() != null) {
			scan(f.getTarget());
			printer.writeSeparator(".");
		}
		printer.writeKeyword("super");

		exitCtExpression(f);
	}

	@Override
	public void visitCtJavaDoc(CtJavaDoc comment) {
		visitCtComment(comment);
	}

	@Override
	public void visitCtJavaDocTag(CtJavaDocTag docTag) {
		/*
		 * is not called during normal printing of java sources.
		 * It can be called only when CtJavaDocTag has to be printed directly.
		 * E.g. from CtJavaDocTag#toString
		 * Write directly to PrinterHelper, because java doc tag is not a java token. Normally it is part of COMMENT token.
		 */
		CommentHelper.printJavaDocTag(printer.getPrinterHelper(), docTag);
	}

	@Override
	public void visitCtImport(CtImport ctImport) {
		if (ctImport.getImportKind() != null) {
			printer.writeKeyword("import");
			printer.writeSpace();

			switch (ctImport.getImportKind()) {
				case TYPE:
					visitCtTypeReference((CtTypeReference) ctImport.getReference());
					break;

				case METHOD:
					printer.writeKeyword("static");
					printer.writeSpace();
					visitCtExecutableReference((CtExecutableReference) ctImport.getReference());
					break;

				case FIELD:
					printer.writeKeyword("static");
					printer.writeSpace();
					visitCtFieldReference((CtFieldReference) ctImport.getReference());
					break;

				case ALL_TYPES:
					visitCtPackageReference((CtPackageReference) ctImport.getReference());
					printer.writeSeparator(".");
					printer.writeIdentifier("*");
					break;

				case ALL_STATIC_MEMBERS:
					printer.writeKeyword("static");
					printer.writeSpace();
					visitCtTypeReference((CtTypeReference) ctImport.getReference());
					break;
			}
			printer.writeSeparator(";");
			printer.writeln();
		}
	}

	@Override
	public void visitCtModule(CtModule module) {
		enter(module);
		if (module.isOpenModule()) {
			printer.writeKeyword("open").writeSpace();
		}
		printer.writeKeyword("module").writeSpace().writeIdentifier(module.getSimpleName());
		printer.writeSpace().writeSeparator("{").incTab().writeln();

		for (CtModuleDirective moduleDirective : module.getModuleDirectives()) {
			scan(moduleDirective);
		}

		printer.decTab().writeSeparator("}");
		exit(module);
	}

	@Override
	public void visitCtModuleReference(CtModuleReference moduleReference) {
		printer.writeIdentifier(moduleReference.getSimpleName());
	}

	@Override
	public void visitCtPackageExport(CtPackageExport moduleExport) {
		if (moduleExport.isOpenedPackage()) {
			printer.writeKeyword("opens");
		} else {
			printer.writeKeyword("exports");
		}
		printer.writeSpace();

		visitCtPackageReference(moduleExport.getPackageReference());
		if (!moduleExport.getTargetExport().isEmpty()) {
			this.elementPrinterHelper.printList(moduleExport.getTargetExport(),
				null, false, " to", true, false, ",", true, false, null,
				moduleReference -> scan(moduleReference));
		}
		printer.writeSeparator(";").writeln();
	}

	@Override
	public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {
		printer.writeKeyword("requires").writeSpace();

		if (!moduleRequirement.getRequiresModifiers().isEmpty()) {
			this.elementPrinterHelper.printList(moduleRequirement.getRequiresModifiers(),
				null, false, null, false, false, " ", false, false, " ",
				modifier -> printer.writeKeyword(modifier.name().toLowerCase()));
		}

		scan(moduleRequirement.getModuleReference());
		printer.writeSeparator(";").writeln();
	}

	@Override
	public void visitCtProvidedService(CtProvidedService moduleProvidedService) {
		printer.writeKeyword("provides").writeSpace();
		scan(moduleProvidedService.getServiceType());
		this.elementPrinterHelper.printList(moduleProvidedService.getImplementationTypes(),
			null, false, " with", true, false, ",", true, false, null,
			implementations -> scan(implementations));
		printer.writeSeparator(";").writeln();
	}

	@Override
	public void visitCtUsedService(CtUsedService usedService) {
		printer.writeKeyword("uses").writeSpace();
		scan(usedService.getServiceType());
		printer.writeSeparator(";").writeln();
	}

	@Override
	public void visitCtComment(CtComment comment) {
		if (!env.isCommentsEnabled() && context.elementStack.size() > 1) {
			return;
		}
		printer.writeComment(comment);
	}

	@Override
	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
		enterCtExpression(annotationFieldAccess);
		try (Writable _context = context.modify()) {
			if (annotationFieldAccess.getTarget() != null) {
				scan(annotationFieldAccess.getTarget());
				printer.writeSeparator(".");
				_context.ignoreStaticAccess(true);
			}
			_context.ignoreGenerics(true);
			scan(annotationFieldAccess.getVariable());
			printer.writeSeparator("(").writeSeparator(")");
		}
		exitCtExpression(annotationFieldAccess);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		boolean isStatic = "class".equals(reference.getSimpleName()) || !"super".equals(reference.getSimpleName()) && reference.isStatic();

		boolean printType = true;

		if (reference.isFinal() && reference.isStatic()) {
			CtTypeReference<?> declTypeRef = reference.getDeclaringType();
			if (declTypeRef.isAnonymous()) {
				//never print anonymous class ref
				printType = false;
			} else {
				if (context.isInCurrentScope(declTypeRef)) {
					//do not printType if we are in scope of that type
					printType = false;
				}
			}
		}

		if (isStatic && printType && !context.ignoreStaticAccess()) {
			try (Writable _context = context.modify().ignoreGenerics(true)) {
				scan(reference.getDeclaringType());
			}
			printer.writeSeparator(".");
		}
		if ("class".equals(reference.getSimpleName())) {
			printer.writeKeyword("class");
		} else {
			printer.writeIdentifier(reference.getSimpleName());
		}
	}

	@Override
	public void visitCtFor(CtFor forLoop) {
		enterCtStatement(forLoop);
		printer.writeKeyword("for").writeSpace().writeSeparator("(");
		List<CtStatement> st = forLoop.getForInit();
		if (!st.isEmpty()) {
			try (Writable _context = context.modify().isFirstForVariable(true)) {
				scan(st.get(0));
			}
		}
		if (st.size() > 1) {
			try (Writable _context = context.modify().isNextForVariable(true)) {
				for (int i = 1; i < st.size(); i++) {
					printer.writeSeparator(",").writeSpace();
					scan(st.get(i));
				}
			}
		}
		printer.writeSeparator(";").writeSpace();
		scan(forLoop.getExpression());
		printer.writeSeparator(";");
		if (!forLoop.getForUpdate().isEmpty()) {
			printer.writeSpace();
		}
		elementPrinterHelper.printList(forLoop.getForUpdate(),
			null, false, null, false, true, ",", true, false, null,
			s -> scan(s));
		printer.writeSeparator(")");
		elementPrinterHelper.writeIfOrLoopBlock(forLoop.getBody());
		exitCtStatement(forLoop);
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {
		enterCtStatement(foreach);
		printer.writeKeyword("for").writeSpace().writeSeparator("(");
		scan(foreach.getVariable());
		printer.writeSpace().writeSeparator(":").writeSpace();
		scan(foreach.getExpression());
		printer.writeSeparator(")");
		elementPrinterHelper.writeIfOrLoopBlock(foreach.getBody());
		exitCtStatement(foreach);
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		enterCtStatement(ifElement);
		printer.writeKeyword("if").writeSpace().writeSeparator("(");
		scan(ifElement.getCondition());
		printer.writeSeparator(")");
		CtStatement thenStmt = ifElement.getThenStatement();
		elementPrinterHelper.writeIfOrLoopBlock(thenStmt);
		if (ifElement.getElseStatement() != null) {
			List<CtComment> comments = elementPrinterHelper.getComments(ifElement, CommentOffset.INSIDE);
			for (CtComment comment : comments) {
				SourcePosition thenPosition =
						ifElement.getThenStatement().getPosition().isValidPosition() ? ifElement.getThenStatement().getPosition() : ((CtBlock) ifElement.getThenStatement()).getStatement(0).getPosition();
				if (comment.getPosition().getSourceStart() > thenPosition.getSourceEnd()) {
					elementPrinterHelper.writeComment(comment);
				}
			}
			if (thenStmt instanceof CtBlock && !thenStmt.isImplicit()) {
				//add space after non implicit block
				printer.writeSpace();
			}
			printer.writeKeyword("else");
			elementPrinterHelper.writeIfOrLoopBlock(ifElement.getElseStatement());
		}
		exitCtStatement(ifElement);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		visitCtType(intrface);
		printer.writeKeyword("interface").writeSpace().writeIdentifier(intrface.getSimpleName());
		if (intrface.getFormalCtTypeParameters() != null) {
			elementPrinterHelper.writeFormalTypeParameters(intrface);
		}

		if (!intrface.getSuperInterfaces().isEmpty()) {
			elementPrinterHelper.printList(intrface.getSuperInterfaces(),
				"extends", false, null, false, true, ",", true, false, null,
				ref -> scan(ref));
		}
		context.pushCurrentThis(intrface);
		printer.writeSpace().writeSeparator("{").incTab();
		// Content
		elementPrinterHelper.writeElementList(intrface.getTypeMembers());
		printer.decTab().writeSeparator("}");
		context.popCurrentThis();
	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		enterCtStatement(invocation);
		enterCtExpression(invocation);
		if (invocation.getExecutable().isConstructor()) {
			// It's a constructor (super or this)
			elementPrinterHelper.writeActualTypeArguments(invocation.getExecutable());
			CtType<?> parentType;
			try {
				parentType = invocation.getParent(CtType.class);
			} catch (ParentNotInitializedException e) {
				parentType = null;
			}
			if (parentType != null && parentType.getQualifiedName() != null && parentType.getQualifiedName().equals(invocation.getExecutable().getDeclaringType().getQualifiedName())) {
				printer.writeKeyword("this");
			} else {
				if (invocation.getTarget() != null && !invocation.getTarget().isImplicit()) {
					scan(invocation.getTarget());
					printer.writeSeparator(".");
				}
				printer.writeKeyword("super");
			}
		} else {
			// It's a method invocation
			boolean isImported = this.isImported(invocation.getExecutable());
			if (!isImported) {
				try (Writable _context = context.modify()) {
					if (invocation.getTarget() instanceof CtTypeAccess) {
						_context.ignoreGenerics(true);
					}
					if (invocation.getTarget() != null && !invocation.getTarget().isImplicit()) {
						scan(invocation.getTarget());
						printer.writeSeparator(".");
					}
				}
			}

			elementPrinterHelper.writeActualTypeArguments(invocation);
			if (env.isPreserveLineNumbers()) {
				getPrinterHelper().adjustStartPosition(invocation);
			}
			printer.writeIdentifier(invocation.getExecutable().getSimpleName());
		}
		elementPrinterHelper.printList(invocation.getArguments(),
			null, false, "(", false, false, ",", true, false, ")",
			e -> scan(e));
		exitCtExpression(invocation);
		exitCtStatement(invocation);
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		enterCtExpression(literal);
		printer.writeLiteral(LiteralHelper.getLiteralToken(literal));
		exitCtExpression(literal);
	}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		enterCtStatement(localVariable);
		if (env.isPreserveLineNumbers()) {
			getPrinterHelper().adjustStartPosition(localVariable);
		}
		if (!context.isNextForVariable()) {
			elementPrinterHelper.writeModifiers(localVariable);
			if (localVariable.isInferred() && this.env.getComplianceLevel() >= 10) {
				getPrinterTokenWriter().writeKeyword("var");
			} else {
				scan(localVariable.getType());
			}
			printer.writeSpace();
		}
		printer.writeIdentifier(localVariable.getSimpleName());
		if (localVariable.getDefaultExpression() != null) {
			printer.writeSpace().writeOperator("=").writeSpace();
			scan(localVariable.getDefaultExpression());
		}
		exitCtStatement(localVariable);
	}

	@Override
	public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
		printer.writeIdentifier(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		if (env.isPreserveLineNumbers()) {
			getPrinterHelper().adjustStartPosition(catchVariable);
		}
		elementPrinterHelper.writeModifiers(catchVariable);
		scan(catchVariable.getType());
		printer.writeSpace();
		printer.writeIdentifier(catchVariable.getSimpleName());
	}

	@Override
	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
		printer.writeIdentifier(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		elementPrinterHelper.writeComment(m, CommentOffset.BEFORE);
		elementPrinterHelper.visitCtNamedElement(m, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(m);
		elementPrinterHelper.writeFormalTypeParameters(m);
		if (!m.getFormalCtTypeParameters().isEmpty()) {
			printer.writeSpace();
		}
		try (Writable _context = context.modify().ignoreGenerics(false)) {
			scan(m.getType());
		}
		printer.writeSpace();
		printer.writeIdentifier(m.getSimpleName());
		elementPrinterHelper.writeExecutableParameters(m);
		elementPrinterHelper.writeThrowsClause(m);
		if (m.getBody() != null) {
			printer.writeSpace();
			scan(m.getBody());
			if (m.getBody().getPosition().isValidPosition()) {
				if (m.getBody().getPosition().getCompilationUnit() == sourceCompilationUnit) {
					if (m.getBody().getStatements().isEmpty() || !(m.getBody().getStatements().get(m.getBody().getStatements().size() - 1) instanceof CtReturn)) {
						getPrinterHelper().putLineNumberMapping(m.getBody().getPosition().getEndLine());
					}
				} else {
					getPrinterHelper().undefineLine();
				}
			} else {
				getPrinterHelper().undefineLine();
			}
		} else {
			printer.writeSeparator(";");
		}
		elementPrinterHelper.writeComment(m, CommentOffset.AFTER);
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		elementPrinterHelper.writeComment(annotationMethod);
		elementPrinterHelper.visitCtNamedElement(annotationMethod, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(annotationMethod);
		scan(annotationMethod.getType());
		printer.writeSpace();
		printer.writeIdentifier(annotationMethod.getSimpleName());

		printer.writeSeparator("(").writeSeparator(")");
		if (annotationMethod.getDefaultExpression() != null) {
			printer.writeSpace().writeKeyword("default").writeSpace();
			scan(annotationMethod.getDefaultExpression());
		}
		printer.writeSeparator(";");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		enterCtExpression(newArray);
		boolean isNotInAnnotation;
		try {
			isNotInAnnotation = (newArray.getParent(CtAnnotationType.class) == null) && (newArray.getParent(CtAnnotation.class) == null);
		} catch (ParentNotInitializedException e) {
			isNotInAnnotation = true;
		}

		if (isNotInAnnotation) {
			CtTypeReference<?> ref = newArray.getType();

			if (ref != null) {
				printer.writeKeyword("new").writeSpace();
			}

			try (Writable _context = context.modify().skipArray(true)) {
				scan(ref);
			}
			for (int i = 0; ref instanceof CtArrayTypeReference; i++) {
				printer.writeSeparator("[");
				if (newArray.getDimensionExpressions().size() > i) {
					CtExpression<Integer> e = newArray.getDimensionExpressions().get(i);
					scan(e);
				}
				printer.writeSeparator("]");
				ref = ((CtArrayTypeReference) ref).getComponentType();
			}
		}
		if (newArray.getDimensionExpressions().isEmpty()) {
			elementPrinterHelper.printList(newArray.getElements(),
				null, false, "{", true, false, ",", true, true, "}",
				e -> scan(e));
			elementPrinterHelper.writeComment(newArray, CommentOffset.INSIDE);
		}
		elementPrinterHelper.writeComment(newArray, CommentOffset.AFTER);
		exitCtExpression(newArray);
	}


	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		enterCtStatement(ctConstructorCall);
		enterCtExpression(ctConstructorCall);

		printConstructorCall(ctConstructorCall);

		exitCtExpression(ctConstructorCall);
		exitCtStatement(ctConstructorCall);
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		enterCtStatement(newClass);
		enterCtExpression(newClass);

		printConstructorCall(newClass);

		scan(newClass.getAnonymousClass());
		exitCtExpression(newClass);
		exitCtStatement(newClass);
	}

	private <T> void printConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		try (Writable _context = context.modify()) {
			if (ctConstructorCall.getTarget() != null) {
				scan(ctConstructorCall.getTarget());
				printer.writeSeparator(".");
				_context.ignoreEnclosingClass(true);
			}

			if (hasDeclaringTypeWithGenerics(ctConstructorCall.getType())) {
				_context.ignoreEnclosingClass(true);
			}

			printer.writeKeyword("new").writeSpace();

			if (!ctConstructorCall.getActualTypeArguments().isEmpty()) {
				elementPrinterHelper.writeActualTypeArguments(ctConstructorCall);
			}

			scan(ctConstructorCall.getType());
		}

		elementPrinterHelper.printList(ctConstructorCall.getArguments(),
			null, false, "(", false, false, ",", true, false, ")",
			exp -> scan(exp));
	}

	/**
	 * JDT doesn't support <code>new Foo<K>.Bar()</code>. To avoid reprinting this kind of type reference,
	 * we check that the reference has a declaring type with generics.
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=474593
	 *
	 * @param reference Type reference concerned by the bug.
	 * @return true if a declaring type has generic types.
	 */
	private <T> boolean hasDeclaringTypeWithGenerics(CtTypeReference<T> reference) {
		// We don't have a declaring type, it can't have generics.
		if (reference == null) {
			return false;
		}
		// If the declaring type isn't a type, we don't need this hack.
		if (reference.getDeclaringType() == null) {
			return false;
		}
		// If current reference is a class declared in a method, we don't need this hack.
		if (reference.isLocalType()) {
			return false;
		}
		// If declaring type have generics, we return true.
		if (!reference.getDeclaringType().getActualTypeArguments().isEmpty()) {
			return true;
		}
		// Checks if the declaring type has generic types.
		return hasDeclaringTypeWithGenerics(reference.getDeclaringType());
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		enterCtExpression(lambda);

		elementPrinterHelper.printList(lambda.getParameters(),
			null, false, "(", false, false, ",", false, false, ")",
			parameter -> scan(parameter));
		printer.writeSpace();
		printer.writeSeparator("->");
		printer.writeSpace();

		if (lambda.getBody() != null) {
			scan(lambda.getBody());
		} else {
			scan(lambda.getExpression());
		}
		exitCtExpression(lambda);
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(CtExecutableReferenceExpression<T, E> expression) {
		enterCtExpression(expression);
		try (Writable _context = context.modify()) {
			if (expression.getExecutable().isStatic()) {
				_context.ignoreGenerics(true);
			}
			scan(expression.getTarget());
		}
		printer.writeSeparator("::");
		if (expression.getExecutable().isConstructor()) {
			printer.writeKeyword("new");
		} else {
			printer.writeIdentifier(expression.getExecutable().getSimpleName());
		}
		exitCtExpression(expression);
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		enterCtStatement(assignment);
		enterCtExpression(assignment);
		scan(assignment.getAssigned());
		printer.writeSpace();
		// the operators like +=, *= are sent as one operator token
		printer.writeOperator(OperatorHelper.getOperatorText(assignment.getKind()) + "=");
		printer.writeSpace();
		scan(assignment.getAssignment());
		exitCtExpression(assignment);
		exitCtStatement(assignment);
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {
		if (!ctPackage.isUnnamedPackage()) {
			elementPrinterHelper.writePackageLine(ctPackage.getQualifiedName());
		} else {
			printer.writeComment(
					ctPackage.getFactory().createComment("default package (CtPackage.TOP_LEVEL_PACKAGE_NAME in Spoon= unnamed package)", CommentType.INLINE)
				).writeln();
		}
	}

	@Override
	public void visitCtPackageReference(CtPackageReference reference) {
		elementPrinterHelper.writeQualifiedName(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		elementPrinterHelper.writeComment(parameter);
		elementPrinterHelper.writeAnnotations(parameter);
		elementPrinterHelper.writeModifiers(parameter);
		if (parameter.isVarArgs()) {
			scan(((CtArrayTypeReference<T>) parameter.getType()).getComponentType());
			printer.writeSeparator("...");
		} else {
			scan(parameter.getType());
		}
		printer.writeSpace();
		printer.writeIdentifier(parameter.getSimpleName());
	}

	@Override
	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		printer.writeIdentifier(reference.getSimpleName());
	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		enterCtStatement(returnStatement);
		printer.writeKeyword("return");
		// checkstyle wants "return;" and not "return ;"
		if (returnStatement.getReturnedExpression() != null) {
			printer.writeSpace();
		}
		scan(returnStatement.getReturnedExpression());
		exitCtStatement(returnStatement);
	}

	private <T> void visitCtType(CtType<T> type) {
		elementPrinterHelper.writeComment(type, CommentOffset.BEFORE);
		getPrinterHelper().mapLine(type, sourceCompilationUnit);
		if (type.isTopLevel()) {
			context.currentTopLevel = type;
		}
		elementPrinterHelper.visitCtNamedElement(type, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(type);
	}

	@Override
	public void visitCtStatementList(CtStatementList statements) {
		for (CtStatement s : statements.getStatements()) {
			scan(s);
		}
	}

	@Override
	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		enterCtStatement(switchStatement);
		printer.writeKeyword("switch").writeSpace().writeSeparator("(");
		scan(switchStatement.getSelector());
		printer.writeSeparator(")").writeSpace().writeSeparator("{").incTab();
		for (CtCase<?> c : switchStatement.getCases()) {
			printer.writeln();
			scan(c);
		}
		if (env.isPreserveLineNumbers()) {
			printer.decTab().writeSeparator("}");
		} else {
			printer.decTab().writeln().writeSeparator("}");
		}
		exitCtStatement(switchStatement);
	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		enterCtStatement(synchro);
		printer.writeKeyword("synchronized");
		if (synchro.getExpression() != null) {
			printer.writeSeparator("(");
			scan(synchro.getExpression());
			printer.writeSeparator(")").writeSpace();
		}
		scan(synchro.getBlock());
		exitCtStatement(synchro);
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		enterCtStatement(throwStatement);
		printer.writeKeyword("throw").writeSpace();
		scan(throwStatement.getThrownExpression());
		exitCtStatement(throwStatement);
	}

	@Override
	public void visitCtTry(CtTry tryBlock) {
		enterCtStatement(tryBlock);
		printer.writeKeyword("try").writeSpace();
		scan(tryBlock.getBody());
		for (CtCatch c : tryBlock.getCatchers()) {
			scan(c);
		}

		if (tryBlock.getFinalizer() != null) {
			printer.writeSpace().writeKeyword("finally").writeSpace();
			scan(tryBlock.getFinalizer());
		}
		exitCtStatement(tryBlock);
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		enterCtStatement(tryWithResource);
		printer.writeKeyword("try").writeSpace();
		if (tryWithResource.getResources() != null && !tryWithResource.getResources().isEmpty()) {
			elementPrinterHelper.printList(tryWithResource.getResources(),
				null, false, "(", false, false, ";", false, false, ")",
				r -> scan(r));
		}
		printer.writeSpace();
		scan(tryWithResource.getBody());
		for (CtCatch c : tryWithResource.getCatchers()) {
			scan(c);
		}

		if (tryWithResource.getFinalizer() != null) {
			printer.writeSpace().writeKeyword("finally").writeSpace();
			scan(tryWithResource.getFinalizer());
		}
		exitCtStatement(tryWithResource);
	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		if (ref.isImplicit()) {
			return;
		}
		elementPrinterHelper.writeAnnotations(ref);
		if (printQualified(ref)) {
			elementPrinterHelper.writeQualifiedName(ref.getQualifiedName());
		} else {
			printer.writeIdentifier(ref.getSimpleName());
		}
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		if (wildcardReference.isImplicit()) {
			return;
		}
		elementPrinterHelper.writeAnnotations(wildcardReference);
		printer.writeSeparator("?");
		// we ignore printing "extends Object" except if it's explicit
		if (!wildcardReference.isDefaultBoundingType() || !wildcardReference.getBoundingType().isImplicit()) {
			if (wildcardReference.isUpper()) {
				printer.writeSpace().writeKeyword("extends").writeSpace();
			} else {
				printer.writeSpace().writeKeyword("super").writeSpace();
			}
			scan(wildcardReference.getBoundingType());
		}
	}

	private boolean printQualified(CtTypeReference<?> ref) {
		if (importsContext.isImported(ref)  // If my.pkg.Something is imported
				|| (this.env.isAutoImports() && ref.getPackage() != null && "java.lang".equals(ref.getPackage().getSimpleName())) // or that we are in java.lang
				) {
			for (CacheBasedConflictFinder typeContext : context.currentThis) {
				//A) we are in the context of a class which is also called "Something",
				if (typeContext.getSimpleName().equals(ref.getSimpleName())
						&& !Objects.equals(typeContext.getPackage(), ref.getPackage())) {
					return true;
				}
				//B) we are in the context of a class which defines field which is also called "Something",
				//	we should still use qualified version my.pkg.Something
				if (typeContext.hasFieldConflict(ref.getSimpleName())
						|| typeContext.hasNestedTypeConflict(ref.getSimpleName()) // fix of #2369
						) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}


	@Override
	public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {
		elementPrinterHelper.printList(reference.getBounds(),
			null, false, null, false, true, "&", true, false, null,
			bound -> scan(bound));
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> ref) {
		visitCtTypeReference(ref, true);
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
		if (typeAccess.isImplicit()) {
			return;
		}
		enterCtExpression(typeAccess);
		scan(typeAccess.getAccessedType());
		exitCtExpression(typeAccess);
	}

	private void visitCtTypeReferenceWithoutGenerics(CtTypeReference<?> ref) {
		visitCtTypeReference(ref, false);
	}

	private void visitCtTypeReference(CtTypeReference<?> ref, boolean withGenerics) {
		if (ref.isImplicit()) {
			return;
		}
		if (ref.isPrimitive()) {
			elementPrinterHelper.writeAnnotations(ref);
			printer.writeKeyword(ref.getSimpleName());
			return;
		}
		boolean isInner = ref.getDeclaringType() != null;
		if (isInner) {
			if (!context.ignoreEnclosingClass() && !ref.isLocalType()) {
				//compute visible type which can be used to print access path to ref
				CtTypeReference<?> accessType = ref.getAccessType();
				if (!accessType.isAnonymous()) {
					try (Writable _context = context.modify()) {
						if (!withGenerics) {
							_context.ignoreGenerics(true);
						}
						scan(accessType);
					}
					printer.writeSeparator(".");
				}
			}
			//?? are these annotations on correct place ??
			elementPrinterHelper.writeAnnotations(ref);
			if (ref.isLocalType()) {
				printer.writeIdentifier(ref.getSimpleName().replaceAll("^[0-9]*", ""));
			} else {
				printer.writeIdentifier(ref.getSimpleName());
			}
		} else {
			if (ref.getPackage() != null && printQualified(ref)) {
				if (!ref.getPackage().isUnnamedPackage()) {
					scan(ref.getPackage());
					printer.writeSeparator(CtPackage.PACKAGE_SEPARATOR);
				}
			}
			elementPrinterHelper.writeAnnotations(ref);
			printer.writeIdentifier(ref.getSimpleName());
		}
		if (withGenerics && !context.ignoreGenerics()) {
			try (Writable _context = context.modify().ignoreEnclosingClass(false)) {
				elementPrinterHelper.writeActualTypeArguments(ref);
			}
		}
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		enterCtStatement(operator);
		enterCtExpression(operator);
		UnaryOperatorKind op = operator.getKind();
		if (OperatorHelper.isPrefixOperator(op)) {
			printer.writeOperator(OperatorHelper.getOperatorText(op));
		}
		scan(operator.getOperand());
		if (OperatorHelper.isSufixOperator(op)) {
			printer.writeOperator(OperatorHelper.getOperatorText(op));
		}
		exitCtExpression(operator);
		exitCtStatement(operator);
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		enterCtExpression(variableRead);
		printer.writeIdentifier(variableRead.getVariable().getSimpleName());
		exitCtExpression(variableRead);
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		enterCtExpression(variableWrite);
		printer.writeIdentifier(variableWrite.getVariable().getSimpleName());
		exitCtExpression(variableWrite);
	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		enterCtStatement(whileLoop);
		printer.writeKeyword("while").writeSpace().writeSeparator("(");
		scan(whileLoop.getLoopingExpression());
		printer.writeSeparator(")");

		elementPrinterHelper.writeIfOrLoopBlock(whileLoop.getBody());
		exitCtStatement(whileLoop);
	}

	@Override
	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
		elementPrinterHelper.writeComment(expression);
		printer.writeCodeSnippet(expression.getValue());
	}

	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		enterCtStatement(statement);
		printer.writeCodeSnippet(statement.getValue());
		exitCtStatement(statement);
	}

	public ElementPrinterHelper getElementPrinterHelper() {
		return elementPrinterHelper;
	}

	public PrintingContext getContext() {
		return context;
	}

	@Override
	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
		printer.writeIdentifier(reference.getSimpleName());
	}

	@Override
	public String printPackageInfo(CtPackage pack) {
		reset();
		elementPrinterHelper.writeComment(pack);

		// we need to compute imports only for annotations
		// we don't want to get all imports coming from content of package
		for (CtAnnotation annotation : pack.getAnnotations()) {
			this.importsContext.computeImports(annotation);
		}
		elementPrinterHelper.writeAnnotations(pack);

		if (!pack.isUnnamedPackage()) {
			elementPrinterHelper.writePackageLine(pack.getQualifiedName());
		}
		elementPrinterHelper.writeImports(this.importsContext.getAllImports());
		return printer.getPrinterHelper().toString();
	}

	@Override
	public String printModuleInfo(CtModule module) {
		reset();
		scan(module);
		return this.getResult();
	}

	@Override
	public String getResult() {
		return printer.getPrinterHelper().toString();
	}

	private void reset() {
		printer.reset();
		context = new PrintingContext();
		if (env.isAutoImports()) {
			this.importsContext = new ImportScannerImpl();
		} else {
			this.importsContext = new MinimalImportScanner();
		}
	}


	/**
	 * Write the compilation unit header.
	 */
	public DefaultJavaPrettyPrinter writeHeader(List<CtType<?>> types, Collection<CtImport> imports) {
		elementPrinterHelper.writeHeader(types, imports);
		return this;
	}

	/**
	 * Write the compilation unit footer.
	 */
	public DefaultJavaPrettyPrinter writeFooter(List<CtType<?>> types) {
		elementPrinterHelper.writeFooter(types);
		return this;
	}

	@Override
	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		// reset the importsContext to avoid errors with multiple CU
		reset();

		this.sourceCompilationUnit = sourceCompilationUnit;
		this.imports = new HashSet<>();
		if (sourceCompilationUnit != null) {
			this.importsContext.initWithImports(sourceCompilationUnit.getImports());
		}

		for (CtType<?> t : types) {
			imports.addAll(computeImports(t));
		}
		this.writeHeader(types, imports);
		printTypes(types);
	}

	protected void printTypes(List<CtType<?>> types) {
		for (CtType<?> t : types) {
			scan(t);
			if (!env.isPreserveLineNumbers()) {
				// saving lines and chars
				printer.writeln().writeln();
			} else {
				getPrinterHelper().adjustEndPosition(t);
			}
		}
		this.writeFooter(types);
	}

	@Override
	public Map<Integer, Integer> getLineNumberMapping() {
		return getPrinterHelper().getLineNumberMapping();
	}

	/**
	 * @return current {@link TokenWriter}, so the subclasses of {@link DefaultJavaPrettyPrinter} can print tokens too
	 */
	protected TokenWriter getPrinterTokenWriter() {
		return printer;
	}

	/**
	 * Set {@link TokenWriter}, which has to be used to print tokens
	 */
	public DefaultJavaPrettyPrinter setPrinterTokenWriter(TokenWriter tokenWriter) {
		elementPrinterHelper = new ElementPrinterHelper(tokenWriter, this, env);
		printer = tokenWriter;
		return this;
	}

	private PrinterHelper getPrinterHelper() {
		return printer.getPrinterHelper();
	}
}
