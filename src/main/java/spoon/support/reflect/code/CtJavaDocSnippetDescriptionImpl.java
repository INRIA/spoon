package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtJavadocSnippetDescription;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtJavaDocSnippetDescriptionImpl extends CtElementImpl implements CtJavadocSnippetDescription {

    @MetamodelPropertyField(role = CtRole.JAVADOC_CONTENT)
    private String content;

    @Override
    public void accept(CtVisitor visitor) {
        visitor.visitCtJavaDocSnippetDescription(this);
    }

    @Override
    public <T extends CtJavadocSnippetDescription> T setContent(String content) {
        this.getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.JAVADOC_CONTENT, content, this.content);
        this.content = content;
        return (T) this;
    }

    @Override
    public String getContent() {
        return this.content;
    }
}
