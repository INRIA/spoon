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
package spoon.test.query_function;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.filter.*;
import spoon.test.query_function.testclasses.EnumValueReferences;
import spoon.test.query_function.testclasses.VariableReferencesFromStaticMethod;
import spoon.test.query_function.testclasses.VariableReferencesModelTest;
import spoon.testing.utils.ModelUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VariableReferencesTest {
	CtClass<?> modelClass;

	@BeforeEach
	public void setup() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.addInputResource("./src/test/java/spoon/test/query_function/testclasses/VariableReferencesModelTest.java");
		launcher.run();
		Factory factory = launcher.getFactory();
		modelClass = factory.Class().get(VariableReferencesModelTest.class);
	}

	@Test
	public void testCheckModelConsistency() {
		class Context {
			final Map<Integer, CtElement> unique = new HashMap<>();
			int maxKey = 0;
			void checkKey(int key, CtElement ele) {
				CtElement ambiguous = unique.put(key, ele);
				if(ambiguous!=null) {
					fail("Two variables [" + ambiguous + " in " + getParentMethodName(ambiguous) + "," + ele.toString() + " in " + getParentMethodName(ele) + "] has same value");
				}
				maxKey = Math.max(maxKey, key);
			}
		}
		Context context = new Context();

		modelClass.filterChildren((CtElement e)->{
			if (e instanceof CtVariable<?> var) {
                if(!isTestFieldName(var.getSimpleName())) {
					return false;
				}
				//check only these variables whose name is isTestFieldName(name)==true
				Integer val = getLiteralValue(var);
				context.checkKey(val, var);
			}
			return false;
		}).list();
		assertFalse(context.unique.isEmpty());
		assertEquals(context.maxKey, context.unique.size(), "Only these keys were found: " + context.unique.keySet());
		assertEquals((int) getLiteralValue((CtVariable<?>) modelClass.filterChildren(new NamedElementFilter<>(CtVariable.class, "maxValue")).first()), context.maxKey, "AllLocalVars#maxValue must be equal to maximum value number ");
	}

	/**
	 * Visits all {@code CtCatchVariable} elements with name <code>field</code> and searches for all their references.
     * The test verifies the correctness of the found references using the following two checks:
	 * <ul>
	 *   <li>Each found reference must be on the left side of a binary operator, with a unique identification number on the right (e.g., {@code field == 7}).</li>
	 *   <li>The model is searched for all variable references with the same identification number and counts them.</li>
	 * </ul>
	 * Finally, it checks that the counted number of references matches the found number.
	 */
	@Test
	public void testCatchVariableReferenceFunction() {
		modelClass.filterChildren((CtCatchVariable<?> var)->{
			if(isTestFieldName(var.getSimpleName())) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new CatchVariableReferenceFunction());
			}
			return false;
		}).list();
	}

	/**
	 * Visits all {@code CtLocalVariable} elements with name {@link #isTestFieldName(String)} and searches for all their references.
     * The test verifies the correctness of the found references using the following two checks:
	 * <ul>
	 *   <li>Each found reference must be on the left side of a binary operator, with a unique identification number on the right (e.g., {@code field == 7}).</li>
	 *   <li>The model is searched for all variable references with the same identification number and counts them.</li>
	 * </ul>
	 * Finally, it checks that the counted number of references matches the found number.
	 */
	@Test
	public void testLocalVariableReferenceFunction() {
		modelClass.filterChildren((CtLocalVariable<?> var)->{
			if(isTestFieldName(var.getSimpleName())) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new LocalVariableReferenceFunction());
			}
			return false;
		}).list();
	}

	/**
	 * Visits all {@code CtParameter} elements with name <code>field</code> and searches for all their references.
     * The test verifies the correctness of the found references using the following two checks:
	 * <ul>
	 *   <li>Each found reference must be on the left side of a binary operator, with a unique identification number on the right (e.g., {@code field == 7}).</li>
	 *   <li>The model is searched for all variable references with the same identification number and counts them.</li>
	 * </ul>
	 * Finally, it checks that the counted number of references matches the found number.
	 */
	@Test
	public void testParameterReferenceFunction() {
		modelClass.filterChildren((CtParameter<?> var)->{
			if(isTestFieldName(var.getSimpleName())) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new ParameterReferenceFunction());
			}
			return false;
		}).list();
	}

    /**
     * Visits all the {@link CtVariable} elements whose name is <code>field</code> and searches for all their references.
     * The test verifies the correctness of the found references using the following two checks:
     * <ol>
     *   <li>Each found reference appears on the left side of a binary operator, with a unique reference identification number on the right side (e.g. {@code field == 7}).</li>
     *   <li>The model is searched for all variable references with the same identification number, and counts these.</li>
     * </ol>
     * Finally, it checks that the counted number of references matches the number of found references.
     */
	@Test
	public void testVariableReferenceFunction() {
		modelClass.filterChildren((CtVariable<?> var)->{
			if(isTestFieldName(var.getSimpleName())) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new VariableReferenceFunction());
			}
			return false;
		}).list();
	}

    private boolean isTestFieldName(String name) {
		return "field".equals(name);
	}

    /**
     * Visits all {@link CtVariable} elements named "field" and searches for all elements in their scopes.
     * Compares the result with those found by basic functions.
     */
	@Test
	public void testVariableScopeFunction() {
		List<CtVariable<?>> list = modelClass.filterChildren((CtVariable<?> var)->{
			if("field".equals(var.getSimpleName())) {
				if(var instanceof CtField) {
					//field scope is not supported
					return false;
				}
				CtElement[] real = var.map(new VariableScopeFunction()).list().toArray(new CtElement[0]);
				if(var instanceof CtLocalVariable) {
					assertArrayEquals(var.map(new LocalVariableScopeFunction()).list().toArray(new CtElement[0]), real);
				} else if(var instanceof CtParameter) {
					assertArrayEquals(var.map(new ParameterScopeFunction()).list().toArray(new CtElement[0]), real);
				} else if(var instanceof CtCatchVariable) {
					assertArrayEquals(var.map(new CatchVariableScopeFunction()).list().toArray(new CtElement[0]), real);
				} else {
					fail("Unexpected variable of type "+var.getClass().getName());
				}
				return true;
			}
			return false;
		}).list();
		assertFalse(list.isEmpty());
	}

	/**
	 * Contract: {@link FieldScopeFunction} matches the correct elements.
	 */
	@Test
	public void testFieldScopeFunction() {
		List<CtElement> real0 = modelClass.getFields().get(0).map(new FieldScopeFunction()).list();
        assertFalse(real0.isEmpty());
		List<CtElement> real1 = modelClass.getFields().get(1).map(new FieldScopeFunction()).list();
        assertFalse(real1.isEmpty());
	}

	@Test
	public void testLocalVariableReferenceDeclarationFunction() {
		modelClass.filterChildren((CtLocalVariableReference<?> varRef)->{
			if(isTestFieldName(varRef.getSimpleName())) {
				CtLocalVariable<?> var = varRef.getDeclaration();
				assertNotNull(var, "The declaration of variable " + varRef.getSimpleName() + " in " + getParentMethodName(varRef) + " on line " + var.getPosition().getLine() + " with value " + getVariableReferenceValue(varRef) + " was not found");
				assertEquals(getVariableReferenceValue(varRef), (int) getLiteralValue(var), "CtLocalVariableReference#getDeclaration returned wrong declaration in " + getParentMethodName(varRef));
			}
			return false;
		}).list();
	}

	private void checkVariableAccess(CtVariable<?> var, int value, CtConsumableFunction<?> query) {
		class Context {
			int realCount = 0;
			int expectedCount = 0;
		}
		try {
			Context context = new Context();
			//use provided reference returning function to found all occurrences of the variable
			var.map(query).forEach((CtVariableReference<?> fr)->{
				//check that all the found field references has expected right hand expression
				assertEquals(value, getVariableReferenceValue(fr));
				//count number of found references
				context.realCount++;
			});
			//use filterChildren to scan all field references in model and count the number of field references which has same value => expectedCount
			modelClass.filterChildren(new TypeFilter<>(CtVariableReference.class))
			.forEach((CtVariableReference varRef)->{
				if(!isTestFieldName(varRef.getSimpleName())) {
					return;
				}
				int refValue = getVariableReferenceValue(varRef);
				if(refValue<0) {
					fail("Variable reference has no value:\n"+varRef);
				}
				if(refValue==value) {
					context.expectedCount++;
				}
			});
			//check that both scans found same number of references
			assertEquals(context.expectedCount, context.realCount, "Number of references to field=" + value + " does not match");
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AssertionError("Test failed on " + getParentMethodName(var), e);
		}
	}

	private String getParentMethodName(CtElement ele) {
		CtMethod parentMethod = ele.getParent(CtMethod.class);
		CtMethod m;
		while(parentMethod!=null && (m=parentMethod.getParent(CtMethod.class))!=null) {
			parentMethod = m;
		}
		if(parentMethod!=null) {
			return parentMethod.getParent(CtType.class).getSimpleName()+"#"+parentMethod.getSimpleName();
		} else {
			return ele.getParent(CtType.class).getSimpleName()+"#annonymous block";
		}
	}

	private int getVariableReferenceValue(CtVariableReference<?> fr) {
		CtBinaryOperator binOp = fr.getParent(CtBinaryOperator.class);
		if(binOp==null) {
			return getCommentValue(fr);
		}
		return getLiteralValue(binOp.getRightHandOperand());
	}

	private Integer getLiteralValue(CtVariable<?> var) {
		CtExpression<?> exp = var.getDefaultExpression();
		if(exp!=null) {
			try {
				return getLiteralValue(exp);
			} catch (ClassCastException e) {
				
			}
		}
		if (var instanceof CtParameter) {
			CtParameter param = (CtParameter) var;
			CtExecutable<?> l_exec = param.getParent(CtExecutable.class);
			int l_argIdx = l_exec.getParameters().indexOf(param);
			assertTrue(l_argIdx>=0);
			if (l_exec instanceof CtLambda) {
				CtLambda<?> lambda = (CtLambda<?>) l_exec;
				CtLocalVariable<?> lamVar = (CtLocalVariable)lambda.getParent();
				CtLocalVariableReference<?> lamVarRef = lamVar.getParent().filterChildren((CtLocalVariableReference ref)->ref.getSimpleName().equals(lamVar.getSimpleName())).first();
				CtAbstractInvocation inv = lamVarRef.getParent(CtAbstractInvocation.class);
				return getLiteralValue((CtExpression<?>)inv.getArguments().get(l_argIdx));
			} else {
				CtExecutableReference<?> l_execRef = l_exec.getReference();
				List<CtAbstractInvocation<?>> list = l_exec.getFactory().Package().getRootPackage().filterChildren((CtAbstractInvocation inv)->{
					return inv.getExecutable().getExecutableDeclaration()==l_exec;
				}).list();
				CtAbstractInvocation inv = list.get(0);
				Integer firstValue = getLiteralValue((CtExpression<?>)inv.getArguments().get(l_argIdx));
				//check that all found method invocations are using same key
				list.forEach(inv2->{
					assertEquals(firstValue, getLiteralValue(inv2.getArguments().get(l_argIdx)));
				});
				return firstValue;
			}
		}
		return getCommentValue(var);
	}

	private int getCommentValue(CtElement e) {
		while(true) {
			if(e==null) {
				return -1;
			}
			if(e.getComments().isEmpty()==false) {
				break;
			}
			e = e.getParent();
		}
		if(e.getComments().size()==1) {
			String l_c = e.getComments().get(0).getContent();
			return Integer.parseInt(l_c);
		}
		return -1;
	}

	private Integer getLiteralValue(CtExpression<?> exp) {
		return ((CtLiteral<Integer>) exp).getValue();
	}

	private SourcePosition getPosition(CtElement e) {
		SourcePosition sp = e.getPosition();
		while(sp instanceof NoSourcePosition) {
			e = e.getParent();
			if(e==null) {
				break;
			}
			sp = e.getPosition();
		}
		return sp;
	}

	@Test
	public void testPotentialVariableAccessFromStaticMethod() throws Exception {
		Factory factory = ModelUtils.build(VariableReferencesFromStaticMethod.class);
		CtClass<?> clazz = factory.Class().get(VariableReferencesFromStaticMethod.class);
		CtMethod staticMethod = clazz.getMethodsByName("staticMethod").get(0);
		CtStatement stmt = staticMethod.getBody().getStatements().get(1);
		assertEquals("org.junit.jupiter.api.Assertions.assertTrue(field == 2)", stmt.toString());
		CtLocalVariableReference varRef = stmt.filterChildren(new TypeFilter<>(CtLocalVariableReference.class)).first();
		List<CtVariable> vars = varRef.map(new PotentialVariableDeclarationFunction()).list();
		assertEquals(1, vars.size(), "Found unexpected variable declaration.");
	}

    /**
     * Check support for enum values in {@link VariableReferenceFunction}.
     * Each enum value should have a single reference linking back to itself.
     */
    @Test
    public void testVariableReferenceFunctionWithEnum() throws Exception {
        Factory factory = ModelUtils.build(EnumValueReferences.class);
        CtClass<?> clazz = factory.Class().get(EnumValueReferences.class);
        CtEnum<?> testEnum = clazz.getNestedType("TestEnum");
        var values = testEnum.getEnumValues();
        assertEquals(2, values.size());
        values.forEach(ev -> {
            List<CtVariableReference<?>> refs = ev.map(new VariableReferenceFunction()).list();
            assertEquals(1, refs.size());
            assertEquals(ev.getReference(), refs.get(0));
        });
    }
}
