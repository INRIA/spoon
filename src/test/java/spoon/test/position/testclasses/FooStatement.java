package spoon.test.position.testclasses;

public class FooStatement {

	private int field = 0;

	public void m(int parm1) {
		int field2 = m2(parm1);
		this.field = m2(parm1);
		if(parm1 > 2 && true) {
			switch (parm1) {
			case 1:
				return;
			default:
				parm1++;
			}
			int count = 0;
			for (int i =0; i< parm1; i++) {
				count ++;
			}
		}
		return;
	}

	public int m2(int parm1) {
		return parm1;
	}
}