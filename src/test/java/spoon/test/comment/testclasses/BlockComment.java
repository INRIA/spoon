/*
 * Top File
 */
package spoon.test.comment.testclasses;

/* comment class */
/**
 * JavaDoc class
 */
public class BlockComment {
	/* Comment Field */
	private int field /* comment in field */ = 10;

	/* comment static block */
	static {
		/* comment inside static */
	}

	/* comment constructor */
	public BlockComment() {
		/* Comment in constructor */
	}

	/* comment method */
	public void m() {
		/* comment empty method block */
	}

	public void m1() {
		/* comment switch */
		switch (1) {
		/* before first case */
		case 0:
			/* comment case 0: empty case */
		case 1:
			/* comment case 1 */
			int i = 0;
		default:
			/* comment default */
		}
		/* comment for */
		for (int i = 0; i < 10; i++) {
			/* comment for block */
		}
		/* comment if */
		if (1 % 2 == 0) {
			/* comment unary operator */
			field++;
		}
		/* comment constructor call */
		new BlockComment();

		/* comment invocation */
		this.m();

		/* comment local variable */
		int i = 0, /* comment multi assignments */ j = 2;
		/* comment dowhile */
		do {
			/* comment in do while */
			i++;
			/* comment end do while */
		} while (i < 10);

		/* comment try */
		try {
			/* comment in try */
			i++;
		} catch (Exception e) {
			/* comment in catch */
		}
		/* comment synchronized */
		synchronized (this) {
			/* comment in synchronized */
		}

		/* comment return */
		return;
	}

	public /* comment before type */ void /* comment before name */ m2 /* comment before parameters */ (/* comment before type parameter */ int /* comment before name parameter */ i /* comment after parameter */ ) /* comment before throws */ throws /* comment before exception 1 */ Exception, /* comment before exception 2 */ Error /* comment before block */  {

	}

	public void m3() {
		if (true){
			/* comment empty if */
		}
		/* comment before else */
		else {
			/* comment empty else */
		}
		/* comment if without block */ 
		if (true)
			/* comment then if without block */
			m3();
		/* comment else without block */
		else /* comment else if without block */
			m3();
	}
	/* comment after class */
}

/*
 * Bottom File
 */