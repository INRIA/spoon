package spoon.test.fieldaccesses.testclasses;

public class Pozole {
	interface Interface1 {
	}
	class Cook {
		public Interface1 m() {
			return null;
		}
	}
	public Cook cook() {
		return new Cook() {
			@Override
			public Interface1 m() {
				return new Interface1() {
					private final Test test = new Test();
				};
			}

			class Test implements Interface1 {
			}
		};
	}
}
