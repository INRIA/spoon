// see https://github.com/inria/spoon/issues/3337

package bug;

import some.pkg.*;

public class TypeAccessStarImport {
    // SomeClass could belong to either the bug package or some.pkg, should not guess which one!
    int value = SomeClass.generateValue();
}