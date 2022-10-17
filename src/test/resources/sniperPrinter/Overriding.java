package sniperPrinter;

public class Overriding {
	public static class Super {
		void foo() {
			System. out. println(1+2
			);
		}
	}

	public static class Sub extends Super {
		@Override
		void foo() {
		}
	}
}
