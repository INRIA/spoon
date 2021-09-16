package spoon.javadoc.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import spoon.javadoc.internal.JavadocInlineTag.Type;

class JavadocInlineTagTest {

  @ParameterizedTest
  @MethodSource("tagNameArgumentProvider")
  void testTagNameTypeParsing(String tagName, Type expectedType) {
    // contract: javadoc tag names should parse to their corresponding spoon type
    JavadocInlineTag element = (JavadocInlineTag) JavadocInlineTag.fromText("{@" + tagName + "}");
    Type actualType = element.getType();

    assertEquals(tagName, element.getName());
    assertEquals(expectedType, actualType);
  }

  @ParameterizedTest
  @MethodSource("tagNameArgumentProvider")
  void testTagNameCaseInsensitiveTypeParsing(String originalTagName, Type expectedType) {
    // contract: javadoc tag names parsing should not depend on the case of the tag
    String tagName = scrambleCase(originalTagName);

    JavadocInlineTag element = (JavadocInlineTag) JavadocInlineTag.fromText("{@" + tagName + "}");
    Type actualType = element.getType();

    assertEquals(tagName, element.getName());
    assertEquals(expectedType, actualType);
  }

  public static Stream<Arguments> tagNameArgumentProvider() {
    return Stream.of(
        Arguments.of("code", Type.CODE),
        Arguments.of("docRoot", Type.DOC_ROOT),
        Arguments.of("inheritDoc", Type.INHERIT_DOC),
        Arguments.of("link", Type.LINK),
        Arguments.of("linkplain", Type.LINKPLAIN),
        Arguments.of("literal", Type.LITERAL),
        Arguments.of("value", Type.VALUE),
        Arguments.of("foobar", Type.UNKNOWN)
    );
  }

  private String scrambleCase(String input) {
    StringBuilder scrambledCaseBuilder = new StringBuilder();
    for (char c : input.toCharArray()) {
      if (ThreadLocalRandom.current().nextBoolean()) {
        scrambledCaseBuilder.append(Character.toUpperCase(c));
      } else {
        scrambledCaseBuilder.append(c);
      }
    }

    return scrambledCaseBuilder.toString();
  }
}
