package spoon.test.prettyprinter.testclasses;

import spoon.test.prettyprinter.testclasses.sub.Constants;

import static org.junit.Assert.assertTrue;


/**
 * Created by urli on 30/01/2017.
 */
public class ImportStatic {
    public static void main(String[] args) throws Exception {
        assertTrue("blabla".equals("toto"));
        System.out.println(Constants.READY);
    }
}
