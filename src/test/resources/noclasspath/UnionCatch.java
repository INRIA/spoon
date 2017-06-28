package spoon.test.exceptions.testclasses;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;
import toto.NbpOperator;

/**
 * Created by urli on 23/06/2017.
 */
public class UnionCatch {
    public void toto() {
        bla((NbpOperator) t -> {
            try {
                Reader reader = new StringReader("machin");
                int i = -1;
                while (reader.ready()) {
                    i = reader.read();
                }
                return i;
            } catch (IOException | NullPointerException e) {
                System.out.printf("Error");
                return 0;
            }
        });

        try {
            Reader reader = new StringReader("machin");
            int i = -1;
            while (reader.ready()) {
                i = reader.read();
            }
            return i;
        } catch (IOException | NullPointerException e) {
            System.out.printf("Error");
            return 0;
        }
    }
}
