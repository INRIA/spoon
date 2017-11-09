package spoon.support.reflect.reference;

import spoon.reflect.reference.CtImport;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.ImportKind;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtImportImpl extends CtElementImpl implements CtImport {
    private ImportKind importKind;
    private CtReference localReference;

    public CtImportImpl() {
        super();
    }

    @Override
    public <T extends CtImport> T setKindImport(ImportKind importKind) {
        this.importKind = importKind;
        return (T) this;
    }

    @Override
    public ImportKind getKindImport() {
        return this.importKind;
    }

    @Override
    public <T extends CtImport> T setReference(CtReference reference) {
        this.localReference = reference;
        return (T) this;
    }

    @Override
    public CtReference getReference() {
        return this.localReference;
    }

    @Override
    public String getSimpleName() {
        if (this.localReference == null) {
            return null;
        }

        return this.localReference.getSimpleName();
    }

    @Override
    public void accept(CtVisitor visitor) {

    }
}
