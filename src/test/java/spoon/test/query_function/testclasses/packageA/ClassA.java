package spoon.test.query_function.testclasses.packageA;

import static org.junit.Assert.assertTrue;

import spoon.test.query_function.testclasses.ClassC;

/**
 * There are several fields/variables with name "field" in this class. Each of them has assigned different ID
 */
public class ClassA {
	
	int field = 0;
	
	int packageProtectedField = 501;
	private int privateField = 502;
	protected int protectedField = 503;
	public int publicField = 504;
	
	
	PrivateChildA c1 = new PrivateChildA(5);
	ProtectedChildA c2 = new ProtectedChildA(6);
	PublicChildA c3 = new PublicChildA(7);
	PackageProtectedChildA c4 = new PackageProtectedChildA(8);
	

	public ClassA() {
		int field = 1;
		assertTrue(field == 1);
		assertTrue(packageProtectedField == 501);
		assertTrue(privateField == 502);
		assertTrue(protectedField == 503);
		assertTrue(publicField == 504);

		assertTrue(this.field == 0);
		
		assertTrue(this.c1.field == 3);
		assertTrue(this.c2.field == 9);
		assertTrue(this.c3.field == 10);
		assertTrue(this.c4.field == 11);
		
		this.c1.m(4);
		this.c2.m(12);
		this.c3.m(13);
		this.c4.m(14);
		
		m();
		
		ClassB classB = new ClassB();
		assertTrue(classB.field == 200);
		assertTrue(classB.packageProtectedField == 201);
		assertTrue(classB.protectedField == 203);
		assertTrue(classB.publicField == 204);

		ClassC classC = new ClassC();
//		assertTrue(classC.field == 300);
//		assertTrue(classC.packageProtectedField == 301);
//		assertTrue(classC.protectedField == 303);
		assertTrue(classC.publicField == 304);
		
		assertTrue(packageProtectedField == 501);
		assertTrue(ClassA.this.packageProtectedField == 501);
		assertTrue(privateField == 502);
		assertTrue(ClassA.this.privateField == 502);
		assertTrue(protectedField == 503);
		assertTrue(ClassA.this.protectedField == 503);
		assertTrue(publicField == 504);
		assertTrue(ClassA.this.publicField == 504);
	}
	
	void m() {
		assertTrue(field == 0);
		assertTrue(this.field == 0);
		assertTrue(ClassA.this.field == 0);
		assertTrue(ClassA.this.c1.field == 3);
		assertTrue(ClassA.this.c2.field == 9);
		assertTrue(ClassA.this.c3.field == 10);
		assertTrue(ClassA.this.c4.field == 11);
		{
			int field = 15;
			assertTrue(field == 15);
		}
		try
		{
			int field = 16;
			assertTrue(field == 16);
			throw new IllegalArgumentException();
		}
		catch(IllegalArgumentException e) {
			int field = 17;
			assertTrue(field == 17);
		}
		catch(Exception /*21*/field) {
			//21
			field.getMessage();
		}
		while(true) {
			int field = 18;
			assertTrue(field == 18);
			break;
		}
		switch(7) {
		case 7:
			int field=19;
			assertTrue(field == 19);
			break;
		}
		{
			int field=22;
			assertTrue(field == 22);
			{
				assertTrue(field == 22);
				assertTrue(this.field == 0);
				assertTrue(ClassA.this.field == 0);
				assertTrue(ClassA.this.c1.field == 3);
				assertTrue(ClassA.this.c2.field == 9);
				assertTrue(ClassA.this.c3.field == 10);
				assertTrue(ClassA.this.c4.field == 11);
			}
			
		}
		int field=20;
		assertTrue(field == 20);
		{
			assertTrue(field == 20);
			assertTrue(this.field == 0);
			assertTrue(ClassA.this.field == 0);
			assertTrue(ClassA.this.c1.field == 3);
			assertTrue(ClassA.this.c2.field == 9);
			assertTrue(ClassA.this.c3.field == 10);
			assertTrue(ClassA.this.c4.field == 11);
		}
	}
	
	
	private class PrivateChildA extends ClassB {
		int field = 3;
		
		PrivateChildA(int field) {
			assertTrue(field == 5);
			assertTrue(this.field == 3);
			assertTrue(field == 5);
		}
		
