package spoon.test.variable.testclasses;


import static spoon.Launcher.SPOONED_CLASSES;

public class BurritosWithLoop {
    void foo() {
        for (int spoon = 0; spoon < 10; spoon++) {
            Object xx = SPOONED_CLASSES;
        }
    }
}
