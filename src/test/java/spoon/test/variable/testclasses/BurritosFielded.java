package spoon.test.variable.testclasses;


import static spoon.Launcher.SPOONED_CLASSES;

public class BurritosFielded {
    Object spoon = null;

    void foo() {
        Object xx = SPOONED_CLASSES; // cannot be written spoon.o, has to be with implicit visibility or static import
    }
}
