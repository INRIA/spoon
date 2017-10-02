// example of a quick scripted analysis in Groovy
// called with mvn groovy:execute -Dsource=doc/analysis.groovy -Dscope=runtime
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.ModifierKind;

class ListClasses extends AbstractProcessor<CtClass> {
  List result=[]
  boolean isToBeProcessed(CtClass c) {
    return c.isTopLevel() && c.getModifiers().contains(ModifierKind.PUBLIC);
  }
  void process(CtClass c) {
    println(c.getQualifiedName());
  }
}
def l = new Launcher();
l.addInputResource("src/main/java");
l.addProcessor(new ListClasses());
l.getEnvironment().setNoClasspath(true);
l.run();
