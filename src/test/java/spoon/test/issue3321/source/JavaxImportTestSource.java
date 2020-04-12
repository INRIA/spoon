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
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public JavaxImportTestSource setObject(@NotNull @Valid SomeObjectDto someObjectDto) {

        return this;
    }

    public JavaxImportTestSource setFirstName(@NotNull @Valid String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public JavaxImportTestSource setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
