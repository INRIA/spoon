package spoon.reflect.code;

import spoon.reflect.declaration.CtElement;

import java.util.List;

public interface CtJavadocDescription extends CtElement {
    List<CtJavadocDescriptionElement> getDescriptionElements();

    <T extends CtJavadocDescription> T setDescriptionElements(List<CtJavadocDescriptionElement> descriptionElements);
}
