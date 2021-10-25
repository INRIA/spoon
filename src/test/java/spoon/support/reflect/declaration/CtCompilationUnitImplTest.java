package spoon.support.reflect.declaration;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;

import static org.junit.jupiter.api.Assertions.*;

class CtCompilationUnitImplTest {
  public static final CoreFactory factory = new Launcher().getFactory().Core();
  public static final ClassFactory classFactory = new Launcher().getFactory().Class();

  @Test
  void testSetDeclaredTypesWithNewClassUsingClassFactory() {
    CtCompilationUnit ctCompilationUnit = factory.createCompilationUnit();
    CtClass<?> ctClass = classFactory.create("A");
    ctClass.addModifier(ModifierKind.PUBLIC);

    ctCompilationUnit.setDeclaredTypes(Arrays.asList(ctClass));
    assertNotNull(ctCompilationUnit.getDeclaredTypes().get(0));
  }

  @Test
  void testSetDeclaredTypesWithNewClassUsingCoreFactory() {
    CtCompilationUnit ctCompilationUnit = factory.createCompilationUnit();
    CtClass<?> ctClass = factory.createClass();
    ctClass.setSimpleName("A");
    ctClass.addModifier(ModifierKind.PUBLIC);

    ctCompilationUnit.setDeclaredTypes(Arrays.asList(ctClass));
    assertNotNull(ctCompilationUnit.getDeclaredTypes().get(0));
  }

  @Test
  void testSetDeclaredTypesWithParsedClassUsingCoreFactory() {
    CtCompilationUnit ctCompilationUnit = factory.createCompilationUnit();
    CtClass<?> ctClass = Launcher.parseClass("public class A { }");

    ctCompilationUnit.setDeclaredTypes(Arrays.asList(ctClass));
    assertNotNull(ctCompilationUnit.getDeclaredTypes().get(0));
  }
}