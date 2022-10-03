package spoon.test.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.testing.utils.ModelTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static spoon.test.SpoonTestHelpers.containsRegexMatch;

/**
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se19/html/jls-4.html#jls-4.11">JLS 4.11</a>
 */
@DisplayName("cover rules for type annotations in places mentioned in JLS 4.11")
class TypeUseAnnotationTest {
	private static final String BASE_PATH = "src/test/resources/typeannotations/";
	private static final String TYPE_USE_A_PATH = BASE_PATH + "TypeUseA.java";
	private static final String TYPE_USE_B_PATH = BASE_PATH + "TypeUseB.java";

	@DisplayName("Types are used in most kinds of declaration and in certain kinds of expression. Specifically, there are 17 type contexts where types are used")
	@Nested
	class TypeContexts {
		@DisplayName("In declarations")
		@Nested
		class InDeclarations {

			@DisplayName("1. A type in the extends or implements clause of a class declaration (§8.1.4, §8.1.5)")
			@ModelTest({TYPE_USE_A_PATH, TYPE_USE_B_PATH, BASE_PATH + "p01/"})
			void testTypeAnnotationOnExtendsOrImplements(Factory factory) {
				// contract: type annotations on extends and implements declarations of classes are part of the model
				CtType<?> type = factory.Type().get("typeannotations.p01.ExtendsAndImplements");

				// first, check the annotation of the extends type
				assertThat(type.getSuperclass().getAnnotations().size(), equalTo(1));
				assertThat(type.getSuperclass().getAnnotations().get(0).getType(), equalTo(typeUseARef(factory)));
				assertThat(type.toString(), containsRegexMatch("extends java\\.lang\\.\\W*@.*TypeUseA\\W+Object"));

				// then, check the annotation of the implements type
				CtTypeReference<?> superInterface = type.getSuperInterfaces().iterator().next();
				assertThat(superInterface.getAnnotations().size(), equalTo(1));
				assertThat(superInterface.getAnnotations().get(0).getType(), equalTo(typeUseBRef(factory)));
				assertThat(type.toString(), containsRegexMatch("implements java\\.lang\\.\\W*@.*TypeUseB\\W+Cloneable"));
			}

			@DisplayName("2. A type in the extends clause of an interface declaration (§9.1.3)")
			@ModelTest({TYPE_USE_A_PATH, BASE_PATH + "p02/"})
			void testTypeAnnotationsOnInterfaceExtends(Factory factory) {
				// contract: type annotations on extends declarations of interfaces are part of the model
				CtType<?> type = factory.Type().get("typeannotations.p02.InterfaceExtends");

				CtTypeReference<?> superInterface = type.getSuperInterfaces().iterator().next();
				assertThat(superInterface.getAnnotations().size(), equalTo(1));
				assertThat(superInterface.getAnnotations().get(0).getType(), equalTo(typeUseARef(factory)));
				assertThat(type.toString(), containsRegexMatch("extends java\\.lang\\.\\W*@.*TypeUseA\\W+Cloneable"));
			}
		}

		@DisplayName("In expressions")
		@Nested
		class InExpressions {

		}
	}

	private CtTypeReference<?> typeUseARef(Factory factory) {
		return factory.Type().get("typeannotations.TypeUseA").getReference();
	}

	private CtTypeReference<?> typeUseBRef(Factory factory) {
		return factory.Type().get("typeannotations.TypeUseB").getReference();
	}
}
