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

	public void prepare() {
		i += 0;
		int j = 0;
		j += 0;
		int[] array = {};
		array[0] += 0;
	}

	abstract class Sandwich {
		abstract int m();
	}

	class Ingredient {
		int next;
	}
}
