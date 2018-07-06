package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtJavadocDescription;
import spoon.reflect.code.CtJavadocDescriptionElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

public class CtJavaDocDescriptionImpl extends CtElementImpl implements CtJavadocDescription {

    @MetamodelPropertyField(role = CtRole.JAVADOC_CONTENT)
    private List<CtJavadocDescriptionElement> javadocDescriptionElements = CtElementImpl.emptyList();

    @Override
    public List<CtJavadocDescriptionElement> getDescriptionElements() {
        return this.javadocDescriptionElements;
    }

    @Override
    public <T extends CtJavadocDescription> T setDescriptionElements(List<CtJavadocDescriptionElement> descriptionElements) {
        if (descriptionElements == null) {
            this.getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.JAVADOC_CONTENT, this.javadocDescriptionElements, new ArrayList(this.javadocDescriptionElements));
            this.javadocDescriptionElements = CtElementImpl.emptyList();
            return (T) this;
        }

        this.getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.JAVADOC_CONTENT, this.javadocDescriptionElements, new ArrayList(this.javadocDescriptionElements));
        this.javadocDescriptionElements.clear();

        for (CtJavadocDescriptionElement descriptionElement : descriptionElements) {
            this.addDescriptionElements(descriptionElement);
        }

        return (T) this;
    }

    @Override
    public <T extends CtJavadocDescription> T addDescriptionElements(CtJavadocDescriptionElement descriptionElement) {
        if (descriptionElement == null) {
            return (T) this;
        }
        if (this.javadocDescriptionElements == CtElementImpl.<CtJavadocDescriptionElement>emptyList()) {
            this.javadocDescriptionElements = new ArrayList<>();
        }

        this.getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.JAVADOC_CONTENT, this.javadocDescriptionElements, descriptionElement);
        this.javadocDescriptionElements.add(descriptionElement);

        return (T) this;
    }

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtJavadocDescription(this);
    }
}
