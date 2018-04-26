/*
 * Top File
 * Line 2
 */
package spoon.test.comment.testclasses;

import java.util.ArrayList;

// comment class
public class InlineComment
	//this comment 1 belongs to class too
	extends ArrayList<String> //this comment 2 belongs to class too 
	{
	// Comment Field
	// comment field 2
	private int field // comment in field
			= 10; // after field

	// comment static block
	static {
		// comment inside static
	}

	// comment constructor
	public InlineComment() {
		// Comment in constructor
	}

	// comment method
	public void m() {
		// comment empty method block
	}

	public void m1() {
		// comment switch
		switch (1) {
		// before first case
		case 0:
			// comment case 0: empty case
		case 1:
			// comment case 1
			int i = 0;
		default:
			// comment default
		}
		// comment for
		for (int i = 0; i < 10; i++) {
			// comment for block
		}
		// comment if
		if (1 % 2 == 0) {
			// comment unary operator
			field++;
		}
		// comment constructor call
		new InlineComment();

		// comment invocation
		this.m();

		// comment local variable
		int i = 0,
				// comment multi assignments
				j = 2;
		// comment dowhile
		do {
			// comment in do while
			i++;
			// comment end do while
		} while (i < 10);

		// comment try
		try {
			// comment in try
			i++;
		}
		// between
		// try/catch
		catch (Exception e) {
			// comment in catch
		}
		// comment synchronized
		synchronized (this) {
			// comment in synchronized
		}

		Double dou =
			(i == 1) // comment after condition CtConditional
			?
				// comment before then CtConditional
				null    // comment after then CtConditional
			:
				// comment before else CtConditional
				new Double(j / (double) (i - 1)); // comment after else CtConditional
		int[] arr = new int[] {
			// comment before array value
			1, // comment after array value
			2,
			3
				// last comment at the end of array
		};

		String m = "" +
				"" + // comment multi line string
				""; // after statement

		boolean c = (i == 1) ?
				// comment before then boolean CtConditional
				i == 1    // comment after then boolean CtConditional
				:
				i == 2;
		// comment return
		return;
	}

	public // comment before type
	void // comment before name
	m2 // comment before parameters
	(// comment before type parameter
	int // comment before name parameter
	i
	// comment after parameter
	)
	// comment before throws
	throws
			// comment before exception 1
			Exception,
			// comment before exception 2
			Error
	// comment before block
	{}

	public void m3() {
		if (true){
			// comment empty if
		}
		// comment before else
		else {
			// comment empty else
		}
		// comment if without block
		if (true)
			// comment then if without block
			m3();
		// comment else without block
		else
			// comment else if without block
			m3();
	}
	// comment after class
}

/*
 * Bottom File
 */