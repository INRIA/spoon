package spoon.test.position.testclasses;

import java.util.List;

public interface TypeParameter {
	<T extends List<?>> void m();
}