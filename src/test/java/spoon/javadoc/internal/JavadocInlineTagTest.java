package spoon.javadoc.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import spoon.javadoc.internal.JavadocInlineTag.Type;

class JavadocInlineTagTest {

  @ParameterizedTest
  @MethodSource("tagNameArgumentProvider")
  void testTagNameTypeParsing(String tagName, Type expectedType) {

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
}
