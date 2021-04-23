/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl;

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.And;
import spoon.smpl.formula.ExistsNext;
import spoon.smpl.formula.ExistsUntil;
import spoon.smpl.formula.ExistsVar;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.MetavariableConstraint;
import spoon.smpl.formula.Not;
import spoon.smpl.formula.Proposition;
import spoon.smpl.formula.Statement;
import spoon.smpl.formula.True;
import spoon.smpl.label.PropositionLabel;
import spoon.smpl.label.StatementLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: a simpler approach for method header matching, something more similar to dots-in-method-calls (see DotsExtPatternMatcher)
// TODO: access modifiers
// TODO: rename since its not just methods but rather executables?

/**
 * A MethodHeaderModel is a CTL model representation of a method header.
 * <p>
 * A method header consists of a return type, a method name and a list of formal parameters.
 * A MethodHeaderModel represents this as a single-path CTL model of the following structure:
 * <p>
 * [StatementLabel(encoding of method return type)]
 * v
 * [StatementLabel(encoding of method name)]
 * v
 * [StatementLabel(CtParameter)]*                  // zero or more
 * v
 * [after]
 */
public class MethodHeaderModel implements Model {
	/**
	 * Create a new MethodHeaderModel for a given method.
	 *
	 * @param ctExecutable Executable from which to extract the header and create model for
	 */
	public MethodHeaderModel(CtExecutable<?> ctExecutable) {
		// type, name, args, exit
		int numstates = 3 + ctExecutable.getParameters().size();

		states = IntStream.range(0, numstates).boxed().collect(Collectors.toList());
		successors = new HashMap<>();
		labels = new HashMap<>();

		states.forEach((s) -> {
			successors.put(s, new ArrayList<>());
			labels.put(s, new ArrayList<>());

			if (s + 1 < numstates) {
				successors.get(s).add(s + 1);
			} else {
				successors.get(s).add(s);
			}
		});

		Factory factory = ctExecutable.getFactory();

		labels.get(0).add(new StatementLabel(createMethodReturnTypeElement(ctExecutable.getType())));
		labels.get(1).add(new StatementLabel(createMethodNameElement(ctExecutable.getSimpleName(), factory)));
		labels.get(numstates - 1).add(new PropositionLabel("after"));

		for (int i = 2; i < numstates - 1; ++i) {
			labels.get(i).add(new StatementLabel(ctExecutable.getParameters().get(i - 2)));
		}
	}

	/**
	 * Get the CTL model states.
	 *
	 * @return List of state IDs
	 */
	@Override
	public List<Integer> getStates() {
		return states;
	}

	/**
	 * Get the CTL model successors for a given state.
	 *
	 * @param state Parent state
	 * @return List of state IDs that are successors to the given state
	 */
	@Override
	public List<Integer> getSuccessors(int state) {
		return successors.get(state);
	}

	/**
	 * Get the CTL model labels for a given state.
	 *
	 * @param state Target state
	 * @return List of CTL model labels
	 */
	@Override
	public List<Label> getLabels(int state) {
		return labels.get(state);
	}

	@Override
	public String toString() {
		return DebugUtils.prettifyModel(this);
	}

	/**
	 * Create an element that encodes a method return type.
	 *
	 * @param ctTypeRef Return type to encode
	 * @return An element that encodes a method return type
	 */
	public static CtElement createMethodReturnTypeElement(CtTypeReference<?> ctTypeRef) {
		return createMethodReturnTypeElement(ctTypeRef, ctTypeRef.getFactory());
	}

	/**
	 * Create an element that encodes a method return type.
	 *
	 * @param ctTypeRef Return type to encode
	 * @param factory   Spoon factory to use
	 * @return Element that encodes a method return type
	 */
	public static CtElement createMethodReturnTypeElement(CtTypeReference<?> ctTypeRef, Factory factory) {
		CtInvocation<Void> result = createWrapperInvocation(methodReturnTypeElementName, factory);

		CtFieldReference<Object> fieldRef = factory.createFieldReference();
		fieldRef.setSimpleName(ctTypeRef.getSimpleName());

		CtFieldRead<Object> fieldRead = factory.createFieldRead();
		fieldRead.setVariable(fieldRef);
		fieldRef.setParent(fieldRead);

		result.addArgument(fieldRead);
		fieldRead.setParent(result);

		return result;
	}

	/**
	 * Check whether the given element is an encoding of a method return type.
	 *
	 * @param e Element to check
	 * @return True if the element is an encoding of a method return type, false otherwise
	 */
	public static boolean isMethodReturnTypeElement(CtElement e) {
		return isExecutableWithName(e, methodReturnTypeElementName);
	}

	/**
	 * Retrieve the method return type from a given encoding.
	 *
	 * @param e Element encoding a method return type
	 * @return Return type encoded by element
	 */
	public static CtTypeReference<?> getEncodedReturnType(CtElement e) {
		if (!isMethodReturnTypeElement(e)) {
			throw new IllegalArgumentException("invalid element");
		} else {
			return (CtTypeReference<?>) ((CtInvocation<?>) e).getArguments().get(0);
		}
	}

	/**
	 * Create an element that encodes a method name.
	 *
	 * @param name    Method name to encode
	 * @param factory Spoon factory to use
	 * @return Element that encodes a method name
	 */
	public static CtElement createMethodNameElement(String name, Factory factory) {
		CtInvocation<Void> result = createWrapperInvocation(methodNameElementName, factory);
		CtVariableRead<Boolean> arg = factory.createVariableRead();
		CtVariableReference<Boolean> argvar = factory.createLocalVariableReference();

		argvar.setType(factory.createCtTypeReference(Boolean.class));
		argvar.setSimpleName(name);

		arg.setType(factory.createCtTypeReference(Boolean.class));
		arg.setVariable(argvar);

		result.addArgument(arg);

		return result;
	}

