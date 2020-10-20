package fr.inria.gforge.spoon.architecture.runner;

import java.lang.reflect.Method;

public interface IReportPrinter {
  
  public default void startPrinting() {

  }
  public default void beforeMethod(Method method) {

  }
  public default void afterMethod(Method method) {

  }

  public default void finishPrinting() {

  }
}
