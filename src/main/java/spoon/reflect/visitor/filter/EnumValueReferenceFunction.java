package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtEnumValue;

/**
 * This query expects a {@link CtEnumValue} as input
 * and returns all {@link spoon.reflect.reference.CtFieldReference}s, which refer to this input.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtEnumValue ev = ...;
 * ev
 *   .map(new EnumValueReferenceFunction())
 *   .forEach((CtEnumValueReference ref)->...process references...);
 * }
 * </pre>
 */
public class EnumValueReferenceFunction extends FieldReferenceFunction {
    public EnumValueReferenceFunction() {
        super();
    }

    public EnumValueReferenceFunction(CtEnumValue<?> element) {
        super(element);
    }
}
