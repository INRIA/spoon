package fr.inria.gforge.spoon.architecture.report;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;

public class ShellPrinter implements IReportPrinter {

  private Instant start;
  private Instant finish;
  private Instant methodStart;
  private Instant methodFinish;
  @Override
  public void afterMethod(Method method) {
    methodFinish = Instant.now();
    System.out.println("Finished check " + method.getName() + " in " + Duration.between(methodStart, methodFinish).toSeconds() + " seconds");
  }

  @Override
  public void beforeMethod(Method method) {
    System.out.println("Running check: " + method.getName());
    methodStart = Instant.now();
  }

  @Override
  public void finishPrinting() {
    finish = Instant.now();
    System.out.println("Finished running architecture checks in " +  Duration.between(start, finish).toSeconds() + " seconds");
  }

  @Override
  public void startPrinting() {
    start = Instant.now();
    System.out.println("Starting running architecture checks");
  }
  
}
