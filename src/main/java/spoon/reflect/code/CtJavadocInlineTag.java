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
        LINK,
        UNKNOWN;

        /**
         * Get the tag type associated to a name
         * @param tagName the tag name
         * @return the tag type
         */
        public static TagType tagFromName(String tagName) {
            for (TagType t : TagType.values()) {
                if (t.name().toLowerCase().equals(tagName.toLowerCase())) {
                    return t;
                }
            }
            return UNKNOWN;
        }
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
    <E extends CtJavadocInlineTag> E setType(String type);

    /**
     * Define the type of the tag
     * @param type the new type
     */
    @PropertySetter(role = DOCUMENTATION_TYPE)
    <E extends CtJavadocInlineTag> E setType(TagType type);

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
    <E extends CtJavadocInlineTag> E setContent(String content);
}
