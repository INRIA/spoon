package spoon.test.modifiers.testclasses;

import java.io.File;
import java.io.IOException;

public class MethodVarArgs {

    protected Object[] getInitValues(String contentClassName, String... initNames) throws IOException {
        for (String s : initNames) {
            File f = new File(s);
            if (f.getAbsolutePath().equals(contentClassName)) {
                return f.listFiles();
            }
        }
        return null;
    }
}
