package spoon.test.query_function;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.filter.CatchVariableReferenceFunction;
import spoon.reflect.visitor.filter.CatchVariableScopeFunction;
import spoon.reflect.visitor.filter.LocalVariableReferenceFunction;
import spoon.reflect.visitor.filter.LocalVariableScopeFunction;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.ParameterReferenceFunction;
import spoon.reflect.visitor.filter.ParameterScopeFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.visitor.filter.VariableReferenceFunction;
import spoon.reflect.visitor.filter.VariableScopeFunction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class VariableReferencesTest {
	CtClass<?> modelClass;
	
	@Before
	public void setup() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.addInputResource("./src/test/java/spoon/test/query_function/VariableReferencesModelTest.java");
		launcher.run();
		Factory factory = launcher.getFactory();
		modelClass = factory.Class().get(VariableReferencesModelTest.class);
	}

	@Test
	public void testCheckModelConsistency() throws Exception {
		
		//1) search for all variable declarations with name isTestFieldName(name)==true 
		//2) check that each of them is using different identification value
		class Context {
			Map<Integer, CtElement> unique = new HashMap<>();
			int maxKey = 0;
			void checkKey(int key, CtElement ele) {
				CtElement ambiquous = unique.put(key, ele);
				if(ambiquous!=null) {
					fail("Two variables ["+ambiquous.toString()+" in "+getParentMethodName(ambiquous)+","+ele.toString()+" in "+getParentMethodName(ele)+"] has same value");
				}
				maxKey = Math.max(maxKey, key);
			}
		}
		Context context = new Context();
		
		modelClass.filterChildren((CtElement e)->{
			if (e instanceof CtVariable) {
				CtVariable<?> var = (CtVariable<?>) e;
				if(isTestFieldName(var.getSimpleName())==false) {
					return false;
				}
				//check only these variables whose name is isTestFieldName(name)==true
				Integer val = getLiteralValue(var);
//				System.out.println("key = "+val+" - "+var.toString());
				context.checkKey(val, var);
			}
			return false;
		}).list();
//		System.out.println("Next available key is: "+(context.maxKey+1));
		assertTrue(context.unique.size()>0);
		assertEquals("Only these keys were found: "+context.unique.keySet(), context.maxKey, context.unique.size());
		assertEquals("AllLocalVars#maxValue must be equal to maximum value number ", (int)getLiteralValue((CtVariable)modelClass.filterChildren(new NameFilter("maxValue")).first()), context.maxKey);
	}
	
	@Test
	public void testCatchVariableReferenceFunction() throws Exception {
		//visits all the CtCatchVariable elements whose name is isTestFieldName(name)==true and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
		modelClass.filterChildren((CtCatchVariable<?> var)->{
			if(isTestFieldName(var.getSimpleName())) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new CatchVariableReferenceFunction());
			}
			return false;
		}).list();
	}

	@Test
	public void testLocalVariableReferenceFunction() throws Exception {
		//visits all the CtLocalVariable elements whose name is isTestFieldName(name)==true and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
		modelClass.filterChildren((CtLocalVariable<?> var)->{
			if(isTestFieldName(var.getSimpleName())) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new LocalVariableReferenceFunction());
			}
			return false;
		}).list();
	}	

	@Test
	public void testParameterReferenceFunction() throws Exception {
		//visits all the CtParameter elements whose name is isTestFieldName(name)==true and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
		modelClass.filterChildren((CtParameter<?> var)->{
			if(isTestFieldName(var.getSimpleName())) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new ParameterReferenceFunction());
			}
			return false;
		}).list();
	}	

	@Test
	public void testVariableReferenceFunction() throws Exception {
		//visits all the CtVariable elements whose name is isTestFieldName(name)==true and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
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

	@Test
	public void testVariableScopeFunction() throws Exception {
		//visits all the CtVariable elements whose name is "field" and search for all elements in their scopes
		//Comparing with the result found by basic functions
		List list = modelClass.filterChildren((CtVariable<?> var)->{
			if(var.getSimpleName().equals("field")) {
				if(var instanceof CtField) {
					//field scope is not supported
					return false;
				}
				CtElement[] real = var.map(new VariableScopeFunction()).list().toArray(new CtElement[0]);
				if(var instanceof CtLocalVariable) {
					assertArrayEquals(var.map(new LocalVariableScopeFunction()).list().toArray(new CtElement[0]), real);
				} else if(var instanceof CtField) {
					//assertArrayEquals(var.map(new FieldScopeFunction()).list().toArray(new CtElement[0]), real);
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
		assertTrue(list.size()>0);
	}
	
	@Test
	public void testLocalVariableReferenceDeclarationFunction() throws Exception {
		modelClass.filterChildren((CtLocalVariableReference<?> varRef)->{
			if(isTestFieldName(varRef.getSimpleName())) {
				CtLocalVariable<?> var = varRef.getDeclaration();
				assertNotNull("The declaration of variable "+varRef.getSimpleName()+" in "+getParentMethodName(varRef)+" on line "+varRef.getPosition().getLine()+" with value "+getVariableReferenceValue(varRef)+" was not found", var);
				assertEquals("CtLocalVariableReference#getDeclaration returned wrong declaration in "+getParentMethodName(varRef), getVariableReferenceValue(varRef), (int)getLiteralValue(var));
			}
			return false;
		}).list();
	}
	

	private void checkVariableAccess(CtVariable<?> var, int value, CtConsumableFunction<?> query) {
		class Context {
			int realCount = 0;
			int expectedCount = 0;
			Set<String> unique = new HashSet<>();
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
				if(isTestFieldName(varRef.getSimpleName())==false) {
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
			assertEquals("Number of references to field="+value+" does not match", context.expectedCount, context.realCount);
//			System.out.println("field="+value+" found "+context.realCount+" referenes");
			
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
//					return inv.getExecutable().equals(l_execRef);
					return inv.getExecutable().getExecutableDeclaration()==l_exec;
				}).list();
				CtAbstractInvocation inv = list.get(0);
				Integer firstValue = getLiteralValue((CtExpression<?>)inv.getArguments().get(l_argIdx));
				//check that all found method invocations are using same key
				list.forEach(inv2->{
					assertEquals(firstValue, getLiteralValue((CtExpression<?>)inv2.getArguments().get(l_argIdx)));
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

}
