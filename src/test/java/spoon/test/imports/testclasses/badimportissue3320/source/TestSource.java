package spoon.test.imports.testclasses.badimportissue3320.source;

import spoon.test.imports.testclasses.badimportissue3320.source.other.SomeObjectDto;
import spoon.test.imports.testclasses.badimportissue3320.source.other.TestAnnotation;

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

    public TestSource setObject(@TestAnnotation SomeObjectDto someObjectDto) {

        return this;
    }

    public TestSource setFirstName(@TestAnnotation String firstName) {
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
