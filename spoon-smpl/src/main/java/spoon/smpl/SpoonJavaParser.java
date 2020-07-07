package spoon.smpl;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.support.compiler.VirtualFile;

/**
 * SpoonJavaParser provides the default spoon-smpl procedure for parsing Java source code using Spoon, wherein source
 * code is parsed with auto-imports disabled and the resulting model is postprocessed by the application of
 * TypeAccessReplacer.
 */
public class SpoonJavaParser {
    /**
     * Parse a string of source code.
     *
     * @param code Source code
     * @return Spoon metamodel
     */
    public static CtModel parse(String code) {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(false);
        launcher.addInputResource(new VirtualFile(code));
        launcher.buildModel();
        new TypeAccessReplacer().scan(launcher.getModel().getRootPackage());
        return launcher.getModel();
    }

    /**
     * Parse a string of source code and return the first CtClass found in the model.
     *
     * @param code Source code
     * @return Spoon metamodel of first class found in model
     */
    public static CtClass<?> parseClass(String code) {
        return (CtClass<?>) parse(code).getRootPackage().getTypes().stream().filter(ctType -> ctType instanceof CtClass).findFirst().get();
    }

    /**
     * Parse a string of source code and return the CtClass of the given class name.
     *
     * @param code Source code
     * @param className Name of class to extract
     * @return Spoon metamodel of class of given class name
     */
    public static CtClass<?> parseClass(String code, String className) {
        return (CtClass<?>) parse(code).getRootPackage().getType(className);
    }
}
