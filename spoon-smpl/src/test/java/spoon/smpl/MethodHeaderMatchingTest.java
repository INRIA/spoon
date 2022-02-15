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

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.MetavariableConstraint;
import spoon.smpl.formula.MethodHeaderPredicate;
import spoon.smpl.label.MethodHeaderLabel;
import spoon.smpl.metavars.IdentifierConstraint;
import spoon.smpl.metavars.TypeConstraint;
import static spoon.smpl.TestUtils.*;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MethodHeaderMatchingTest {
    /*
    public void generateMethods() {
        Set<String> generated = new HashSet<>();
        Random random = new Random();

        List<String> ftypes = Arrays.asList("void", "byte", "short", "int", "long", "float", "double", "boolean", "char",
                                            "Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character");

        List<String> ptypes = Arrays.asList("byte", "short", "int", "long", "float", "double", "boolean", "char",
                                            "Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character");

        List<String> fnames = Arrays.asList("f", "g", "h", "square", "createDocument", "sendRequest", "format");

        List<String> varnames = Arrays.asList("x", "y", "z", "category", "message", "documentRoot");

        while (generated.size() < 25) {
            StringBuilder sb = new StringBuilder();
            Set<String> usedVarNames = new HashSet<>();

            sb.append(ftypes.get(random.nextInt(ftypes.size())));
            sb.append(" ");

            sb.append(fnames.get(random.nextInt(fnames.size())));
            sb.append("(");

            int numParams = random.nextInt(5);

            for (int j = 0; j < numParams; ++j) {
                String varname = varnames.get(random.nextInt(varnames.size()));

                while (usedVarNames.contains(varname)) {
                    varname = varnames.get(random.nextInt(varnames.size()));
                }

                usedVarNames.add(varname);

                sb.append(ptypes.get(random.nextInt(ptypes.size())));
                sb.append(" ");
                sb.append(varname);
                sb.append(", ");
            }

            if (numParams > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }

            sb.append(") { }");

            generated.add(sb.toString());
        }

        for (String s : generated) {
            System.out.println("methods.put(\"" + s + "\", parseMethod(\"" + s + "\"));");
        }
    }*/

	private Map<String, CtMethod<?>> methods = null;

	@BeforeEach
	public void setUp() {
		if (methods == null) {
			methods = new HashMap<>();

			methods.put("Integer format(long category) { }", parseMethod("Integer format(long category) { }"));
			methods.put("Double f(long message, Double x) { }", parseMethod("Double f(long message, Double x) { }"));
			methods.put("Float f(Boolean category, long documentRoot, Long x) { }", parseMethod("Float f(Boolean category, long documentRoot, Long x) { }"));
			methods.put("Character sendRequest(Integer y, Boolean category) { }", parseMethod("Character sendRequest(Integer y, Boolean category) { }"));
			methods.put("byte f() { }", parseMethod("byte f() { }"));
			methods.put("double square(long x, Long category, byte documentRoot, Long y) { }", parseMethod("double square(long x, Long category, byte documentRoot, Long y) { }"));
			methods.put("int square(Float x, Float message) { }", parseMethod("int square(Float x, Float message) { }"));
			methods.put("Character square(Character category) { }", parseMethod("Character square(Character category) { }"));
			methods.put("short f(float z) { }", parseMethod("short f(float z) { }"));
			methods.put("short square(int x, double z, boolean category) { }", parseMethod("short square(int x, double z, boolean category) { }"));
			methods.put("float format(Float y, long documentRoot) { }", parseMethod("float format(Float y, long documentRoot) { }"));
			methods.put("float f(byte x, short message) { }", parseMethod("float f(byte x, short message) { }"));
			methods.put("short createDocument(double category, Integer message, int y, Integer documentRoot) { }", parseMethod("short createDocument(double category, Integer message, int y, Integer documentRoot) { }"));
			methods.put("Long f(Long message, Long y, Float z) { }", parseMethod("Long f(Long message, Long y, Float z) { }"));
			methods.put("long createDocument() { }", parseMethod("long createDocument() { }"));
			methods.put("short format(Double message, Character x, short y, long category) { }", parseMethod("short format(Double message, Character x, short y, long category) { }"));
			methods.put("double sendRequest(Integer z) { }", parseMethod("double sendRequest(Integer z) { }"));
			methods.put("Byte f(Double x, Character message) { }", parseMethod("Byte f(Double x, Character message) { }"));
			methods.put("int createDocument(double category) { }", parseMethod("int createDocument(double category) { }"));
			methods.put("Boolean f(float documentRoot, float y, short z) { }", parseMethod("Boolean f(float documentRoot, float y, short z) { }"));
			methods.put("Double h(boolean documentRoot, int x, int y, boolean category) { }", parseMethod("Double h(boolean documentRoot, int x, int y, boolean category) { }"));
			methods.put("Float g(double y, byte x) { }", parseMethod("Float g(double y, byte x) { }"));
			methods.put("Boolean h() { }", parseMethod("Boolean h() { }"));
			methods.put("int f() { }", parseMethod("int f() { }"));
			methods.put("Integer f(double message) { }", parseMethod("Integer f(double message) { }"));
		}
	}

	public boolean matches(CtMethod<?> labelInput, CtMethod<?> predicateInput) {
		return matches(labelInput, predicateInput, new HashMap<>());
	}

	public boolean matches(CtMethod<?> labelInput, CtMethod<?> predicateInput, Map<String, MetavariableConstraint> metavars) {
		MethodHeaderPredicate predicate = new MethodHeaderPredicate(predicateInput, metavars);
		return matches(labelInput, predicate);
	}

	public boolean matches(CtMethod<?> labelInput, MethodHeaderPredicate predicate) {
		MethodHeaderLabel label = new MethodHeaderLabel(labelInput);
		return label.matches(predicate);
	}

	@Test
	public void testLiteralMatch() {
		// contract: MethodHeader(Label|Predicate) should be able to match headers that are literally the same, and reject headers that are not (when no metavariables or dots are involved)

		for (String k1 : methods.keySet()) {
			assertEquals(true, matches(methods.get(k1), methods.get(k1)));

			for (String k2 : methods.keySet()) {
				if (!k1.equals(k2)) {
					assertEquals(false, matches(methods.get(k1), methods.get(k2)));
				}
			}
		}
	}

	@Test
	public void testMatchAny() {
		// contract: the predicate T fn(...) with T and fn being metavariables should match any method header

		CtMethod<?> predicateMethod = parseMethod("T fn(Object " + SmPLJavaDSL.getDotsStatementElementName() + ") { }");

		Map<String, MetavariableConstraint> metavars = new HashMap<>();
		metavars.put("T", new TypeConstraint());
		metavars.put("fn", new IdentifierConstraint());

		MethodHeaderPredicate predicate = new MethodHeaderPredicate(predicateMethod, metavars);

		for (String key : methods.keySet()) {
			if (!matches(methods.get(key), predicate)) {
				fail(new MethodHeaderLabel(methods.get(key)).toString() + " does not match " + predicate.toString());
			}
		}
	}

	@Test
	public void testMatchAnyOfType() {
		// contract: the predicate SomeType fn(...) with fn being a metavariable should match any method header of type SomeType

		Map<String, MetavariableConstraint> metavars = new HashMap<>();
		metavars.put("fn", new IdentifierConstraint());

		for (String key : methods.keySet()) {
			String typename = methods.get(key).getType().getSimpleName();

			CtMethod<?> matchingPredicateMethod = parseMethod(typename + " fn(Object " + SmPLJavaDSL.getDotsStatementElementName() + ") { }");
			CtMethod<?> mismatchedPredicateMethod = parseMethod((typename.equals("int") ? "void" : "int") + " fn(Object " + SmPLJavaDSL.getDotsStatementElementName() + ") { }");

			assertEquals(true, matches(methods.get(key), new MethodHeaderPredicate(matchingPredicateMethod, metavars)));
			assertEquals(false, matches(methods.get(key), new MethodHeaderPredicate(mismatchedPredicateMethod, metavars)));
		}
	}

	@Test
	public void testDotsMatchingEmptySequence() {
		// contract: dots should be allowed to match the empty sequence, e.g "f(..., int x)" should match "f(int x)"

		String dots = SmPLJavaDSL.createDotsParameterString();
		Map<String, MetavariableConstraint> emptyMetavars = new HashMap<>();

		Function<String, MethodHeaderPredicate> makePredicate = (s) -> {
			return new MethodHeaderPredicate(parseMethod(s.replace("...", dots)), emptyMetavars);
		};

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(..., long x, Long category, byte documentRoot, Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, ..., Long category, byte documentRoot, Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, Long category, ..., byte documentRoot, Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, Long category, byte documentRoot, ..., Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, Long category, byte documentRoot, Long y, ...) { }")));
	}

	@Test
	public void testIntermixedDots() {
		// contract: dots should be able to match arbitrary nonempty sequences of parameters

		String dots = SmPLJavaDSL.createDotsParameterString();
		Map<String, MetavariableConstraint> emptyMetavars = new HashMap<>();

		Function<String, MethodHeaderPredicate> makePredicate = (s) -> {
			return new MethodHeaderPredicate(parseMethod(s.replace("...", dots)), emptyMetavars);
		};

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(..., Long category, byte documentRoot, Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, ..., byte documentRoot, Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, Long category, ..., Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, Long category, byte documentRoot, ...) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(..., byte documentRoot, Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, ..., Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, Long category, ...) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(..., Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, ...) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(..., Long category, ..., Long y) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(long x, ..., byte documentRoot, ...) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(..., Long category, ...) { }")));

		assertEquals(true, matches(methods.get("double square(long x, Long category, byte documentRoot, Long y) { }"),
								   makePredicate.apply("double square(..., byte documentRoot, ...) { }")));
	}
}
