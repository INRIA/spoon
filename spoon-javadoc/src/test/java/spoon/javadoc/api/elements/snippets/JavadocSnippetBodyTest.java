package spoon.javadoc.api.elements.snippets;

import static org.assertj.core.api.Assertions.assertThat;
import static spoon.javadoc.api.TestHelper.region;
import static spoon.javadoc.api.elements.snippets.JavadocSnippetRegionType.START;

import java.util.Map;
import org.junit.jupiter.api.Test;

class JavadocSnippetBodyTest {

	@Test
	void testOverlap() {
		JavadocSnippetBody body = JavadocSnippetBody.fromString(
			"class Foo { // @start region=\"foo\"\n" +
			"  int p0 = 0; // @start region=\"bar\"\n" +
			"  int p1 = 1;\n" +
			"  int p2 = 2; // @end\n" +
			"  int p3 = 3; // @end\n" +
			"}\n"
		);
		assertThat(body.getLines()).hasSize(6);

		var first = region(0, 4, Map.of("region", "foo"), START);
		var second = region(1, 3, Map.of("region", "bar"), START);

		assertThat(body.getActiveRegionsAtLine(0)).containsExactly(first);
		assertThat(body.getActiveRegionsAtLine(1)).containsExactlyInAnyOrder(first, second);
		assertThat(body.getActiveRegionsAtLine(2)).containsExactlyInAnyOrder(first, second);
		assertThat(body.getActiveRegionsAtLine(3)).containsExactlyInAnyOrder(first, second);
		assertThat(body.getActiveRegionsAtLine(4)).containsExactlyInAnyOrder(first);
		assertThat(body.getActiveRegionsAtLine(5)).containsExactlyInAnyOrder();
	}

}
