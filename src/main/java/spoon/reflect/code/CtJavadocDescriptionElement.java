package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

public interface CtJavadocDescriptionElement extends CtElement {
    @PropertyGetter(role = CtRole.JAVADOC_CONTENT)
    String getContent();
}
