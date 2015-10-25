package spoon.test.fieldaccesses.testclasses;

public class Panini {

	public Sandwich m() {
		return new Sandwich() {
			Ingredient ingredient;
			@Override
			int m() {
				return ingredient.next;
			}
		};
	}

	abstract class Sandwich {
		abstract int m();
	}

	class Ingredient {
		int next;
	}
}