		void m(int field) {
			assertTrue(field == 4);
			assertTrue(ClassA.this.field == 0);
			assertTrue(this.field == 3);
			assertTrue(ClassA.this.c1.field == 3);
			assertTrue(ClassA.this.c2.field == 9);
			assertTrue(ClassA.this.c3.field == 10);
			assertTrue(ClassA.this.c4.field == 11);
			
			assertTrue(super.field == 200);
			assertTrue(packageProtectedField == 201);
			assertTrue(super.packageProtectedField == 201);
//			assertTrue(privateField == 202);
			assertTrue(protectedField == 203);
			assertTrue(super.protectedField == 203);
			assertTrue(publicField == 204);
			assertTrue(super.publicField == 204);
			
//			assertTrue(packageProtectedField == 501);
			assertTrue(ClassA.this.packageProtectedField == 501);
			assertTrue(privateField == 502);
			assertTrue(ClassA.this.privateField == 502);
//			assertTrue(protectedField == 503);
			assertTrue(ClassA.this.protectedField == 503);
//			assertTrue(publicField == 504);
			assertTrue(ClassA.this.publicField == 504);

		}
	}
	
	protected class ProtectedChildA extends ClassC {
		int field = 9;
		ProtectedChildA(int field) {
			assertTrue(field == 6);
		}
		void m(int field) {
			assertTrue(field == 12);
			assertTrue(this.field == 9);
			assertTrue(ClassA.this.field == 0);
			assertTrue(ClassA.this.c1.field == 3);
			assertTrue(ClassA.this.c2.field == 9);
			assertTrue(ClassA.this.c3.field == 10);
			assertTrue(ClassA.this.c4.field == 11);

//			assertTrue(super.field == 300);
//			assertTrue(packageProtectedField == 301);
//			assertTrue(super.packageProtectedField == 301);
//			assertTrue(privateField == 302);
			assertTrue(protectedField == 303);
			assertTrue(super.protectedField == 303);
			assertTrue(publicField == 304);
			assertTrue(super.publicField == 304);
			
			assertTrue(packageProtectedField == 501);
			assertTrue(ClassA.this.packageProtectedField == 501);
			assertTrue(privateField == 502);
			assertTrue(ClassA.this.privateField == 502);
			assertTrue(ClassA.this.protectedField == 503);
			assertTrue(ClassA.this.publicField == 504);
		}
	}
	
	public class PublicChildA {
		int field = 10;
		
		int packageProtectedField = 401;
		private int privateField = 402;
		protected int protectedField = 403;
		public int publicField = 404;
		
		PublicChildA(int field) {
			assertTrue(field == 7);
		}
		void m(int field) {
			assertTrue(field == 13);
			assertTrue(packageProtectedField == 401);
			assertTrue(privateField == 402);
			assertTrue(protectedField == 403);
			assertTrue(publicField == 404);
			assertTrue(this.field == 10);
			assertTrue(ClassA.this.field == 0);
			assertTrue(ClassA.this.c1.field == 3);
			assertTrue(ClassA.this.c2.field == 9);
			assertTrue(ClassA.this.c3.field == 10);
			assertTrue(ClassA.this.c4.field == 11);
			
//			assertTrue(packageProtectedField == 501);
			assertTrue(ClassA.this.packageProtectedField == 501);
//			assertTrue(privateField == 502);
			assertTrue(ClassA.this.privateField == 502);
//			assertTrue(protectedField == 503);
			assertTrue(ClassA.this.protectedField == 503);
//			assertTrue(publicField == 504);
			assertTrue(ClassA.this.publicField == 504);
		}
		
	}

	class PackageProtectedChildA {
		int field = 11;
		PackageProtectedChildA(int field) {
			assertTrue(field == 8);
		}
		void m(int field) {
			assertTrue(field == 14);
			assertTrue(this.field == 11);
			assertTrue(ClassA.this.field == 0);
			assertTrue(ClassA.this.c1.field == 3);
			assertTrue(ClassA.this.c2.field == 9);
			assertTrue(ClassA.this.c3.field == 10);
			assertTrue(ClassA.this.c4.field == 11);
		}
	}
}
