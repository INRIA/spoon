package spoon.test.annotation.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
public @interface BoundNumber {
	ByteOrder byteOrder = ByteOrder.LittleEndian;

	enum ByteOrder {
		LittleEndian;
	}
}
