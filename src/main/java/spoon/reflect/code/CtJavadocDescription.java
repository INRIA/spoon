package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

import java.util.List;

public interface CtJavadocDescription extends CtElement {
    @PropertyGetter(role = CtRole.JAVADOC_CONTENT)
    List<CtJavadocDescriptionElement> getDescriptionElements();

    @PropertySetter(role = CtRole.JAVADOC_CONTENT)
    <T extends CtJavadocDescription> T setDescriptionElements(List<CtJavadocDescriptionElement> descriptionElements);

    @PropertySetter(role = CtRole.JAVADOC_CONTENT)
    <T extends CtJavadocDescription> T addDescriptionElements(CtJavadocDescriptionElement descriptionElement);
}
