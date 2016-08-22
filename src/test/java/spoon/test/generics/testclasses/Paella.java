package spoon.test.generics.testclasses;

import java.util.Comparator;

public class Paella<T extends Comparator<? extends String>> {
	public <T extends Comparator<? extends String>> Paella() {
	}

	public <T extends Comparator<? extends String>> T make() {
		return null;
	}
}
