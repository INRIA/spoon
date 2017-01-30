package spoon.test.query_function;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.filter.CatchVariableReferenceFunction;
import spoon.reflect.visitor.filter.FieldReferenceFunction;
import spoon.reflect.visitor.filter.LocalVariableReferenceFunction;
import spoon.reflect.visitor.filter.ParameterReferenceFunction;
import spoon.test.query_function.testclasses.ClassC;
import spoon.test.query_function.testclasses.packageA.ClassA;
import spoon.test.query_function.testclasses.packageA.ClassB;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QueryTest {
	
	static int countOfModelClasses = 12;

	Factory factory;
	CtClass<?> classA;
	CtClass<?> classA_privateChild;
	CtClass<?> classA_protectedChild;
	CtClass<?> classA_publicChild;
	CtClass<?> classA_packProtChild;

	@Before
	public void setup() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.addInputResource("./src/test/java/spoon/test/query_function/testclasses");
		launcher.run();
		factory = launcher.getFactory();
		classA = factory.Class().get(ClassA.class);
		classA_privateChild = factory.Class().get(ClassA.class.getName()+"$PrivateChildA");
		classA_protectedChild = factory.Class().get(ClassA.class.getName()+"$ProtectedChildA");
		classA_publicChild = factory.Class().get(ClassA.class.getName()+"$PublicChildA");
		classA_packProtChild = factory.Class().get(ClassA.class.getName()+"$PackageProtectedChildA");
	}
	
	@Test
	public void testCheckModelConsistency() {
		//this constructor creates all nested classes, creates all fields and calls all methods and checks that each field occurrence has assigned correct literal
		new ClassA();
		new ClassB();
		new spoon.test.query_function.testclasses.packageB.ClassA();
		new ClassC();
		
		//1) search for all variable declarations with name "field" 
		//2) check that each of them is using different identification value
		class Context {
			int classCount = 0;
			Map<Integer, CtElement> unique = new HashMap<>();
		}
		Context context = new Context();
		
		factory.Package().getRootPackage().filterChildren((CtElement e)->{
			if (e instanceof CtType) {
				context.classCount++;
			}
			if (e instanceof CtVariable) {
				CtVariable<?> var = (CtVariable<?>) e;
				if("field".equals(var.getSimpleName())==false) {
					return false;
				}
				//check only these variables whose name is "field"
				Integer val = getLiteralValue(var);
				CtElement ambiquous = context.unique.put(val, var);
				if(ambiquous!=null) {
					fail("Two variables ["+ambiquous.toString()+","+var.toString()+"] has same value");
				}
			}
			return false;
		}).list();
		assertEquals("Update count of model classes:", countOfModelClasses, context.classCount);
	}

	@Test
	public void testParameterReferenceFunction() throws Exception {
		//visits all the CtParameter elements whose name is "field" and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
		factory.Package().getRootPackage().filterChildren((CtParameter<?> param)->{
			if(param.getSimpleName().equals("field")) {
				int value = getLiteralValue(param);
				checkVariableAccess(param, value, new ParameterReferenceFunction());
			}
			return false;
		}).list();
	}
  
	@Test
	public void testCatchVariableReferenceFunction() throws Exception {
		//visits all the CtCatchVariable elements whose name is "field" and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
		factory.Package().getRootPackage().filterChildren((CtCatchVariable<?> var)->{
			if(var.getSimpleName().equals("field")) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new CatchVariableReferenceFunction());
			}
			return false;
		}).list();
	}

	@Test
	public void testLocalVariableReferenceFunction() throws Exception {
		//visits all the CtLocalVariable elements whose name is "field" and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
		factory.Package().getRootPackage().filterChildren((CtLocalVariable<?> var)->{
			if(var.getSimpleName().equals("field")) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new LocalVariableReferenceFunction());
			}
			return false;
		}).list();
	}	

	@Test
	public void testFieldReferenceFunction() throws Exception {
		//visits all the CtField elements whose name is "field" and search for all their references
		//The test detects whether found references are correct by these two checks:
		//1) the each found reference is on the left side of binary operator and on the right side there is unique reference identification number. Like: (field == 7)
		//2) the model is searched for all variable references which has same identification number and counts them
		//Then it checks that counted number of references and found number of references is same 
		factory.Package().getRootPackage().filterChildren((CtField<?> var)->{
			if(var.getSimpleName().equals("field")) {
				int value = getLiteralValue(var);
				checkVariableAccess(var, value, new FieldReferenceFunction());
			}
			return false;
		}).list();
	}

	private void checkVariableAccess(CtVariable<?> var, int value, CtConsumableFunction<?> query) {
		class Context {
			int classCount = 0;
			int realCount = 0;
			int expectedCount = 0;
			Set<String> unique = new HashSet<>();
		}
		Context context = new Context();
		//use FieldReferenceQuery to found all occurences of the field
		var.map(query).forEach((CtVariableReference<?> fr)->{
			//check that all the found field references has same right hand expression
			assertEquals(value, getVariableReferenceValue(fr));
			context.realCount++;
		});
		//use filterChildren to scan all field references and count the number of field references which has same value => expectedCount
		factory.Package().getRootPackage().filterChildren((CtElement e)->{
			if (e instanceof CtType) {
				context.classCount++;
			}
			if (e instanceof CtVariableReference) {
				CtVariableReference<?> fr = (CtVariableReference<?>) e;
				if("field".equals(fr.getSimpleName())==false) {
					return false;
				}
				int refValue = getVariableReferenceValue(fr);
				if(refValue<0) {
					fail();
				}
				if(refValue==value) {
					context.expectedCount++;
				}
			}
			return false;
		}).list();
		//check that we are counting the correct set of classes. Update this number when you change the model
		//check that both scans found same number of references
		assertEquals("field="+value, context.expectedCount, context.realCount);
		System.out.println("field="+value+" found "+context.realCount+" referenes");
	}
	
	private int getVariableReferenceValue(CtVariableReference<?> fr) {
		CtBinaryOperator binOp = fr.getParent(CtBinaryOperator.class);
		if(binOp==null) {
			if (fr instanceof CtCatchVariableReference) {
				return getCommentValue(fr);
			}
			return -1;
		}
		return getLiteralValue(binOp.getRightHandOperand());
	}

	private Integer getLiteralValue(CtVariable<?> var) {
		CtExpression<?> exp = var.getDefaultExpression();
		if(exp!=null) {
			return getLiteralValue(exp);
		}
		if (var instanceof CtParameter) {
			CtParameter param = (CtParameter) var;
			CtExecutable<?> l_exec = param.getParent(CtExecutable.class);
			int l_argIdx = l_exec.getParameters().indexOf(param);
			assertTrue(l_argIdx>=0);
			CtExecutableReference<?> l_execRef = l_exec.getReference();
			List<CtAbstractInvocation<?>> list = l_exec.getFactory().Package().getRootPackage().filterChildren((CtAbstractInvocation inv)->{
				return inv.getExecutable().equals(l_execRef);
			}).list();
			assertEquals(1, list.size());
			CtAbstractInvocation inv = list.get(0);
			return getLiteralValue((CtExpression<?>)inv.getArguments().get(l_argIdx));
		}
		if(var instanceof CtCatchVariable) {
			return getCommentValue(var);
		}
		throw new AssertionError("Unexpected variable "+var.toString());
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
