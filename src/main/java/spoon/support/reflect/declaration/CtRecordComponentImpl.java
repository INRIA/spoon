package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtRecordComponentImpl<T> extends CtNamedElementImpl implements CtRecordComponent<T> {

  private CtTypeReference<T> type;
  
  @Override
  public CtMethod<?> toMethod() {
    CtMethod<?> method = this.getFactory().createMethod();
    method.setSimpleName(getSimpleName());
    method.setType((CtTypeReference) getType());
    method.addModifier(ModifierKind.PUBLIC);
    method.setImplicit(true);
    method.setBody(getFactory().createCodeSnippetStatement("return " + getSimpleName()));
    return method;
  }

  @Override
  public CtField<?> toField() {
    CtField<?> field = this.getFactory().createField();
    field.setSimpleName(getSimpleName());
    field.setType((CtTypeReference) getType());
    field.addModifier(ModifierKind.PRIVATE);
    field.addModifier(ModifierKind.FINAL);
    field.setImplicit(true);
    return field;
  }

  @Override
  public boolean isImplicit() {
    return true;
  }

  @Override
  public CtTypeReference<T> getType() {
    return type;
  }

  @Override
  public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
    this.type = type; 
		return (C) this;  }

  @Override
  public void accept(CtVisitor visitor) {
    visitor.visitCtRecordComponent(this);    
  }
  
}
