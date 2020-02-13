package spoon.test.prettyprinter.testclasses;

import spoon.test.prettyprinter.testclasses.sub.Constants;

import static org.junit.Assert.assertTrue;

import static java.lang.System.out;


/**
 * Created by urli on 30/01/2017.
 */
public class ImportStatic {
    public static void main(String[] args, java.lang.String[] args2,
    		String args3, java.lang.String args4) throws Exception {
        assertTrue("blabla".equals("toto"));
        out.println(Constants.READY);
        System.out.println(Constants.READY);
        java.lang.System.out.println(Constants.READY);
    }
}
