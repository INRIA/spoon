package P.F.G;

/**
 * @author Charm
 */

public interface DuplicateInnerClass {

    abstract class B {

        private static class B implements DuplicateInnerClass {

        }
    }
}