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
import spoon.reflect.visitor.printer.CommentOffset;
import spoon.reflect.visitor.printer.ElementPrinterHelper;
import spoon.reflect.visitor.printer.PrinterHelper;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
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

	protected static class TypeContext {
		CtTypeReference<?> type;
		Set<String> memberNames;

		TypeContext(CtTypeReference<?> p_type) {
			type = p_type;
		}

		public boolean isNameConflict(String name) {
			if (memberNames == null) {
				Collection<CtFieldReference<?>> allFields = type.getAllFields();
				memberNames = new HashSet<>(allFields.size());
				for (CtFieldReference<?> field : allFields) {
					memberNames.add(field.getSimpleName());
				}
			}
			return memberNames.contains(name);
		}

		public String getSimpleName() {
			return type.getSimpleName();
		}

		public CtPackageReference getPackage() {
			return type.getPackage();
		}
	}

	public class PrintingContext {
		boolean noTypeDecl = false;

		Deque<TypeContext> currentThis = new ArrayDeque<>();

		public CtTypeReference<?> getCurrentTypeReference() {
			if (context.currentTopLevel != null) {
				if (currentThis != null && currentThis.size() > 0) {
					return currentThis.peekFirst().type;
				}
				return context.currentTopLevel.getReference();
			}
			return null;
		}

		public void pushCurrentThis(CtTypeReference<?> type) {
			currentThis.push(new TypeContext(type));
		}
		public void popCurrentThis() {
			currentThis.pop();
		}


		Deque<CtElement> elementStack = new ArrayDeque<>();

		Deque<CtExpression<?>> parenthesedExpression = new ArrayDeque<>();

		CtType<?> currentTopLevel;

		boolean ignoreGenerics = false;

		boolean skipArray = false;

		boolean ignoreStaticAccess = false;

		boolean ignoreEnclosingClass = false;

		public void enterIgnoreGenerics() {
			ignoreGenerics = true;
		}

		public void exitIgnoreGenerics() {
			ignoreGenerics = false;
		}

		@Override
		public String toString() {
			return "context.ignoreGenerics: " + context.ignoreGenerics + "\n";
		}
	}

	/**
	 * The printing context.
	 */
	public PrintingContext context = new PrintingContext();

	/**
	 * Handle imports of classes.
	 */
	private ImportScanner importsContext = new ImportScannerImpl();

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
	}

	/**
	 * Enters an expression.
	 */
	protected void enterCtExpression(CtExpression<?> e) {
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
	}

	/**
	 * Make the imports for a given type.
	 */
	public Collection<CtTypeReference<?>> computeImports(CtType<?> type) {
		if (env.isAutoImports()) {
			context.currentTopLevel = type;
			return importsContext.computeImports(context.currentTopLevel);
		}
		return Collections.emptyList();
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
					printer.adjustPosition(e, sourceCompilationUnit);
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
		if (!context.skipArray) {
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
		if (env.isPreserveLineNumbers()) {
			if (!block.isImplicit()) {
				printer.write("}");
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
		context.pushCurrentThis(ctClass.getReference());

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
		printer.decTab().writeTabs().write("}");
		context.popCurrentThis();
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		visitCtTypeParameterReference(typeParameter.getReference());
	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		enterCtExpression(conditional);
		CtExpression<Boolean> condition = conditional.getCondition();
		if (!(condition instanceof CtStatement)) {
			elementPrinterHelper.writeComment(condition, CommentOffset.BEFORE);
		}
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
		if (!(condition instanceof CtStatement)) {
			elementPrinterHelper.writeComment(condition, CommentOffset.AFTER);
		}
		printer.write(" ? ");
		CtExpression<T> thenExpression = conditional.getThenExpression();
		if (!(thenExpression instanceof CtStatement)) {
			elementPrinterHelper.writeComment(thenExpression, CommentOffset.BEFORE);
		}
		scan(thenExpression);
		if (!(thenExpression instanceof CtStatement)) {
			elementPrinterHelper.writeComment(thenExpression, CommentOffset.AFTER);
		}
		printer.write(" : ");

		CtExpression<T> elseExpression = conditional.getElseExpression();
		boolean isAssign = false;
		if ((isAssign = elseExpression instanceof CtAssignment)) {
			printer.write("(");
		}
		if (!(elseExpression instanceof CtStatement)) {
			elementPrinterHelper.writeComment(elseExpression, CommentOffset.BEFORE);
		}
		scan(elseExpression);
		if (!(elseExpression instanceof CtStatement)) {
			elementPrinterHelper.writeComment(elseExpression, CommentOffset.AFTER);
		}
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
		if (constructor.getDeclaringType().isLocalType()) {
			printer.write(constructor.getDeclaringType().getSimpleName().replaceAll("^[0-9]*", ""));
		} else {
			printer.write(constructor.getDeclaringType().getSimpleName());
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
		context.pushCurrentThis(ctEnum.getReference());
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
		if (f.getVariable().isStatic() && f.getTarget() instanceof CtTypeAccess) {
			context.ignoreGenerics = true;
		}
		if (f.getTarget() != null) {
			if (!isInitializeStaticFinalField(f.getTarget())) {
				scan(f.getTarget());
				if (!f.getTarget().isImplicit()) {
					printer.write(".");
				}
			}
			context.ignoreStaticAccess = true;
		}
		scan(f.getVariable());

		context.ignoreGenerics = false;
		context.ignoreStaticAccess = false;
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
		enterCtExpression(thisAccess);
		if (thisAccess.getTarget() != null && thisAccess.getTarget() instanceof CtTypeAccess
				&& !tryToInitializeFinalFieldInConstructor(thisAccess)
				&& !thisAccess.isImplicit()
				&& !thisAccess.getTarget().isImplicit()) {
			final CtTypeReference accessedType = ((CtTypeAccess) thisAccess.getTarget()).getAccessedType();
			if (accessedType.isLocalType()) {
				printer.write(accessedType.getSimpleName().replaceAll("^[0-9]*", "") + ".");
			} else if (!accessedType.isAnonymous()) {
				visitCtTypeReferenceWithoutGenerics(accessedType);
				printer.write(".");
			}
		}
		if (!thisAccess.isImplicit()) {
			printer.write("this");
		}
		exitCtExpression(thisAccess);
	}

	/**
	 * Check if the this access expression is a target of a private final field in a constructor.
	 */
	private <T> boolean tryToInitializeFinalFieldInConstructor(CtThisAccess<T> thisAccess) {
		try {
			final CtElement parent = thisAccess.getParent();
			if (!(parent instanceof CtFieldWrite) || !thisAccess.equals(((CtFieldWrite) parent).getTarget()) || thisAccess.getParent(CtConstructor.class) == null) {
				return false;
			}
			final CtFieldReference variable = ((CtFieldWrite) parent).getVariable();
			if (variable == null) {
				return false;
			}
			final CtField declaration = variable.getDeclaration();
			if (declaration == null) {
				return true;
			}
			return declaration.getModifiers().contains(ModifierKind.FINAL);
		} catch (ParentNotInitializedException e) {
			return false;
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
	public void visitCtComment(CtComment comment) {
		if (!env.isCommentsEnabled() && context.elementStack.size() > 1) {
			return;
		}
		switch (comment.getCommentType()) {
		case FILE:
			printer.write("/**").writeln();
			break;
		case JAVADOC:
			printer.write("/**").writeln().writeTabs();
			break;
		case INLINE:
			printer.write("// ");
			break;
		case BLOCK:
			printer.write("/* ");
			break;
		}
		String content = comment.getContent();
		switch (comment.getCommentType()) {
		case INLINE:
			printer.write(content);
			break;
		default:
			String[] lines = content.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String com = lines[i];
				if (comment.getCommentType() == CtComment.CommentType.BLOCK) {
					printer.write(com);
					if (lines.length > 1) {
						printer.writeln().writeTabs();
					}
				} else {
					if (com.length() > 0) {
						printer.write(" * " + com).writeln().writeTabs();
					} else {
						printer.write(" *" /* no trailing space */ + com).writeln().writeTabs();
					}
				}

			}
			break;
		}

		switch (comment.getCommentType()) {
		case BLOCK:
			printer.write(" */");
			break;
		case FILE:
			printer.write(" */");
			break;
		case JAVADOC:
			printer.write(" */");
			break;
		}
	}

	@Override
	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
		enterCtExpression(annotationFieldAccess);
		if (annotationFieldAccess.getTarget() != null) {
			scan(annotationFieldAccess.getTarget());
			printer.write(".");
			context.ignoreStaticAccess = true;
		}
		context.ignoreGenerics = true;
		scan(annotationFieldAccess.getVariable());
		printer.write("()");
		context.ignoreGenerics = false;
		context.ignoreStaticAccess = false;
		exitCtExpression(annotationFieldAccess);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		boolean isStatic = reference.getSimpleName().equals("class") || !reference.getSimpleName().equals("super") && reference.isStatic();

		boolean printType = true;

		if (reference.isFinal() && reference.isStatic()) {
			CtTypeReference<?> declTypeRef = reference.getDeclaringType();
			CtTypeReference<?> ref2 = context.getCurrentTypeReference();
			if (ref2 != null) {
				// print type if not anonymous class ref and not within the
				// current scope
				printType = !"".equals(declTypeRef.getSimpleName()) && !(declTypeRef.equals(ref2));
			} else {
				printType = !"".equals(declTypeRef.getSimpleName());
			}
		}

		if (isStatic && printType && !context.ignoreStaticAccess) {
			context.ignoreGenerics = true;
			scan(reference.getDeclaringType());
			context.ignoreGenerics = false;
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
			context.noTypeDecl = true;
			for (int i = 1; i < st.size(); i++) {
				printer.write(", ");
				scan(st.get(i));
			}
			context.noTypeDecl = false;
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
		printer.write(" {").incTab();
		// Content
		elementPrinterHelper.writeElementList(intrface.getTypeMembers());
		printer.decTab().writeTabs().write("}");
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
				if (invocation.getTarget() != null) {
					scan(invocation.getTarget());
					printer.write(".");
				}
				printer.write("super");
			}
		} else {
			// It's a method invocation
			if (invocation.getTarget() != null) {
				if (invocation.getTarget() instanceof CtTypeAccess) {
					context.ignoreGenerics = true;
				}
				scan(invocation.getTarget());
				context.ignoreGenerics = false;
				if (!invocation.getTarget().isImplicit()) {
					printer.write(".");
				}
			}
			elementPrinterHelper.writeActualTypeArguments(invocation);
			if (env.isPreserveLineNumbers()) {
				printer.adjustPosition(invocation, sourceCompilationUnit);
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
		if (!context.noTypeDecl) {
			enterCtStatement(localVariable);
		}
		if (env.isPreserveLineNumbers()) {
			printer.adjustPosition(localVariable, sourceCompilationUnit);
		}
		if (!context.noTypeDecl) {
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
			printer.adjustPosition(catchVariable, sourceCompilationUnit);
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
		final boolean old = context.ignoreGenerics;
		context.ignoreGenerics = false;
		scan(m.getType());
		context.ignoreGenerics = old;
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

			context.skipArray = true;
			scan(ref);
			context.skipArray = false;
			for (int i = 0; ref instanceof CtArrayTypeReference; i++) {
				printer.write("[");
				if (newArray.getDimensionExpressions().size() > i) {
					CtExpression<Integer> e = newArray.getDimensionExpressions().get(i);
					if (!(e instanceof CtStatement)) {
						elementPrinterHelper.writeComment(e, CommentOffset.BEFORE);
					}
					scan(e);
					if (!(e instanceof CtStatement)) {
						elementPrinterHelper.writeComment(e, CommentOffset.AFTER);
					}
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
				if (!(e instanceof CtStatement)) {
					elementPrinterHelper.writeComment(e, CommentOffset.BEFORE);
				}
				scan(e);
				printer.write(" , ");
				if (i + 1 == l_elements.size()) {
					/*
					 * we have to remove last char before we writeComment.
					 * We cannot simply skip adding of " , ",
					 * because it influences formatting and EOL too
					 */
					printer.removeLastChar();
				}
				if (!(e instanceof CtStatement)) {
					elementPrinterHelper.writeComment(e, CommentOffset.AFTER);
				}
			}
			printer.write(" }");
		}
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
		if (ctConstructorCall.getTarget() != null) {
			scan(ctConstructorCall.getTarget());
			printer.write(".");
			context.ignoreEnclosingClass = true;
		}

		if (hasDeclaringTypeWithGenerics(ctConstructorCall.getType())) {
			context.ignoreEnclosingClass = true;
		}

		printer.write("new ");

		if (ctConstructorCall.getActualTypeArguments().size() > 0) {
			elementPrinterHelper.writeActualTypeArguments(ctConstructorCall);
		}

		scan(ctConstructorCall.getType());
		context.ignoreEnclosingClass = false;

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
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		visitCtTypeParameterReference(wildcardReference);
	}

	private boolean printQualified(CtTypeReference<?> ref) {
		if (importsContext.isImported(ref)) {
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
			if (!context.ignoreEnclosingClass && !ref.isLocalType() && !ref.getDeclaringType().isAnonymous()) {
				boolean ign = context.ignoreGenerics;
				if (!withGenerics) {
					context.ignoreGenerics = true;
				}
				scan(ref.getDeclaringType());
				if (!withGenerics) {
					context.ignoreGenerics = ign;
				}
				printer.write(".");
			}
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
		if (withGenerics && !context.ignoreGenerics) {
			final boolean old = context.ignoreEnclosingClass;
			context.ignoreEnclosingClass = false;
			elementPrinterHelper.writeActualTypeArguments(ref);
			context.ignoreEnclosingClass = old;
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
		printer.write(expression.getValue());
	}

	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
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
		printer = new PrinterHelper(env);

		elementPrinterHelper.writeComment(pack);

		for (CtAnnotation<?> a : pack.getAnnotations()) {
			a.accept(this);
		}

		if (!pack.isUnnamedPackage()) {
			printer.write("package " + pack.getQualifiedName() + ";");
		}
		String ret = printer.toString();
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
	}

	@Override
	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		this.sourceCompilationUnit = sourceCompilationUnit;
		Set<CtTypeReference<?>> imports = new HashSet<>();
		for (CtType<?> t : types) {
			imports.addAll(computeImports(t));
		}
		elementPrinterHelper.writeHeader(types, imports);
		for (CtType<?> t : types) {
			scan(t);
			printer.writeln().writeln().writeTabs();
		}
	}

	@Override
	public Map<Integer, Integer> getLineNumberMapping() {
		return printer.getLineNumberMapping();
	}
}
