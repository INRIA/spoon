package spoon.test.query_function.testclasses.packageA;

import static org.junit.Assert.assertTrue;

/**
 * There are several fields/variables with name "field" in this class. Each of them has assigned different ID
 */
public class ClassA {
	
	int field = 0;
	
	PrivateChildA c1 = new PrivateChildA(5);
	ProtectedChildA c2 = new ProtectedChildA(6);
	PublicChildA c3 = new PublicChildA(7);
	PackageProtectedChildA c4 = new PackageProtectedChildA(8);
	

	public ClassA() {
		int field = 1;
		assertTrue(field == 1);
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
	
	
	private class PrivateChildA {
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
		}
	}
	
	protected class ProtectedChildA {
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
		}
	}
	
	public class PublicChildA {
		int field = 10;
		PublicChildA(int field) {
			assertTrue(field == 7);
		}
		void m(int field) {
			assertTrue(field == 13);
			assertTrue(this.field == 10);
			assertTrue(ClassA.this.field == 0);
			assertTrue(ClassA.this.c1.field == 3);
			assertTrue(ClassA.this.c2.field == 9);
			assertTrue(ClassA.this.c3.field == 10);
			assertTrue(ClassA.this.c4.field == 11);
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
