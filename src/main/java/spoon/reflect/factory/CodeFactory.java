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

package spoon.reflect.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.Factory;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

/**
 * This sub-factory contains utility methods to create code elements. To avoid
 * over-using reflection, consider using {@link spoon.template.Template}.
 */
public class CodeFactory extends SubFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 */
	public CodeFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a binary operator.
	 * 
	 * @param <T>
	 *            the type of the expression
	 * @param left
	 *            the left operand
	 * @param right
	 *            the right operand
	 * @param kind
	 *            the operator kind
	 * @return a binary operator expression
	 */
	public <T> CtBinaryOperator<T> createBinaryOperator(CtExpression<?> left,
			CtExpression<?> right, BinaryOperatorKind kind) {
		CtBinaryOperator<T> op = factory.Core().createBinaryOperator();
		op.setLeftHandOperand(left);
		op.setRightHandOperand(right);
		setParent(op, left, right);
		op.setKind(kind);
		return op;
	}

	/**
	 * Creates a class access expression of the form <code>C.class</code>.
	 * 
	 * @param <T>
	 *            the actual type of the accessed class if available
	 * @param type
	 *            a type reference to the accessed class
	 * @return the class access expression.
	 */
	@SuppressWarnings("unchecked")
	public <T> CtFieldAccess<Class<T>> createClassAccess(CtTypeReference<T> type) {
		CtFieldAccess<Class<T>> ca = factory.Core().createFieldAccess();
		CtTypeReference classType = factory.Type().createReference(Class.class);

		ca.setType(classType);
		CtFieldReference field = factory.Core().createFieldReference();
		field.setDeclaringType(type);
		field.setType(classType);
		field.setSimpleName("class");
		ca.setVariable(field);
		return ca;
	}

	/**
	 * Creates an invocation (can be a statement or an expression).
	 * 
	 * @param <T>
	 *            the return type of the invoked method
	 * @param target
	 *            the target expression
	 * @param executable
	 *            the invoked executable
	 * @param arguments
	 *            the argument list
	 * @return the new invocation
	 */
	public <T> CtInvocation<T> createInvocation(CtExpression<?> target,
			CtExecutableReference<T> executable, CtExpression<?>... arguments) {
		List<CtExpression<?>> ext = new ArrayList<CtExpression<?>>();
		for (CtExpression<?> arg : arguments) {
			ext.add(arg);
		}
		return createInvocation(target, executable, ext);
	}

	/**
	 * Creates an invocation (can be a statement or an expression).
	 * 
	 * @param <T>
	 *            the return type of the invoked method
	 * @param target
	 *            the target expression
	 * @param executable
	 *            the invoked executable
	 * @param arguments
	 *            the argument list
	 * @return the new invocation
	 */
	public <T> CtInvocation<T> createInvocation(CtExpression<?> target,
			CtExecutableReference<T> executable, List<CtExpression<?>> arguments) {
		CtInvocation<T> invocation = factory.Core().createInvocation();
		invocation.setTarget(target);
		invocation.setExecutable(executable);
		invocation.setArguments(arguments);
		return invocation;
	}

	/**
	 * Creates a literal with a given value.
	 * 
	 * @param <T>
	 *            the type of the literal
	 * @param value
	 *            the value of the litteral
	 * @return a new literal
	 */
	public <T> CtLiteral<T> createLiteral(T value) {
		CtLiteral<T> l = factory.Core().createLiteral();
		l.setValue(value);
		return l;
	}

	/**
	 * Creates a one-dimension array that must only contain literals.
	 */
	@SuppressWarnings("unchecked")
	public <T> CtNewArray<T[]> createLiteralArray(T[] value) {
		if (!value.getClass().isArray())
			throw new RuntimeException("value is not an array");
		if (value.getClass().getComponentType().isArray())
			throw new RuntimeException("can only create one-dimension arrays");
		CtNewArray<T[]> array = factory.Core().createNewArray();
		array.setType(factory.Type().createArrayReference(
				factory.Type().createReference(
						(Class<T>) value.getClass().getComponentType())));
		for (T e : value) {
			CtLiteral<T> l = factory.Core().createLiteral();
			l.setValue(e);
			l.setParent(array);
			array.getElements().add(l);
		}
		return array;
	}

	/**
	 * Creates a local variable declaration.
	 * 
	 * @param <T>
	 *            the local variable type
	 * @param type
	 *            the reference to the type
	 * @param name
	 *            the name of the variable
	 * @param defaultExpression
	 *            the assigned default expression
	 * @return a new local variable declaration
	 */
	public <T> CtLocalVariable<T> createLocalVariable(CtTypeReference<T> type,
			String name, CtExpression<T> defaultExpression) {
		CtLocalVariable<T> var = factory.Core().createLocalVariable();
		var.setSimpleName(name);
		var.setType(type);
		var.setDefaultExpression(defaultExpression);
		return var;
	}

	/**
	 * Creates a local variable reference that points to an existing local
	 * variable (strong referencing).
	 */
	public <T> CtLocalVariableReference<T> createLocalVariableReference(
			CtLocalVariable<T> localVariable) {
		CtLocalVariableReference<T> ref = factory.Core()
				.createLocalVariableReference();
		ref.setType(localVariable.getType());
		ref.setSimpleName(localVariable.getSimpleName());
		ref.setDeclaration(localVariable);
		return ref;
	}

	/**
	 * Creates a local variable reference with its name an type (weak
	 * referencing).
	 */
	public <T> CtLocalVariableReference<T> createLocalVariableReference(
			CtTypeReference<T> type, String name) {
		CtLocalVariableReference<T> ref = factory.Core()
				.createLocalVariableReference();
		ref.setType(type);
		ref.setSimpleName(name);
		return ref;
	}

	/**
	 * Creates a new statement list from an existing block.
	 */
	@SuppressWarnings("unchecked")
	public CtStatementList createStatementList(CtBlock<?> block) {
		CtStatementList l = factory.Core().createStatementList();
		for (CtStatement s : block.getStatements()) {
			l.getStatements().add(factory.Core().clone(s));
		}
		return l;
	}

	/**
	 * Creates an access to a <code>this</code> variable (of the form
	 * <code>type.this</code>).
	 * 
	 * @param <T>
	 *            the actual type of <code>this</code>
	 * @param type
	 *            the reference to the type that holds the <code>this</code>
	 *            variable
	 * @return a <code>type.this</code> expression
	 */
	public <T> CtFieldAccess<T> createThisAccess(CtTypeReference<T> type) {
		CtFieldAccess<T> fa = factory.Core().createFieldAccess();
		fa.setType(type);
		CtFieldReference<T> field = factory.Core().createFieldReference();
		field.setDeclaringType(type);
		field.setType(type);
		field.setSimpleName("this");
		fa.setVariable(field);
		return fa;
	}

	/**
	 * Creates a variable access.
	 */
	public <T> CtVariableAccess<T> createVariableAccess(
			CtVariableReference<T> variable, boolean isStatic) {
		CtVariableAccess<T> va;
		if (variable instanceof CtFieldReference) {
			va = factory.Core().createFieldAccess();
			// creates a this target for non-static fields to avoid name
			// conflicts...
			if (!isStatic) {
				((CtFieldAccess<T>) va)
						.setTarget(createThisAccess(((CtFieldReference<T>) variable)
								.getDeclaringType()));
			}
		} else {
			va = factory.Core().createVariableAccess();
		}
		va.setVariable(variable);
		va.setType(variable.getType());
		return va;
	}

	/**
	 * Creates a list of variable accesses.
	 * 
	 * @param variables
	 *            the variables to be accessed
	 */
	public List<CtExpression<?>> createVariableAccesses(
			List<? extends CtVariable<?>> variables) {
		List<CtExpression<?>> result = new ArrayList<CtExpression<?>>();
		for (CtVariable<?> v : variables) {
			result.add(createVariableAccess(v.getReference(), v.getModifiers()
					.contains(ModifierKind.STATIC)));
		}
		return result;
	}

	/**
	 * Creates a variable assignment (can be an expression or a statement).
	 * 
	 * @param <T>
	 *            the type of the assigned variable
	 * @param variable
	 *            a reference to the assigned variable
	 * @param isStatic
	 *            tells if the assigned variable is static or not
	 * @param expression
	 *            the assigned expression
	 * @return a variable assignment
	 */
	public <A,T extends A> CtAssignment<A,T> createVariableAssignment(
			CtVariableReference<A> variable, boolean isStatic,
			CtExpression<T> expression) {
		CtAssignment<A, T> va = factory.Core().createAssignment();
		va.setAssignment(expression);
		expression.setParent(va);
		CtVariableAccess<A> vaccess = createVariableAccess(variable, isStatic);
		va.setAssigned(vaccess);
		vaccess.setParent(va);
		return va;
	}

	/**
	 * Creates a list of statements that contains the assignments of a set of
	 * variables.
	 * 
	 * @param variables
	 *            the variables to be assigned
	 * @param expressions
	 *            the assigned expressions
	 * @return a list of variable assignments
	 */
	@SuppressWarnings("unchecked")
	public CtStatementList createVariableAssignments(
			List<? extends CtVariable> variables,
			List<? extends CtExpression> expressions) {
		CtStatementList<?> result = factory.Core().createStatementList();
		for (int i = 0; i < variables.size(); i++) {
			result.getStatements().add(
					createVariableAssignment(variables.get(i).getReference(),
							variables.get(i).getModifiers().contains(
									ModifierKind.STATIC), expressions.get(i)));
		}
		return result;
	}

	/**
	 * Gets a list of references from a list of elements.
	 * 
	 * @param <R>
	 *            the expected reference type
	 * @param <E>
	 *            the element type
	 * @param elements
	 *            the element list
	 * @return the corresponding list of references
	 */
	@SuppressWarnings("unchecked")
	public <R extends CtReference, E extends CtNamedElement> List<R> getReferences(
			List<E> elements) {
		List<R> refs = new ArrayList<R>();
		for (E e : elements) {
			refs.add((R) e.getReference());
		}
		return refs;
	}

	/**
	 * Creates a modifier set.
	 * 
	 * @param modifiers
	 *            to put in set
	 * @return Set of given modifiers
	 */
	public Set<ModifierKind> modifiers(ModifierKind... modifiers) {
		Set<ModifierKind> ret = new TreeSet<ModifierKind>();
		for (ModifierKind m : modifiers)
			ret.add(m);
		return ret;
	}

	/**
	 * Creates a Code Snippet expression.
	 * 
	 * @param <T> The type of the expression represented by the CodeSnippet
	 * @param expression The string that contains the expression.
	 * @return a new CtCodeSnippetExpression.
	 */
	public <T> CtCodeSnippetExpression<T> createCodeSnippetExpression(String expression) {
		CtCodeSnippetExpression<T> e = factory.Core().createCodeSnippetExpression();
		e.setValue(expression);
		return e;
	}	

	/**
	 * Creates a Code Snippet statement.
	 * 
	 * @param statement The String containing the statement.
	 * @return a new CtCodeSnippetStatement
	 */
	public CtCodeSnippetStatement createCodeSnippetStatement(String statement) {
		CtCodeSnippetStatement e = factory.Core().createCodeSnippetStatement();
		e.setValue(statement);
		return e;
	}	
	
}
