/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.experimental.CtUnresolvedImport;
import spoon.processing.Processor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtAbstractSwitch;
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
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
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
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTextBlock;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtUnnamedPattern;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.PrintingContext.Writable;
import spoon.reflect.visitor.printer.CommentOffset;
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;
import spoon.support.util.ModelList;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static spoon.reflect.visitor.ElementPrinterHelper.PrintTypeArguments.ALSO_PRINT_DIAMOND_OPERATOR;
import static spoon.reflect.visitor.ElementPrinterHelper.PrintTypeArguments.ONLY_PRINT_EXPLICIT_TYPES;

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

	/**
	 * Handle imports of classes.
	 */
	protected final List<Processor<CtElement>> preprocessors = new ArrayList<>();

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
	protected CtCompilationUnit sourceCompilationUnit;

	/**
	 * If true: always prints fully qualified names by ignoring the isImplicit attribute of AST nodes
	 * Default value is "true" for backward compatibility.
	 * If false: obey "implicit" directive
	 */
	protected boolean ignoreImplicit = true;

	/**
	 * EXPERIMENTAL: If true, the printer will attempt to print a minimal set of round brackets in
	 * expressions while preserving the syntactical structure of the AST.
	 */
	private boolean minimizeRoundBrackets = false;

	public boolean inlineElseIf = true;

	/**
	 * Creates a new code generator visitor.
	 */
	public DefaultJavaPrettyPrinter(Environment env) {
		this.env = env;
		setPrinterTokenWriter(new DefaultTokenWriter(new PrinterHelper(env)));
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


	protected static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String ERROR_MESSAGE_TO_STRING = "Error in printing the node. One parent isn't initialized!";
	/**
	 * Prints an element. This method shall be called by the toString() method of an element.
	 * It is responsible for any initialization required to print an arbitrary element.
	 * @param element
	 * @return A string containing the pretty printed element (and descendants).
	 */
	public String printElement(CtElement element) {

		String errorMessage = "";
		try {
			// now that pretty-printing can change the model, we only do it on a clone
			CtElement clone = element.clone();

			// required: in DJPP some decisions are taken based on the content of the parent
			if (element.isParentInitialized()) {
				clone.setParent(element.getParent());
			}
			applyPreProcessors(clone);
			scan(clone);
		} catch (ParentNotInitializedException ignore) {
			LOGGER.error(ERROR_MESSAGE_TO_STRING, ignore);
			errorMessage = ERROR_MESSAGE_TO_STRING;
		}
		// in line-preservation mode, newlines are added at the beginning to matches the lines
		// removing them from the toString() representation
		return toString().replaceFirst("^\\s+", "") + errorMessage;
	}

	/**
	 * Enters an expression.
	 */
	protected void enterCtExpression(CtExpression<?> e) {
		if (!(e instanceof CtStatement)) {
			elementPrinterHelper.writeComment(e, CommentOffset.BEFORE);
		}
		getPrinterHelper().mapLine(e, sourceCompilationUnit);
		if (shouldSetBracketAroundExpressionAndCast(e)) {
			context.parenthesedExpression.push(e);
			printer.writeSeparator("(");
		}
		if (!e.getTypeCasts().isEmpty()) {
			for (CtTypeReference<?> r : e.getTypeCasts()) {
				printer.writeSeparator("(");
				scan(r);
				printer.writeSeparator(")").writeSpace();
			}
			if (shouldSetBracketAroundCastTarget(e)) {
				printer.writeSeparator("(");
				context.parenthesedExpression.push(e);
			}
		}
	}

	private boolean shouldSetBracketAroundCastTarget(CtExpression<?> expr) {
		if (!isMinimizeRoundBrackets()) {
			return true;
		}

		if (expr instanceof CtTargetedExpression) {
			return false;
		}
		if (expr instanceof CtLiteral) {
			return false;
		}
		if (expr instanceof CtVariableAccess) {
			return false;
		}
		return true;
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

	@Override
	public String prettyprint(CtElement e) {
		reset();
		applyPreProcessors(e);
		scan(e);
		return this.getResult();
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
			e.accept(this);
			context.elementStack.pop();
			exit(e);
		}
		return this;
	}

	private boolean shouldSetBracketAroundExpressionAndCast(CtExpression<?> e) {
		boolean hasCasts = !e.getTypeCasts().isEmpty();
		if (isMinimizeRoundBrackets()) {
			RoundBracketAnalyzer.EncloseInRoundBrackets requiresBrackets =
				RoundBracketAnalyzer.requiresRoundBrackets(e);
			if (requiresBrackets != RoundBracketAnalyzer.EncloseInRoundBrackets.UNKNOWN) {
				return requiresBrackets == RoundBracketAnalyzer.EncloseInRoundBrackets.YES || hasCasts;
			}
		} else if (hasCasts) {
			return true;
		}
		if (e.isParentInitialized()) {
			if ((e.getParent() instanceof CtBinaryOperator) || (e.getParent() instanceof CtUnaryOperator)) {
				return (e instanceof CtAssignment) || (e instanceof CtConditional) || (e instanceof CtUnaryOperator) || e instanceof CtBinaryOperator;
			}
			return requiresParenthesesInTargetContext(e);
		}
		return false;
	}

	/**
	 * {@return whether the expression requires parentheses if it is the child of a targeted expression}
	 * The targets of method calls typically don't require additional parentheses, e.g.,
	 * {@code var.method()}, {@code method().method()}. However, some expressions do, e.g.,
	 * {@code (x = y).method()}, {@code ("a" + "b").method()}.
	 * If the expression is not the target of a method call, {@code false} is returned.
	 * <br/>
	 * Note: This method does not consider casts.
	 */
	private static boolean requiresParenthesesInTargetContext(CtExpression<?> expression) {
		if (expression.isParentInitialized() && expression.getParent() instanceof CtTargetedExpression<?, ?> targeted && targeted.getTarget() == expression) {
			return !expression.getTypeCasts().isEmpty()
				|| expression instanceof CtBinaryOperator<?>
				|| expression instanceof CtSwitchExpression<?, ?>
				|| expression instanceof CtAssignment<?, ?>
				|| expression instanceof CtConditional<?>
				|| expression instanceof CtUnaryOperator<?>;
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
					if (!(annotation.getValues().size() == 1 && "value".equals(e.getKey()))) {
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
		if (arrayAccess.getTarget() instanceof CtNewArray
				&& ((CtNewArray<?>) arrayAccess.getTarget()).getElements().isEmpty()) {
			printer.writeSeparator("(");
			scan(arrayAccess.getTarget());
			printer.writeSeparator(")");
		} else {
			scan(arrayAccess.getTarget());
		}
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
				_context.forceWildcardGenerics(canForceWildcardInInstanceof());
			}
			scan(operator.getRightHandOperand());
		}
		exitCtExpression(operator);
	}

	/**
	 * Since Java 16, it is allowed to have type parameters other than {@code <?>} in instanceof checks.
	 * This is allowed in cases where the generic type can be carried over from the type on the left side.
	 * In previous Java versions, only {@code <?>} is allowed, and to keep the original behavior for such
	 * versions, this method returns {@code true} if the compliance level is below 16.
	 */
	private boolean canForceWildcardInInstanceof() {
		return env.getComplianceLevel() < 16;
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
		if (!breakStatement.isImplicit()) {
			printer.writeKeyword("break");
			if (breakStatement.getTargetLabel() != null) {
				printer.writeSpace().writeKeyword(breakStatement.getTargetLabel());
			}
		}
		exitCtStatement(breakStatement);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		enterCtStatement(caseStatement);
		if (caseStatement.getCaseExpression() != null) {
			printer.writeKeyword("case").writeSpace();
			List<CtExpression<E>> caseExpressions = caseStatement.getCaseExpressions();
			for (int i = 0; i < caseExpressions.size(); i++) {
				CtExpression<E> caseExpression = caseExpressions.get(i);
				// writing enum case expression
				if (caseExpression instanceof CtFieldAccess<E> fieldAccess) {
					final CtFieldReference variable = ((CtFieldAccess) caseExpression).getVariable();
					// In noclasspath mode, we don't have always the type of the declaring type.
					if (((fieldAccess.getTarget() != null && fieldAccess.getTarget().isImplicit()) || env.getComplianceLevel() < 21)
							&& variable.getType() != null
							&& variable.getDeclaringType() != null
							&& variable.getType().getQualifiedName().equals(variable.getDeclaringType().getQualifiedName())) {
						printer.writeIdentifier(variable.getSimpleName());
					} else {
						scan(caseExpression);
					}
				} else {
					scan(caseExpression);
				}
				if (i != caseExpressions.size() - 1) {
					printer.writeSeparator(",").writeSpace();
				}
			}
			if (caseStatement.getIncludesDefault()) {
				printer.writeSeparator(",")
					.writeSpace()
					.writeKeyword("default");
			}
		} else {
			printer.writeKeyword("default");
		}
		if (caseStatement.getGuard() != null) {
			printer.writeSpace().writeKeyword("when").writeSpace();
			scan(caseStatement.getGuard());
		}
		String separator = caseStatement.getCaseKind() == CaseKind.ARROW ? "->" : ":";
		printer.writeSpace().writeSeparator(separator).incTab();

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
			printer.writeKeyword("class").writeSpace()
					.writeIdentifier(stripLeadingDigits(ctClass.getSimpleName()));

			elementPrinterHelper.writeFormalTypeParameters(ctClass);
			elementPrinterHelper.writeExtendsClause(ctClass);
			elementPrinterHelper.writeImplementsClause(ctClass);
		}
		elementPrinterHelper.printPermits(ctClass);
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
		if (typeParameter.getSuperclass() != null && !typeParameter.getSuperclass().isImplicit()) {
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
			printer.writeIdentifier(stripLeadingDigits(constructor.getDeclaringType().getSimpleName()));
		}
		if (!constructor.isCompactConstructor()) {
			elementPrinterHelper.writeExecutableParameters(constructor);
		}
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
		printer.writeKeyword("enum").writeSpace()
				.writeIdentifier(stripLeadingDigits(ctEnum.getSimpleName()));

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
			printer.writeln();
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
		if (f.getType() instanceof CtArrayTypeReference<?>) {
			try (Writable unused = context.modify()
					.skipArray(shouldSquareBracketBeSkipped(
							(CtArrayTypeReference<?>) f.getType(),
							CtArrayTypeReferenceImpl.DeclarationKind.TYPE))) {
				scan(f.getType());
			}
		} else {
			scan(f.getType());
		}

		printer.writeSpace();
		printer.writeIdentifier(f.getSimpleName());

		if (f.getType() instanceof CtArrayTypeReference<?>) {
			try (Writable unused = context.modify()
					.skipArray(shouldSquareBracketBeSkipped(
							(CtArrayTypeReference<?>) f.getType(),
							CtArrayTypeReferenceImpl.DeclarationKind.IDENTIFIER))) {
				printSquareBrackets((CtArrayTypeReference<?>) f.getType());
			}
		}

		if (f.getDefaultExpression() != null) {
			printer.writeSpace().writeOperator("=").writeSpace();
			scan(f.getDefaultExpression());
		}
		printer.writeSeparator(";");
		elementPrinterHelper.writeComment(f, CommentOffset.AFTER);
	}

	private boolean shouldSquareBracketBeSkipped(
			CtArrayTypeReference<?> arrayTypeReference,
			CtArrayTypeReferenceImpl.DeclarationKind declarationStyle) {
		return ((CtArrayTypeReferenceImpl<?>) arrayTypeReference).getDeclarationKind() != declarationStyle;
	}

	private void printSquareBrackets(CtArrayTypeReference<?> arrayTypeReference) {
		if (!context.skipArray()) {
			for (int i = 0; i < arrayTypeReference.getDimensionCount(); ++i) {
				printer.writeSeparator("[").writeSeparator("]");
			}
		}
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

	private <T> void printCtFieldAccess(CtFieldAccess<T> f) {
		enterCtExpression(f);
		try (Writable _context = context.modify()) {
			if ((f.getVariable().isStatic() || "class".equals(f.getVariable().getSimpleName())) && f.getTarget() instanceof CtTypeAccess) {
				_context.ignoreGenerics(true);
			}
			CtExpression<?> target = f.getTarget();
			if (target != null) {
					// the implicit drives the separator
				if (shouldPrintTarget(target)) {
						scan(target);
						printer.writeSeparator(".");
					}
				_context.ignoreStaticAccess(true);
			}
			scan(f.getVariable());
		}
		exitCtExpression(f);
	}

	private boolean shouldPrintTarget(CtExpression target) {
		if (target == null) {
			return false;
		}
		if (!target.isImplicit()) {
			//target is not implicit, we always print it
			return true;
		}
		//target is implicit, we should not print it
		if (!ignoreImplicit) {
			//fully qualified mode is not forced so we should not print implicit target
			return false;
		}
		//forceFullyQualified is ON, we should print full qualified names
		if (target instanceof CtThisAccess) {
			//the implicit this access is never printed even in forceFullyQualified mode
			return false;
		}
		return true;
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
		if (f.isImplicit()) {
			return;
		}
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
		CommentHelper.printJavaDocTag(printer.getPrinterHelper(), docTag, x -> { return x; });
	}

	@Override
	public void visitCtImport(CtImport ctImport) {
		if (ctImport.getImportKind() != null) {
			printer.writeKeyword("import");
			printer.writeSpace();
			ctImport.accept(new CtImportVisitor() {

				@Override
				public <T> void visitTypeImport(CtTypeReference<T> typeReference) {
					writeImportReference(typeReference);
				}

				@Override
				public <T> void visitMethodImport(CtExecutableReference<T> execRef) {
					printer.writeKeyword("static");
					printer.writeSpace();
					if (execRef.getDeclaringType() != null) {
						writeImportReference(execRef.getDeclaringType());
						printer.writeSeparator(".");
					}
					printer.writeIdentifier(execRef.getSimpleName());
				}

				@Override
				public <T> void visitFieldImport(CtFieldReference<T> fieldReference) {
					printer.writeKeyword("static");
					printer.writeSpace();
					if (fieldReference.getDeclaringType() != null) {
						writeImportReference(fieldReference.getDeclaringType());
						printer.writeSeparator(".");
					}
					printer.writeIdentifier(fieldReference.getSimpleName());
				}

				@Override
				public void visitAllTypesImport(CtPackageReference packageReference) {
					visitCtPackageReference(packageReference);
					printer.writeSeparator(".");
					printer.writeIdentifier("*");
				}

				@Override
				public <T> void visitAllStaticMembersImport(CtTypeMemberWildcardImportReference typeReference) {
					printer.writeKeyword("static");
					printer.writeSpace();
					writeImportReference(typeReference.getTypeReference());
					printer.writeSeparator(".");
					printer.writeIdentifier("*");
				}

				@Override
				public <T> void visitUnresolvedImport(CtUnresolvedImport ctUnresolvedImport) {
					if (ctUnresolvedImport.isStatic()) {
						printer.writeKeyword("static");
						printer.writeSpace();
					}
					printer.writeCodeSnippet(ctUnresolvedImport.getUnresolvedReference());
				}
			});
			printer.writeSeparator(";");
		}
	}

	private void writeImportReference(CtTypeReference<?> ref) {
		boolean prevIgnoreImplicit = ignoreImplicit;
		// force fqn, import are never short
		ignoreImplicit = true;
		visitCtTypeReference(ref, false);
		ignoreImplicit = prevIgnoreImplicit;
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
	public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
		CtCompilationUnit outerCompilationUnit = this.sourceCompilationUnit;
		try {
			this.sourceCompilationUnit = compilationUnit;
			elementPrinterHelper.writeComment(compilationUnit, CommentOffset.BEFORE);
		switch (compilationUnit.getUnitType()) {
		case MODULE_DECLARATION:
				CtModule module = compilationUnit.getDeclaredModule();
				scan(module);
			break;
		case PACKAGE_DECLARATION:
				CtPackage pack = compilationUnit.getDeclaredPackage();
				scan(pack);
				//note: the package-info.java may contain type declarations too
			break;
		case TYPE_DECLARATION:
			scan(compilationUnit.getPackageDeclaration());

			CtPackage pkg = compilationUnit.getDeclaredPackage();
			if (pkg != null && !pkg.isUnnamedPackage()) {
				printer.writeln();
			}

			for (CtImport imprt : getImports(compilationUnit)) {
				scan(imprt);
				printer.writeln();
			}
			for (CtType<?> t : compilationUnit.getDeclaredTypes()) {
				scan(t);
			}
			break;
		default:
				throw new SpoonException("Unexpected compilation unit type: " + compilationUnit.getUnitType());
			}
			elementPrinterHelper.writeComment(compilationUnit, CommentOffset.AFTER);
		} finally {
			this.sourceCompilationUnit = outerCompilationUnit;
		}
		// by convention, we add a newline at the end of the file
		// we guard this with a check to avoid adding a newline if there is already one
		if (!getResult().endsWith(System.lineSeparator())) {
			printer.writeln();
		}
	}

	protected ModelList<CtImport> getImports(CtCompilationUnit compilationUnit) {
		return compilationUnit.getImports();
	}

	@Override
	public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {
		CtPackageReference ctPackage = packageDeclaration.getReference();
		elementPrinterHelper.writeComment(ctPackage, CommentOffset.BEFORE);
		if (!ctPackage.isUnnamedPackage()) {
			elementPrinterHelper.writePackageStatement(ctPackage.getQualifiedName());
		}
	}

	@Override
	public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference) {
		scan(wildcardReference.getTypeReference());
		printer.writeSeparator(".").writeSeparator("*");
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
		CtStatement elseStmt = ifElement.getElseStatement();
		elementPrinterHelper.writeIfOrLoopBlock(thenStmt);
		if (elseStmt != null) {
			List<CtComment> comments = elementPrinterHelper.getComments(ifElement, CommentOffset.INSIDE);
			if (thenStmt != null) {
				SourcePosition thenPosition = thenStmt.getPosition();
				if (!thenPosition.isValidPosition() && thenStmt instanceof CtBlock && !((CtBlock<?>) thenStmt).getStatements().isEmpty()) {
					CtStatement thenExpression = ((CtBlock<?>) thenStmt).getStatement(0);
					thenPosition = thenExpression.getPosition();
				}
				for (CtComment comment : comments) {
					if (comment.getPosition().getSourceStart() > thenPosition.getSourceEnd()) {
						elementPrinterHelper.writeComment(comment);
					}
				}
			}
			if (thenStmt instanceof CtBlock && !thenStmt.isImplicit()) {
				//add space after non implicit block
				printer.writeSpace();
			}
			printer.writeKeyword("else");
			if (inlineElseIf && elementPrinterHelper.isElseIf(ifElement)) {
				printer.writeSpace();
				CtIf child;
				if (elseStmt instanceof CtBlock) {
					child = ((CtBlock<?>) elseStmt).getStatement(0);
				} else {
					child = (CtIf) elseStmt;
				}
				scan(child);
			} else {
				elementPrinterHelper.writeIfOrLoopBlock(elseStmt);
			}
		}
		exitCtStatement(ifElement);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		visitCtType(intrface);
		printer.writeKeyword("interface").writeSpace()
				.writeIdentifier(stripLeadingDigits(intrface.getSimpleName()));
		if (intrface.getFormalCtTypeParameters() != null) {
			elementPrinterHelper.writeFormalTypeParameters(intrface);
		}

		if (!intrface.getSuperInterfaces().isEmpty()) {
			elementPrinterHelper.printList(intrface.getSuperInterfaces(),
				"extends", false, null, false, true, ",", true, false, null,
				ref -> scan(ref));
		}
		elementPrinterHelper.printPermits(intrface);
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
			elementPrinterHelper.writeActualTypeArguments(invocation.getExecutable(), ONLY_PRINT_EXPLICIT_TYPES);
			CtType<?> parentType = invocation.getParent(CtType.class);
			if (parentType == null || parentType.getQualifiedName() != null && parentType.getQualifiedName().equals(invocation.getExecutable().getDeclaringType().getQualifiedName())) {
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
			if (invocation.getTarget() != null && (ignoreImplicit || !invocation.getTarget().isImplicit())) {
				try (Writable _context = context.modify()) {
					if (invocation.getTarget() instanceof CtTypeAccess) {
						_context.ignoreGenerics(true);
					}
					if (shouldPrintTarget(invocation.getTarget())) {
						scan(invocation.getTarget());
						printer.writeSeparator(".");
					}
				}
			}

			elementPrinterHelper.writeActualTypeArguments(invocation, ONLY_PRINT_EXPLICIT_TYPES);
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
	public void visitCtTextBlock(CtTextBlock ctTextBlock) {
		enterCtExpression(ctTextBlock);
		printer.writeLiteral(
				LiteralHelper.getTextBlockToken(ctTextBlock)
		);
		exitCtExpression(ctTextBlock);
	}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		enterCtStatement(localVariable);
		if (env.isPreserveLineNumbers()) {
			getPrinterHelper().adjustStartPosition(localVariable);
		}
		if (!context.isNextForVariable()
				&& !localVariable.isImplicit() // for resources in try-with-resources
		) {
			elementPrinterHelper.writeModifiers(localVariable);
			if (localVariable.isInferred() && this.env.getComplianceLevel() >= 10) {
				getPrinterTokenWriter().writeKeyword("var");
			} else {
				if (localVariable.getType() instanceof CtArrayTypeReference<?>) {
					try (Writable unused = context.modify()
							.skipArray(
									shouldSquareBracketBeSkipped(
											(CtArrayTypeReference<?>) localVariable.getType(),
											CtArrayTypeReferenceImpl.DeclarationKind.TYPE))) {
						scan(localVariable.getType());
					}
				} else {
					scan(localVariable.getType());
				}
			}
			printer.writeSpace();
		}
		printer.writeIdentifier(localVariable.getSimpleName());
		if (localVariable.getType() instanceof CtArrayTypeReference<?>) {
			try (Writable unused = context.modify()
					.skipArray(
							shouldSquareBracketBeSkipped(
									(CtArrayTypeReference<?>) localVariable.getType(),
									CtArrayTypeReferenceImpl.DeclarationKind.IDENTIFIER))) {
				printSquareBrackets((CtArrayTypeReference<?>) localVariable.getType());
			}
		}
		if (localVariable.getDefaultExpression() != null
				&& !localVariable.isImplicit() // for resources in try-with-resources
		) {
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

		boolean isNotInAnnotation = (newArray.getParent(CtAnnotationType.class) == null) && (newArray.getParent(CtAnnotation.class) == null);
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

			elementPrinterHelper.writeActualTypeArguments(ctConstructorCall, ALSO_PRINT_DIAMOND_OPERATOR);

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
		// single parameter lambdas with implicit type can be printed without parantheses
		if (isSingleParameterWithoutExplicitType(lambda) && !ignoreImplicit) {
			elementPrinterHelper.printList(lambda.getParameters(), null, false, "", false, false, ",",
					false, false, "", this::scan);
		} else {
			elementPrinterHelper.printList(lambda.getParameters(), null, false, "(", false, false, ",",
					false, false, ")", this::scan);
		}

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

	private <T> boolean isSingleParameterWithoutExplicitType(CtLambda<T> lambda) {
		return lambda.getParameters().size() == 1 && (lambda.getParameters().get(0).getType() == null
				|| lambda.getParameters().get(0).getType().isImplicit());
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
		if (!expression.getExecutable().getActualTypeArguments().isEmpty()) {
			elementPrinterHelper.printList(expression.getExecutable().getActualTypeArguments(), null, false, "<", false, false, ", ", false, false, ">", this::scan);
		}
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
		//prints content of package-info.java
		elementPrinterHelper.writeComment(ctPackage);

		elementPrinterHelper.writeAnnotations(ctPackage);

		if (!ctPackage.isUnnamedPackage()) {
			elementPrinterHelper.writePackageLine(ctPackage.getQualifiedName());
		}
		elementPrinterHelper.writeImports(getImports(ctPackage.getPosition().getCompilationUnit()));
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
		} else if (parameter.isInferred() && this.env.getComplianceLevel() >= 11) {
			getPrinterTokenWriter().writeKeyword("var");
		} else {
			scan(parameter.getType());
		}
		// after an implicit type, there is no space because we dont print anything
		if (isParameterWithImplicitType(parameter) || isNotFirstParameter(parameter)
				|| ignoreImplicit) {
			printer.writeSpace();
		}
		printer.writeIdentifier(parameter.getSimpleName());
	}

	private <T> boolean isParameterWithImplicitType(CtParameter<T> parameter) {
		return parameter.getType() != null && !parameter.getType().isImplicit();
	}

	private <T> boolean isNotFirstParameter(CtParameter<T> parameter) {
		return parameter.getParent() != null
				&& parameter.getParent().getParameters().indexOf(parameter) != 0;
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

	private <S> void writeSwitch(CtAbstractSwitch<S> abstractSwitch) {
		printer.writeKeyword("switch").writeSpace().writeSeparator("(");
		scan(abstractSwitch.getSelector());
		printer.writeSeparator(")").writeSpace().writeSeparator("{").incTab();
		for (CtCase<?> c : abstractSwitch.getCases()) {
			printer.writeln();
			scan(c);
		}
		if (env.isPreserveLineNumbers()) {
			printer.decTab().writeSeparator("}");
		} else {
			printer.decTab().writeln().writeSeparator("}");
		}
	}

	@Override
	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		enterCtStatement(switchStatement);
		writeSwitch(switchStatement);
		exitCtStatement(switchStatement);
	}

	@Override
	public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> switchExpression) {
		enterCtExpression(switchExpression);
		writeSwitch(switchExpression);
		exitCtExpression(switchExpression);
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
		return ignoreImplicit || !ref.isSimplyQualified();
		}


	@Override
	public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {
		if (reference.isImplicit()) {
			return;
		}
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
		if (!ignoreImplicit && typeAccess.isImplicit()) {
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
		if (!isPrintTypeReference(ref)) {
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
				if (!accessType.isAnonymous() && isPrintTypeReference(accessType)) {
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
				printer.writeIdentifier(stripLeadingDigits(ref.getSimpleName()));
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
			// You don't want to include annotations in import of an annotated object
			if (!ref.isParentInitialized() || !(ref.getParent() instanceof CtImport)) {
				elementPrinterHelper.writeAnnotations(ref);
			}
			printer.writeIdentifier(ref.getSimpleName());
		}
		if (withGenerics && !context.ignoreGenerics()) {
			try (Writable _context = context.modify().ignoreEnclosingClass(false)) {
				elementPrinterHelper.writeActualTypeArguments(ref, ALSO_PRINT_DIAMOND_OPERATOR);
			}
		}
	}

	private boolean isPrintTypeReference(CtTypeReference<?> accessType) {
		if (!accessType.isImplicit()) {
			//always print explicit type refs
			return true;
		}
		if (ignoreImplicit) {
			//print access type always if fully qualified mode is forced
			return true;
		}
		if (context.forceWildcardGenerics() && accessType.getTypeDeclaration().getFormalCtTypeParameters().size() > 0) {
			//print access type if access type is generic and we have to force wildcard generics
			/*
			 * E.g.
			 * class A<T> {
			 *  class B {
			 *  }
			 *  boolean m(Object o) {
			 *   return o instanceof B;			//compilation error
			 *   return o instanceof A.B; 		// OK
			 *   return o instanceof A<?>.B; 	// OK
			 *  }
			 * }
			 */
			return true;
		}
		return false;
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
	public String printCompilationUnit(CtCompilationUnit compilationUnit) {
		calculate(compilationUnit, compilationUnit.getDeclaredTypes());
		return getResult();
	}

	/** Warning, this may change the state of the object */
	public void applyPreProcessors(CtElement el) {
		for (Processor<CtElement> preprocessor : preprocessors) {
			preprocessor.process(el);
		}
	}

	@Override
	public String printPackageInfo(CtPackage pack) {
		CtCompilationUnit cu = pack.getFactory().CompilationUnit().getOrCreate(pack);
		return printCompilationUnit(cu);
		}

	@Override
	public String printModuleInfo(CtModule module) {
		CtCompilationUnit cu = module.getFactory().CompilationUnit().getOrCreate(module);
		return printCompilationUnit(cu);
	}

	@Override
	public String printTypes(CtType<?>... type) {
		calculate(null, Arrays.asList(type));
		return getResult();
	}

	@Override
	public String getResult() {
		return printer.getPrinterHelper().toString();
	}

	public void reset() {
		printer.reset();
		context = new PrintingContext();
	}

	@Override
	public void calculate(CtCompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		reset();
		// if empty => is package-info.java, we cannot call types.get(0) in the then branch
		if (!types.isEmpty()) {
			CtType<?> type = types.get(0);
			if (sourceCompilationUnit == null) {
				sourceCompilationUnit = type.getFactory().CompilationUnit().getOrCreate(type);
			}
			if (type.getPackage() == null) {
				type.setParent(type.getFactory().Package().getRootPackage());
			}
			CtPackageReference packRef = type.getPackage().getReference();
			if (!packRef.equals(sourceCompilationUnit.getPackageDeclaration().getReference())) {
				//the type was cloned and moved to different package. Adapt package reference of compilation unit too
				sourceCompilationUnit.getPackageDeclaration().setReference(packRef);
			}
			if (!hasSameTypes(sourceCompilationUnit, types)) {
				//the provided CU has different types, then these which has to be printed
				//clone CU and assign it expected types
				sourceCompilationUnit = sourceCompilationUnit.clone();
				sourceCompilationUnit.setDeclaredTypes(types);
			}
		}
		applyPreProcessors(sourceCompilationUnit);
		scan(sourceCompilationUnit);
	}

	private boolean hasSameTypes(CtCompilationUnit compilationUnit, List<CtType<?>> types) {
		List<CtTypeReference<?>> cuTypes = compilationUnit.getDeclaredTypeReferences();
		if (cuTypes.size() != types.size()) {
			return false;
		}
		Set<String> cuQnames = cuTypes.stream().map(CtTypeReference::getQualifiedName).collect(Collectors.toSet());
		Set<String> qnames = types.stream().map(CtType::getQualifiedName).collect(Collectors.toSet());
		return cuQnames.equals(qnames);
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

	/**
	 * Set preprocessors that the printer automatically runs on the model before printing it.
	 * Typically, such preprocessors validate or adjust the model before printing.
	 *
	 * @param preprocessors list of processors to run on the model before printing
	 */
	public void setPreprocessors(List<Processor<CtElement>> preprocessors) {
		this.preprocessors.clear();
		this.preprocessors.addAll(preprocessors);
	}

	/**
	 * @return all processors currently set to run on the model before printing
	 */
	public List<Processor<CtElement>> getPreprocessors() {
		return this.preprocessors;
	}

	/**
	 * @param ignoreImplicit true to ignore `isImplicit` attribute on model and always print fully qualified names
	 */
	public void setIgnoreImplicit(boolean ignoreImplicit) {
		this.ignoreImplicit = ignoreImplicit;
}

	@Override
	public void visitCtYieldStatement(CtYieldStatement statement) {
		if (statement.isImplicit()) {
			scan(statement.getExpression());
			exitCtStatement(statement);
			return;
		}
		enterCtStatement(statement);
		printer.writeKeyword("yield");
		if (statement.getExpression() != null) {
			printer.writeSpace();
		}
		scan(statement.getExpression());
		exitCtStatement(statement);
	}

	@Override
	public void visitCtTypePattern(CtTypePattern pattern) {
		enterCtExpression(pattern);
		scan(pattern.getVariable());
		exitCtExpression(pattern);
	}

	/**
	 * @return true if the printer is minimizing the amount of round brackets in expressions
	 */
	protected boolean isMinimizeRoundBrackets() {
		return minimizeRoundBrackets;
	}

	/**
	 * When set to true, this activates round bracket minimization for expressions. This means that
	 * the printer will attempt to only write round brackets strictly necessary for preserving
	 * syntactical structure (and by extension, semantics).
	 *
	 * As an example, the expression <code>1 + 2 + 3 + 4</code> is written as
	 * <code>((1 + 2) + 3) + 4</code> without round bracket minimization, but entirely without
	 * parentheses when minimization is enabled. However, an expression <code>1 + 2 + (3 + 4)</code>
	 * is still written as <code>1 + 2 + (3 + 4)</code> to preserve syntactical structure, even though
	 * the brackets are semantically redundant.
	 *
	 * @param minimizeRoundBrackets set whether or not to minimize round brackets in expressions
	 */
	protected void setMinimizeRoundBrackets(boolean minimizeRoundBrackets) {
		this.minimizeRoundBrackets = minimizeRoundBrackets;
	}

	protected String stripLeadingDigits(String simpleName) {
		int i = 0;
		while (i < simpleName.length()) {
			if (!Character.isDigit(simpleName.charAt(i))) {
				return simpleName.substring(i);
			}
			i++;
		}
		return simpleName;
	}
	@Override
	public void visitCtRecord(CtRecord recordType) {
		context.pushCurrentThis(recordType);
		visitCtType(recordType);
		printer.writeKeyword("record").writeSpace().writeIdentifier(stripLeadingDigits(recordType.getSimpleName()));
		elementPrinterHelper.writeFormalTypeParameters(recordType);
		elementPrinterHelper.printList(recordType.getRecordComponents(), null, false, "(", false, false, ",", true, false, ")", this::visitCtRecordComponent);
		elementPrinterHelper.writeImplementsClause(recordType);

		printer.writeSpace().writeSeparator("{").incTab();
		elementPrinterHelper.writeElementList(recordType.getTypeMembers());
		getPrinterHelper().adjustEndPosition(recordType);
		printer.decTab().writeSeparator("}");
		context.popCurrentThis();
	}


	@Override
	public void visitCtRecordComponent(CtRecordComponent recordComponent) {
		elementPrinterHelper.writeAnnotations(recordComponent);
		visitCtTypeReference(recordComponent.getType());
		printer.writeSpace();
		printer.writeIdentifier(recordComponent.getSimpleName());
	}

	@Override
	public void visitCtCasePattern(CtCasePattern casePattern) {
		scan(casePattern.getPattern());
	}

	@Override
	public void visitCtRecordPattern(CtRecordPattern recordPattern) {
		scan(recordPattern.getRecordType());
		elementPrinterHelper.printList(recordPattern.getPatternList(),
			null, false, "(", false, false, ",", true, false, ")", this::scan);
	}

	@Override
	public void visitCtReceiverParameter(CtReceiverParameter receiverParameter) {
		elementPrinterHelper.writeComment(receiverParameter);
		elementPrinterHelper.writeAnnotations(receiverParameter);

		printer.writeIdentifier(receiverParameter.getType().getSimpleName());
		printer.writeSpace();
		// if the receiver parameter is in an inner class, we need to print the outer class name
		boolean isInnerClass = !receiverParameter.getType().getTopLevelType().getQualifiedName().equals(receiverParameter.getParent(CtType.class).getQualifiedName());
		boolean isConstructor = receiverParameter.getParent() instanceof CtConstructor;
		if (isConstructor && isInnerClass) {
			// inside a ctor of an inner class, the identifier is $SimpleName.this
			printer.writeSeparator(receiverParameter.getType().getSimpleName() + ".this");
		} else {
			printer.writeSeparator("this");
		}
	}

	@Override
	public void visitCtUnnamedPattern(CtUnnamedPattern unnamedPattern) {
		printer.writeKeyword(CtLocalVariable.UNNAMED_VARIABLE_NAME);
	}
}
