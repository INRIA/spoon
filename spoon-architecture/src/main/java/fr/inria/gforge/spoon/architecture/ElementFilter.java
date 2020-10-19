package fr.inria.gforge.spoon.architecture;
//TODO: Naming

import java.util.function.Predicate;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ElementFilter {
  
  public static <T extends CtElement> Filter<T> ofClassObject(Class<T> elementType, Predicate<? super T> predicate) {
        TypeFilter<T> typeFilter = new TypeFilter<T>(elementType);
        return new Filter<T>(){  
          @Override
          public boolean matches(T element) {
            return typeFilter.matches(element) && predicate.test(element);
          }
          
        };
  }
  public static <T extends CtElement> Filter<T> ofTypeFilter(TypeFilter<T> typeFilter, Predicate<? super T> predicate) {
    return new Filter<T>(){  
      @Override
      public boolean matches(T element) {
        return typeFilter.matches(element) && predicate.test(element);
      }
      
    };
}
}
