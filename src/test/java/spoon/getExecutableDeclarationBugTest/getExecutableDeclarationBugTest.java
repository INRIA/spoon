package spoon.getExecutableDeclarationBugTest;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import java.util.List;
import static org.junit.Assert.assertNotEquals;


/*
* Issue #3245 https://github.com/INRIA/spoon/issues/3245
* Problem: I have a CtAbstractInvocation variable (validateCall) that represents a method invocation made in
* the changePassword method in UserController.java class to a method in the ChangePasswordValidator.java class,
* and when I do validateCall.getExecutable().getExecutableDeclaration() I receive null when I should be
* receiving the method that is declared in that class.
* Both classes are present in the testClasses folder that is being given to the Spoon Launcher.
* */
public class getExecutableDeclarationBugTest {

    @Test
    public void getExecutableDeclarationOnKnownClassShouldNotReturnNull() {
        final Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        // folder with both classes
        launcher.addInputResource("./src/test/resources/getExecutableDeclarationTestClasses");
        launcher.buildModel();
        Factory factory = launcher.getFactory();

        // save UserController class in a variable
        CtType userControllerClass = null;
        List<CtType<?>> all = factory.Class().getAll();
        for (CtType clazz : all) {
            if (clazz.getAnnotations().size() > 0) {
                userControllerClass = clazz;
                break;
            }
        }

        // get changePassword method of the UserController class
        CtExecutable changePasswordMethod = (CtExecutable) userControllerClass.getMethodsByName("changePassword").get(0);
        List<CtAbstractInvocation> calleeMethods = changePasswordMethod.getElements(new TypeFilter<CtAbstractInvocation>(CtAbstractInvocation.class));

        // get the "validator.validate(form, formBinding);" call of line 37 in file UserController.java
        CtAbstractInvocation validateCall = null;
        for (CtAbstractInvocation invocation : calleeMethods) {
            if (invocation.getExecutable().getSimpleName().equals("validate")) {
                validateCall = invocation;
                break;
            }
        }

        // Try to reach the method that is being called and that belongs to a class known by the Launcher
        CtExecutable executableDeclaration = validateCall.getExecutable().getExecutableDeclaration();

        // should be != null
        assertNotEquals(null, executableDeclaration);
    }

}
