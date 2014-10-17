package spoon.test.intercession.insertBefore;

public class InsertBeforeExample2 {

	public InsertBeforeExample2(boolean par, int par2) {
		if (par)
			while (par) {
				par = par2 % 3 == 0;
				par2 *= 2;
			}
	}
}
