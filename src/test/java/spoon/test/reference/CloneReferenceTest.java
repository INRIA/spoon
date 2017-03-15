package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.refactoring.Refactoring;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

public class CloneReferenceTest {

    @Test
    public void testGetDeclarationAfterClone() throws Exception {
        // contract: all variable references of the clone (but fields) should point to the variable of the clone
        Launcher spoon = new Launcher();

        List<String> names = Arrays.asList("f1", "f2", "a", "b", "x", "param", "e");
        spoon.addInputResource("./src/test/resources/noclasspath/A2.java");
        spoon.getEnvironment().setComplianceLevel(8);
        spoon.getEnvironment().setNoClasspath(true);
        spoon.buildModel();


        final CtClass<Object> a = spoon.getFactory().Class().get("A2");
        // test before clone
        for (String name : names) {
            CtVariable var1 = findVariable(a, name);
            CtVariable var2 = findReference(a, name).getDeclaration();
            assertTrue(var1 == var2);
        }

        CtClass b = a.clone();

        // test after clone
        for (String name : names) {
            CtVariable var1 = findVariable(b, name);
            CtVariableReference refVar1 = findReference(b, name);
            CtVariable var2 = refVar1.getDeclaration();
            assertTrue("Var1 and var2 are not the same element", var1 == var2);
        }
    }

    @Test
    public void testGetDeclarationOfFieldAfterClone() throws Exception {
        // contract: all field references of the clone point to the old class
        // behaviour changed on https://github.com/INRIA/spoon/pull/1215
        Launcher spoon = new Launcher();

        String name = "field";
        spoon.addInputResource("./src/test/resources/noclasspath/A2.java");
        spoon.getEnvironment().setComplianceLevel(8);
        spoon.getEnvironment().setNoClasspath(true);
        spoon.buildModel();


        final CtClass<Object> a = spoon.getFactory().Class().get("A2");
        // test before clone
        CtField oldVar1 = (CtField)findVariable(a, name);
        CtField oldVar2 = (CtField)findReference(a, name).getDeclaration();
        assertTrue(oldVar1 == oldVar2);

        CtClass b = a.clone();

        // test after clone
        CtField var1 = (CtField)findVariable(b, name);
        CtVariableReference refVar1 = findReference(b, name);
        CtField var2 = (CtField)refVar1.getDeclaration();
        assertTrue(var1 != var2);
        assertTrue(var2 == oldVar1);
        assertTrue(var1.getParent(CtClass.class) == b);
    }

    class Finder<T> extends CtScanner {

        private final Class<T> c;
        private final Predicate<T> filter;
        private T result;

        public Finder(Class<T> c, Predicate<T> filter) {
            this.c = c;
            this.filter = filter;
        }

        @Override
        public void scan(CtElement element) {
            if (element != null && c.isAssignableFrom(element.getClass()) && filter.test((T) element)) {
                result = (T) element;
            } else {
                super.scan(element);
            }
        }

        public T find(CtElement root) {
            scan(root);
            return result;
        }
    }

    public <T extends CtElement> T find(CtElement root, Class<T> c, Predicate<T> filter) {
        return new Finder<>(c, filter).find(root);
    }

    public CtVariable findVariable(CtElement root, String name) {
        return find(root, CtVariable.class, var -> name.equals(var.getSimpleName()));
    }

    public CtVariableReference findReference(CtElement root, String name) {
        return find(root, CtVariableReference.class, ref -> name.equals(ref.getSimpleName()));
    }
}