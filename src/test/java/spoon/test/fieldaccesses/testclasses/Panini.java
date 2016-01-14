package spoon.test.fieldaccesses.testclasses;

public class Panini {
	int i;

	public Sandwich m() {
		return new Sandwich() {
			Ingredient ingredient;
			@Override
			int m() {
				return ingredient.next;
			}
		};
	}

	public void make() {
		i++;
		++i;
	}

	abstract class Sandwich {
		abstract int m();
	}

	class Ingredient {
		int next;
	}
}
