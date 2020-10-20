package fr.inria.gforge.spoon.architecture.runner;

import java.util.Map;
import spoon.reflect.CtModel;

public abstract class AbstractModelBuilder {
  
  public abstract void insertInputPath(String name, String path);
  public abstract void insertInputPath(String name, CtModel model);
  
  public abstract Map<String, CtModel> getModelByName();
}
