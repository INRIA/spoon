package fr.inria.gforge.spoon.architecture.simpleChecks.xwiki;

import fr.inria.gforge.spoon.architecture.ArchitectureTest;
import fr.inria.gforge.spoon.architecture.Constraint;
import fr.inria.gforge.spoon.architecture.DefaultElementFilter;
import fr.inria.gforge.spoon.architecture.Precondition;
import fr.inria.gforge.spoon.architecture.errorhandling.IError;
import fr.inria.gforge.spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;

public class Junit4Junit5Test {
  
  @Architecture(modelNames = "testModel")
  public void junit4Junit5Test(CtModel testModel) {
    // get all Classes as precondition
    Precondition<CtClass<?>> pre = Precondition.of(DefaultElementFilter.CLASSES.getFilter());
    // check that no class has a junit4 and junit5 type usage.
    // a constraint will call the ErrorReporter if all conditions are true. Each condition is connected by logical AND by default.
    // You write the constraint as forall Elements X must hold and the runner checks that there is no where not X is true. 
    Constraint<CtClass<?>>  con = Constraint.of(new ErrorReporter(),
      // check for any junit 5 usage
      (element) -> element.getReferencedTypes().stream().anyMatch(clazz -> clazz.getQualifiedName().startsWith("org.junit.jupiter")),
      // check for any junit 4 usage
      (element) -> element.getReferencedTypes().stream().anyMatch(clazz -> clazz.getQualifiedName().startsWith("org.junit")));
      // run the check
      ArchitectureTest.of(pre, con).runCheck(testModel);
  }
  private class ErrorReporter implements IError<CtClass<?>> {

    @Override
    public void printError(CtClass<?> element) {
      System.out.println(String.format("There's a mix of JUnit4 and JUnit5 APIs at [%s]", element.getPosition()));
    }
    
  }

}
