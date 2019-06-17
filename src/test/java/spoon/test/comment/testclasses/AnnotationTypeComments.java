package spoon.test.comment.testclasses;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

public class AnnotationTypeComments {

    @Documented
    @Inherited
    @Target({ TYPE, METHOD, FIELD, PARAMETER })
    public @interface Mapper1 { //comment1
    }

    @Documented
    @Inherited
    @Target({ TYPE, METHOD, FIELD, PARAMETER })
    public @interface Mapper2 { //comment1
        //comment2
        String author();
        String date(); //comment3
        String lastModified() default "now";
        //comment4
        String[] reviewers();
    }
}
