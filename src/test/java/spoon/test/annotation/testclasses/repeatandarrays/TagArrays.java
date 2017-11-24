package spoon.test.annotation.testclasses.repeatandarrays;

import java.lang.annotation.Repeatable;

@Repeatable(TagsArrays.class)
public @interface TagArrays {
    String[] value();
}
