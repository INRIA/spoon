package spoon.reflect.reference;

import spoon.reflect.declaration.CtElement;

public interface CtImport extends CtElement {
    <T extends CtImport> T setKindImport(ImportKind importKind);

    ImportKind getKindImport();

    CtReference getReference();

    <T extends CtImport> T setReference(CtReference reference);

    String getSimpleName();
}
