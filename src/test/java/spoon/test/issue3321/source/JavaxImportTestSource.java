package spoon.test.issue3321.source;

import spoon.test.issue3321.SomeObjectDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Apr, 2020
 **/
public class JavaxImportTestSource {
    public JavaxImportTestSource setObject(@NotNull SomeObjectDto someObjectDto) {
        return this;
    }
}
