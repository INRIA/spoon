package spoon.test.imports.testclasses.badimportissue3320.source;

import spoon.test.imports.testclasses.badimportissue3320.source.other.SomeObjectDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Apr, 2020
 **/
public class TestSource {
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public TestSource setObject(@NotNull @Valid SomeObjectDto someObjectDto) {

        return this;
    }

    public TestSource setFirstName(@NotNull @Valid String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public TestSource setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
