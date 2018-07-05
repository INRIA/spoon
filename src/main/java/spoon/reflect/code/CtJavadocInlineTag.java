package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.COMMENT_CONTENT;
import static spoon.reflect.path.CtRole.DOCUMENTATION_TYPE;

public interface CtJavadocInlineTag extends CtJavadocDescriptionElement {

    /**
     * Pattern used to detect inline tags
     */
    String JAVADOC_INLINE_PATTERN = "\\{@(?<name>\\S+) (?<ref>\\S+)\\}";

    /**
     * Define the possible type for a tag
     */
    enum TagType {
        LINK
    }

    /**
     * The type of the tag
     * @return the type of the tag
     */
    @PropertyGetter(role = DOCUMENTATION_TYPE)
    TagType getType();

    /**
     * Define the type of the tag
     * @param type the type name
     */
    @PropertySetter(role = DOCUMENTATION_TYPE)
    <E extends CtJavaDocTag> E setType(String type);

    /**
     * Define the type of the tag
     * @param type the new type
     */
    @PropertySetter(role = DOCUMENTATION_TYPE)
    <E extends CtJavaDocTag> E setType(TagType type);

    /**
     * Get the content of the tag
     * @return the content of the tag
     */
    @PropertyGetter(role = COMMENT_CONTENT)
    String getContent();

    /**
     * Define the content of the tag
     * @param content the new content of the tag
     */
    @PropertySetter(role = COMMENT_CONTENT)
    <E extends CtJavaDocTag> E setContent(String content);


    @Override
    CtJavaDocTag clone();
}
