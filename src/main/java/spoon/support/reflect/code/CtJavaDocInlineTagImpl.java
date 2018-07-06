package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtJavadocInlineTag;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtJavaDocInlineTagImpl extends CtElementImpl implements CtJavadocInlineTag {

    @MetamodelPropertyField(role = CtRole.DOCUMENTATION_TYPE)
    private TagType tagType;

    @MetamodelPropertyField(role = CtRole.COMMENT_CONTENT)
    private String content;

    @Override
    public TagType getType() {
        return this.tagType;
    }

    @Override
    public <E extends CtJavadocInlineTag> E setType(String type) {
        TagType tagType;
        if (type == null) {
            tagType = TagType.UNKNOWN;
        } else {
            tagType = TagType.tagFromName(type);
        }

        return this.setType(tagType);
    }

    @Override
    public <E extends CtJavadocInlineTag> E setType(TagType type) {
        this.getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.DOCUMENTATION_TYPE, type, this.tagType);
        this.tagType = type;
        return (E) this;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public <E extends CtJavadocInlineTag> E setContent(String content) {
        this.getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.COMMENT_CONTENT, content, this.content);
        this.content = content;
        return (E) this;
    }

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtJavaDocInlineTag(this);
    }
}
