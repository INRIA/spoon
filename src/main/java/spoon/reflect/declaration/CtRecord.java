package spoon.reflect.declaration;

import java.util.Set;

public interface CtRecord<T> extends CtClass<T> {

  <C> CtRecord<T>  addRecordComponent(CtRecordComponent<C> component);

  <C> CtRecord<T>  removeRecordComponent(CtRecordComponent<C> component);

  Set<CtRecordComponent<?>> getRecordComponents();

  CtRecord<T> setRecordComponents(Set<CtRecordComponent<?>> components);
}
