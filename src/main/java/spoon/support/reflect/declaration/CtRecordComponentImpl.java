package spoon.support.reflect.declaration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import spoon.SpoonException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.CtExtendedModifier;

public class CtRecordComponentImpl<T> extends CtNamedElementImpl implements CtRecordComponent<T> {

  private static Set<String> forbiddenNames = createForbiddenNames();
  private CtTypeReference<T> type;
  
  @Override
  public CtMethod<?> toMethod() {
    CtMethod<?> method = this.getFactory().createMethod();
    method.setSimpleName(getSimpleName());
    method.setType((CtTypeReference) getType());
    method.setExtendedModifiers(Collections.singleton(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
    method.setImplicit(true);
    method.setBody(getFactory().createCodeSnippetStatement("return " + getSimpleName()));
    return method;
  }

  @Override
  public CtField<?> toField() {
    CtField<?> field = this.getFactory().createField();
    field.setSimpleName(getSimpleName());
    field.setType((CtTypeReference) getType());
    Set<CtExtendedModifier> modifiers = new HashSet<>();
    modifiers.add(new CtExtendedModifier(ModifierKind.PRIVATE, true));
    modifiers.add(new CtExtendedModifier(ModifierKind.FINAL, true));
    field.setExtendedModifiers(modifiers);
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

  @Override
  public <T extends CtNamedElement> T setSimpleName(String simpleName) {
    checkName(simpleName);
    return super.setSimpleName(simpleName);
  }
  
  private void checkName(String simpleName) {
    if (forbiddenNames.contains(simpleName)) {
      throw new SpoonException("The name '" + simpleName + "' is not allowed as record component name.");
    }
  }
  private static Set<String> createForbiddenNames() {
    Set<String> names = new HashSet<>();
    names.add("clone");
    names.add("finalize");
    names.add("getClass");
    names.add("hashCode");
    names.add("notify");
    names.add("notifyAll");
    names.add("toString");
    names.add("wait");
    return names;
  }
}
