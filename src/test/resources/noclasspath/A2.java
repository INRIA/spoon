public class A2 {

	int field;

	public void b(int param) {
		IntUnaryOperator f1 = x -> x;
		IntBinaryOperator f2 = (a, b) -> a + b;
		try {
			System.out.println(f1.applyAsInt(f2.applyAsInt(field, param)));
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void c(int param) {
		c(param);
	}
}