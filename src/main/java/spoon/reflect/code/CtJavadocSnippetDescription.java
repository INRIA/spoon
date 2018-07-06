package spoon.reflect.code;

import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;

public interface CtJavadocSnippetDescription extends CtJavadocDescriptionElement {

    @PropertySetter(role = CtRole.JAVADOC_CONTENT)
    <T extends CtJavadocSnippetDescription> T setContent(String content);
}
