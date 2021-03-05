package spoon.test.prettyprinter.testclasses;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 02/12/2020
 */

import static spoon.test.prettyprinter.testclasses.ClassWithStaticMethod.findFirst;
public class ClassUsingStaticMethod {

    public void callFindFirst() {
        findFirst();
        new ClassWithStaticMethod().notStaticFindFirst();
    }

}
