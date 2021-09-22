package spoon.test.ctInterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.util.Collection;
import java.util.stream.Collectors;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.CtExtendedModifier;

public class CtInterfaceTest {

  @Test
  public void testNestedTypesInInterfaceArePublic() {
    // contract: nested types in interfaces are implicitly public
    // (https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.5)

    Launcher launcher = new Launcher();
    launcher.addInputResource("src/test/resources/nestedInInterface");
    CtModel model = launcher.buildModel();

    Collection<CtType<?>> types = model.getAllTypes()
        .stream()
        .flatMap(it -> it.getNestedTypes().stream())
        .collect(Collectors.toList());

    assertEquals(4, types.size());

    for (CtType<?> type : types) {
      assertTrue("Nested type " + type.getQualifiedName() + " is not public", type.isPublic());
      CtExtendedModifier modifier = type.getExtendedModifiers()
          .stream()
          .filter(it -> it.getKind() == ModifierKind.PUBLIC)
          .findFirst()
          .get();
      assertTrue(
          "nested type " + type.getQualifiedName() + " has explicit modifier",
          modifier.isImplicit()
      );
    }
  }

  @Test
  public void testNestedTypesInInterfaceAreStatic() {
    // contract: nested types in interfaces are implicitly static
    // (https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.5)

    Launcher launcher = new Launcher();
    launcher.addInputResource("src/test/resources/nestedInInterface");
    CtModel model = launcher.buildModel();

    Collection<CtType<?>> types = model.getAllTypes()
        .stream()
        .flatMap(it -> it.getNestedTypes().stream())
        .collect(Collectors.toList());

    assertEquals(4, types.size());

    for (CtType<?> type : types) {
      assertTrue("Nested type " + type.getQualifiedName() + " is not static", type.isStatic());
      CtExtendedModifier modifier = type.getExtendedModifiers()
          .stream()
          .filter(it -> it.getKind() == ModifierKind.STATIC)
          .findFirst()
          .get();
      assertTrue(
          "nested type " + type.getQualifiedName() + " has explicit modifier",
          modifier.isImplicit()
      );
    }
  }

  @Test
  public void testImplicitPublicModifierInNestedInterfaceTypeIsRemoved() {
    // contract: implicit public modifier for nested types is deleted when they are removed from the interface
    Factory factory = createFactory();
    CtInterface<?> ctInterface = factory.Interface().create("foo.Bar");
    CtClass<?> nestedClass = factory.Class().create("foo.Bar$Inner");
    ctInterface.addNestedType(nestedClass);

    assertTrue("Class wasn't made public", nestedClass.isPublic());
    ctInterface.removeNestedType(nestedClass);

    assertFalse("public modifier wasn't removed", nestedClass.isPublic());
  }

  @Test
  public void testImplicitStaticModifierInNestedInterfaceTypeIsRemoved() {
    // contract: implicit static modifier for nested types is deleted when they are removed from the interface
    Factory factory = createFactory();
    CtInterface<?> ctInterface = factory.Interface().create("foo.Bar");
    CtClass<?> nestedClass = factory.Class().create("foo.Bar$Inner");
    ctInterface.addNestedType(nestedClass);

    assertTrue("Class wasn't made static", nestedClass.isStatic());
    ctInterface.removeNestedType(nestedClass);

    assertFalse("static modifier wasn't removed", nestedClass.isStatic());
  }
}
