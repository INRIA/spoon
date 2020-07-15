package spoon.test.statementComment.testclasses;
public class AllStmtExtensions{
	public AllStmtExtensions() {}
	void m1() {
		assert 1 == 5;
		int r = 10;
		r = 20;
		String s = "This is a new String!";
		{
			int j = 10;
		}
	}
	void m2() {
		
	}
	void m3() {
		try {
			throw new Exception();
		}catch(Exception e) {
			System.out.println(e);
		}
		int r = 30;
		r++;
	}
	void m4() {
		if (5 > 6) {
			System.out.println("Impossible!");
		} else {
			System.out.println("Seems right...");
		}
	}
}