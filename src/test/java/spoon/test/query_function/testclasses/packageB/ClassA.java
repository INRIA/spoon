package spoon.test.query_function.testclasses.packageB;

import static org.junit.Assert.assertTrue;

import spoon.test.query_function.testclasses.packageA.ClassB;

/**
 * There are several fields/variables with name "field" in this class. Each of them has assigned different ID
 * This ...packageB.ClassA is a copy of structure from packageA.ClassA, including same name - to create ambiguity as much as possible to test variable searching algorithms 
 * - with new IDs
 * - with fields packageA.ClassA 
 */
public class ClassA {
	
	int field = 100;
	
	PrivateChildA c1 = new PrivateChildA(105);
	ProtectedChildA c2 = new ProtectedChildA(106);
	PublicChildA c3 = new PublicChildA(107);
	PackageProtectedChildA c4 = new PackageProtectedChildA(108);
	

	public ClassA() {
		int field = 101;
		assertTrue(field == 101);
		assertTrue(this.field == 100);
		
		assertTrue(this.c1.field == 103);
		assertTrue(this.c2.field == 109);
		assertTrue(this.c3.field == 110);
		assertTrue(this.c4.field == 111);
		
		this.c1.m(104);
		this.c2.m(112);
		this.c3.m(113);
		this.c4.m(114);
		
		m();
		
		ClassB classB = new ClassB();
		//these fields are not accessible
//		assertTrue(classB.field == 200);
//		assertTrue(classB.packageProtectedField == 201);
//		assertTrue(classB.protectedField == 203);
		assertTrue(classB.publicField == 204);
	}
	
	void m() {
		assertTrue(field == 100);
		assertTrue(this.field == 100);
		assertTrue(ClassA.this.field == 100);
		assertTrue(ClassA.this.c1.field == 103);
		assertTrue(ClassA.this.c2.field == 109);
		assertTrue(ClassA.this.c3.field == 110);
		assertTrue(ClassA.this.c4.field == 111);
		{
			int field = 115;
			assertTrue(field == 115);
		}
		try
		{
			int field = 116;
			assertTrue(field == 116);
			throw new IllegalArgumentException();
		}
		catch(IllegalArgumentException e) {
			int field = 117;
			assertTrue(field == 117);
		}
		catch(Exception /*121*/field) {
			//121
			field.getMessage();
		}
		while(true) {
			int field = 118;
			assertTrue(field == 118);
			break;
		}
		switch(7) {
		case 7:
			int field=119;
			assertTrue(field == 119);
			break;
		}
		{
			int field=122;
			assertTrue(field == 122);
			{
				assertTrue(field == 122);
				assertTrue(this.field == 100);
				assertTrue(ClassA.this.field == 100);
				assertTrue(ClassA.this.c1.field == 103);
				assertTrue(ClassA.this.c2.field == 109);
				assertTrue(ClassA.this.c3.field == 110);
				assertTrue(ClassA.this.c4.field == 111);
			}
			
		}
		int field=120;
		assertTrue(field == 120);
		{
			assertTrue(field == 120);
			assertTrue(this.field == 100);
			assertTrue(ClassA.this.field == 100);
			assertTrue(ClassA.this.c1.field == 103);
			assertTrue(ClassA.this.c2.field == 109);
			assertTrue(ClassA.this.c3.field == 110);
			assertTrue(ClassA.this.c4.field == 111);
		}
	}
	
	
	private class PrivateChildA extends ClassB {
		int field = 103;
		
		PrivateChildA(int field) {
			assertTrue(field == 105);
			assertTrue(this.field == 103);
			assertTrue(field == 105);
		}
		
		void m(int field) {
			assertTrue(field == 104);
			assertTrue(ClassA.this.field == 100);
			assertTrue(this.field == 103);
			assertTrue(ClassA.this.c1.field == 103);
			assertTrue(ClassA.this.c2.field == 109);
			assertTrue(ClassA.this.c3.field == 110);
			assertTrue(ClassA.this.c4.field == 111);
			
//			assertTrue(super.field == 200);
//			assertTrue(packageProtectedField == 201);
//			assertTrue(super.packageProtectedField == 201);
//			assertTrue(privateField == 202);
			assertTrue(protectedField == 203);
			assertTrue(super.protectedField == 203);
			assertTrue(publicField == 204);
			assertTrue(super.publicField == 204);
			
		}
	}
	
	protected class ProtectedChildA {
		int field = 109;
		ProtectedChildA(int field) {
			assertTrue(field == 106);
		}
		void m(int field) {
			assertTrue(field == 112);
			assertTrue(this.field == 109);
			assertTrue(ClassA.this.field == 100);
			assertTrue(ClassA.this.c1.field == 103);
			assertTrue(ClassA.this.c2.field == 109);
			assertTrue(ClassA.this.c3.field == 110);
			assertTrue(ClassA.this.c4.field == 111);
		}
	}
	
	public class PublicChildA {
		int field = 110;
		PublicChildA(int field) {
			assertTrue(field == 107);
		}
		void m(int field) {
			assertTrue(field == 113);
			assertTrue(this.field == 110);
			assertTrue(ClassA.this.field == 100);
			assertTrue(ClassA.this.c1.field == 103);
			assertTrue(ClassA.this.c2.field == 109);
			assertTrue(ClassA.this.c3.field == 110);
			assertTrue(ClassA.this.c4.field == 111);
		}
		
	}

	class PackageProtectedChildA {
		int field = 111;
		PackageProtectedChildA(int field) {
			assertTrue(field == 108);
		}
		void m(int field) {
			assertTrue(field == 114);
			assertTrue(this.field == 111);
			assertTrue(ClassA.this.field == 100);
			assertTrue(ClassA.this.c1.field == 103);
			assertTrue(ClassA.this.c2.field == 109);
			assertTrue(ClassA.this.c3.field == 110);
			assertTrue(ClassA.this.c4.field == 111);
		}
	}
}
