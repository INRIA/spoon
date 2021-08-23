package spoon.reflect.declaration;

public interface CtRecordComponent<T> extends CtTypedElement<T>, CtNamedElement {
  
  CtMethod<?> toMethod();
  CtField<?> toField();
}