	/**
	 * Check whether the given element is an encoding of a method name.
	 *
	 * @param e Element to check
	 * @return True if the element is an encoding of a method name, false otherwise
	 */
	public static boolean isMethodNameElement(CtElement e) {
		return isExecutableWithName(e, methodNameElementName);
	}

	/**
	 * Retrieve the method name from a given encoding.
	 *
	 * @param e Element encoding a method name
	 * @return Name encoded by element
	 */
	public static String getEncodedName(CtElement e) {
		if (!isMethodNameElement(e)) {
			throw new IllegalArgumentException("invalid element");
		} else {
			return ((CtVariableRead<?>) ((CtInvocation<?>) e).getArguments().get(0)).getVariable().getSimpleName();
		}
	}

	/**
	 * Compile a formula for a MethodHeaderModel of a given executable.
	 *
	 * @param executable           Executable to compile a formula for
	 * @param metavars             Metavariable names and their corresponding constraints
	 * @param metavarsUsedInHeader Secondary output; metavariable names seen in the method header are written to here
	 * @return Formula
	 */
	public static Formula compileMethodHeaderFormula(CtExecutable<?> executable, Map<String, MetavariableConstraint> metavars, Set<String> metavarsUsedInHeader) {
		Set<String> potentialMetavarsSet = new HashSet<>();
		Stack<Formula> parts = new Stack<>();

		parts.push(new Statement(createMethodReturnTypeElement(executable.getType()), metavars));
		parts.push(new Statement(createMethodNameElement(executable.getSimpleName(), executable.getFactory()), metavars));

		potentialMetavarsSet.add(executable.getType().getSimpleName());
		potentialMetavarsSet.add(executable.getSimpleName());

		if (executable.getParameters().size() > 0) {
			Formula paramsFormula = new Proposition("after");
			List<CtParameter<?>> params = executable.getParameters();

			for (int i = params.size() - 1; i >= 0; --i) {
				CtParameter<?> ctLocal = params.get(i);

				if (SmPLJavaDSL.isParameterLevelDots(ctLocal)) {
					Formula lhs = null;

					if (i > 0) {
						CtParameter<?> pre = params.get(i - 1);
						lhs = new Not(new Statement(pre, metavars));
					}

					if (i + 1 < params.size()) {
						CtParameter<?> post = params.get(i + 1);

						lhs = (lhs == null) ? new Not(new Statement(post, metavars))
								: new And(lhs, new Not(new Statement(post, metavars)));
					}

					paramsFormula = new ExistsUntil(lhs == null ? new True() : lhs, paramsFormula);
				} else {
					paramsFormula = new And(new Statement(ctLocal, metavars), new ExistsNext(paramsFormula));

					potentialMetavarsSet.add(ctLocal.getType().getSimpleName());
					potentialMetavarsSet.add(ctLocal.getSimpleName());
				}
			}

			parts.push(paramsFormula);
		} else {
			parts.push(new Proposition("after"));
		}

		Formula formula = parts.pop();

		for (int i = 0, n = parts.size(); i < n; ++i) {
			formula = new And(parts.pop(), new ExistsNext(formula));
		}

		List<String> potentialMetavars = new ArrayList<>(potentialMetavarsSet);

		Collections.sort(potentialMetavars);
		Collections.reverse(potentialMetavars);

		for (String var : potentialMetavars) {
			if (metavars.containsKey(var)) {
				formula = new ExistsVar(var, formula);
				metavarsUsedInHeader.add(var);
			}
		}

		return formula;
	}

	/**
	 * Create an invocation of an executable with the given name.
	 *
	 * @param name    Name of executable to invoke
	 * @param factory Spoon factory to use
	 * @return Invocation of executable with the given name
	 */
	private static CtInvocation<Void> createWrapperInvocation(String name, Factory factory) {
		CtExecutableReference<Void> ctExe = factory.createExecutableReference();
		ctExe.setSimpleName(name);
		ctExe.setType(factory.createCtTypeReference(Void.class));
		ctExe.setDeclaringType(null);
		ctExe.setStatic(true);
		ctExe.setParameters(Arrays.asList(factory.createCtTypeReference(Object.class)));

		CtInvocation<Void> ctInvocation = factory.createInvocation();
		ctInvocation.setType(factory.createCtTypeReference(Void.class));
		ctInvocation.setTarget(null);
		ctInvocation.setExecutable(ctExe);

		return ctInvocation;
	}

	/**
	 * Check if a given AST element is an invocation of a given executable name.
	 *
	 * @param e    Element to check
	 * @param name Executable name to match
	 * @return True if the given element is an invocation matching the given executable name, false otherwise
	 */
	private static boolean isExecutableWithName(CtElement e, String name) {
		return e instanceof CtInvocation<?>
				&& ((CtInvocation<?>) e).getExecutable().getSimpleName().equals(name);
	}

	/**
	 * States of the CTL model.
	 */
	private List<Integer> states;

	/**
	 * Successors of the CTL model.
	 */
	private Map<Integer, List<Integer>> successors;

	/**
	 * Labels of the CTL model.
	 */
	private Map<Integer, List<Label>> labels;

	/**
	 * Name of executable in wrapper invocation for encoding method return type.
	 */
	private static final String methodReturnTypeElementName = "__MethodHeaderReturnType__";

	/**
	 * Name of executable in wrapper invocation for encoding method name.
	 */
	private static final String methodNameElementName = "__MethodHeaderName__";
}
