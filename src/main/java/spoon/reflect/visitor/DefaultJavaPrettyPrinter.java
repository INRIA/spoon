/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;

import spoon.processing.Environment;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.Import;
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
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.cu.CtLineElementComparator;
import spoon.support.util.SortedList;

/**
 * A visitor for generating Java code from the program compile-time metamodel.
 */
public class DefaultJavaPrettyPrinter implements CtVisitor, PrettyPrinter {

	/**
	 * Java file extension (.java).
	 */
	public static final String JAVA_FILE_EXTENSION = ".java";

	/**
	 * Package declaration file name.
	 */
	public static final String JAVA_PACKAGE_DECLARATION = "package-info"
			+ JAVA_FILE_EXTENSION;

	Map<Integer, Integer> lineNumberMapping = new HashMap<Integer, Integer>();

	/**
	 * A scanner that calculates the imports for a given model.
	 */
	private class ImportScanner extends CtScanner {
		@SuppressWarnings("unchecked")
		Map<String, CtTypeReference<?>> imports = new TreeMap<String, CtTypeReference<?>>();

		/**
		 * Adds a type to the imports.
		 */
		public <T> boolean addImport(CtTypeReference<T> ref) {
			if (imports.containsKey(ref.getSimpleName())) {
				return isImported(ref);
			}
			imports.put(ref.getSimpleName(), ref);
			return true;
		}

		/**
		 * Calculates needed imports for the given field access.
		 */
		@Override
		public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
			enter(fieldAccess);
			scan(fieldAccess.getVariable());
			// scan(fieldAccess.getType());
			scan(fieldAccess.getAnnotations());
			scanReferences(fieldAccess.getTypeCasts());
			scan(fieldAccess.getVariable());
			scan(fieldAccess.getTarget());
			exit(fieldAccess);
		}

