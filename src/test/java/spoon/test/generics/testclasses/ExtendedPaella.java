package spoon.test.generics.testclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by urli on 14/06/2017.
 */
public class ExtendedPaella<T extends List> extends Paella {

    public <T extends ArrayList> T machin() {
        return null;
    }

    public T toto() {
        return null;
    }
}
