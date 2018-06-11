package spoon.test.position.testclasses;

public class PositionTry {
    public void withoutModifier() {
        try {

        } catch (java.lang.Exception e) {

        }
    }

    public void withModifier() {
        try {

        } catch (final java.lang.Exception e) {

        }
    }

    public void multipleCatch() {
        try {

        } catch (NullPointerException | java.lang.ArithmeticException e) {

        }
    }
}