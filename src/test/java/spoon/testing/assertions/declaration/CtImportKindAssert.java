package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtImportKind;
public class CtImportKindAssert extends AbstractAssert<CtImportKindAssert, CtImportKind> {
    CtImportKindAssert(CtImportKind actual) {
        super(actual, CtImportKindAssert.class);
    }
}