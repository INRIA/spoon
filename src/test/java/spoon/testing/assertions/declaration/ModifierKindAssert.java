package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.ModifierKind;
public class ModifierKindAssert extends AbstractAssert<ModifierKindAssert, ModifierKind> {
    ModifierKindAssert(ModifierKind actual) {
        super(actual, ModifierKindAssert.class);
    }
}