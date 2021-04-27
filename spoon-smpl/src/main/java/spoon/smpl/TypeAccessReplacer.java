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

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Stack;

// TODO: find a way to get rid of this entire thing

/**
 * TypeAccessReplacer replaces certain CtTypeAccess elements in a given AST with potentially nested CtFieldReads.
 * The elements replaced are CtTypeAccesses parented by either a CtMethod or CtConstructor.
 * <p>
 * The motivation is illustrated by the following example:
 * <p>
 * Input 1:
 * class A {
 * static class WebSettings { public enum TextSize { SMALL, NORMAL, LARGE } } // inner class
 * void setTextSize(WebSettings.TextSize size) { ... }
 * void m() { setTextSize(WebSettings.TextSize.LARGE); }
 * }
 * <p>
 * Input 2:
 * class WebSettings { public enum TextSize { SMALL, NORMAL, LARGE } } // external class
 * <p>
 * class A {
 * void setTextSize(WebSettings.TextSize size) { ... }
 * void m() { setTextSize(WebSettings.TextSize.LARGE); }
 * }
 * <p>
 * Input 3:
 * import android.webkit.WebSettings; // not pulled into Spoon model
 * <p>
 * class A {
 * void setTextSize(WebSettings.TextSize size) { ... }
 * void m() { setTextSize(WebSettings.TextSize.LARGE); } // missing information
 * }
 * <p>
 * AST for method call produced by Spoon with auto-imports disabled:
 * Input 1: setTextSize(CtFieldRead("LARGE", target=CtTypeAccess("A.WebSettings.TextSize"))
 * Input 2: setTextSize(CtFieldRead("LARGE", target=CtTypeAccess("WebSettings.TextSize"))
 * Input 3: setTextSize(CtTypeAccess("WebSettings.TextSize.LARGE"))
 * <p>
 * AST for method call after TypeAccessReplacer processing:
 * Inputs 1, 2, 3: setTextSize(CtFieldRead("LARGE", target=CtFieldRead("TextSize", target=CtFieldRead("WebSettings", target=null)))
 * <p>
 * Thus the idea is to make matching easier while outputting semantically equivalent source programs.
 */
public class TypeAccessReplacer extends CtScanner {
	/**
	 * Instance options.
	 */
	public enum Options {
		/**
		 * Disable checking that encountered CtTypeAccess elements are parented by a CtMethod or CtConstructor.
		 * Default: parents are checked.
		 */
		NoCheckParents
	}

	/**
	 * Create a TypeAccessReplacer using default options.
	 */
	public TypeAccessReplacer() {
		this(EnumSet.noneOf(Options.class));
	}

	/**
	 * Create a TypeAccessReplacer using the given set of options.
	 *
	 * @param options Options to use
	 */
	public TypeAccessReplacer(EnumSet<Options> options) {
		super();

		if (options.contains(Options.NoCheckParents)) {
			checkParents = false;
		}
	}

	/**
	 * Create a replacement CtFieldRead chain for a given CtTypeAccess element.
	 *
	 * @param originalElement CtTypeAccess element to generate a replacement for
	 * @param typeSpec        Typename as reported by CtTypeAccess::toString
	 * @return Chain of CtFieldRead elements intended to produce the equivalent source code
	 */
	@SuppressWarnings("unchecked")
	private static CtExpression createTypeAccessReplacement(CtTypeAccess<?> originalElement, String typeSpec) {
		Factory factory = originalElement.getFactory();
		Stack<String> parts = new Stack<>();
		parts.addAll(Arrays.asList(typeSpec.split("\\.")));
		String lastPart = parts.pop();

		CtTypeReference fieldType = factory.createTypeReference();
		fieldType.setSimpleName(String.join(".", parts));

		CtFieldReference fieldRef = factory.createFieldReference();
		fieldRef.setSimpleName(lastPart);
		fieldRef.setType(fieldType);

		CtFieldRead fieldRead = factory.createFieldRead();
		fieldRead.setVariable(fieldRef);

		if (!parts.isEmpty()) {
			// TODO: find a way to get rid of the need for this hack (removal of "Self." part of CtTypeAccess on inner class)
			if (parts.size() == 1 && parts.peek().equals(((CtClass<?>) originalElement.getParent(CtClass.class)).getSimpleName())) {
				return fieldRead;
			}

			CtExpression<?> target = createTypeAccessReplacement(originalElement, String.join(".", parts));
			target.setParent(fieldRead);
			fieldRead.setTarget(target);
		}

		return fieldRead;
	}

	/**
	 * Scanner implementation that replaces certain CtTypeAccess elements.
	 *
	 * @param element Element being scanned
	 */
	@Override
	public void enter(CtElement element) {
		if (element instanceof CtTypeAccess) {
			CtTypeAccess<?> ctTypeAccess = (CtTypeAccess<?>) element;

			if (ctTypeAccess.getAccessedType() != null && !(ctTypeAccess.getAccessedType().isImplicit()) // case for static members
				&& (!checkParents || (element.getParent(CtMethod.class) != null || element.getParent(CtConstructor.class) != null))
				&& !(element.getParent() instanceof CtThisAccess)) {

				element.replace(createTypeAccessReplacement((CtTypeAccess<?>) element, ctTypeAccess.toString()));
			}
		}
	}

	/**
	 * If true, scanned CtTypeAccess elements will be checked for being parented by a CtMethod or CtConstructor before
	 * being replaced.
	 */
	private boolean checkParents = true;
}
