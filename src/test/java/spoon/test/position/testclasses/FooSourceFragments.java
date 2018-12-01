/**
 * Javadoc at top of file
 */
/* comment before package declaration*/
package spoon.test.position.testclasses;

/*
 * Comment before import
 */
import java.lang.Deprecated;

import java.lang.Class;

/*
 * Comment before type
 */
public class FooSourceFragments {
	void m1(int x) {
		if(x > 0){this.getClass();}else{/*empty*/}
	}
	void m2(int x) {
		/*c0*/ if  /*c1*/	( //c2
				x > 0 /*c3*/ ) /*c4*/ { 
			this.getClass();
		} /*c5*/ else /*c6*/ {
			/*empty*/
		} /*c7*/
	}
	/**
	 * c0
	 */
	public
	@Deprecated //c1 ends with tab and space	 
	static /*c2*/ <T, U> T m3(U param, @Deprecated int p2) {
		return null;
	}
	
	void m4() {
		label: while(true);
	}

	void m5(double f) {
		f = 7.2;
	}
	//after last type member
}

//comment at the end of file