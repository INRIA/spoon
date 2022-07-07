package spoon.test.imports.testclasses;

public class DuplicatedImport {
    void duplicatedImport(spoon.test.imports.testclasses.A a1){
        spoon.test.imports.testclasses.memberaccess.A a2 = null;
        spoon.test.imports.testclasses.multiplecu.A a3 = null;
    }
}
