package spoon.javadoc.api.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static spoon.javadoc.api.elements.snippets.JavadocSnippetRegionType.HIGHLIGHT;
import static spoon.javadoc.api.elements.snippets.JavadocSnippetRegionType.LINK;
import static spoon.javadoc.api.elements.snippets.JavadocSnippetRegionType.START;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import spoon.javadoc.api.TestHelper;
import spoon.javadoc.api.elements.snippets.JavadocSnippetMarkupRegion;

class SnippetFileParserTest {

	@Test
	void testGeneralParsing() {
		Set<JavadocSnippetMarkupRegion> regions = new SnippetFileParser(List.of(
			"class Foo { // @highlight",
			"  int p1 = 0; //@highlight substring=\"p1\" type=\"italic\"",
			"  int p2 = 0; //@highlight substring=\"p3\" type=\"italic\" region=\"foo\" :",
			"  int p3 = 0;",
			"  static {  // @end region=\"foo\"",
			"    System.out.println(\"hey\"); // @link substring=\"System.out\" target=\"System#out\"",
			"  }",
			"  int p4 = 0; // @start region=\"baz\"",
			"  int p5 = 0; // @end",
			"}"
		)).parse();
		List<JavadocSnippetMarkupRegion> expected = List.of(
			TestHelper.region(0, 0, Map.of(), HIGHLIGHT),
			TestHelper.region(1, 1, Map.of("substring", "p1", "type", "italic"), HIGHLIGHT),
			TestHelper.region(
				3, 4, Map.of("substring", "p3", "type", "italic", "region", "foo"),
				HIGHLIGHT
			),
			TestHelper.region(5, 5, Map.of("substring", "System.out", "target", "System#out"), LINK),
			TestHelper.region(7, 8, Map.of("region", "baz"), START)
		);

		assertThat(regions).containsExactlyInAnyOrderElementsOf(expected);
	}

	@Test
	void testOverlap() {
		Set<JavadocSnippetMarkupRegion> regions = new SnippetFileParser(List.of(
			"class Foo { // @start region=\"foo\"",
			"  int p0 = 0; // @start region=\"bar\"",
			"  int p1 = 1;",
			"  int p2 = 2; // @end",
			"  int p3 = 3; // @end",
			"}"
		)).parse();
		List<JavadocSnippetMarkupRegion> expected = List.of(
			TestHelper.region(0, 4, Map.of("region", "foo"), START),
			TestHelper.region(1, 3, Map.of("region", "bar"), START)
		);

		assertThat(regions).containsExactlyInAnyOrderElementsOf(expected);
	}

}
