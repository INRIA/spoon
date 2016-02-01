package spoon.support.compiler.jdt;

import org.junit.Test;
import spoon.compiler.builder.AdvancedOptions;
import spoon.compiler.builder.AnnotationProcessingOptions;
import spoon.compiler.builder.ClasspathOptions;
import spoon.compiler.builder.ComplianceOptions;
import spoon.compiler.builder.JDTBuilderImpl;
import spoon.compiler.builder.SourceOptions;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class JDTBuilderTest {
	private final static String TEST_CLASSPATH = "./src/test/java/spoon/test/";

	@Test
	public void testJdtBuilder() throws Exception {
		final String[] builder = new JDTBuilderImpl() //
				.classpathOptions(new ClasspathOptions().classpath(TEST_CLASSPATH).bootclasspath(TEST_CLASSPATH).binaries(".").encoding("UTF-8")) //
				.complianceOptions(new ComplianceOptions().compliance(8)) //
				.annotationProcessingOptions(new AnnotationProcessingOptions().compileProcessors()) //
				.advancedOptions(new AdvancedOptions().continueExecution().enableJavadoc().preserveUnusedVars()) //
				.sources(new SourceOptions().sources(".")) //
				.build();

		assertEquals("-cp", builder[0]);
		assertEquals(TEST_CLASSPATH, builder[1]);
		assertEquals("-bootclasspath", builder[2]);
		assertEquals(TEST_CLASSPATH, builder[3]);
		assertEquals("-d", builder[4]);
		assertEquals(new File(".").getAbsolutePath(), builder[5]);
		assertEquals("-encoding", builder[6]);
		assertEquals("UTF-8", builder[7]);
		assertEquals("-1.8", builder[8]);
		assertEquals("-proc:none", builder[9]);
		assertEquals("-noExit", builder[10]);
		assertEquals("-enableJavadoc", builder[11]);
		assertEquals("-preserveAllLocals", builder[12]);
		assertEquals(".", builder[13]);
	}
}
