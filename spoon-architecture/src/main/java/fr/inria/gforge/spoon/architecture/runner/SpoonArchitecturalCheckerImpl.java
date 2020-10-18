package fr.inria.gforge.spoon.architecture.runner;

import java.util.HashMap;
import java.util.Map;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class SpoonArchitecturalCheckerImpl extends AbstractSpoonArchitecturalChecker {


  private Map<String, CtModel> modelByName;
  private static Map.Entry<String,String> defaultSrcPath = Map.entry("srcmodel", "src/main/java");
  private static Map.Entry<String,String> defaultTestPath = Map.entry("testmodel", "src/test/java");
  @Override
  Map<String, CtModel> getModelByName() {
    return modelByName;
  }

  private SpoonArchitecturalCheckerImpl() {
    modelByName = new HashMap<>();
  }

  @Override
  public void insertInputPath(String name, String path) {
    Launcher launcher = new Launcher();
    launcher.addInputResource(path);
    CtModel model = launcher.buildModel();

    modelByName.put(name.toLowerCase(), model);
  }

  @Override
  public void insertInputPath(String name, CtModel model) {
    modelByName.put(name.toLowerCase(), model);
  }

  public static SpoonArchitecturalCheckerImpl createChecker() {
    SpoonArchitecturalCheckerImpl checker = new SpoonArchitecturalCheckerImpl();
    checker.insertInputPath(defaultSrcPath.getKey(), defaultSrcPath.getValue());
    checker.insertInputPath(defaultTestPath.getKey(), defaultTestPath.getValue());

    return checker;
  }

  public static SpoonArchitecturalCheckerImpl createCheckerWithoutDefault() {
    return new SpoonArchitecturalCheckerImpl();
  }
}
