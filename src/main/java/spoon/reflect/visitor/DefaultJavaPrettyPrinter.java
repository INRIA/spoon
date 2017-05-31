/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
import spoon.reflect.code.CtCodeElement;
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
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
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
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
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
import spoon.reflect.visitor.printer.ElementPrinterHelper;
import spoon.reflect.visitor.printer.PrinterHelper;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	 * The beginning of a inline comment
	 */
	public static final String INLINE_COMMENT_START = "// ";

	/**
	 * The beginning of a block comment
	 */
	public static final String BLOCK_COMMENT_START = "/* ";

	/**
	 * The printing context.
	 */
	public PrintingContext context = new PrintingContext();

	/**
	 * Handle imports of classes.
	 */
	private ImportScanner importsContext;

	/**
	 * Environment which Spoon is executed.
	 */
	private Environment env;

	/**
	 * Printer helper.
	 */
	private PrinterHelper printer;

	/**
	 * Element printer helper.
	 */
	private ElementPrinterHelper elementPrinterHelper;

	/**
	 * Compilation unit we are printing.
	 */
	private CompilationUnit sourceCompilationUnit;

	/**
	 * Creates a new code generator visitor.
	 */
	public DefaultJavaPrettyPrinter(Environment env) {
		this.env = env;
		printer = new PrinterHelper(env);
		elementPrinterHelper = new ElementPrinterHelper(printer, this, env);
		if (env.isAutoImports()) {
			this.importsContext = new ImportScannerImpl();
		} else {
			this.importsContext = new MinimalImportScanner();
		}
	}

	/**
	 * Enters an expression.
	 */
	protected void enterCtExpression(CtExpression<?> e) {
		if (!(e instanceof CtStatement)) {
			elementPrinterHelper.writeComment(e, CommentOffset.BEFORE);
		}
		printer.mapLine(e, sourceCompilationUnit);
		if (shouldSetBracket(e)) {
			context.parenthesedExpression.push(e);
			printer.write("(");
		}
		if (!e.getTypeCasts().isEmpty()) {
			for (CtTypeReference<?> r : e.getTypeCasts()) {
				printer.write("(");
				DefaultJavaPrettyPrinter.this.scan(r);
				printer.write(") ");
				printer.write("(");
				context.parenthesedExpression.push(e);
			}
		}
	}

	/**
	 * Enters a statement.
	 */
	protected void enterCtStatement(CtStatement s) {
		elementPrinterHelper.writeComment(s, CommentOffset.BEFORE);
		printer.mapLine(s, sourceCompilationUnit);
		elementPrinterHelper.writeAnnotations(s);
		if (s.getLabel() != null) {
			printer.write(s.getLabel()).write(" : ");
		}
	}

	/**
	 * Exits an expression.
	 */
	protected void exitCtExpression(CtExpression<?> e) {
		while ((context.parenthesedExpression.size() > 0) && e == context.parenthesedExpression.peek()) {
			context.parenthesedExpression.pop();
			printer.write(")");
		}
		if (!(e instanceof CtStatement)) {
			elementPrinterHelper.writeComment(e, CommentOffset.AFTER);
		}
	}

	/**
	 * Make the imports for a given type.
	 */
	public Collection<CtReference> computeImports(CtType<?> type) {
		context.currentTopLevel = type;
		return importsContext.computeAllImports(context.currentTopLevel);
	}

	/**
	 * Make the imports for all elements.
	 */
	public void computeImports(CtElement element) {
		if (env.isAutoImports()) {
			importsContext.computeImports(element);
		}
	}

	/**
	 * The generic scan method for an element.
	 */
	public DefaultJavaPrettyPrinter scan(CtElement e) {
		if (e != null) {
			context.elementStack.push(e);
			if (env.isPreserveLineNumbers()) {
				if (!(e instanceof CtNamedElement)) {
					printer.adjustStartPosition(e);
				}
			}
			e.accept(this);
			context.elementStack.pop();
		}
		return this;
	}

	/**
	 * The generic scan method for a reference.
	 */
	public DefaultJavaPrettyPrinter scan(CtReference ref) {
		if (ref != null) {
			ref.accept(this);
		}
		return this;
	}

	private boolean shouldSetBracket(CtExpression<?> e) {
		if (e.getTypeCasts().size() != 0) {
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
		return printer.toString();
	}

	@Override
	public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		elementPrinterHelper.writeAnnotations(annotation);
		printer.write("@");
		scan(annotation.getAnnotationType());
		if (annotation.getValues().size() > 0) {
			printer.write("(");
			for (Entry<String, CtExpression> e : annotation.getValues().entrySet()) {
				printer.write(e.getKey() + " = ");
				elementPrinterHelper.writeAnnotationElement(annotation.getFactory(), e.getValue());
				printer.write(", ");
			}
			printer.removeLastChar();
			printer.write(")");
		}
		printer.writeln().writeTabs();
	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
		visitCtType(annotationType);
		printer.write("@interface " + annotationType.getSimpleName() + " {").incTab();

		elementPrinterHelper.writeElementList(annotationType.getTypeMembers());
		printer.decTab().writeTabs().write("}");
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
		printer.write("[");
		scan(arrayAccess.getIndexExpression());
		printer.write("]");
		exitCtExpression(arrayAccess);
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		if (reference.isImplicit()) {
			return;
		}
		scan(reference.getComponentType());
		if (!context.skipArray()) {
			printer.write("[]");
		}
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
		enterCtStatement(asserted);
		printer.write("assert ");
		scan(asserted.getAssertExpression());
		if (asserted.getExpression() != null) {
			printer.write(" : ");
			scan(asserted.getExpression());
		}

	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
		enterCtStatement(assignement);
		enterCtExpression(assignement);
		scan(assignement.getAssigned());
		printer.write(" = ");
		scan(assignement.getAssignment());
		exitCtExpression(assignement);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		enterCtExpression(operator);
		scan(operator.getLeftHandOperand());
		printer.write(" ").writeOperator(operator.getKind()).write(" ");
		scan(operator.getRightHandOperand());
		exitCtExpression(operator);
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		enterCtStatement(block);
		if (!block.isImplicit()) {
			printer.write("{");
		}
		printer.incTab();
		for (CtStatement statement : block.getStatements()) {
			if (!statement.isImplicit()) {
				printer.writeln().writeTabs();
				elementPrinterHelper.writeStatement(statement);
			}
		}
		printer.decTab();
		printer.adjustEndPosition(block);
		if (env.isPreserveLineNumbers()) {
			if (!block.isImplicit()) {
				printer.writeTabs().write("}");
			}
		} else {
			printer.writeln().writeTabs();
			if (!block.isImplicit()) {
				printer.write("}");
			}
		}
	}

	@Override
	public void visitCtBreak(CtBreak breakStatement) {
		enterCtStatement(breakStatement);
		printer.write("break");
		if (breakStatement.getTargetLabel() != null) {
			printer.write(" " + breakStatement.getTargetLabel());
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		enterCtStatement(caseStatement);
		if (caseStatement.getCaseExpression() != null) {
			printer.write("case ");
			// writing enum case expression
			if (caseStatement.getCaseExpression() instanceof CtFieldAccess) {
				final CtFieldReference variable = ((CtFieldAccess) caseStatement.getCaseExpression()).getVariable();
				// In noclasspath mode, we don't have always the type of the declaring type.
				if (variable.getType() != null
						&& variable.getDeclaringType() != null
						&& variable.getType().getQualifiedName().equals(variable.getDeclaringType().getQualifiedName())) {
					printer.write(variable.getSimpleName());
				} else {
					scan(caseStatement.getCaseExpression());
				}
			} else {
				scan(caseStatement.getCaseExpression());
			}
		} else {
			printer.write("default");
		}
		printer.write(" :").incTab();

		for (CtStatement statement : caseStatement.getStatements()) {
			printer.writeln().writeTabs();
			elementPrinterHelper.writeStatement(statement);
		}
		printer.decTab();
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		elementPrinterHelper.writeComment(catchBlock, CommentOffset.BEFORE);
		printer.write(" catch (");
		CtCatchVariable<? extends Throwable> parameter = catchBlock.getParameter();
		if (parameter.getMultiTypes().size() > 0) {
			for (int i = 0; i < parameter.getMultiTypes().size(); i++) {
				CtTypeReference<?> type = parameter.getMultiTypes().get(i);
				scan(type);
				if (i < parameter.getMultiTypes().size() - 1) {
					printer.write(" | ");
				}
			}
			printer.write(" " + parameter.getSimpleName());
		} else {
			scan(parameter);
		}
		printer.write(") ");
		scan(catchBlock.getBody());
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		context.pushCurrentThis(ctClass);
		if (ctClass.getSimpleName() != null && !CtType.NAME_UNKNOWN.equals(ctClass.getSimpleName()) && !ctClass.isAnonymous()) {
			visitCtType(ctClass);
			if (ctClass.isLocalType()) {
				printer.write("class " + ctClass.getSimpleName().replaceAll("^[0-9]*", ""));
			} else {
				printer.write("class " + ctClass.getSimpleName());
			}

			elementPrinterHelper.writeFormalTypeParameters(ctClass);
			elementPrinterHelper.writeExtendsClause(ctClass);
			elementPrinterHelper.writeImplementsClause(ctClass);
		}
		// lst.addAll(elementPrinterHelper.getComments(ctClass, CommentOffset.INSIDE));
		printer.write(" {").incTab();
		elementPrinterHelper.writeElementList(ctClass.getTypeMembers());
		printer.adjustEndPosition(ctClass).decTab().writeTabs().write("}");
		context.popCurrentThis();
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		CtTypeParameterReference ref = typeParameter.getReference();
		if (ref.isImplicit()) {
			return;
		}
		elementPrinterHelper.writeAnnotations(ref);
		if (printQualified(ref)) {
			printer.write(ref.getQualifiedName());
		} else {
			printer.write(ref.getSimpleName());
		}
		if (ref.getBoundingType() != null) {
			if (ref.isUpper()) {
				printer.write(" extends ");
			} else {
				printer.write(" super ");
			}
			scan(ref.getBoundingType());
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
			printer.write("(");
		}
		scan(condition);
		if (parent) {
			printer.write(")");
		}
		printer.write(" ? ");
		CtExpression<T> thenExpression = conditional.getThenExpression();
		scan(thenExpression);
		printer.write(" : ");

		CtExpression<T> elseExpression = conditional.getElseExpression();
		boolean isAssign = false;
		if ((isAssign = elseExpression instanceof CtAssignment)) {
			printer.write("(");
		}
		scan(elseExpression);
		if (isAssign) {
			printer.write(")");
		}
		exitCtExpression(conditional);
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> constructor) {
		elementPrinterHelper.writeComment(constructor);
		elementPrinterHelper.visitCtNamedElement(constructor, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(constructor);
		elementPrinterHelper.writeFormalTypeParameters(constructor);
		if (constructor.getFormalCtTypeParameters().size() > 0) {
			printer.write(' ');
		}
		if (constructor.getDeclaringType() != null) {
			if (constructor.getDeclaringType().isLocalType()) {
				printer.write(constructor.getDeclaringType().getSimpleName().replaceAll("^[0-9]*", ""));
			} else {
				printer.write(constructor.getDeclaringType().getSimpleName());
			}
		}
		elementPrinterHelper.writeExecutableParameters(constructor);
		elementPrinterHelper.writeThrowsClause(constructor);
		printer.write(" ");
		scan(constructor.getBody());
	}

	@Override
	public void visitCtContinue(CtContinue continueStatement) {
		enterCtStatement(continueStatement);
		printer.write("continue");
		if (continueStatement.getTargetLabel() != null) {
			printer.write(" " + continueStatement.getTargetLabel());
		}
	}

	@Override
	public void visitCtDo(CtDo doLoop) {
		enterCtStatement(doLoop);
		printer.write("do");
		elementPrinterHelper.writeIfOrLoopBlock(doLoop.getBody());
		printer.write("while (");
		scan(doLoop.getLoopingExpression());
		printer.write(" )");
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		visitCtType(ctEnum);
		printer.write("enum " + ctEnum.getSimpleName());
		elementPrinterHelper.writeImplementsClause(ctEnum);
		context.pushCurrentThis(ctEnum);
		printer.write(" {").incTab().writeln();

		if (ctEnum.getEnumValues().size() == 0) {
			printer.writeTabs().write(";").writeln();
		} else {
			for (CtEnumValue<?> enumValue : ctEnum.getEnumValues()) {
				scan(enumValue);
				printer.write(", ");
			}
			printer.removeLastChar();
			printer.write(";");
		}

		elementPrinterHelper.writeElementList(ctEnum.getTypeMembers());
		printer.decTab().writeTabs().write("}");
		context.popCurrentThis();
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		printer.write(reference.getSignature());
	}

	@Override
	public <T> void visitCtField(CtField<T> f) {
		elementPrinterHelper.writeComment(f);
		elementPrinterHelper.visitCtNamedElement(f, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(f);
		scan(f.getType());
		printer.write(" ");
		printer.write(f.getSimpleName());

		if (f.getDefaultExpression() != null) {
			printer.write(" = ");
			scan(f.getDefaultExpression());
		}
		printer.write(";");
	}

	@Override
	public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {
		elementPrinterHelper.visitCtNamedElement(enumValue, sourceCompilationUnit);
		printer.write(enumValue.getSimpleName());
		if (enumValue.getDefaultExpression() != null) {
			CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) enumValue.getDefaultExpression();
			if (constructorCall.getArguments().size() > 0) {
				printer.write("(");
				boolean first = true;
				for (CtExpression<?> ctexpr : constructorCall.getArguments()) {
					if (first) {
						first = false;
					} else {
						printer.write(",");
					}
					scan(ctexpr);
				}
				printer.write(")");
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
			if (f.getVariable().isStatic() && f.getTarget() instanceof CtTypeAccess) {
				_context.ignoreGenerics(true);
			}
			CtExpression<?> target = f.getTarget();
			if (target != null) {
				boolean isInitializeStaticFinalField = isInitializeStaticFinalField(f.getTarget());
				boolean isStaticField = f.getVariable().isStatic();
				boolean isImportedField = importsContext.isImported(f.getVariable());

				if (!isInitializeStaticFinalField && !(isStaticField && isImportedField)) {
					if (target.isImplicit()) {
						/*
						 * target is implicit, check whether there is no conflict with an local variable, catch variable or parameter
						 * in case of conflict make it explicit, otherwise the field access is shadowed by that variable.
						 * Search for potential variable declaration until we found a class which declares or inherits this field
						 */
						final CtField<?> field = f.getVariable().getFieldDeclaration();
						final String fieldName = field.getSimpleName();
						CtVariable<?> var = f.getVariable().map(new PotentialVariableDeclarationFunction(fieldName)).first();
						if (var != field) {
							//another variable declaration was found which is hiding the field declaration for this field access. Make the field access expicit
							target.setImplicit(false);
						}
					}
					printer.snapshotLength();
					scan(target);
					if (printer.hasNewContent()) {
						printer.write(".");
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
		if (parent instanceof CtFieldWrite
				&& targetExp.equals(((CtFieldWrite) parent).getTarget())
				&& anonymousParent != null
				&& ((CtFieldWrite) parent).getVariable() != null
				&& ((CtFieldWrite) parent).getVariable().getModifiers().contains(ModifierKind.STATIC)
				&& ((CtFieldWrite) parent).getVariable().getModifiers().contains(ModifierKind.FINAL)) {
			return true;
		}
		return false;
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		try {
			enterCtExpression(thisAccess);

			// we only write qualified this when this is required
			// this is good both in fully-qualified mode and in readable (with-imports) mode
			// the implicit information is used for analysis (eg are visibility caused by implicit bugs?) but
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
				printer.write("this");
				return; // still go through finally block below
			}

			// we cannot have fully-qualified this in anonymous classes
			// we simply print "this" and it always works
			// this has to come after the implicit test just before
			if (targetType.isAnonymous()) {
				printer.write("this");
				return;
			}

			// complex case of qualifed this
			if (!context.currentThis.isEmpty()) {

				CtType lastType = context.currentThis.peekFirst().type;
				String lastTypeQualifiedName = lastType.getQualifiedName();
				String targetTypeQualifiedName = targetType.getQualifiedName();

				if (!lastTypeQualifiedName.equals(targetTypeQualifiedName)) {
					printer.snapshotLength();
					visitCtTypeReferenceWithoutGenerics(targetType);
					if (printer.hasNewContent()) {
						printer.write(".");
					}
					printer.write("this");
					return;
				}
			}

			// the default super simple case only comes at the end
			printer.write("this");
		} finally {
			exitCtExpression(thisAccess);
		}
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		enterCtExpression(f);
		if (f.getTarget() != null) {
			scan(f.getTarget());
			printer.write(".");
		}
		printer.write("super");

		exitCtExpression(f);
	}

	@Override
	public void visitCtJavaDoc(CtJavaDoc comment) {
		visitCtComment(comment);
	}

	@Override
	public void visitCtJavaDocTag(CtJavaDocTag docTag) {
		printer.write(COMMENT_STAR);
		printer.write(CtJavaDocTag.JAVADOC_TAG_PREFIX);
		printer.write(docTag.getType().name().toLowerCase());
		printer.write(" ");
		if (docTag.getType().hasParam()) {
			printer.write(docTag.getParam()).writeln().writeTabs();
		}

		String[] tagLines = docTag.getContent().split(LINE_SEPARATOR);
		for (int i = 0; i < tagLines.length; i++) {
			String com = tagLines[i];
			if (i > 0 || docTag.getType().hasParam()) {
				printer.write(COMMENT_STAR);
			}
			if (docTag.getType().hasParam()) {
				printer.write("\t\t");
			}
			printer.write(com.trim()).writeln().writeTabs();
		}
	}

	@Override
	public void visitCtComment(CtComment comment) {
		if (!env.isCommentsEnabled() && context.elementStack.size() > 1) {
			return;
		}
		switch (comment.getCommentType()) {
		case FILE:
			printer.write(JAVADOC_START).writeln();
			break;
		case JAVADOC:
			printer.write(JAVADOC_START).writeln().writeTabs();
			break;
		case INLINE:
			printer.write(INLINE_COMMENT_START);
			break;
		case BLOCK:
			printer.write(BLOCK_COMMENT_START);
			break;
		}
		String content = comment.getContent();
		switch (comment.getCommentType()) {
		case INLINE:
			printer.write(content);
			break;
		default:
			String[] lines = content.split(LINE_SEPARATOR);
			for (int i = 0; i < lines.length; i++) {
				String com = lines[i];
				if (comment.getCommentType() == CtComment.CommentType.BLOCK) {
					printer.write(com);
					if (lines.length > 1) {
						printer.writeln().writeTabs();
					}
				} else {
					if (com.length() > 0) {
						printer.write(COMMENT_STAR + com).writeln().writeTabs();
					} else {
						printer.write(" *" /* no trailing space */ + com).writeln().writeTabs();
					}
				}

			}
			if (comment instanceof CtJavaDoc) {
				if (!((CtJavaDoc) comment).getTags().isEmpty()) {
					printer.write(" *").writeln().writeTabs();
				}
				for (CtJavaDocTag docTag : ((CtJavaDoc) comment).getTags()) {
					scan(docTag);
				}
			}
			break;
		}

		switch (comment.getCommentType()) {
		case BLOCK:
			printer.write(BLOCK_COMMENT_END);
			break;
		case FILE:
			printer.write(BLOCK_COMMENT_END);
			break;
		case JAVADOC:
			printer.write(BLOCK_COMMENT_END);
			break;
		}
	}

	@Override
	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
		enterCtExpression(annotationFieldAccess);
		try (Writable _context = context.modify()) {
			if (annotationFieldAccess.getTarget() != null) {
				scan(annotationFieldAccess.getTarget());
				printer.write(".");
				_context.ignoreStaticAccess(true);
			}
			_context.ignoreGenerics(true);
			scan(annotationFieldAccess.getVariable());
			printer.write("()");
		}
		exitCtExpression(annotationFieldAccess);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		boolean isStatic = reference.getSimpleName().equals("class") || !reference.getSimpleName().equals("super") && reference.isStatic();

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
			printer.write(".");
		}
		printer.write(reference.getSimpleName());
	}

	@Override
	public void visitCtFor(CtFor forLoop) {
		enterCtStatement(forLoop);
		printer.write("for (");
		List<CtStatement> st = forLoop.getForInit();
		if (st.size() > 0) {
			scan(st.get(0));
		}
		if (st.size() > 1) {
			try (Writable _context = context.modify().noTypeDecl(true)) {
				for (int i = 1; i < st.size(); i++) {
					printer.write(", ");
					scan(st.get(i));
				}
			}
		}
		printer.write("; ");
		scan(forLoop.getExpression());
		printer.write(";");
		if (!forLoop.getForUpdate().isEmpty()) {
			printer.write(" ");
		}
		for (CtStatement s : forLoop.getForUpdate()) {
			scan(s);
			printer.write(" , ");
		}
		if (forLoop.getForUpdate().size() > 0) {
			printer.removeLastChar();
		}
		printer.write(")");
		elementPrinterHelper.writeIfOrLoopBlock(forLoop.getBody());
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {
		enterCtStatement(foreach);
		printer.write("for (");
		scan(foreach.getVariable());
		printer.write(" : ");
		scan(foreach.getExpression());
		printer.write(")");
		elementPrinterHelper.writeIfOrLoopBlock(foreach.getBody());
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		enterCtStatement(ifElement);
		printer.write("if (");
		scan(ifElement.getCondition());
		printer.write(")");
		elementPrinterHelper.writeIfOrLoopBlock(ifElement.getThenStatement());
		if (ifElement.getElseStatement() != null) {
			List<CtComment> comments = elementPrinterHelper.getComments(ifElement, CommentOffset.INSIDE);
			for (CtComment comment : comments) {
				SourcePosition thenPosition =
						ifElement.getThenStatement().getPosition() == null ? ((CtBlock) ifElement.getThenStatement()).getStatement(0).getPosition() : ifElement.getThenStatement().getPosition();
				if (comment.getPosition().getSourceStart() > thenPosition.getSourceEnd()) {
					elementPrinterHelper.writeComment(comment);
				}
			}
			printer.write("else");
			elementPrinterHelper.writeIfOrLoopBlock(ifElement.getElseStatement());
		}
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		visitCtType(intrface);
		printer.write("interface " + intrface.getSimpleName());
		if (intrface.getFormalCtTypeParameters() != null) {
			elementPrinterHelper.writeFormalTypeParameters(intrface);
		}

		if (intrface.getSuperInterfaces().size() > 0) {
			printer.write(" extends ");
			for (CtTypeReference<?> ref : intrface.getSuperInterfaces()) {
				scan(ref);
				printer.write(" , ");
			}
			printer.removeLastChar();
		}
		context.pushCurrentThis(intrface);
		printer.write(" {").incTab();
		// Content
		elementPrinterHelper.writeElementList(intrface.getTypeMembers());
		printer.decTab().writeTabs().write("}");
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
				printer.write("this");
			} else {
				printer.snapshotLength();
				scan(invocation.getTarget());
				if (printer.hasNewContent()) {
					printer.write(".");
				}
				printer.write("super");
			}
		} else {
			// It's a method invocation
			printer.snapshotLength();
			try (Writable _context = context.modify()) {
				if (invocation.getTarget() instanceof CtTypeAccess) {
					_context.ignoreGenerics(true);
				}
				scan(invocation.getTarget());
			}
			if (printer.hasNewContent()) {
				printer.write(".");
			}

			elementPrinterHelper.writeActualTypeArguments(invocation);
			if (env.isPreserveLineNumbers()) {
				printer.adjustStartPosition(invocation);
			}
			printer.write(invocation.getExecutable().getSimpleName());
		}
		printer.write("(");
		boolean remove = false;
		for (CtExpression<?> e : invocation.getArguments()) {
			scan(e);
			printer.write(", ");
			remove = true;
		}
		if (remove) {
			printer.removeLastChar();
		}
		printer.write(")");
		exitCtExpression(invocation);
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		enterCtExpression(literal);
		if (literal.getValue() == null) {
			printer.write("null");
		} else if (literal.getValue() instanceof Long) {
			printer.write(literal.getValue() + "L");
		} else if (literal.getValue() instanceof Float) {
			printer.write(literal.getValue() + "F");
		} else if (literal.getValue() instanceof Character) {
			printer.write("'");

			boolean mayContainsSpecialCharacter = true;

			SourcePosition position = literal.getPosition();
			if (position != null) {
				// the size of the string in the source code, the -1 is the size of the ' or " in the source code
				int stringLength = position.getSourceEnd() - position.getSourceStart() - 1;
				// if the string in the source is not the same as the string in the literal, the string may contains special characters
				mayContainsSpecialCharacter = stringLength != 1;
			}
			printer.writeStringLiteral(new String(new char[] { (Character) literal.getValue() }), mayContainsSpecialCharacter);

			printer.write("'");
		} else if (literal.getValue() instanceof String) {
			printer.write('\"');

			boolean mayContainsSpecialCharacters = true;

			SourcePosition position = literal.getPosition();
			if (position != null) {
				// the size of the string in the source code, the -1 is the size of the ' or " in the source code
				int stringLength = position.getSourceEnd() - position.getSourceStart() - 1;
				// if the string in the source is not the same as the string in the literal, the string may contains special characters
				mayContainsSpecialCharacters = ((String) literal.getValue()).length() != stringLength;
			}
			printer.writeStringLiteral((String) literal.getValue(), mayContainsSpecialCharacters);

			printer.write('\"');
		} else if (literal.getValue() instanceof Class) {
			printer.write(((Class<?>) literal.getValue()).getName());
		} else {
			printer.write(literal.getValue().toString());
		}
		exitCtExpression(literal);
	}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		if (!context.noTypeDecl()) {
			enterCtStatement(localVariable);
		}
		if (env.isPreserveLineNumbers()) {
			printer.adjustStartPosition(localVariable);
		}
		if (!context.noTypeDecl()) {
			elementPrinterHelper.writeModifiers(localVariable);
			scan(localVariable.getType());
			printer.write(" ");
		}
		printer.write(localVariable.getSimpleName());
		if (localVariable.getDefaultExpression() != null) {
			printer.write(" = ");
			scan(localVariable.getDefaultExpression());
		}
	}

	@Override
	public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
		printer.write(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		if (env.isPreserveLineNumbers()) {
			printer.adjustStartPosition(catchVariable);
		}
		elementPrinterHelper.writeModifiers(catchVariable);
		scan(catchVariable.getType());
		printer.write(" ");
		printer.write(catchVariable.getSimpleName());
	}

	@Override
	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
		printer.write(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		elementPrinterHelper.writeComment(m);
		elementPrinterHelper.visitCtNamedElement(m, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(m);
		if (m.isDefaultMethod()) {
			printer.write("default ");
		}
		elementPrinterHelper.writeFormalTypeParameters(m);
		if (m.getFormalCtTypeParameters().size() > 0) {
			printer.write(' ');
		}
		try (Writable _context = context.modify().ignoreGenerics(false)) {
			scan(m.getType());
		}
		printer.write(" ");
		printer.write(m.getSimpleName());
		elementPrinterHelper.writeExecutableParameters(m);
		elementPrinterHelper.writeThrowsClause(m);
		if (m.getBody() != null) {
			printer.write(" ");
			scan(m.getBody());
			if (m.getBody().getPosition() != null) {
				if (m.getBody().getPosition().getCompilationUnit() == sourceCompilationUnit) {
					if (m.getBody().getStatements().isEmpty() || !(m.getBody().getStatements().get(m.getBody().getStatements().size() - 1) instanceof CtReturn)) {
						printer.putLineNumberMapping(m.getBody().getPosition().getEndLine());
					}
				} else {
					printer.undefineLine();
				}
			} else {
				printer.undefineLine();
			}
		} else {
			printer.write(";");
		}
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		elementPrinterHelper.writeComment(annotationMethod);
		elementPrinterHelper.visitCtNamedElement(annotationMethod, sourceCompilationUnit);
		elementPrinterHelper.writeModifiers(annotationMethod);
		scan(annotationMethod.getType());
		printer.write(" ");
		printer.write(annotationMethod.getSimpleName());

		printer.write("()");
		if (annotationMethod.getDefaultExpression() != null) {
			printer.write(" default ");
			scan(annotationMethod.getDefaultExpression());
		}
		printer.write(";");
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
				printer.write("new ");
			}

			try (Writable _context = context.modify().skipArray(true)) {
				scan(ref);
			}
			for (int i = 0; ref instanceof CtArrayTypeReference; i++) {
				printer.write("[");
				if (newArray.getDimensionExpressions().size() > i) {
					CtExpression<Integer> e = newArray.getDimensionExpressions().get(i);
					scan(e);
				}
				printer.write("]");
				ref = ((CtArrayTypeReference) ref).getComponentType();
			}
		}
		if (newArray.getDimensionExpressions().size() == 0) {
			printer.write("{ ");
			List<CtExpression<?>> l_elements = newArray.getElements();
			for (int i = 0; i < l_elements.size(); i++) {
				CtExpression e = l_elements.get(i);
				scan(e);
				printer.write(" , ");
				if (i + 1 == l_elements.size()) {
					printer.removeLastChar();
					// if the last element c
					List<CtComment> comments = elementPrinterHelper.getComments(e, CommentOffset.AFTER);
					// if the last element contains an inline comment, print a new line before closing the array
					if (!comments.isEmpty() && comments.get(comments.size() - 1).getCommentType() == CtComment.CommentType.INLINE) {
						printer.writeln();
					}
				}
			}

			elementPrinterHelper.writeComment(newArray, CommentOffset.INSIDE);
			printer.write(" }");
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
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		enterCtStatement(newClass);
		enterCtExpression(newClass);

		printConstructorCall(newClass);

		scan(newClass.getAnonymousClass());
		exitCtExpression(newClass);
	}

	private <T> void printConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		try (Writable _context = context.modify()) {
			if (ctConstructorCall.getTarget() != null) {
				scan(ctConstructorCall.getTarget());
				printer.write(".");
				_context.ignoreEnclosingClass(true);
			}

			if (hasDeclaringTypeWithGenerics(ctConstructorCall.getType())) {
				_context.ignoreEnclosingClass(true);
			}

			printer.write("new ");

			if (ctConstructorCall.getActualTypeArguments().size() > 0) {
				elementPrinterHelper.writeActualTypeArguments(ctConstructorCall);
			}

			scan(ctConstructorCall.getType());
		}

		printer.write("(");
		for (CtCodeElement exp : ctConstructorCall.getArguments()) {
			scan(exp);
			printer.write(", ");
		}
		if (ctConstructorCall.getArguments().size() > 0) {
			printer.removeLastChar();
		}
		printer.write(")");
	}

	/**
	 * JDT doesn't support <code>new Foo<K>.Bar()</code>. To avoid reprint this kind of type reference,
	 * we check that the reference has a declaring type with generics.
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=474593
	 *
	 * @param reference
	 * 		Type reference concerned by the bug.
	 * @return true if a declaring type have generic types.
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
		if (reference.getDeclaringType().getActualTypeArguments().size() != 0) {
			return true;
		}
		// Checks if the declaring type has generic types.
		return hasDeclaringTypeWithGenerics(reference.getDeclaringType());
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		enterCtExpression(lambda);

		printer.write("(");
		if (lambda.getParameters().size() > 0) {
			for (CtParameter<?> parameter : lambda.getParameters()) {
				scan(parameter);
				printer.write(",");
			}
			printer.removeLastChar();
		}
		printer.write(") -> ");

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
		scan(expression.getTarget());
		printer.write("::");
		if (expression.getExecutable().isConstructor()) {
			printer.write("new");
		} else {
			printer.write(expression.getExecutable().getSimpleName());
		}
		exitCtExpression(expression);
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		enterCtStatement(assignment);
		enterCtExpression(assignment);
		scan(assignment.getAssigned());
		printer.write(" ");
		printer.writeOperator(assignment.getKind());
		printer.write("= ");
		scan(assignment.getAssignment());
		exitCtExpression(assignment);
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {
		if (!ctPackage.isUnnamedPackage()) {
			printer.write("package " + ctPackage.getQualifiedName() + ";");
		} else {
			printer.write("// default package (CtPackage.TOP_LEVEL_PACKAGE_NAME in Spoon= unnamed package)\n");
		}
	}

	@Override
	public void visitCtPackageReference(CtPackageReference reference) {
		printer.write(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		elementPrinterHelper.writeComment(parameter);
		elementPrinterHelper.writeAnnotations(parameter);
		elementPrinterHelper.writeModifiers(parameter);
		if (parameter.isVarArgs()) {
			scan(((CtArrayTypeReference<T>) parameter.getType()).getComponentType());
			printer.write("...");
		} else {
			scan(parameter.getType());
		}
		printer.write(" ");
		printer.write(parameter.getSimpleName());
	}

	@Override
	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		printer.write(reference.getSimpleName());
	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		enterCtStatement(returnStatement);
		printer.write("return ");
		scan(returnStatement.getReturnedExpression());
	}

	private <T> void visitCtType(CtType<T> type) {
		elementPrinterHelper.writeComment(type, CommentOffset.BEFORE);
		printer.mapLine(type, sourceCompilationUnit);
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
		printer.write("switch (");
		scan(switchStatement.getSelector());
		printer.write(") {").incTab();
		for (CtCase<?> c : switchStatement.getCases()) {
			printer.writeln().writeTabs();
			scan(c);
		}
		if (env.isPreserveLineNumbers()) {
			printer.decTab().write("}");
		} else {
			printer.decTab().writeln().writeTabs().write("}");
		}
	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		enterCtStatement(synchro);
		printer.write("synchronized");
		if (synchro.getExpression() != null) {
			printer.write("(");
			scan(synchro.getExpression());
			printer.write(") ");
		}
		scan(synchro.getBlock());
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		enterCtStatement(throwStatement);
		printer.write("throw ");
		scan(throwStatement.getThrownExpression());
	}

	@Override
	public void visitCtTry(CtTry tryBlock) {
		enterCtStatement(tryBlock);
		printer.write("try ");
		scan(tryBlock.getBody());
		for (CtCatch c : tryBlock.getCatchers()) {
			scan(c);
		}

		if (tryBlock.getFinalizer() != null) {
			printer.write(" finally ");
			scan(tryBlock.getFinalizer());
		}
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		enterCtStatement(tryWithResource);
		printer.write("try ");
		if (tryWithResource.getResources() != null && !tryWithResource.getResources().isEmpty()) {
			printer.write("(");
			for (CtLocalVariable<?> r : tryWithResource.getResources()) {
				scan(r);
				printer.write(";");
			}
			printer.removeLastChar();
			printer.write(") ");
		}
		scan(tryWithResource.getBody());
		for (CtCatch c : tryWithResource.getCatchers()) {
			scan(c);
		}

		if (tryWithResource.getFinalizer() != null) {
			printer.write(" finally ");
			scan(tryWithResource.getFinalizer());
		}
	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		if (ref.isImplicit()) {
			return;
		}
		elementPrinterHelper.writeAnnotations(ref);
		if (printQualified(ref)) {
			printer.write(ref.getQualifiedName());
		} else {
			printer.write(ref.getSimpleName());
		}
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		if (wildcardReference.isImplicit()) {
			return;
		}
		elementPrinterHelper.writeAnnotations(wildcardReference);
		if (printQualified(wildcardReference)) {
			printer.write(wildcardReference.getQualifiedName());
		} else {
			printer.write(wildcardReference.getSimpleName());
		}
		if (wildcardReference.getBoundingType() != null) {
			if (wildcardReference.isUpper()) {
				printer.write(" extends ");
			} else {
				printer.write(" super ");
			}
			scan(wildcardReference.getBoundingType());
		}
	}

	private boolean printQualified(CtTypeReference<?> ref) {
		if (importsContext.isImported(ref) || (this.env.isAutoImports() && ref.getPackage() != null && ref.getPackage().getSimpleName().equals("java.lang"))) {
			// If my.pkg.Something is imported, but
			//A) we are in the context of a class which is also called "Something",
			//B) we are in the context of a class which defines field which is also called "Something",
			//	we should still use qualified version my.pkg.Something
			for (TypeContext typeContext : context.currentThis) {
				if (typeContext.getSimpleName().equals(ref.getSimpleName())
						&& !Objects.equals(typeContext.getPackage(), ref.getPackage())) {
					return true;
				}
				if (typeContext.isNameConflict(ref.getSimpleName())) {
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
		for (CtTypeReference<?> bound : reference.getBounds()) {
			scan(bound);
			printer.write(" & ");
		}
		printer.removeLastChar();
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
			printer.write(ref.getSimpleName());
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
					printer.write(".");
				}
			}
			//?? are these annotations on correct place ??
			elementPrinterHelper.writeAnnotations(ref);
			if (ref.isLocalType()) {
				printer.write(ref.getSimpleName().replaceAll("^[0-9]*", ""));
			} else {
				printer.write(ref.getSimpleName());
			}
		} else {
			if (ref.getPackage() != null && printQualified(ref)) {
				if (!ref.getPackage().isUnnamedPackage()) {
					scan(ref.getPackage());
					printer.write(CtPackage.PACKAGE_SEPARATOR);
				}
			}
			elementPrinterHelper.writeAnnotations(ref);
			printer.write(ref.getSimpleName());
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
		printer.preWriteUnaryOperator(operator.getKind());
		scan(operator.getOperand());
		printer.postWriteUnaryOperator(operator.getKind());
		exitCtExpression(operator);
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		enterCtExpression(variableRead);
		printer.write(variableRead.getVariable().getSimpleName());
		exitCtExpression(variableRead);
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		enterCtExpression(variableWrite);
		printer.write(variableWrite.getVariable().getSimpleName());
		exitCtExpression(variableWrite);
	}

	public void visitCtWhile(CtWhile whileLoop) {
		enterCtStatement(whileLoop);
		printer.write("while (");
		scan(whileLoop.getLoopingExpression());
		printer.write(")");

		elementPrinterHelper.writeIfOrLoopBlock(whileLoop.getBody());
	}

	@Override
	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
		elementPrinterHelper.writeComment(expression);
		printer.write(expression.getValue());
	}

	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		elementPrinterHelper.writeComment(statement);
		printer.write(statement.getValue());
	}

	public ElementPrinterHelper getElementPrinterHelper() {
		return elementPrinterHelper;
	}

	public PrintingContext getContext() {
		return context;
	}

	@Override
	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
		printer.write(reference.getSimpleName());
	}

	@Override
	public String getPackageDeclaration() {
		return printPackageInfo(context.currentTopLevel.getPackage());
	}

	@Override
	public String printPackageInfo(CtPackage pack) {
		PrinterHelper bck = printer;
		ElementPrinterHelper bck2 = elementPrinterHelper;
		printer = new PrinterHelper(env);
		elementPrinterHelper = new ElementPrinterHelper(printer, this, env);

		elementPrinterHelper.writeComment(pack);

		for (CtAnnotation<?> a : pack.getAnnotations()) {
			a.accept(this);
		}

		if (!pack.isUnnamedPackage()) {
			printer.write("package " + pack.getQualifiedName() + ";");
		}
		String ret = printer.toString();
		elementPrinterHelper = bck2;
		printer = bck;

		return ret;
	}

	@Override
	public String getResult() {
		return printer.toString();
	}

	@Override
	public void reset() {
		printer = new PrinterHelper(env);
		elementPrinterHelper.setPrinter(printer);
		context = new PrintingContext();
	}

	@Override
	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		this.sourceCompilationUnit = sourceCompilationUnit;

		// reset the importsContext to avoid errors with multiple CU
		if (env.isAutoImports()) {
			this.importsContext = new ImportScannerImpl();
		} else {
			this.importsContext = new MinimalImportScanner();
		}

		Set<CtReference> imports = new HashSet<>();
		for (CtType<?> t : types) {
			imports.addAll(computeImports(t));
		}
		elementPrinterHelper.writeHeader(types, imports);
		for (CtType<?> t : types) {
			scan(t);
			if (!env.isPreserveLineNumbers()) {
				// saving lines and chars
				printer.writeln().writeln().writeTabs();
			} else {
				printer.adjustEndPosition(t);
			}
		}
	}

	@Override
	public Map<Integer, Integer> getLineNumberMapping() {
		return printer.getLineNumberMapping();
	}
}
