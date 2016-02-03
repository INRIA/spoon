/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import org.apache.log4j.Level;
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
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
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
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.internal.CtImplicitArrayTypeReference;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.support.reflect.cu.CtLineElementComparator;
import spoon.support.util.SortedList;
import spoon.support.visitor.SignaturePrinter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

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

	Map<Integer, Integer> lineNumberMapping = new HashMap<Integer, Integer>();

	public class PrintingContext {
		boolean noTypeDecl = false;

		Stack<CtTypeReference<?>> currentThis = new Stack<CtTypeReference<?>>();

		Stack<CtElement> elementStack = new Stack<CtElement>();

		CtType<?> currentTopLevel;

		boolean ignoreGenerics = false;

		public boolean getIgnoreGenerics() {
			return ignoreGenerics;
		}

		/**
		 * Layout variables
		 */
		int jumped = 0;

		int nbTabs = 0;

		Stack<CtExpression<?>> parenthesedExpression = new Stack<CtExpression<?>>();

		boolean isInvocation = false;

		boolean skipArray = false;

		boolean ignoreStaticAccess = false;

		boolean ignoreEnclosingClass = false;

		boolean noNewLines = false;

		void enterTarget() {
		}

		void exitTarget() {
			if (jumped > 0) {
				jumped--;
			}
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
	 * The string buffer in which the code is generated.
	 */
	private StringBuffer sbf = new StringBuffer();

	/**
	 * Environment which Spoon is executed.
	 */
	private Environment env;

	/**
	 * Creates a new code generator visitor.
	 */
	public DefaultJavaPrettyPrinter(Environment env) {
		this.env = env;
	}

	/**
	 * Decrements the current number of tabs.
	 */
	public DefaultJavaPrettyPrinter decTab() {
		context.nbTabs--;
		return this;
	}

	private void undefLine(int line) {
		if (lineNumberMapping.get(line) == null) {
			// overload mapping (undefined line)
			lineNumberMapping.put(line, 0);
		}
	}

	private void mapLine(int line, CtElement e) {
		if ((e.getPosition() != null) && (e.getPosition().getCompilationUnit() == sourceCompilationUnit)) {
			// only map elements coming from the source CU
			lineNumberMapping.put(line, e.getPosition().getLine());
		} else {
			undefLine(line);
		}
	}

	/**
	 * Enters an expression.
	 */
	protected void enterCtExpression(CtExpression<?> e) {
		mapLine(line, e);
		if (shouldSetBracket(e)) {
			context.parenthesedExpression.push(e);
			write("(");
		}
		if (!e.getTypeCasts().isEmpty()) {
			for (CtTypeReference<?> r : e.getTypeCasts()) {
				write("(");
				DefaultJavaPrettyPrinter.this.scan(r);
				write(")");
				write("(");
				context.parenthesedExpression.push(e);
			}
		}
	}

	/**
	 * Enters a statement.
	 */
	protected void enterCtStatement(CtStatement s) {
		mapLine(line, s);
		writeAnnotations(s);
		if (s.getLabel() != null) {
			write(s.getLabel()).write(" : ");
		}
	}

	/**
	 * Exits an expression.
	 */
	protected void exitCtExpression(CtExpression<?> e) {
		while ((context.parenthesedExpression.size() > 0) && e == context.parenthesedExpression.peek()) {
			context.parenthesedExpression.pop();
			write(")");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.visitor.JavaPrettyPrinter#getPackageDeclaration()
	 */
	public String getPackageDeclaration() {
		StringBuffer bck = sbf;
		sbf = new StringBuffer();

		for (CtAnnotation<?> a : context.currentTopLevel.getPackage().getAnnotations()) {
			a.accept(this);
		}

		if (!context.currentTopLevel.getPackage().getQualifiedName().equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
			write("package " + context.currentTopLevel.getPackage().getQualifiedName() + ";");
		}
		String ret = sbf.toString();
		sbf = bck;

		return ret;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.visitor.JavaPrettyPrinter#getResult()
	 */
	public String getResult() {
		return sbf.toString();
	}

	/**
	 * Increments the current number of tabs.
	 */
	public DefaultJavaPrettyPrinter incTab() {
		context.nbTabs++;
		return this;
	}

	/**
	 * Sets the current number of tabs.
	 */
	public DefaultJavaPrettyPrinter setTabCount(int tabCount) {
		context.nbTabs = tabCount;
		return this;
	}

	private boolean isWhite(char c) {
		return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
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
	 * Write a post unary operator.
	 */
	protected void postWriteUnaryOperator(UnaryOperatorKind o) {
		switch (o) {
		case POSTINC:
			write("++");
			break;
		case POSTDEC:
			write("--");
			break;
		default:
			// do nothing (this does not feel right to ignore invalid ops)
		}
	}

	/**
	 * Write a pre unary operator.
	 */
	void preWriteUnaryOperator(UnaryOperatorKind o) {
		switch (o) {
		case POS:
			write("+");
			break;
		case NEG:
			write("-");
			break;
		case NOT:
			write("!");
			break;
		case COMPL:
			write("~");
			break;
		case PREINC:
			write("++");
			break;
		case PREDEC:
			write("--");
			break;
		default:
			// do nothing (this does not feel right to ignore invalid ops)
		}
	}

	/**
	 * Removes the last non-white character.
	 */
	protected DefaultJavaPrettyPrinter removeLastChar() {
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			if (sbf.charAt(sbf.length() - 1) == '\n') {
				line--;
			}
			sbf.deleteCharAt(sbf.length() - 1);
		}
		sbf.deleteCharAt(sbf.length() - 1);
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			if (sbf.charAt(sbf.length() - 1) == '\n') {
				line--;
			}
			sbf.deleteCharAt(sbf.length() - 1);
		}
		return this;
	}

	/**
	 * The generic scan method for an element.
	 */
	public DefaultJavaPrettyPrinter scan(CtElement e) {
		if (e != null) {
			context.elementStack.push(e);
			if (env.isPreserveLineNumbers()) {
				context.noNewLines = e.getPosition() == null || e.getPosition().getCompilationUnit() != sourceCompilationUnit;
				if (!(e instanceof CtNamedElement)) {
					adjustPosition(e);
				}
			}
			e.accept(this);
			context.elementStack.pop();
		}
		return this;
	}

	private void insertLine() {
		// System.out.println("insert");
		int i = sbf.length() - 1;
		while (i >= 0 && (sbf.charAt(i) == ' ' || sbf.charAt(i) == '\t')) {
			i--;
		}
		sbf.insert(i + 1, LINE_SEPARATOR);
		line++;
	}

	private boolean removeLine() {
		// System.out.println("remove");
		String ls = LINE_SEPARATOR;
		int i = sbf.length() - ls.length();
		boolean hasWhite = false;
		while (i > 0 && !ls.equals(sbf.substring(i, i + ls.length()))) {
			if (!isWhite(sbf.charAt(i))) {
				return false;
			}
			hasWhite = true;
			i--;
		}
		if (i <= 0) {
			return false;
		}
		hasWhite = hasWhite || isWhite(sbf.charAt(i - 1));
		sbf.replace(i, i + ls.length(), hasWhite ? "" : " ");
		line--;
		return true;
	}

	protected void printCharArray(char[] c) {
		for (int i = 0; i < c.length; i++) {
			switch (c[i]) {
			case '\b':
				System.out.print("\\b"); //$NON-NLS-1$
				break;
			case '\t':
				System.out.print("\\t"); //$NON-NLS-1$
				break;
			case '\n':
				System.out.print("\\n"); //$NON-NLS-1$
				break;
			case '\f':
				System.out.print("\\f"); //$NON-NLS-1$
				break;
			case '\r':
				System.out.print("\\r"); //$NON-NLS-1$
				break;
			case '\"':
				System.out.print("\\\""); //$NON-NLS-1$
				break;
			case '\'':
				System.out.print("\\'"); //$NON-NLS-1$
				break;
			case '\\': // take care not to display the escape as a potential
				// real char
				System.out.println("\\\\"); //$NON-NLS-1$
				break;
			default:
				System.out.print(c[i]);
			}
			if (i < c.length - 1) {
				System.out.print(",");
			}
		}
	}

	private void adjustPosition(CtElement e) {
		// System.out.println(" -- " + e.getSignature() + " - " +
		// e.getPosition()
		// + " - " + line);
		// System.out.print("===================");
		// printCharArray(sbf.toString().toCharArray());
		// System.out.println("===================");
		if (e.getPosition() != null && e.getPosition().getCompilationUnit() != null && e.getPosition().getCompilationUnit() == sourceCompilationUnit) {
			while (line < e.getPosition().getLine()) {
				insertLine();
			}
			while (line > e.getPosition().getLine()) {
				if (!removeLine()) {
					if (line > e.getPosition().getEndLine()) {
						env.report(null, Level.WARN, e,
								"cannot adjust position of " + e.getClass().getSimpleName() + " '" + e.getSignature() + "' " + " to match lines: " + line + " > [" + e.getPosition().getLine() + ", "
										+ e.getPosition().getEndLine() + "]");
					}
					break;
				}
			}
		}
		// System.out.print("===================");
		// printCharArray(sbf.toString().toCharArray());
		// System.out.println("===================");

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
				return (e instanceof CtTargetedExpression) || (e instanceof CtAssignment) || (e instanceof CtConditional) || (e instanceof CtUnaryOperator);
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
		return sbf.toString();
	}

	public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		writeAnnotations(annotation);
		write("@");
		scan(annotation.getAnnotationType());
		if (annotation.getElementValues().size() > 0) {
			write("(");
			for (Entry<String, Object> e : annotation.getElementValues().entrySet()) {
				write(e.getKey() + " = ");
				writeAnnotationElement(annotation.getFactory(), e.getValue());
				write(", ");
			}
			removeLastChar();
			write(")");
		}
		writeln().writeTabs();
	}

	public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
		visitCtType(annotationType);
		write("@interface " + annotationType.getSimpleName() + " {").incTab();

		SortedList<CtElement> lst = new SortedList<CtElement>(new CtLineElementComparator());

		lst.addAll(annotationType.getNestedTypes());
		lst.addAll(annotationType.getFields());

		for (CtElement el : lst) {
			writeln().writeTabs().scan(el);
			if (!env.isPreserveLineNumbers()) {
				writeln();
			}
		}
		decTab().writeTabs().write("}");
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable impl) {
		writeAnnotations(impl);
		writeModifiers(impl);
		scan(impl.getBody());
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		printCtArrayAccess(arrayAccess);
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		printCtArrayAccess(arrayRead);
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		printCtArrayAccess(arrayWrite);
	}

	public <T, E extends CtExpression<?>> void printCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		enterCtExpression(arrayAccess);
		scan(arrayAccess.getTarget());
		write("[").scan(arrayAccess.getIndexExpression()).write("]");
		exitCtExpression(arrayAccess);
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		scan(reference.getComponentType());
		if (!context.skipArray) {
			write("[]");
		}
	}

	@Override
	public <T> void visitCtImplicitArrayTypeReference(CtImplicitArrayTypeReference<T> reference) {
		// The array type is implicit, we don't print it!
	}

	public <T> void visitCtAssert(CtAssert<T> asserted) {
		enterCtStatement(asserted);
		write("assert ");
		scan(asserted.getAssertExpression());
		if (asserted.getExpression() != null) {
			write(" : ");
			scan(asserted.getExpression());
		}

	}

	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
		enterCtStatement(assignement);
		enterCtExpression(assignement);
		scan(assignement.getAssigned());
		write(" = ");
		scan(assignement.getAssignment());
		exitCtExpression(assignement);
	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		enterCtExpression(operator);
		boolean paren = false;
		try {
			paren = (operator.getParent() instanceof CtBinaryOperator) || (operator.getParent() instanceof CtUnaryOperator);
		} catch (ParentNotInitializedException ex) {
			// nothing if we have no parent
		}
		if (paren) {
			write("(");
		}
		scan(operator.getLeftHandOperand());
		write(" ").writeOperator(operator.getKind()).write(" ");
		scan(operator.getRightHandOperand());
		if (paren) {
			write(")");
		}
		exitCtExpression(operator);
	}

	public <R> void visitCtBlock(CtBlock<R> block) {
		enterCtStatement(block);
		write("{").incTab();
		for (CtStatement e : block.getStatements()) {
			if (!e.isImplicit()) {
				writeln().writeTabs();
				writeStatement(e);
			}
		}
		if (env.isPreserveLineNumbers()) {
			decTab().write("}");
		} else {
			decTab().writeln().writeTabs().write("}");
		}
	}

	public void visitCtBreak(CtBreak breakStatement) {
		enterCtStatement(breakStatement);
		write("break");
		if (breakStatement.getTargetLabel() != null) {
			write(" " + breakStatement.getTargetLabel());
		}
	}

	@SuppressWarnings("rawtypes")
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		enterCtStatement(caseStatement);
		if (caseStatement.getCaseExpression() != null) {
			write("case ");
			// writing enum case expression
			if (caseStatement.getCaseExpression() instanceof CtFieldAccess) {
				final CtFieldReference variable = ((CtFieldAccess) caseStatement.getCaseExpression()).getVariable();
				// In noclasspath mode, we don't have always the type of the declaring type.
				if (variable.getType() != null
						&& variable.getDeclaringType() != null
						&& variable.getType().getQualifiedName().equals(variable.getDeclaringType().getQualifiedName())) {
					write(variable.getSimpleName());
				} else {
					scan(caseStatement.getCaseExpression());
				}
			} else {
				scan(caseStatement.getCaseExpression());
			}
		} else {
			write("default");
		}
		write(" :").incTab();

		for (CtStatement s : caseStatement.getStatements()) {
			writeln().writeTabs().writeStatement(s);
		}
		decTab();
	}

	public void visitCtCatch(CtCatch catchBlock) {
		write(" catch (");
		CtCatchVariable<? extends Throwable> parameter = catchBlock.getParameter();
		if (parameter.getMultiTypes().size() > 0) {
			for (int i = 0; i < parameter.getMultiTypes().size(); i++) {
				CtTypeReference<?> type = parameter.getMultiTypes().get(i);
				scan(type);
				if (i < parameter.getMultiTypes().size() - 1) {
					write(" | ");
				}
			}
			write(" " + parameter.getSimpleName());
		} else {
			scan(parameter);
		}
		write(") ");
		scan(catchBlock.getBody());
	}

	public DefaultJavaPrettyPrinter writeExtendsClause(CtClass<?> c) {
		if (c.getSuperclass() != null) {
			write(" extends ");
			scan(c.getSuperclass());
		}
		return this;
	}

	public DefaultJavaPrettyPrinter writeImplementsClause(CtType<?> t) {
		if (t.getSuperInterfaces().size() > 0) {
			write(" implements ");
			for (CtTypeReference<?> ref : t.getSuperInterfaces()) {
				scan(ref);
				write(" , ");
			}
			removeLastChar();
		}
		return this;
	}

	public <T> void visitCtClass(CtClass<T> ctClass) {
		SortedList<CtElement> lst = new SortedList<CtElement>(new CtLineElementComparator());
		if (ctClass.getSimpleName() != null && !ctClass.isAnonymous()) {
			visitCtType(ctClass);
			if (ctClass.isLocalType()) {
				write("class " + ctClass.getSimpleName().replaceAll("^[0-9]*", ""));
			} else {
				write("class " + ctClass.getSimpleName());
			}

			writeFormalTypeParameters(ctClass.getFormalTypeParameters());

			writeExtendsClause(ctClass);
			writeImplementsClause(ctClass);
			for (CtConstructor<T> c : ctClass.getConstructors()) {
				if (!c.isImplicit()) {
					lst.add(c);
				}
			}
		}
		lst.addAll(ctClass.getAnonymousExecutables());
		lst.addAll(ctClass.getNestedTypes());
		lst.addAll(ctClass.getFields());
		lst.addAll(ctClass.getMethods());

		if ((ctClass.getSimpleName() == null || ctClass.getSimpleName().isEmpty()) && ctClass.getParent() != null && ctClass.getParent() instanceof CtNewClass) {
			context.currentThis.push(((CtNewClass<?>) ctClass.getParent()).getType());
		} else {
			context.currentThis.push(ctClass.getReference());
		}
		write(" {").incTab();
		for (CtElement el : lst) {
			writeln().writeTabs().scan(el);
			if (!env.isPreserveLineNumbers()) {
				writeln();
			}
		}
		decTab().writeTabs().write("}");
		context.currentThis.pop();
	}

	public <T> void visitCtConditional(CtConditional<T> conditional) {
		enterCtExpression(conditional);
		scan(conditional.getCondition());
		write(" ? ");
		scan(conditional.getThenExpression());
		write(" : ");
		boolean isAssign = false;
		if ((isAssign = conditional.getElseExpression() instanceof CtAssignment)) {
			write("(");
		}
		scan(conditional.getElseExpression());
		if (isAssign) {
			write(")");
		}
		exitCtExpression(conditional);
	}

	public <T> void visitCtConstructor(CtConstructor<T> c) {
		visitCtNamedElement(c);
		writeModifiers(c);
		writeFormalTypeParameters(c.getFormalTypeParameters());
		write(c.getDeclaringType().getSimpleName());
		write("(");
		if (c.getParameters().size() > 0) {
			for (CtParameter<?> p : c.getParameters()) {
				visitCtParameter(p);
				write(" ,");
			}
			removeLastChar();
		}
		write(") ");
		if ((c.getThrownTypes() != null) && (c.getThrownTypes().size() > 0)) {
			write("throws ");
			for (CtTypeReference<?> ref : c.getThrownTypes()) {
				scan(ref);
				write(" , ");
			}
			removeLastChar();
			write(" ");
		}
		scan(c.getBody());
	}

	public void visitCtContinue(CtContinue continueStatement) {
		enterCtStatement(continueStatement);
		write("continue");
		if (continueStatement.getTargetLabel() != null) {
			write(" " + continueStatement.getTargetLabel());
		}
	}

	public void visitCtDo(CtDo doLoop) {
		enterCtStatement(doLoop);
		write("do ");
		writeStatement(doLoop.getBody());
		write(" while (");
		scan(doLoop.getLoopingExpression());
		write(" )");
		// write(";");
	}

	private void writeEnumField(CtField<?> f) {
		visitCtNamedElement(f);
		write(f.getSimpleName());
		if (f.getDefaultExpression() != null) {
			CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) f.getDefaultExpression();
			if (constructorCall.getArguments().size() > 0) {
				write("(");
				boolean first = true;
				for (CtExpression<?> ctexpr : constructorCall.getArguments()) {
					if (first) {
						first = false;
					} else {
						write(",");
					}
					scan(ctexpr);
				}
				write(")");
			}
			if (constructorCall instanceof CtNewClass) {
				scan(((CtNewClass<?>) constructorCall).getAnonymousClass());
			}
		}
	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		visitCtType(ctEnum);
		write("enum " + ctEnum.getSimpleName());
		if (ctEnum.getSuperInterfaces().size() > 0) {
			write(" implements ");
			for (CtTypeReference<?> ref : ctEnum.getSuperInterfaces()) {
				scan(ref);
				write(" , ");
			}
			removeLastChar();
		}
		context.currentThis.push(ctEnum.getReference());
		write(" {").incTab().writeln();
		List<CtField<?>> l1 = new ArrayList<CtField<?>>();
		List<CtField<?>> l2 = new ArrayList<CtField<?>>();
		if (ctEnum.getFields().size() == 0) {
			writeTabs().write(";").writeln();
		} else {
			for (CtField<?> ec : ctEnum.getFields()) {
				if (ec.getType() == null) {
					l1.add(ec);
				} else {
					l2.add(ec);
				}
			}
			if (l1.size() > 0) {
				for (CtField<?> ec : l1) {
					writeEnumField(ec);
					write(", ");
				}
				removeLastChar();
				write(";");
			}
			for (CtField<?> ec : l2) {
				writeln().writeTabs().scan(ec);
			}
		}
		for (CtConstructor<?> c : ctEnum.getConstructors()) {
			if (!c.isImplicit()) {
				writeln().writeTabs().scan(c);
			}
		}

		SortedList<CtElement> lst = new SortedList<CtElement>(new CtLineElementComparator());

		lst.addAll(ctEnum.getAnonymousExecutables());
		lst.addAll(ctEnum.getNestedTypes());
		lst.addAll(ctEnum.getMethods());

		for (CtElement el : lst) {
			writeln().writeTabs().scan(el);
			if (!env.isPreserveLineNumbers()) {
				writeln();
			}
		}
		decTab().writeTabs().write("}");
		context.currentThis.pop();
	}

	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(reference);
		write(pr.getSignature());
	}

	public <T> void visitCtField(CtField<T> f) {
		visitCtNamedElement(f);
		writeModifiers(f);
		scan(f.getType());
		write(" ");
		write(f.getSimpleName());

		if ((!f.isParentInitialized()) || !CtAnnotationType.class.isAssignableFrom(f.getParent().getClass()) || f.getModifiers().contains(ModifierKind.STATIC)) {
			if (f.getDefaultExpression() != null) {
				write(" = ");
				scan(f.getDefaultExpression());
			}
		} else {
			write("()");
			if (f.getDefaultExpression() != null) {
				write(" default ");
				scan(f.getDefaultExpression());
			}
		}
		write(";");
	}

	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> f) {
		printCtFieldAccess(f);
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
					write(".");
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
		final CtElement parent = targetExp.getParent();
		if (parent instanceof CtFieldWrite && targetExp.equals(((CtFieldWrite) parent).getTarget())
				&& targetExp.getParent(CtAnonymousExecutable.class) != null
				&& ((CtFieldWrite) parent).getVariable() != null
				&& ((CtFieldWrite) parent).getVariable().getModifiers().contains(ModifierKind.STATIC)) {
			return true;
		}
		return false;
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		enterCtExpression(thisAccess);
		if (thisAccess.getTarget() != null && thisAccess.getTarget() instanceof CtTypeAccess
				&& !tryToInitializeFinalFieldInConstructor(thisAccess)
				&& !thisAccess.isImplicit()) {
			final CtTypeReference accessedType = ((CtTypeAccess) thisAccess.getTarget()).getAccessedType();
			if (accessedType.isLocalType()) {
				write(accessedType.getSimpleName().replaceAll("^[0-9]*", "") + ".");
			} else if (!accessedType.isAnonymous()) {
				visitCtTypeReferenceWithoutGenerics(accessedType);
				write(".");
			}
		}
		if (!thisAccess.isImplicit()) {
			write("this");
		}
		exitCtExpression(thisAccess);
	}

	/**
	 * Check if the this access expression is a target of a private final field in a constructor.
	 */
	private <T> boolean tryToInitializeFinalFieldInConstructor(CtThisAccess<T> thisAccess) {
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
		return variable.getDeclaration().getModifiers().contains(ModifierKind.FINAL);
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		enterCtExpression(f);
		if (f.getTarget() != null) {
			scan(f.getTarget());
			write(".");
		}
		write("super");

		exitCtExpression(f);
	}

	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
		enterCtExpression(annotationFieldAccess);
		if (annotationFieldAccess.getTarget() != null) {
			scan(annotationFieldAccess.getTarget());
			write(".");
			context.ignoreStaticAccess = true;
		}
		context.ignoreGenerics = true;
		scan(annotationFieldAccess.getVariable());
		write("()");
		context.ignoreGenerics = false;
		context.ignoreStaticAccess = false;
		exitCtExpression(annotationFieldAccess);
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		boolean isStatic = false;
		if (reference.getSimpleName().equals("class")) {
			isStatic = true;
		} else if (reference.getSimpleName().equals("super")) {
			isStatic = false;
		} else {
			isStatic = reference.isStatic();
		}

		boolean printType = true;

		if (reference.isFinal() && reference.isStatic()) {
			CtTypeReference<?> declTypeRef = reference.getDeclaringType();
			if (context.currentTopLevel != null) {
				CtTypeReference<?> ref2;
				if (context.currentThis != null && context.currentThis.size() > 0) {
					ref2 = context.currentThis.lastElement();
				} else {
					ref2 = context.currentTopLevel.getReference();
				}
				// print type if not annonymous class ref and not within the
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
			write(".");
		}
		write(reference.getSimpleName());
	}

	public void visitCtFor(CtFor forLoop) {
		enterCtStatement(forLoop);
		write("for (");
		List<CtStatement> st = forLoop.getForInit();
		if (st.size() > 0) {
			scan(st.get(0));
		}
		if (st.size() > 1) {
			context.noTypeDecl = true;
			for (int i = 1; i < st.size(); i++) {
				write(", ");
				scan(st.get(i));
			}
			context.noTypeDecl = false;
		}
		write(" ; ");
		scan(forLoop.getExpression());
		write(" ; ");
		for (CtStatement s : forLoop.getForUpdate()) {
			scan(s);
			write(" , ");
		}
		if (forLoop.getForUpdate().size() > 0) {
			removeLastChar();
		}
		write(")");
		if (forLoop.getBody() instanceof CtBlock) {
			write(" ");
			scan(forLoop.getBody());
		} else {
			incTab().writeln().writeTabs();
			writeStatement(forLoop.getBody());
			decTab();
		}
	}

	public void visitCtForEach(CtForEach foreach) {
		enterCtStatement(foreach);
		write("for (");
		scan(foreach.getVariable());
		write(" : ");
		scan(foreach.getExpression());
		write(")");

		if (foreach.getBody() instanceof CtBlock) {
			write(" ");
			scan(foreach.getBody());
		} else {
			incTab().writeln().writeTabs();
			writeStatement(foreach.getBody());
			decTab();
		}
	}

	public void visitCtIf(CtIf ifElement) {
		enterCtStatement(ifElement);
		write("if (");
		scan(ifElement.getCondition());
		write(")");
		if (ifElement.getThenStatement() instanceof CtBlock) {
			write(" ");
			scan((CtStatement) ifElement.getThenStatement());
			write(" ");
		} else {
			incTab().writeln().writeTabs();
			writeStatement(ifElement.getThenStatement());
			if (env.isPreserveLineNumbers()) {
				decTab();
			} else {
				decTab().writeln().writeTabs();
			}
		}
		if (ifElement.getElseStatement() != null) {
			write("else");
			if (ifElement.getElseStatement() instanceof CtIf) {
				write(" ");
				scan((CtStatement) ifElement.getElseStatement());
			} else if (ifElement.getElseStatement() instanceof CtBlock) {
				write(" ");
				scan((CtStatement) ifElement.getElseStatement());
			} else {
				incTab().writeln().writeTabs();
				writeStatement(ifElement.getElseStatement());
				if (env.isPreserveLineNumbers()) {
					decTab();
				} else {
					decTab().writeln().writeTabs();
				}
			}
		}
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		visitCtType(intrface);
		write("interface " + intrface.getSimpleName());
		if (intrface.getFormalTypeParameters() != null) {
			writeFormalTypeParameters(intrface.getFormalTypeParameters());
		}

		if (intrface.getSuperInterfaces().size() > 0) {
			write(" extends ");
			for (CtTypeReference<?> ref : intrface.getSuperInterfaces()) {
				scan(ref);
				write(" , ");
			}
			removeLastChar();
		}
		write(" {").incTab();
		SortedList<CtElement> lst = new SortedList<CtElement>(new CtLineElementComparator());
		lst.addAll(intrface.getNestedTypes());
		lst.addAll(intrface.getFields());
		lst.addAll(intrface.getMethods());
		// Content
		for (CtElement e : lst) {
			writeln().writeTabs().scan(e);
			if (!env.isPreserveLineNumbers()) {
				writeln();
			}
		}
		decTab().writeTabs().write("}");
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		enterCtStatement(invocation);
		enterCtExpression(invocation);
		if (invocation.getExecutable().isConstructor()) {
			// It's a constructor (super or this)
			writeActualTypeArguments(invocation.getExecutable());
			CtType<?> parentType = invocation.getParent(CtType.class);
			if (parentType != null && parentType.getQualifiedName() != null && parentType.getQualifiedName().equals(invocation.getExecutable().getDeclaringType().getQualifiedName())) {
				write("this");
			} else {
				if (invocation.getTarget() != null) {
					scan(invocation.getTarget());
					write(".");
				}
				write("super");
			}
		} else {
			// It's a method invocation
			if (invocation.getTarget() != null) {
				if (invocation.getTarget() instanceof CtTypeAccess) {
					context.ignoreGenerics = true;
				}
				context.enterTarget();
				scan(invocation.getTarget());
				context.exitTarget();
				context.ignoreGenerics = false;
				if (!invocation.getTarget().isImplicit()) {
					write(".");
				}
			}
			writeActualTypeArguments(invocation.getExecutable());
			// TODO: this does not work because the invocation does not have the
			// right line number
			if (env.isPreserveLineNumbers()) {
				adjustPosition(invocation);
			}
			write(invocation.getExecutable().getSimpleName());
		}
		write("(");
		boolean remove = false;
		for (CtExpression<?> e : invocation.getArguments()) {
			scan(e);
			write(", ");
			remove = true;
		}
		if (remove) {
			removeLastChar();
		}
		write(")");
		exitCtExpression(invocation);
	}

	public static String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
		};
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	public static String charToHex(char c) {
		// Returns hex String representation of char c
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return byteToHex(hi) + byteToHex(lo);
	}

	private void writeStringLiteral(String value, boolean mayContainsSpecialCharacter) {
		if (!mayContainsSpecialCharacter) {
			write(value);
			return;
		}
		// handle some special char.....
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
				if (c < 0x10) {
					write("\\u000" + Integer.toHexString(c));
				} else if (c < 0x100) {
					write("\\u00" + Integer.toHexString(c));
				} else if (c < 0x1000) {
					write("\\u0" + Integer.toHexString(c));
				} else {
					write("\\u" + Integer.toHexString(c));
				}
				continue;
			}
			switch (c) {
			case '\b':
				write("\\b"); //$NON-NLS-1$
				break;
			case '\t':
				write("\\t"); //$NON-NLS-1$
				break;
			case '\n':
				write("\\n"); //$NON-NLS-1$
				break;
			case '\f':
				write("\\f"); //$NON-NLS-1$
				break;
			case '\r':
				write("\\r"); //$NON-NLS-1$
				break;
			case '\"':
				write("\\\""); //$NON-NLS-1$
				break;
			case '\'':
				write("\\'"); //$NON-NLS-1$
				break;
			case '\\': // take care not to display the escape as a potential
				// real char
				write("\\\\"); //$NON-NLS-1$
				break;
			default:
				write(value.charAt(i));
			}
		}
	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		enterCtExpression(literal);
		if (literal.getValue() == null) {
			write("null");
		} else if (literal.getValue() instanceof Long) {
			write(literal.getValue() + "L");
		} else if (literal.getValue() instanceof Float) {
			write(literal.getValue() + "F");
		} else if (literal.getValue() instanceof Character) {
			write("'");

			boolean mayContainsSpecialCharacter = true;

			SourcePosition position = literal.getPosition();
			if (position != null) {
				// the size of the string in the source code, the -1 is the size of the ' or " in the source code
				int stringLength = position.getSourceEnd() - position.getSourceStart() - 1;
				// if the string in the source is not the same as the string in the literal, the string may contains special characters
				mayContainsSpecialCharacter = stringLength != 1;
			}
			writeStringLiteral(new String(new char[] { (Character) literal.getValue() }), mayContainsSpecialCharacter);

			write("'");
		} else if (literal.getValue() instanceof String) {
			write('\"');

			boolean mayContainsSpecialCharacters = true;

			SourcePosition position = literal.getPosition();
			if (position != null) {
				// the size of the string in the source code, the -1 is the size of the ' or " in the source code
				int stringLength = position.getSourceEnd() - position.getSourceStart() - 1;
				// if the string in the source is not the same as the string in the literal, the string may contains special characters
				mayContainsSpecialCharacters = ((String) literal.getValue()).length() != stringLength;
			}
			writeStringLiteral((String) literal.getValue(), mayContainsSpecialCharacters);

			write('\"');
		} else if (literal.getValue() instanceof Class) {
			write(((Class<?>) literal.getValue()).getName());
		} else if (literal.getValue() instanceof CtReference) {
			scan((CtReference) literal.getValue());
		} else {
			write(literal.getValue().toString());
		}
		exitCtExpression(literal);
	}

	public <T> DefaultJavaPrettyPrinter writeLocalVariable(CtLocalVariable<T> localVariable) {
		if (env.isPreserveLineNumbers()) {
			adjustPosition(localVariable);
		}
		if (!context.noTypeDecl) {
			writeModifiers(localVariable);
			scan(localVariable.getType());
			write(" ");
		}
		write(localVariable.getSimpleName());
		if (localVariable.getDefaultExpression() != null) {
			write(" = ");
			scan(localVariable.getDefaultExpression());
		}
		return this;
	}

	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		enterCtStatement(localVariable);
		writeLocalVariable(localVariable);
	}

	public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
		write(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		if (env.isPreserveLineNumbers()) {
			adjustPosition(catchVariable);
		}
		if (!context.noTypeDecl) {
			writeModifiers(catchVariable);
			scan(catchVariable.getType());
			write(" ");
		}
		write(catchVariable.getSimpleName());
	}

	@Override
	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
		write(reference.getSimpleName());
	}

	public DefaultJavaPrettyPrinter writeTypeReference(CtTypeReference<?> t) {
		scan(t);
		return this;
	}

	public DefaultJavaPrettyPrinter writeExecutableParameters(CtExecutable<?> e) {
		if (e.getParameters().size() > 0) {
			for (CtParameter<?> p : e.getParameters()) {
				scan(p);
				write(", ");
			}
			removeLastChar();
		}
		return this;
	}

	public DefaultJavaPrettyPrinter writeThrowsClause(CtExecutable<?> e) {
		if (e.getThrownTypes().size() > 0) {
			write(" throws ");
			for (CtTypeReference<?> ref : e.getThrownTypes()) {
				scan(ref);
				write(", ");
			}
			removeLastChar();
		}
		return this;
	}

	public <T> void visitCtMethod(CtMethod<T> m) {
		visitCtNamedElement(m);
		writeModifiers(m);
		if (m.isDefaultMethod()) {
			write("default ");
		}
		writeFormalTypeParameters(m.getFormalTypeParameters());
		final boolean old = context.ignoreGenerics;
		context.ignoreGenerics = false;
		scan(m.getType());
		context.ignoreGenerics = old;
		write(" ");
		write(m.getSimpleName());
		write("(");
		writeExecutableParameters(m);
		write(")");
		writeThrowsClause(m);
		if (m.getBody() != null) {
			write(" ");
			scan(m.getBody());
			if (m.getBody().getPosition() != null) {
				if (m.getBody().getPosition().getCompilationUnit() == sourceCompilationUnit) {
					if (m.getBody().getStatements().isEmpty() || !(m.getBody().getStatements().get(m.getBody().getStatements().size() - 1) instanceof CtReturn)) {
						lineNumberMapping.put(line, m.getBody().getPosition().getEndLine());
					}
				} else {
					undefLine(line);
				}
			} else {
				undefLine(line);
			}
		} else {
			write(";");
		}
	}

	public void reset() {
		sbf = new StringBuffer();
	}

	public DefaultJavaPrettyPrinter writeModifiers(CtModifiable m) {
		for (ModifierKind mod : m.getModifiers()) {
			write(mod.toString() + " ");
		}
		return this;
	}

	public void visitCtNamedElement(CtNamedElement e) {
		// Write comments
		if (!env.isPreserveLineNumbers() && env.isGenerateJavadoc() && (e.getDocComment() != null)) {
			write("/** ").writeln().writeTabs();
			String[] lines = e.getDocComment().split("\n");
			for (int i = 0; i < lines.length; i++) {
				String com = lines[i];
				if ("".equals(com) && (i == 0 || i == lines.length - 1)) {
					continue;
				}
				if (com.startsWith("//")) {
					write(com).writeln().writeTabs();
				} else {
					write(" * " + com).writeln().writeTabs();
				}
			}
			write(" */").writeln().writeTabs();
		}
		// Write element parameters (Annotations)
		writeAnnotations(e);
		if (env.isPreserveLineNumbers()) {
			adjustPosition(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		enterCtExpression(newArray);

		if ((newArray.getParent(CtAnnotationType.class) == null) && (newArray.getParent(CtAnnotation.class) == null)) {
			CtTypeReference<?> ref = newArray.getType();

			if (ref != null) {
				write("new ");
			}

			context.skipArray = true;
			scan(ref);
			context.skipArray = false;
			for (int i = 0; ref instanceof CtArrayTypeReference; i++) {
				write("[");
				if (newArray.getDimensionExpressions().size() > i) {
					scan(newArray.getDimensionExpressions().get(i));
				}
				write("]");
				ref = ((CtArrayTypeReference) ref).getComponentType();
			}
		}
		if (newArray.getDimensionExpressions().size() == 0) {
			write("{ ");
			for (CtExpression e : newArray.getElements()) {
				scan(e);
				write(" , ");
			}
			if (newArray.getElements().size() > 0) {
				removeLastChar();
			}
			write(" }");
		}
		exitCtExpression(newArray);
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		enterCtStatement(ctConstructorCall);
		enterCtExpression(ctConstructorCall);

		if (ctConstructorCall.getTarget() != null) {
			scan(ctConstructorCall.getTarget()).write(".");
			context.ignoreEnclosingClass = true;
		}

		// JDT doesn't support new Foo<K>.Bar(); So we check that the parent of the constructor
		// call and the declaring type of the type of the constructor call are equals or not.
		// If yes, Bar is a intern class of Foo and we don't need to print fully qualified name.
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=474593
		CtType<?> topLevelType = getTopLevelType(ctConstructorCall);
		CtTypeReference<?> constructorDeclaringType = ctConstructorCall.getType().getDeclaringType();
		if (topLevelType != null
				&& topLevelType.getReference().equals(constructorDeclaringType)
				&& constructorDeclaringType.getActualTypeArguments().size() > 0) {
			context.ignoreEnclosingClass = true;
		}

		write("new ");

		if (ctConstructorCall.getActualTypeArguments().size() > 0) {
			writeActualTypeArguments(ctConstructorCall);
		}

		scan(ctConstructorCall.getType());
		context.ignoreEnclosingClass = false;

		write("(");
		boolean remove = false;
		for (CtCodeElement e : ctConstructorCall.getArguments()) {
			scan(e);
			write(" , ");
			remove = true;
		}
		if (remove) {
			removeLastChar();
		}

		write(")");

		exitCtExpression(ctConstructorCall);
	}

	/**
	 * Get the top level type. The method returns null if the parent is not initialized.
	 * @param e the element
	 * @return the top level types or null if not initialized.
	 */
	private CtType<?> getTopLevelType(CtElement e) {
		CtType parent;
		try {
			parent = e.getParent(CtType.class);
		} catch (ParentNotInitializedException ex) {
			parent = null;
		}
		if (parent == null && this instanceof CtType) {
			return (CtType<?>) this;
		}
		return getTypeParent(parent);
	}

	private CtType<?> getTypeParent(CtType<?> parent) {
		if (parent == null) {
			return null;
		}
		CtType typeParent;
		try {
			typeParent = parent.getParent(CtType.class);
		} catch (ParentNotInitializedException ex) {
			typeParent = null;
		}
		if (typeParent == null) {
			return parent;
		}
		return getTypeParent(typeParent);
	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		enterCtStatement(newClass);
		enterCtExpression(newClass);

		if (newClass.getTarget() != null) {
			scan(newClass.getTarget()).write(".");
		}

		write("new ");
		if (newClass.getAnonymousClass().getSuperclass() != null) {
			scan(newClass.getAnonymousClass().getSuperclass());
		} else if (newClass.getAnonymousClass().getSuperInterfaces().size() > 0) {
			for (CtTypeReference<?> ref : newClass.getAnonymousClass().getSuperInterfaces()) {
				scan(ref);
			}
		}
		write("(");
		for (CtExpression<?> exp : newClass.getArguments()) {
			scan(exp);
			write(", ");
		}
		if (newClass.getArguments().size() > 0) {
			removeLastChar();
		}
		write(")");
		scan(newClass.getAnonymousClass());
		exitCtExpression(newClass);
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		enterCtExpression(lambda);

		write("(");
		if (lambda.getParameters().size() > 0) {
			for (CtParameter<?> parameter : lambda.getParameters()) {
				scan(parameter);
				write(",");
			}
			removeLastChar();
		}
		write(") -> ");

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
		write("::");
		if (expression.getExecutable().isConstructor()) {
			write("new");
		} else {
			write(expression.getExecutable().getSimpleName());
		}
		exitCtExpression(expression);
	}

	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		enterCtStatement(assignment);
		enterCtExpression(assignment);
		scan(assignment.getAssigned());
		write(" ");
		writeOperator(assignment.getKind());
		write("= ");
		scan(assignment.getAssignment());
		exitCtExpression(assignment);
	}

	public void visitCtPackage(CtPackage ctPackage) {
		if (!ctPackage.getQualifiedName().equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
			write("package " + ctPackage.getQualifiedName() + ";");
		} else {
			write("// default package (CtPackage.TOP_LEVEL_PACKAGE_NAME in Spoon= unnamed package)\n");
		}
	}

	public void visitCtPackageReference(CtPackageReference reference) {
		write(reference.getSimpleName());
	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {
		writeAnnotations(parameter);
		writeModifiers(parameter);
		if (parameter.isVarArgs()) {
			scan(((CtArrayTypeReference<T>) parameter.getType()).getComponentType());
			write("...");
		} else {
			scan(parameter.getType());
		}
		write(" ");
		write(parameter.getSimpleName());
	}

	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		write(reference.getSimpleName());
	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		enterCtStatement(returnStatement);
		write("return ");
		scan(returnStatement.getReturnedExpression());
	}

	<T> void visitCtType(CtType<T> type) {
		mapLine(line, type);
		if (type.isTopLevel()) {
			context.currentTopLevel = type;
		}
		visitCtNamedElement(type);
		writeModifiers(type);
	}

	public <R> void visitCtStatementList(CtStatementList statements) {
		for (CtStatement s : statements.getStatements()) {
			scan(s);
		}
	}

	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		enterCtStatement(switchStatement);
		write("switch (");
		scan(switchStatement.getSelector());
		write(") {").incTab();
		for (CtCase<?> c : switchStatement.getCases()) {
			writeln().writeTabs().scan(c);
		}
		if (env.isPreserveLineNumbers()) {
			decTab().write("}");
		} else {
			decTab().writeln().writeTabs().write("}");
		}
	}

	public void visitCtSynchronized(CtSynchronized synchro) {
		enterCtStatement(synchro);
		write("synchronized");
		if (synchro.getExpression() != null) {
			write("(");
			scan(synchro.getExpression());
			write(") ");
		}
		scan(synchro.getBlock());
	}

	public void visitCtThrow(CtThrow throwStatement) {
		enterCtStatement(throwStatement);
		write("throw ");
		scan(throwStatement.getThrownExpression());
	}

	public void visitCtTry(CtTry tryBlock) {
		enterCtStatement(tryBlock);
		write("try ");
		scan(tryBlock.getBody());
		for (CtCatch c : tryBlock.getCatchers()) {
			scan(c);
		}

		if (tryBlock.getFinalizer() != null) {
			write(" finally ");
			scan(tryBlock.getFinalizer());
		}
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		enterCtStatement(tryWithResource);
		write("try ");
		if (tryWithResource.getResources() != null && !tryWithResource.getResources().isEmpty()) {
			write("(");
			for (CtLocalVariable<?> r : tryWithResource.getResources()) {
				scan(r);
				write(";");
			}
			removeLastChar();
			write(") ");
		}
		scan(tryWithResource.getBody());
		for (CtCatch c : tryWithResource.getCatchers()) {
			scan(c);
		}

		if (tryWithResource.getFinalizer() != null) {
			write(" finally ");
			scan(tryWithResource.getFinalizer());
		}
	}

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		write(typeParameter.getSimpleName());
		if (!typeParameter.getBounds().isEmpty()) {
			write(" extends ");
			for (CtTypeReference<?> ref : typeParameter.getBounds()) {
				scan(ref);
				write(" & ");
			}
			removeLastChar();
		}
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		writeAnnotations(ref);
		if (importsContext.isImported(ref)) {
			write(ref.getSimpleName());
		} else {
			write(ref.getQualifiedName());
		}
		if ((!context.isInvocation || "?".equals(ref.getSimpleName()))
				&& ref.getBounds() != null
				&& !ref.getBounds().isEmpty()) {
			if (ref.isUpper()) {
				write(" extends ");
			} else {
				write(" super ");
			}
			for (CtTypeReference<?> b : ref.getBounds()) {
				scan(b);
				write(" & ");
			}
			removeLastChar();
		}
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> ref) {
		if (ref.isPrimitive()) {
			write(ref.getSimpleName());
			return;
		}

		if (importsContext.isImported(ref) && ref.getPackage() != null) {
			writeAnnotations(ref);
			write(ref.getSimpleName());
		} else {
			if (ref.getDeclaringType() != null) {
				if (!context.ignoreEnclosingClass && !ref.isLocalType()) {
					scan(ref.getDeclaringType());
					write(".");
				}
				if (ref.isLocalType()) {
					write(ref.getSimpleName().replaceAll("^[0-9]*", ""));
				} else {
					write(ref.getSimpleName());
				}
			} else {
				write(ref.getQualifiedName());
			}
		}
		if (!context.ignoreGenerics) {
			writeActualTypeArguments(ref);
		}
	}

	@Override
	public void visitCtCircularTypeReference(CtCircularTypeReference reference) {
		visitCtTypeReference(reference);
	}

	@Override
	public <T> void visitCtImplicitTypeReference(CtImplicitTypeReference<T> reference) {
		// The type is implicit, we don't print it!
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
		enterCtExpression(typeAccess);
		scan(typeAccess.getAccessedType());
		exitCtExpression(typeAccess);
	}

	public void visitCtTypeReferenceWithoutGenerics(CtTypeReference<?> ref) {
		if (ref.getDeclaringType() != null) {
			if (!context.ignoreEnclosingClass && !ref.getDeclaringType().isAnonymous()) {
				visitCtTypeReferenceWithoutGenerics(ref.getDeclaringType());
				write(".");
			}
			write(ref.getSimpleName());
		} else {
			write(ref.getQualifiedName());
		}
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		enterCtStatement(operator);
		enterCtExpression(operator);
		preWriteUnaryOperator(operator.getKind());
		context.enterTarget();
		scan(operator.getOperand());
		context.exitTarget();
		postWriteUnaryOperator(operator.getKind());
		exitCtExpression(operator);
	}

	@Override
	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		enterCtExpression(variableAccess);
		write(variableAccess.getVariable().getSimpleName());
		exitCtExpression(variableAccess);
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		enterCtExpression(variableRead);
		write(variableRead.getVariable().getSimpleName());
		exitCtExpression(variableRead);
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		enterCtExpression(variableWrite);
		write(variableWrite.getVariable().getSimpleName());
		exitCtExpression(variableWrite);
	}

	public void visitCtWhile(CtWhile whileLoop) {
		enterCtStatement(whileLoop);
		write("while (");
		scan(whileLoop.getLoopingExpression());
		write(")");

		if (whileLoop.getBody() instanceof CtBlock) {
			write(" ");
			scan(whileLoop.getBody());
		} else {
			incTab().writeln().writeTabs();
			writeStatement(whileLoop.getBody());
			decTab();
		}
	}

	/**
	 * Outputs a string.
	 */
	public DefaultJavaPrettyPrinter write(String s) {
		if (s != null) {
			sbf.append(s);
		}
		return this;
	}

	/**
	 * Outputs a char.
	 */
	public DefaultJavaPrettyPrinter write(char c) {
		sbf.append(c);
		return this;
	}

	/**
	 * Writes the annotations for the given element.
	 */
	public DefaultJavaPrettyPrinter writeAnnotations(CtElement e) {
		for (CtAnnotation<?> a : e.getAnnotations()) {
			scan(a);
		}
		return this;
	}

	/**
	 * Writes an annotation element.
	 */
	public DefaultJavaPrettyPrinter writeAnnotationElement(Factory factory, Object value) {
		if (value instanceof CtTypeAccess) {
			scan((CtTypeAccess) value).write(".class");
		} else if (value instanceof CtFieldReference) {
			scan(((CtFieldReference<?>) value).getDeclaringType());
			write("." + ((CtFieldReference<?>) value).getSimpleName());
		} else if (value instanceof CtReference) {
			scan((CtReference) value);
		} else if (value instanceof CtElement) {
			scan((CtElement) value);
		} else if (value instanceof String) {
			write("\"" + value.toString() + "\"");
		} else if (value instanceof Collection) {
			write("{");
			if (!((Collection<?>) value).isEmpty()) {
				for (Object obj : (Collection<?>) value) {
					writeAnnotationElement(factory, obj);
					write(" ,");
				}
				removeLastChar();
			}
			write("}");
		} else if (value instanceof Object[]) {
			write("{");
			if (((Object[]) value).length > 0) {
				for (Object obj : (Object[]) value) {
					writeAnnotationElement(factory, obj);
					write(" ,");
				}
				removeLastChar();
			}
			write("}");
		} else if (value instanceof Enum) {
			context.ignoreGenerics = true;
			scan(factory.Type().createReference(((Enum<?>) value).getDeclaringClass()));
			context.ignoreGenerics = false;
			write(".");
			write(value.toString());
		} else {
			write(value.toString());
		}
		return this;
	}

	/**
	 * Writes formal type parameters given in parameter.
	 *
	 * @param params
	 * 		List of formal type parameters.
	 * @return current instance of the {@link DefaultJavaPrettyPrinter}
	 */
	public DefaultJavaPrettyPrinter writeFormalTypeParameters(Collection<CtTypeReference<?>> params) {
		if (params == null) {
			return this;
		}
		if (params.size() > 0) {
			write("<");
			for (CtTypeReference<?> param : params) {
				scan(param);
				write(", ");
			}
			removeLastChar();
			write(">");
		}
		return this;
	}

	/**
	 * Writes actual type arguments in a {@link CtGenericElementReference} element.
	 *
	 * @param ctGenericElementReference
	 * 		Reference with actual type arguments.
	 * @return current instance of the {@link DefaultJavaPrettyPrinter}
	 */
	public DefaultJavaPrettyPrinter writeActualTypeArguments(CtGenericElementReference ctGenericElementReference) {
		Collection<CtTypeReference<?>> params = ctGenericElementReference.getActualTypeArguments();
		if (params != null && params.size() > 0) {
			write("<");
			boolean isImplicitTypeReference = true;
			for (CtTypeReference<?> param : params) {
				if (!(param instanceof CtImplicitTypeReference)) {
					isImplicitTypeReference = false;
					scan(param);
					write(", ");
				}
			}
			if (!isImplicitTypeReference) {
				removeLastChar();
			}
			write(">");
		}
		return this;
	}

	/**
	 * Write the compilation unit header.
	 */
	public DefaultJavaPrettyPrinter writeHeader(List<CtType<?>> types, Collection<CtTypeReference<?>> imports) {
		if (!types.isEmpty()) {
			CtPackage pack = types.get(0).getPackage();
			scan(pack).writeln().writeln().writeTabs();
			if (env.isAutoImports()) {
				for (CtTypeReference<?> ref : imports) {
					write("import " + ref.getQualifiedName() + ";").writeln().writeTabs();
				}
			}
			writeln().writeTabs();
		}
		return this;
	}

	private int line = 1;

	/**
	 * Generates a new line.
	 */
	public DefaultJavaPrettyPrinter writeln() {
		// context.currentLength = 0;
		if (context.noNewLines) {
			sbf.append(" ");
		} else {
			sbf.append(LINE_SEPARATOR);
			line++;
		}
		return this;
	}

	public DefaultJavaPrettyPrinter writeTabs() {
		// context.currentLength = 0;
		for (int i = 0; i < context.nbTabs; i++) {
			// context.currentLength += TAB.length();
			if (env.isUsingTabulations()) {
				sbf.append("\t");
			} else {
				for (int j = 0; j < env.getTabulationSize(); j++) {
					sbf.append(" ");
				}
			}
		}
		return this;
	}

	/**
	 * Writes a binary operator.
	 */
	public DefaultJavaPrettyPrinter writeOperator(BinaryOperatorKind o) {
		switch (o) {
		case OR:
			write("||");
			break;
		case AND:
			write("&&");
			break;
		case BITOR:
			write("|");
			break;
		case BITXOR:
			write("^");
			break;
		case BITAND:
			write("&");
			break;
		case EQ:
			write("==");
			break;
		case NE:
			write("!=");
			break;
		case LT:
			write("<");
			break;
		case GT:
			write(">");
			break;
		case LE:
			write("<=");
			break;
		case GE:
			write(">=");
			break;
		case SL:
			write("<<");
			break;
		case SR:
			write(">>");
			break;
		case USR:
			write(">>>");
			break;
		case PLUS:
			write("+");
			break;
		case MINUS:
			write("-");
			break;
		case MUL:
			write("*");
			break;
		case DIV:
			write("/");
			break;
		case MOD:
			write("%");
			break;
		case INSTANCEOF:
			write("instanceof");
			break;
		}
		return this;
	}

	/**
	 * Write some parameters.
	 */
	protected void writeParameters(Collection<CtTypeReference<?>> params) {
		if (params.size() > 0) {
			write("(");
			for (CtTypeReference<?> param : params) {
				scan(param);
				write(", ");
			}
			removeLastChar();
			write(")");
		}
	}

	/**
	 * Writes a statement.
	 */
	protected void writeStatement(CtStatement e) {
		scan(e);
		if (!((e instanceof CtBlock) || (e instanceof CtIf) || (e instanceof CtFor) || (e instanceof CtForEach) || (e instanceof CtWhile) || (e instanceof CtTry) || (e instanceof CtSwitch)
				|| (e instanceof CtSynchronized) || (e instanceof CtClass))) {
			write(";");
		}
	}

	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
		write(expression.getValue());
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		write(statement.getValue());
	}

	private CompilationUnit sourceCompilationUnit;

	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		this.sourceCompilationUnit = sourceCompilationUnit;
		Collection<CtTypeReference<?>> imports = Collections.emptyList();
		for (CtType<?> t : types) {
			imports = computeImports(t);
		}
		writeHeader(types, imports);
		for (CtType<?> t : types) {
			scan(t);
			writeln().writeln().writeTabs();
		}
	}

	public Map<Integer, Integer> getLineNumberMapping() {
		return lineNumberMapping;
	}

	public PrintingContext getContext() {
		return context;
	}

	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
		write(reference.getSimpleName());
	}
}
