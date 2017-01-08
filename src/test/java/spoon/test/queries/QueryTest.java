package spoon.test.queries;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.query.FieldReferenceQuery;
import spoon.test.queries.testclasses.packageA.ClassA;


import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class QueryTest {

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
		launcher.addInputResource("./src/test/java/spoon/test/queries/testclasses");
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
	}

	@Test
	public void testFieldReferenceQuery() throws Exception {
		factory.Package().getRootPackage().filterChildren((CtField<?> f)->{
			checkFieldAccess(f);
			return false;
		}).list();
	}
	
	private void checkFieldAccess(CtField<?> field) {
		int value = getLiteralValue(field.getDefaultExpression());
		String fieldName = field.getSimpleName();
		class Context {
			int classCount = 0;
			int realCount = 0;
			int expectedCount = 0;
		}
		Context context = new Context();
		//use FieldReferenceQuery to found all occurences of the field
		field.map(new FieldReferenceQuery()).forEach((CtFieldReference<?> fr)->{
			//check that all the found field references has same right hand expression
			assertEquals(value, getFieldReferenceValue(fr));
			context.realCount++;
		});
		//use filterChildren to scan all field references and count the number of field references which has same value => expectedCount
		factory.Package().getRootPackage().filterChildren((CtElement e)->{
			if (e instanceof CtType) {
				context.classCount++;
			}
			if (e instanceof CtFieldReference) {
				CtFieldReference<?> fr = (CtFieldReference<?>) e;
				if(fieldName.equals(fr.getSimpleName()) && getFieldReferenceValue(fr)==value) {
					context.expectedCount++;
					/* debugging of problems
					if(value==3) {
						SourcePosition sp = getPosition(fr);
						System.out.println(sp);
						if(sp.getLine()==32){
							this.getClass();
						}
					}
					*/
				}
			}
			return false;
		}).list();
		//check that we are counting the correct set of classes. Update this number when you change the model
		assertEquals(5, context.classCount);
		//check that both scans found same number of references
		assertEquals("field="+value, context.expectedCount, context.realCount);
		System.out.println("field="+value+" found "+context.realCount+" referenes");
	}
	
	private int getFieldReferenceValue(CtFieldReference<?> fr) {
		return getLiteralValue(fr.getParent(CtBinaryOperator.class).getRightHandOperand());
	}

	private int getLiteralValue(CtExpression<?> exp) {
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
