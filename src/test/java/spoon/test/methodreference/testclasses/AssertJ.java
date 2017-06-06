package spoon.test.methodreference.testclasses;

import java.util.Collections;
import java.util.List;
import java.util.stream.BaseStream;

/**
 * Created by urli on 06/06/2017.
 */
public class AssertJ {
    @SuppressWarnings("unchecked")
    public <ELEMENT, STREAM extends BaseStream<ELEMENT, STREAM>> List<ELEMENT> assertThat(BaseStream<? extends ELEMENT, STREAM> actual) {
        return Collections.EMPTY_LIST;
    }
}
