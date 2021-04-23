/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
	 * Hide utility class constructor.
	 */
	private SpoonJavaParser() { }

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
		// removes type accesses and replaces by field accesses, see #TypeAccessReplacer
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
	 * @param code      Source code
	 * @param className Name of class to extract
	 * @return Spoon metamodel of class of given class name
	 */
	public static CtClass<?> parseClass(String code, String className) {
		return (CtClass<?>) parse(code).getRootPackage().getType(className);
	}
}
