package fr.inria.gforge.spoon.architecture.runner;

import java.util.Map;
import fr.inria.gforge.spoon.architecture.report.IReportPrinter;
import spoon.reflect.CtModel;

public class SpoonArchitecturalCheckerImpl extends AbstractSpoonArchitecturalChecker {


  private static Map.Entry<String,String> defaultSrcPath = Map.entry("srcmodel", "src/main/java");
  private static Map.Entry<String,String> defaultTestPath = Map.entry("testmodel", "src/test/java");
  private AbstractModelBuilder builder;
  private IReportPrinter printer;
  private SpoonArchitecturalCheckerImpl() {
    builder = new ModelBuilder();
    //use as default a NOP printer
    printer = new IReportPrinter(){ };
  }


  public static SpoonArchitecturalCheckerImpl createChecker() {
    SpoonArchitecturalCheckerImpl checker = new SpoonArchitecturalCheckerImpl();
    checker.builder.insertInputPath(defaultSrcPath.getKey(), defaultSrcPath.getValue());
    checker.builder.insertInputPath(defaultTestPath.getKey(), defaultTestPath.getValue());
    return checker;
  }

  public static SpoonArchitecturalCheckerImpl createCheckerWithoutDefault() {
    return new SpoonArchitecturalCheckerImpl();
  }

  /**
   * @return the printer
   */
  public IReportPrinter getPrinter() {
    return printer;
  }
  @Override
  Map<String, CtModel> getModelByName() {
    return builder.getModelByName();
  }

  public static class Builder {

    private SpoonArchitecturalCheckerImpl checker; 
    public Builder() {
      checker = new SpoonArchitecturalCheckerImpl();
    }
    public Builder addModelBuilder(AbstractModelBuilder modelBuilder) {
      checker.builder = modelBuilder;
      return this;
    }
    public Builder addReportPrinter(IReportPrinter printer) {
      checker.printer = printer;
      return this;
    }
    public Builder useDefaultPath() {
      checker.builder.insertInputPath(defaultSrcPath.getKey(), defaultSrcPath.getValue());
      checker.builder.insertInputPath(defaultTestPath.getKey(), defaultTestPath.getValue());
      return this;
    }
    public SpoonArchitecturalCheckerImpl build() {
      return checker;
    }
  } 
}