		@Override
		public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
			enterReference(reference);
			scan(reference.getDeclaringType());
			// scan(reference.getType());
			exitReference(reference);
		}

		public <T> boolean isImported(CtTypeReference<T> ref) {
			if (imports.containsKey(ref.getSimpleName())) {
				CtTypeReference<?> exist = imports.get(ref.getSimpleName());
				if (exist.getQualifiedName().equals(ref.getQualifiedName())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public <T> void visitCtExecutableReference(
				CtExecutableReference<T> reference) {
			enterReference(reference);
			if (reference.getDeclaringType() != null && reference.getDeclaringType().getDeclaringType()==null){
				addImport(reference.getDeclaringType());
			}
			scanReferences(reference.getActualTypeArguments());
			exitReference(reference);
		}

		@Override
		public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
			if (!(reference instanceof CtArrayTypeReference)) {
				if (reference.getDeclaringType() == null) {
					addImport(reference);
				} else {
					addImport(reference.getDeclaringType());
				}
			}
			super.visitCtTypeReference(reference);

		}

		@Override
		public <A extends Annotation> void visitCtAnnotationType(
				CtAnnotationType<A> annotationType) {
			addImport(annotationType.getReference());
			super.visitCtAnnotationType(annotationType);
		}

		@Override
		public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
			addImport(ctEnum.getReference());
			super.visitCtEnum(ctEnum);
		}

		@Override
		public <T> void visitCtInterface(CtInterface<T> intrface) {
			addImport(intrface.getReference());
			for (CtSimpleType<?> t : intrface.getNestedTypes()) {
				addImport(t.getReference());
			}
			super.visitCtInterface(intrface);
		}

		@Override
		public <T> void visitCtClass(CtClass<T> ctClass) {
			addImport(ctClass.getReference());
			for (CtSimpleType<?> t : ctClass.getNestedTypes()) {
				addImport(t.getReference());
			}
			super.visitCtClass(ctClass);
		}
	}

	private class Printingcontext {
		boolean noTypeDecl = false;

		Stack<CtTypeReference<?>> currentThis = new Stack<CtTypeReference<?>>();

		CtSimpleType<?> currentTopLevel;

		boolean ignoreGenerics = false;

		boolean ignoreImport = false;

		/** Layout variables */
		int jumped = 0;

		int lineLength = 80;

		int lineLengthMargin = 5;

		int nbTabs = 0;

		Stack<CtExpression<?>> parenthesedExpression = new Stack<CtExpression<?>>();

		boolean printDocs = true;

		boolean printShortName = false;
		boolean isInvocation = false;

		boolean skipArray = false;

		int target = 0;

		boolean ignoreStaticAccess = false;

		boolean ignoreEnclosingClass = false;

		void enterTarget() {
			target++;
		}

		void exitTarget() {
			if (jumped > 0) {
				jumped--;
			} else {
				target--;
			}
		}

		void jumpTarget() {
			jumped++;
			target--;
		}

	}

	/**
	 * The tabulation string.
	 */
	// public static final String TAB = " ";
	/**
	 * The printing context.
	 */
	public Printingcontext context = new Printingcontext();

	private ImportScanner importsContext = new ImportScanner();

	/**
	 * The string buffer in which the code is generated.
	 */
	private StringBuffer sbf = new StringBuffer();

	Environment env;

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
		if ((e.getPosition() != null)
				&& (e.getPosition().getCompilationUnit() == sourceCompilationUnit)) {
			// only map elements comming from the source CU
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
		while ((context.parenthesedExpression.size() > 0)
				&& e.equals(context.parenthesedExpression.peek())) {
			context.parenthesedExpression.pop();
			write(")");
		}
	}

	/**
	 * Gets the imports.
	 */
	public Collection<CtTypeReference<?>> getImports() {
		return importsContext.imports.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see spoon.reflect.visitor.JavaPrettyPrinter#getPackageDeclaration()
	 */
	public String getPackageDeclaration() {
		StringBuffer bck = sbf;
		sbf = new StringBuffer();
		Map<String, CtTypeReference<?>> tmp = importsContext.imports;
		importsContext.imports = new TreeMap<String, CtTypeReference<?>>();

		for (CtAnnotation<?> a : context.currentTopLevel.getPackage()
				.getAnnotations()) {
			a.accept(this);
		}

		if (!context.currentTopLevel.getPackage().getQualifiedName()
				.equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
			write("package "
					+ context.currentTopLevel.getPackage().getQualifiedName()
					+ ";");
		}
		String ret = sbf.toString();
		sbf = bck;

		importsContext.imports = tmp;
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see spoon.reflect.visitor.JavaPrettyPrinter#getResult()
	 */
	public StringBuffer getResult() {
		return sbf;
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

	private boolean isHiddenByField(CtType<?> container, CtTypeReference<?> type) {
		if (container == null) {
			return false;
		}
		// TODO: Deal with anonymous class better
		if ((container.getSimpleName() == null)
				|| container.getSimpleName().equals("")) {
			return false;
		}
		// TODO: Deal with internal class better
		if (container.getQualifiedName().contains("$")) {
			return false;
		}
		for (CtFieldReference<?> f : container.getReference().getAllFields()) {
			if (f.getSimpleName().equals(type.getSimpleName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isWhite(char c) {
		return (c == ' ') || (c == '\t') || (c == '\n');
	}

	/**
	 * Make the imports for a given type.
	 */
	public void makeImports(CtSimpleType<?> type) {
		context.currentTopLevel = type;
		importsContext.addImport(context.currentTopLevel.getReference());
		importsContext.scan(context.currentTopLevel);
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
		}
	}

	/**
	 * Removes the last non-white charater.
	 */
	protected DefaultJavaPrettyPrinter removeLastChar() {
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			sbf.deleteCharAt(sbf.length() - 1);
		}
		sbf.deleteCharAt(sbf.length() - 1);
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			sbf.deleteCharAt(sbf.length() - 1);
		}
		return this;
	}

	/**
	 * The generic scan method for an element.
	 */
	public DefaultJavaPrettyPrinter scan(CtElement e) {
		if (e != null) {
			e.accept(this);
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
		if ((e.getParent() instanceof CtBinaryOperator)
				|| (e.getParent() instanceof CtUnaryOperator)) {
			return (e instanceof CtTargetedExpression)
					|| (e instanceof CtAssignment)
					|| (e instanceof CtConditional)
					|| (e instanceof CtUnaryOperator);
		}
		if (e.getParent() instanceof CtTargetedExpression) {
			return (e instanceof CtBinaryOperator)
					|| (e instanceof CtAssignment)
					|| (e instanceof CtConditional);
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

	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {
		writeAnnotations(annotation);
		write("@");
		scan(annotation.getAnnotationType());
		if (annotation.getElementValues().size() > 0) {
			write("(");
			for (Entry<String, Object> e : annotation.getElementValues()
					.entrySet()) {
				write(e.getKey() + " = ");
				writeAnnotationElement(e.getValue());
				write(", ");
			}
			removeLastChar();
			write(")");
		}
		writeln();
	}

	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		visitCtSimpleType(annotationType);
		write("@interface " + annotationType.getSimpleName() + " {").incTab();

		SortedList<CtElement> lst = new SortedList<CtElement>(
				new CtLineElementComparator());

		lst.addAll(annotationType.getNestedTypes());
		lst.addAll(annotationType.getFields());

		for (CtElement el : lst) {
			writeln().scan(el).writeln();
		}
		decTab().writeln().write("}");
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable impl) {
		writeAnnotations(impl);
		writeModifiers(impl);
		impl.getBody().accept(this);
	}

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
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

	public <T> void visitCtAssert(CtAssert<T> asserted) {
		enterCtStatement(asserted);
		write("assert ");
		scan(asserted.getAssertExpression());
		if (asserted.getExpression() != null) {
			write(" : ");
			scan(asserted.getExpression());
		}

	}

	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		enterCtStatement(assignement);
		enterCtExpression(assignement);
		scan(assignement.getAssigned());
		write(" = ");
		scan(assignement.getAssignment());
		exitCtExpression(assignement);
	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		enterCtExpression(operator);
		boolean paren = (operator.getParent() instanceof CtBinaryOperator)
				|| (operator.getParent() instanceof CtUnaryOperator);
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
				writeln();
				writeStatement(e);
			}
		}
		decTab().writeln().write("}");
	}

	public void visitCtBreak(CtBreak breakStatement) {
		enterCtStatement(breakStatement);
		write("break");
		if (breakStatement.getTargetLabel() != null) {
			write(" " + breakStatement.getTargetLabel());
		}
	}

	@SuppressWarnings("unchecked")
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		enterCtStatement(caseStatement);
		if (caseStatement.getCaseExpression() != null) {
			write("case ");
			// writing enum case expression
			if ((caseStatement.getCaseExpression() instanceof CtFieldAccess)
					&& ((CtFieldAccess) caseStatement.getCaseExpression())
							.getVariable()
							.getType()
							.getQualifiedName()
							.equals(((CtFieldAccess) caseStatement
									.getCaseExpression()).getVariable()
									.getDeclaringType().getQualifiedName())) {
				write(((CtFieldAccess) caseStatement.getCaseExpression())
						.getVariable().getSimpleName());
			} else {
				scan(caseStatement.getCaseExpression());
			}
		} else {
			write("default");
		}
		write(" :").incTab();

		for (CtStatement s : caseStatement.getStatements()) {
			writeln().writeStatement(s);
		}
		decTab();
	}

	public void visitCtCatch(CtCatch catchBlock) {
		write(" catch (");
		scan(catchBlock.getParameter());
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
		SortedList<CtElement> lst = new SortedList<CtElement>(
				new CtLineElementComparator());
		if ((ctClass.getSimpleName() != null)
				&& (ctClass.getSimpleName().length() > 0)) {
			visitCtType(ctClass);
			write("class " + ctClass.getSimpleName());

			writeGenericsParameter(ctClass.getFormalTypeParameters());

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

		context.currentThis.push(ctClass.getReference());
		write(" {").incTab();
		for (CtElement el : lst) {
			writeln().scan(el).writeln();
		}
		decTab().writeln().write("}");
		context.currentThis.pop();
	}

	public <T> void visitCtConditional(CtConditional<T> conditional) {
		enterCtExpression(conditional);
		scan(conditional.getCondition());
		write(" ? ");
		scan(conditional.getThenExpression());
		write(" : ");
		scan(conditional.getElseExpression());
		exitCtExpression(conditional);
	}

	public <T> void visitCtConstructor(CtConstructor<T> c) {
		visitCtNamedElement(c);
		writeGenericsParameter(c.getFormalTypeParameters());
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
		write(f.getSimpleName());
		if (f.getDefaultExpression() != null) {
			CtNewClass<?> nc = (CtNewClass<?>) f.getDefaultExpression();
			if (nc.getArguments().size() > 0) {
				write("(");
				boolean first = true;
				for (CtExpression<?> ctexpr : nc.getArguments()) {
					if (first)
						first = false;
					else
						write(",");
					write(ctexpr.toString());
				}
				write(")");
			}
			scan(nc.getAnonymousClass());
		}
	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		visitCtSimpleType(ctEnum);
		write("enum " + ctEnum.getSimpleName());
		if (ctEnum.getSuperInterfaces().size() > 0) {
			write(" implements ");
			for (CtTypeReference<?> ref : ctEnum.getSuperInterfaces()) {
				scan(ref);
				write(" , ");
			}
			removeLastChar();
		}
		write(" {").incTab().writeln();
		List<CtField<?>> l1 = new ArrayList<CtField<?>>();
		List<CtField<?>> l2 = new ArrayList<CtField<?>>();
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
			writeln().scan(ec);
		}
		for (CtConstructor<?> c : ctEnum.getConstructors()) {
			if (!c.isImplicit()) {
				writeln().scan(c);
			}
		}

		SortedList<CtElement> lst = new SortedList<CtElement>(
				new CtLineElementComparator());

		lst.addAll(ctEnum.getAnonymousExecutables());
		lst.addAll(ctEnum.getNestedTypes());
		lst.addAll(ctEnum.getMethods());

		context.currentThis.push(ctEnum.getReference());
		for (CtElement el : lst) {
			writeln().scan(el).writeln();
		}
		decTab().writeln().write("}");
		context.currentThis.pop();
	}

	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		scan(reference.getDeclaringType());
		write(".");
		if (reference.getSimpleName().equals("<init>")) {
			write(reference.getDeclaringType().getSimpleName());
		} else {
			write(reference.getSimpleName());
		}
		writeGenericsParameter(reference.getActualTypeArguments());
		writeParameters(reference.getParameterTypes());
	}

	public <T> void visitCtField(CtField<T> f) {
		visitCtNamedElement(f);
		scan(f.getType());
		write(" ");
		write(f.getSimpleName());

		if ((f.getParent() == null)
				|| !CtAnnotationType.class.isAssignableFrom(f.getParent()
						.getClass())
				|| f.getModifiers().contains(ModifierKind.STATIC)) {
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

	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		enterCtExpression(fieldAccess);
		if (fieldAccess.getTarget() != null) {
			scan(fieldAccess.getTarget());
			write(".");
			context.ignoreStaticAccess = true;
		}
		context.ignoreGenerics = true;
		scan(fieldAccess.getVariable());
		context.ignoreGenerics = false;
		context.ignoreStaticAccess = false;
		exitCtExpression(fieldAccess);
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		if (reference.getSimpleName().equals("this")) {
			if (context.currentThis.isEmpty()
					|| (!reference.getType().equals(context.currentThis.peek()) && !reference
							.getDeclaringType().isAnonymous())) {
				context.ignoreGenerics = true;
				scan(reference.getDeclaringType());
				write(".");
				context.ignoreGenerics = false;
			}
		} else {
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
				if (context.currentTopLevel != null) {
					CtTypeReference<?> ref = reference.getDeclaringType();
					CtTypeReference<?> ref2;
					if(context.currentThis!=null && context.currentThis.size()>0)
						ref2 = context.currentThis.lastElement();
					else
						ref2 = context.currentTopLevel.getReference();
					// print type if not annonymous class ref and not within the
					// current scope
					printType = !ref.getSimpleName().equals("")
							&& !(ref.equals(ref2));
				} else {
					printType = true;
				}
			}

			if (isStatic && printType && !context.ignoreStaticAccess) {
				context.ignoreGenerics = true;
				scan(reference.getDeclaringType());
				context.ignoreGenerics = false;
				write(".");
			}
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
			incTab().writeln();
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
			incTab().writeln();
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
			scan(ifElement.getThenStatement());
			write(" ");
		} else {
			incTab().writeln();
			writeStatement(ifElement.getThenStatement());
			decTab().writeln();
		}
		if (ifElement.getElseStatement() != null) {
			write("else");
			if (ifElement.getElseStatement() instanceof CtIf) {
				write(" ");
				scan(ifElement.getElseStatement());
			} else if (ifElement.getElseStatement() instanceof CtBlock) {
				write(" ");
				scan(ifElement.getElseStatement());
			} else {
				incTab().writeln();
				writeStatement(ifElement.getElseStatement());
				decTab().writeln();
			}
		}
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		visitCtType(intrface);
		write("interface " + intrface.getSimpleName());
		if (intrface.getFormalTypeParameters() != null) {
			writeGenericsParameter(intrface.getFormalTypeParameters());
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
		SortedList<CtElement> lst = new SortedList<CtElement>(
				new CtLineElementComparator());
		lst.addAll(intrface.getNestedTypes());
		lst.addAll(intrface.getFields());
		lst.addAll(intrface.getMethods());
		// Content
		for (CtElement e : lst) {
			writeln().scan(e);
		}
		decTab().writeln().write("}");
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		enterCtStatement(invocation);
		enterCtExpression(invocation);
		if (invocation.getExecutable().getSimpleName().equals("<init>")) {
			// It's a constructor (super or this)
			CtType<?> parentType = invocation.getParent(CtType.class);
			if ((parentType != null)
					&& (parentType.getQualifiedName() != null)
					&& parentType.getQualifiedName().equals(
							invocation.getExecutable().getDeclaringType()
									.getQualifiedName())) {
				write("this");
			} else {
				write("super");
			}
		} else {
			// It's a method invocation
			if (invocation.getExecutable().isStatic()) {
				CtTypeReference<?> type = invocation.getExecutable()
						.getDeclaringType();
				CtType typeTmp = invocation.getParent(CtType.class);
//				if (isHiddenByField(invocation.getParent(CtType.class), type)) {
//					importsContext.imports.remove(type.getSimpleName());
//				}
				context.ignoreGenerics = true;
				scan(type);
				context.ignoreGenerics = false;
				write(".");
			} else if (invocation.getTarget() != null) {
				context.enterTarget();
				scan(invocation.getTarget());
				context.exitTarget();
				write(".");
			}
			boolean removeLastChar = false;
			if(invocation.getGenericTypes()!=null && invocation.getGenericTypes().size()>0){
				write("<");
				for (CtTypeReference<?> ref : invocation.getGenericTypes()) {
					context.isInvocation = true;
					scan(ref);
					context.isInvocation = false;
					write(",");
					removeLastChar=true;
				}
				if (removeLastChar)
					removeLastChar();
				write(">");
			}
			write(invocation.getExecutable().getSimpleName());
		}
		write("(");
		boolean remove = false;
		for (CtExpression<?> e : invocation.getArguments()) {
			scan(e);
			write(" ,");
			remove = true;
		}
		if (remove) {
			removeLastChar();
		}
		write(")");
		exitCtExpression(invocation);
	}

	/**
	 * Quotes a string.
	 */
	public static String quote(String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '\n':
				buf.append("\\n");
				break;
			case '\t':
				buf.append("\\t");
				break;
			case '\b':
				buf.append("\\b");
				break;
			case '\f':
				buf.append("\\f");
				break;
			case '\r':
				buf.append("\\r");
				break;
			case '\"':
				buf.append("\\\"");
				break;
			case '\'':
				buf.append("\\\'");
				break;
			case '\\':
				buf.append("\\\\");
				break;
			default:
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	static public String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	static public String charToHex(char c) {
		// Returns hex String representation of char c
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return byteToHex(hi) + byteToHex(lo);
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
			write(quote(String.valueOf(literal.getValue())));
			write("'");
		} else if (literal.getValue() instanceof String) {
			// JDT removes unicode values, as a result, it may change
			// the semantics of spooned programs.
			// Consequently, we fixed JDT it self (see char[] org.eclipse.jdt.internal.compiler.parser.Scanner.getCurrentTokenSourceString())
			// and we then disable the old quoting which is now incorrect (no quoting of \)
			// If we port Spoon to a newer version of JDT
			// we have to forward port the JDT fix and keep this one
			// write("\"" + quote((String) literal.getValue()) + "\"");
			write("\"" + ((String) literal.getValue()) + "\"");
		} else if (literal.getValue() instanceof Class) {
			write(((Class<?>) literal.getValue()).getName());
		} else if (literal.getValue() instanceof CtReference) {
			scan((CtReference) literal.getValue());
		} else {
			write(literal.getValue().toString());
		}
		exitCtExpression(literal);
	}

	public <T> DefaultJavaPrettyPrinter writeLocalVariable(
			CtLocalVariable<T> localVariable) {
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

	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {
		write(reference.getSimpleName());
	}

	public DefaultJavaPrettyPrinter writeTypeReference(CtTypeReference<?> t) {
		scan(t);
		return this;
	}

	public DefaultJavaPrettyPrinter writeExecutableParameters(CtExecutable<?> e) {
		if (e.getParameters().size() > 0) {
			for (CtParameter<?> p : e.getParameters()) {
				visitCtParameter(p);
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
		writeGenericsParameter(m.getFormalTypeParameters());
		scan(m.getType());
		write(" ");
		write(m.getSimpleName());
		write("(");
		writeExecutableParameters(m);
		write(")");
		writeThrowsClause(m);
		if (m.getBody() != null) {
			write(" ");
			visitCtBlock(m.getBody());
			if (m.getBody().getPosition() != null) {
				if (m.getBody().getPosition().getCompilationUnit() == sourceCompilationUnit) {
					if (m.getBody().getStatements().isEmpty()
							|| !(m.getBody()
									.getStatements()
									.get(m.getBody().getStatements().size() - 1) instanceof CtReturn)) {
						lineNumberMapping.put(line, m.getBody().getPosition()
								.getEndLine());
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
		importsContext = new ImportScanner();
		sbf = new StringBuffer();
	}

	public DefaultJavaPrettyPrinter writeModifiers(CtModifiable m) {
		for (ModifierKind mod : m.getModifiers()) {
			write(mod.toString().toLowerCase() + " ");
		}
		return this;
	}

	public void visitCtNamedElement(CtNamedElement e) {
		// Write comments
		if (context.printDocs && (e.getDocComment() != null)) {
			write("/** ").writeln();
			for (String com : e.getDocComment().split("\n")) {
				write(" *" + com).writeln();
			}
			write(" */").writeln();
		}
		// Write element parameters (Annotations)
		writeAnnotations(e);
		writeModifiers(e);
	}

	@SuppressWarnings("unchecked")
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		enterCtExpression(newArray);

		if (!(context.currentTopLevel instanceof CtAnnotationType)) {
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

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		enterCtStatement(newClass);
		enterCtExpression(newClass);

		if (newClass.getTarget() != null) {
			scan(newClass.getTarget()).write(".");
		}

		if (newClass.getAnonymousClass() != null) {
			write("new ");
			if (newClass.getAnonymousClass().getSuperclass() != null) {
				scan(newClass.getAnonymousClass().getSuperclass());
			} else if (newClass.getAnonymousClass().getSuperInterfaces().size() > 0) {
				for (CtTypeReference<?> ref : newClass.getAnonymousClass()
						.getSuperInterfaces()) {
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
		} else {
			if (newClass.getTarget() != null)
				context.ignoreEnclosingClass = true;

			write("new ").scan(newClass.getType());
			context.ignoreEnclosingClass = false;

			if ((newClass.getExecutable() != null)
					&& (newClass.getExecutable().getActualTypeArguments() != null)) {
				writeGenericsParameter(newClass.getExecutable()
						.getActualTypeArguments());
			}
			write("(");
			boolean remove = false;
			for (CtCodeElement e : newClass.getArguments()) {
				scan(e);
				write(" , ");
				remove = true;
			}
			if (remove) {
				removeLastChar();
			}

			write(")");
		}
		exitCtExpression(newClass);
	}

	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {
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
		if (!ctPackage.getQualifiedName().equals(
				CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
			if (context.currentTopLevel == null) {
				for (CtAnnotation<?> a : ctPackage.getAnnotations()) {
					scan(a);
				}
			}
			write("package " + ctPackage.getQualifiedName() + ";");
		}
	}

	public void visitCtPackageReference(CtPackageReference reference) {
		write(reference.getSimpleName());
	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {
		writeAnnotations(parameter);
		writeModifiers(parameter);
		if (parameter.isVarArgs()) {
			scan(((CtArrayTypeReference<T>) parameter.getType())
					.getComponentType());
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

	<T> void visitCtSimpleType(CtSimpleType<T> type) {
		mapLine(line, type);
		if (type.isTopLevel()) {
			context.currentTopLevel = type;
		}
		visitCtNamedElement(type);
	}

	public <R> void visitCtStatementList(CtStatementList<R> statements) {
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
			writeln().scan(c);
		}
		decTab().writeln().write("}");
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

	<T> void visitCtType(CtType<T> type) {
		visitCtSimpleType(type);
	}

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		write(typeParameter.getName());
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
		// write(ref.getSimpleName());

		if (importsContext.isImported(ref) || context.printShortName) {
			write(ref.getSimpleName());
		} else {
			write(ref.getQualifiedName());
		}
		if (!context.ignoreGenerics) {
			writeGenericsParameter(ref.getActualTypeArguments());
		}
		if (!context.isInvocation && !ref.getBounds().isEmpty()
				&& !((ref.getBounds().size() == 1) && ref.getBounds().get(0)
						.getQualifiedName().equals("java.lang.Object"))) {
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
		if (
//				(importsContext.isImported(ref) && !context.ignoreImport && ref.getPackage()!=null)|| 
				context.printShortName) {
			write(ref.getSimpleName());
		} else {
			if (ref.getDeclaringType() != null) {
				if (!context.ignoreEnclosingClass
						&& context.currentTopLevel != null) {
					boolean ign = context.ignoreGenerics;
					context.ignoreGenerics = true;
					scan(ref.getDeclaringType());
					write(".");
					context.ignoreGenerics = ign;
				}
				write(ref.getSimpleName());
			} else {
				write(ref.getQualifiedName());
			}
		}
		if(ref.isSuperReference())
			write(".super");
		if (!context.ignoreGenerics) {
			writeGenericsParameter(ref.getActualTypeArguments());
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

	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		enterCtExpression(variableAccess);
		write(variableAccess.getVariable().getSimpleName());
		exitCtExpression(variableAccess);
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
			incTab().writeln();
			writeStatement(whileLoop.getBody());
			decTab();
		}
	}

	/**
	 * Generates a string.
	 */
	public DefaultJavaPrettyPrinter write(String s) {
		if (s != null) {
			sbf.append(s);
		}
		return this;
	}

	/**
	 * Writes the annotations for the given element.
	 */
	public DefaultJavaPrettyPrinter writeAnnotations(CtElement e) {
		for (CtAnnotation<?> a : e.getAnnotations()) {
			a.accept(this);
		}
		return this;
	}

	/**
	 * Writes an annotation element.
	 */
	public DefaultJavaPrettyPrinter writeAnnotationElement(Object value) {
		if (value instanceof CtTypeReference) {
			context.ignoreGenerics = true;
			scan((CtTypeReference<?>) value).write(".class");
			context.ignoreGenerics = false;
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
					writeAnnotationElement(obj);
					write(" ,");
				}
				removeLastChar();
			}
			write("}");
		} else if (value instanceof Enum) {
			context.ignoreGenerics = true;
			scan(env.getFactory().Type()
					.createReference(((Enum) value).getDeclaringClass()));
			context.ignoreGenerics = false;
			write(".");
			write(value.toString());
		} else {
			write(value.toString());
		}
		return this;
	}

	/**
	 * Writes a generics parameter.
	 */
	public DefaultJavaPrettyPrinter writeGenericsParameter(
			Collection<CtTypeReference<?>> params) {
		if (params == null) {
			return this;
		}
		if (params.size() > 0) {
			write("<");
			context.ignoreImport = true;
			for (CtTypeReference<?> param : params) {
				scan(param);
				write(", ");
			}
			context.ignoreImport = false;
			removeLastChar();
			write("> ");
		}
		return this;
	}

	/**
	 * Write the compilation unit header.
	 */
	public DefaultJavaPrettyPrinter writeHeader(List<CtSimpleType<?>> types) {
		if (!types.isEmpty()) {
			CtPackage pack = types.get(0).getPackage();
			scan(pack).writeln().writeln();
			if ((sourceCompilationUnit != null)
					&& !sourceCompilationUnit.isAutoImport()) {
				for (Import i : sourceCompilationUnit.getManualImports()) {
					write(i + ";").writeln();
				}
			} 
			// no more imports :)
//			else {
//				for (CtTypeReference<?> ref : importsContext.imports.values()) {
//					// ignore non-top-level type
//					if (ref.getPackage() != null) {
//						// ignore java.lang package
//						if (!ref.getPackage().getSimpleName()
//								.equals("java.lang")) {
//							// ignore type in same package
//							if (!ref.getPackage().getSimpleName()
//									.equals(pack.getQualifiedName())) {
//								write("import " + ref.getQualifiedName() + ";")
//										.writeln();
//							}
//						}
//					}
//				}
//			}
			writeln();
		}
		return this;
	}

	int line = 1;

	/**
	 * Generates a new line starting with the current number of tabs.
	 */
	public DefaultJavaPrettyPrinter writeln() {
		// context.currentLength = 0;
		sbf.append(System.getProperty("line.separator"));
		line++;
		return writeTabs();
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
		if (!((e instanceof CtBlock) || (e instanceof CtIf)
				|| (e instanceof CtFor) || (e instanceof CtForEach)
				|| (e instanceof CtWhile) || (e instanceof CtTry)
				|| (e instanceof CtSwitch) || (e instanceof CtSynchronized) || (e instanceof CtClass))) {
			write(";");
		}
	}

	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> expression) {
		write(expression.getValue());
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		write(statement.getValue());
	}

	private CompilationUnit sourceCompilationUnit;

	public void calculate(CompilationUnit sourceCompilationUnit,
			List<CtSimpleType<?>> types) {
		this.sourceCompilationUnit = sourceCompilationUnit;
		for (CtSimpleType<?> t : types) {
			makeImports(t);
		}
		writeHeader(types);
		for (CtSimpleType<?> t : types) {
			scan(t);
			writeln().writeln();
		}
	}

	public Map<Integer, Integer> getLineNumberMapping() {
		return lineNumberMapping;
	}
}