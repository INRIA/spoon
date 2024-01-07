/**
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
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.ZipFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class ZippedCodeBaseTestContext {
	public ZippedCodeBaseTestContext(String smpl, String pathToSourcesZipFile, boolean useAutoImports) {
		this.rule = SmPLParser.parse(smpl);
		this.spoonModel = buildModel(pathToSourcesZipFile, useAutoImports);
	}

	public static SmPLRule rule;
	public static CtModel spoonModel;

	public static CtModel buildModel(String pathToZipFile, boolean useAutoImports) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(useAutoImports);
		launcher.getEnvironment().setIgnoreSyntaxErrors(true);

		try {
			launcher.addInputResource(new ZipFolder(new File(pathToZipFile)));
			launcher.buildModel();
		} catch (IOException e) {
			fail("failed to build model");
		}

		return launcher.getModel();
	}

	public CtClass<?> getClassFromModel(String qualifiedName) {
		for (CtType<?> ctType : spoonModel.getAllTypes()) {
			if (ctType instanceof CtClass && ctType.getQualifiedName().equals(qualifiedName)) {
				return (CtClass<?>) ctType;
			}
		}

		fail("class " + qualifiedName + " not found");
		return null;
	}

	public CtClass<?> getInnerClass(String qualifiedName) {
		String[] parts = qualifiedName.split("\\$", 2);
		CtClass<?> class_ = getClassFromModel(parts[0]);
		return class_.getNestedType(parts[1]);
	}

	public CtMethod<?> getMethod(String qualifiedName) {
		String[] parts = qualifiedName.split("::", 2);
		CtClass<?> class_ = getClassFromModel(parts[0]);

		return class_.getMethodsByName(parts[1]).stream().findFirst().get();
	}

	public CtMethod<?> getMethod(String qualifiedName, int n) {
		String[] parts = qualifiedName.split("::", 2);
		CtClass<?> class_ = getClassFromModel(parts[0]);

		return class_.getMethodsByName(parts[1]).get(n);
	}

	public CtMethod<?> getMethodFromInnerClass(String qualifiedName) {
		String[] parts = qualifiedName.split("::", 2);
		CtClass<?> class_ = getInnerClass(parts[0]);

		return class_.getMethodsByName(parts[1]).stream().findFirst().get();
	}

	public String testMethod(String qualifiedName) {
		return applySmplPatch(getMethod(qualifiedName));
	}

	public String applySmplPatch(CtExecutable<?> ctExecutable) {
		if (!rule.isPotentialMatch(ctExecutable)) {
			return null;
		}

		new TypeAccessReplacer().scan(ctExecutable);

		CFGModel model = new CFGModel(new SmPLMethodCFG(ctExecutable));
		ModelChecker checker = new ModelChecker(model);
		rule.getFormula().accept(checker);

		ModelChecker.ResultSet results = checker.getResult();
		Transformer.transform(model, results.getAllWitnesses());

		if (results.size() > 0 && rule.getMethodsAdded().size() > 0) {
			Transformer.copyAddedMethods(model, rule);
		}

		model.getCfg().restoreUnsupportedElements();

		return ctExecutable.toString();
	}
}
