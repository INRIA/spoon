package spoon.test.ctClass.testclasses;

public class Pozole {
	public void m() {
		class Cook {
			public Cook() {
			}

			public void m() {
				final Class<Cook> cookClass = Cook.class;
			}
		}
		new Cook();
	}
}
