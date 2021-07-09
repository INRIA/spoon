package spoon.testing.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import spoon.Launcher;
import spoon.reflect.declaration.CtField;
import spoon.support.reflect.declaration.CtEnumValueImpl;
import spoon.support.reflect.declaration.CtFieldImpl;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CheckTest {

    @Test
    public void testAssertNotNull() {
        // contract: assertNotNull throws AssertionError as we pass a null reference, with a default
        // error message

        String expectedMessage = "Your parameter can't be null.";
        
        AssertionError error = assertThrows(AssertionError.class, () -> {
            Check.assertNotNull(null);
        });
        
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void testAssertNotNullWithMessageParameter() {
        // contract: assertNotNull throws AssertionError as we pass a null reference, with an
        // error message that was passed as an argument

        String messageParameter = "testMessage";

        AssertionError error = assertThrows(AssertionError.class, () -> {
            Check.assertNotNull(messageParameter,null);
        });

        assertEquals(messageParameter, error.getMessage());
    }

    @Test
    public void testAssertNotNullWithNotNullReference() {
        // contract: assertNotNull returns the reference passed without any changes as the reference
        // passed was not null

        int i = 0;

        int j = Check.assertNotNull(i);

        assertEquals(i, j);
    }

    @Test
    public void testAssertExistWithoutAnExistingFile(@TempDir File tempDir) {
        // contract: assertExists throws AssertionError as a non existing file is passed

        File file =  getFileWithTestFilePathName(tempDir);
        String expectedMessage = "You should specify an existing file.";

        AssertionError error = assertThrows(AssertionError.class, () -> {
            Check.assertExists(file);
        });

        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void testAssertExistWithExistingFile(@TempDir File tempDir) throws IOException {
        // contract: assertExists the passed file exists, and as it exists it returns the passed file back

        File file = getFileWithTestFilePathName(tempDir);
        file.createNewFile();

        File returnedFile = Check.assertExists(file);

        assertEquals(file, returnedFile);
    }

    @Test
    public void testAssertIsSameWithSameElementsIsTrue() {
        // contract: assertIsSame returns the assumedActualElement without any changes as the assumedExpectedElement
        // and the assumedActualElement were objects of the same class

        CtEnumValueImpl<String> assumedExpectedElement =  new CtEnumValueImpl();
        CtField<Integer> assumedActualElement = new CtEnumValueImpl();

        CtField<?> returnedActualElement = Check.assertIsSame(assumedActualElement, assumedExpectedElement);

        assertEquals(assumedActualElement, returnedActualElement);
    }

    @Test
    public void testAssertIsSameWithDissimilarElements() {
        // contract: assertIsSame throws AssertionError as the assumedExpectedElement and the assumeActualElement
        // were not objects of the same class

        // arrange
        CtEnumValueImpl<String> assumedExpectedElement =  new CtEnumValueImpl();
        CtField<String> assumedActualElement = new CtFieldImpl();
        String expectedMessage = String.format(
                "Actual value is typed by %1$s and expected is typed by %2$s, these objects should be the same type.",
                assumedActualElement.getClass().getName(), assumedExpectedElement.getClass().getName()
        );

        // act
        AssertionError error = assertThrows(AssertionError.class, () -> {
            Check.assertIsSame(assumedActualElement, assumedExpectedElement);
        });

        // assert
        assertEquals(expectedMessage, error.getMessage());
    }

    private static File getFileWithTestFilePathName(File tempDir) {
        Launcher launcher = new Launcher();
        launcher.setSourceOutputDirectory(tempDir.getAbsolutePath());
        return new File (launcher.getModelBuilder().getSourceOutputDirectory() + "testFile.txt");
    }
}